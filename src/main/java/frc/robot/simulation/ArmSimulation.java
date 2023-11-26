package frc.robot.simulation;

import edu.wpi.first.wpilibj.simulation.DutyCycleEncoderSim;
import frc.robot.helpers.UnitConversions;
import java.util.function.DoubleSupplier;

/**
 * Simulates the arm as-if it were a real-world object. E.g. if the arm
 * is extended too far, it will break.
 */
public class ArmSimulation {
  private DoubleSupplier m_stringUnspooledLenSupplier;
  private DutyCycleEncoderSim m_winchAbsoluteEncoderSim;
  private double m_currentSignedDegrees;
  private boolean m_isCurrentSignedDegreesSet = false;
  private double m_topSignedDegreesBreak;
  private double m_bottomSignedDegreesBreak;
  private double m_encoderRotationsOffset;
  private boolean m_isBroken;
  private CalcArmAngleHelper m_calcArmAngleHelper;
  private ArmSimLogicInterface m_robotSpecificArmLogic = null;

  private void commonInitialization(DoubleSupplier stringUnspooledLenSupplier,
      DutyCycleEncoderSim winchAbsoluteEncoderSim,
      ArmSimulationParams armParams) {

    if (stringUnspooledLenSupplier == null) {
      throw new IllegalArgumentException("stringUnspooledLenSupplier");
    }

    if (winchAbsoluteEncoderSim == null) {
      throw new IllegalArgumentException("winchAbsoluteEncoderSim");
    }

    if (armParams == null) {
      throw new IllegalArgumentException("armParams");
    }

    if (armParams.armLengthFromEdgeToPivot < armParams.armLengthFromEdgeToPivotMin) {
      throw new IllegalArgumentException("armLengthFromEdgeToPivot needs to be at least "
          + armParams.armLengthFromEdgeToPivotMin + " meters, otherwise the arm cant be pivoted");
    }

    if (!UnitConversions.isRotationValid(armParams.encoderRotationsOffset)) {
      throw new IllegalArgumentException("encoderRotationsOffset must be between 0 and 1");
    }

    if (!UnitConversions.isInRightHalfPlane(armParams.topSignedDegreesBreak)) {
      throw new IllegalArgumentException("topSignedDegreesBreak must be between -90 and 90");
    }

    if (!UnitConversions.isInRightHalfPlane(armParams.bottomSignedDegreesBreak)) {
      throw new IllegalArgumentException("bottomSignedDegreesBreak must be between -90 and 90");
    }

    if (armParams.topSignedDegreesBreak <= armParams.bottomSignedDegreesBreak) {
      throw new IllegalArgumentException(
          "topSignedDegreesBreak must be > bottomSignedDegreesBreak");
    }

    // Copy into member variables
    m_topSignedDegreesBreak = armParams.topSignedDegreesBreak;
    m_bottomSignedDegreesBreak = armParams.bottomSignedDegreesBreak;
    m_stringUnspooledLenSupplier = stringUnspooledLenSupplier;
    m_winchAbsoluteEncoderSim = winchAbsoluteEncoderSim;
    m_encoderRotationsOffset = armParams.encoderRotationsOffset;
    m_isBroken = false;
    m_calcArmAngleHelper = new CalcArmAngleHelper(armParams.heightFromWinchToPivotPoint,
        armParams.armLengthFromEdgeToPivot);
  }

  /**
   * Constructor. Note that we call commoonInitialization() in all constructors,
   * rather than calling a primary constructor. We do this because constructor
   * calls updateAbsoluteEncoderPosition(), which assumes the object is fully
   * initialized.
   */
  public ArmSimulation(DoubleSupplier stringUnspooledLenSupplier,
      DutyCycleEncoderSim winchAbsoluteEncoderSim,
      ArmSimulationParams armParams) {

    commonInitialization(stringUnspooledLenSupplier, winchAbsoluteEncoderSim, armParams);

    // Forces the absolute encoder to show the correct position
    updateAbsoluteEncoderPosition();

  }

  /**
   * Optional constructor that also takes ArmSimLogicInterface parameter,
   * which allows for robot-specific logic to be used for arm broken/stuck.
   * Note that we call commoonInitialization() in all constructors,
   * rather than calling a primary constructor. We do this because constructor
   * calls updateAbsoluteEncoderPosition(), which assumes the object is fully
   * initialized.
   */
  public ArmSimulation(DoubleSupplier stringUnspooledLenSupplier,
      DutyCycleEncoderSim winchAbsoluteEncoderSim,
      ArmSimulationParams armParams,
      ArmSimLogicInterface robotSpecificArmLogic) {

    // Instead of calling this(), we call commonInitialization() directly
    commonInitialization(stringUnspooledLenSupplier, winchAbsoluteEncoderSim, armParams);

    if (robotSpecificArmLogic == null) {
      throw new IllegalArgumentException("robotSpecificArmLogic");
    }

    m_robotSpecificArmLogic = robotSpecificArmLogic;

    // Forces the absolute encoder to show the correct position
    updateAbsoluteEncoderPosition();
  }

