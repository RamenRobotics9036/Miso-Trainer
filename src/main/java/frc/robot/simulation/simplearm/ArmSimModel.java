package frc.robot.simulation.simplearm;

import edu.wpi.first.math.Pair;
import edu.wpi.first.wpilibj.simulation.DutyCycleEncoderSim;
import frc.robot.helpers.UnitConversions;
import frc.robot.simulation.framework.SimModelInterface;

import java.util.function.DoubleSupplier;

/**
 * Simulates the arm as-if it were a real-world object. E.g. if the arm
 * is extended too far, it will break.
 */
public class ArmSimModel implements SimModelInterface<Double, Double> {
  private DoubleSupplier m_desiredArmAngleSupplier;
  private DutyCycleEncoderSim m_winchAbsoluteEncoderSim;
  private double m_currentSignedDegrees;
  private boolean m_isCurrentSignedDegreesSet = false;
  private double m_topSignedDegreesBreak;
  private double m_bottomSignedDegreesBreak;
  private double m_encoderRotationsOffset;
  private boolean m_isBroken;
  private ExtendArmInterface m_robotSpecificArmLogic = null;

  /**
   * Constructor.
   */
  public ArmSimModel(DoubleSupplier desiredArmAngleSupplier,
      DutyCycleEncoderSim winchAbsoluteEncoderSim,
      ArmSimParams armParams) {

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
   * Optional constructor that also takes ArmSimLogicInterface parameter,
   * which allows for robot-specific logic to be used for arm broken/stuck.
   */
  public ArmSimModel(DoubleSupplier desiredArmAngleSupplier,
      DutyCycleEncoderSim winchAbsoluteEncoderSim,
      ArmSimParams armParams,
      ExtendArmInterface robotSpecificArmLogic) {

    this(desiredArmAngleSupplier, winchAbsoluteEncoderSim, armParams);

    if (robotSpecificArmLogic == null) {
      throw new IllegalArgumentException("robotSpecificArmLogic");
    }

    m_robotSpecificArmLogic = robotSpecificArmLogic;
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
