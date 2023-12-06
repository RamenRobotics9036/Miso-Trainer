package frc.robot.simulation.simplearm;

import edu.wpi.first.math.Pair;
import edu.wpi.first.wpilibj.simulation.DutyCycleEncoderSim;
import frc.robot.helpers.UnitConversions;
import java.util.function.DoubleSupplier;

/**
 * Simulates the arm as-if it were a real-world object. E.g. if the arm
 * is extended too far, it will break.
 */
public class ArmSimModel {
  private DoubleSupplier m_desiredArmAngleSupplier;
  private DutyCycleEncoderSim m_winchAbsoluteEncoderSim;
  private double m_currentSignedDegrees;
  private boolean m_isCurrentSignedDegreesSet = false;
  private double m_topSignedDegreesBreak;
  private double m_bottomSignedDegreesBreak;
  private double m_encoderRotationsOffset;
  private boolean m_isBroken;
  private ExtendArmInterface m_robotSpecificArmLogic = null;

  private void commonInitialization(DoubleSupplier desiredArmAngleSupplier,
      DutyCycleEncoderSim winchAbsoluteEncoderSim,
      ArmSimulationParams armParams) {

    if (desiredArmAngleSupplier == null) {
      throw new IllegalArgumentException("desiredArmAngleSupplier");
    }

    if (winchAbsoluteEncoderSim == null) {
      throw new IllegalArgumentException("winchAbsoluteEncoderSim");
    }

    if (armParams == null) {
      throw new IllegalArgumentException("armParams");
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
    m_desiredArmAngleSupplier = desiredArmAngleSupplier;
    m_winchAbsoluteEncoderSim = winchAbsoluteEncoderSim;
    m_encoderRotationsOffset = armParams.encoderRotationsOffset;
    m_isBroken = false;
  }

  /**
   * Constructor. Note that we call commoonInitialization() in all constructors,
   * rather than calling a primary constructor. We do this because constructor
   * calls updateAbsoluteEncoderPosition(), which assumes the object is fully
   * initialized.
   */
  public ArmSimModel(DoubleSupplier desiredArmAngleSupplier,
      DutyCycleEncoderSim winchAbsoluteEncoderSim,
      ArmSimulationParams armParams) {

    commonInitialization(desiredArmAngleSupplier, winchAbsoluteEncoderSim, armParams);

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
  public ArmSimModel(DoubleSupplier desiredArmAngleSupplier,
      DutyCycleEncoderSim winchAbsoluteEncoderSim,
      ArmSimulationParams armParams,
      ExtendArmInterface robotSpecificArmLogic) {

    // Instead of calling this(), we call commonInitialization() directly
    commonInitialization(desiredArmAngleSupplier, winchAbsoluteEncoderSim, armParams);

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

  private Pair<Boolean, Double> checkIfArmBroken(double oldSignedDegrees,
      boolean isOldSignedDegreesSet,
      double newSignedDegrees) {

    boolean isValid = true;
    double resetPositionTo = newSignedDegrees;

    // First, check robot-specific logic for arm broken
    if (m_robotSpecificArmLogic != null) {
      Pair<Boolean, Double> tempResult = m_robotSpecificArmLogic
          .checkIfArmBroken(oldSignedDegrees, isOldSignedDegreesSet, newSignedDegrees);

      if (tempResult != null && !tempResult.getFirst()) {
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

    return isValid ? null : new Pair<Boolean, Double>(isValid, resetPositionTo);
  }

  private Pair<Boolean, Double> checkIfArmStuck(double oldSignedDegrees,
      boolean isOldSignedDegreesSet,
      double newSignedDegrees) {

    boolean isValid = true;
    double resetPositionTo = newSignedDegrees;

    // First, check robot-specific logic for arm stuck
    if (m_robotSpecificArmLogic != null) {
      Pair<Boolean, Double> tempResult = m_robotSpecificArmLogic
          .checkIfArmStuck(oldSignedDegrees, isOldSignedDegreesSet, newSignedDegrees);

      if (tempResult != null && !tempResult.getFirst()) {
        return tempResult;
      }
    }

    // Now check general cases for arm stuck

    return isValid ? null : new Pair<Boolean, Double>(isValid, resetPositionTo);
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

    double newAbsoluteEncoderSignedDegrees = m_desiredArmAngleSupplier.getAsDouble();

    Pair<Boolean, Double> resultPairStuck = checkIfArmStuck(m_currentSignedDegrees,
        m_isCurrentSignedDegreesSet,
        newAbsoluteEncoderSignedDegrees);

    if (resultPairStuck != null && !resultPairStuck.getFirst()) {
      newAbsoluteEncoderSignedDegrees = resultPairStuck.getSecond();
    }

    Pair<Boolean, Double> resultPairBroken = checkIfArmBroken(m_currentSignedDegrees,
        m_isCurrentSignedDegreesSet,
        newAbsoluteEncoderSignedDegrees);

    if (resultPairBroken != null && !resultPairBroken.getFirst()) {
      m_isBroken = true;
      newAbsoluteEncoderSignedDegrees = resultPairBroken.getSecond();
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