  public boolean getIsBroken() {
    return m_isBroken;
  }

  private ResultPairArm checkIfArmBroken(double oldSignedDegrees,
      boolean isOldSignedDegreesSet,
      double newSignedDegrees) {

    boolean isValid = true;
    double resetPositionTo = newSignedDegrees;

    // First, check robot-specific logic for arm broken
    if (m_robotSpecificArmLogic != null) {
      ResultPairArm tempResult = m_robotSpecificArmLogic
          .checkIfArmBroken(oldSignedDegrees, isOldSignedDegreesSet, newSignedDegrees);

      if (tempResult != null && !tempResult.isValid) {
        return tempResult;
      }
    }

    // Now check general cases for arm broken
    if (newSignedDegrees > m_topSignedDegreesBreak) {
      System.out.println("ARM: Angle is above top break limit of " + m_topSignedDegreesBreak);
      resetPositionTo = m_topSignedDegreesBreak;
      isValid = false;
    }

    if (newSignedDegrees < m_bottomSignedDegreesBreak) {
      System.out.println("ARM: Angle is below break limit of " + m_bottomSignedDegreesBreak);
      resetPositionTo = m_bottomSignedDegreesBreak;
      isValid = false;
    }

    return isValid ? null : new ResultPairArm(isValid, resetPositionTo);
  }

  private ResultPairArm checkIfArmStuck(double oldSignedDegrees,
      boolean isOldSignedDegreesSet,
      double newSignedDegrees) {

    boolean isValid = true;
    double resetPositionTo = newSignedDegrees;

    // First, check robot-specific logic for arm stuck
    if (m_robotSpecificArmLogic != null) {
      ResultPairArm tempResult = m_robotSpecificArmLogic
          .checkIfArmStuck(oldSignedDegrees, isOldSignedDegreesSet, newSignedDegrees);

      if (tempResult != null && !tempResult.isValid) {
        return tempResult;
      }
    }

    // Now check general cases for arm stuck

    return isValid ? null : new ResultPairArm(isValid, resetPositionTo);
  }

  private void updateAbsoluteEncoderPosition() {
    // If the arm is broken, there's nothing to update
    if (m_isBroken) {
      return;
    }

    // $LATER - For now, we assume that robot specific object is always there
    if (m_robotSpecificArmLogic == null) {
      throw new IllegalStateException("We assume robotSpecificArmLogic is always there");
    }

    double newStringLen = m_stringUnspooledLenSupplier.getAsDouble();
    CalcArmAngleHelper.Result resultPair = m_calcArmAngleHelper
        .calcSignedDegreesForStringLength(newStringLen);

    // Check if we got back that string length was invalid
    if (!resultPair.m_isValid) {
      System.out.println("ARM: Angle is out of bounds, needs to be in right half plane");
      m_isBroken = true;
    }

    double newAbsoluteEncoderSignedDegrees = resultPair.m_value;

    ResultPairArm resultPairStuck = checkIfArmStuck(m_currentSignedDegrees,
        m_isCurrentSignedDegreesSet,
        newAbsoluteEncoderSignedDegrees);

    if (resultPairStuck != null && !resultPairStuck.isValid) {
      newAbsoluteEncoderSignedDegrees = resultPairStuck.value;
    }

    ResultPairArm resultPairBroken = checkIfArmBroken(m_currentSignedDegrees,
        m_isCurrentSignedDegreesSet,
        newAbsoluteEncoderSignedDegrees);

    if (resultPairBroken != null && !resultPairBroken.isValid) {
      m_isBroken = true;
      newAbsoluteEncoderSignedDegrees = resultPairBroken.value;
    }

    // Update the current position
    m_currentSignedDegrees = newAbsoluteEncoderSignedDegrees;
    m_isCurrentSignedDegreesSet = true;

    // Add arm offset position back and convert to rotations units
    double newAbsoluteEncoderPosition = m_encoderRotationsOffset
        + UnitConversions.signedDegreesToRotation(newAbsoluteEncoderSignedDegrees);

    m_winchAbsoluteEncoderSim.set(newAbsoluteEncoderPosition);
  }

  public void simulationPeriodic() {
    updateAbsoluteEncoderPosition();
  }
}
