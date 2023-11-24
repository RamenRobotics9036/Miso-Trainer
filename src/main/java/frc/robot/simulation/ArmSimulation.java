package frc.robot.simulation;

import edu.wpi.first.wpilibj.simulation.DutyCycleEncoderSim;
import frc.robot.helpers.CalcArmAngleHelper;
import frc.robot.helpers.UnitConversions;
import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;

/**
 * Simulates the arm as-if it were a real-world object. E.g. if the arm
 * is extended too far, it will break.
 */
public class ArmSimulation {
  // Internal class to return a pair of values: a boolean result and a double resetPosition
  private class ResultPairArm {
    @SuppressWarnings("checkstyle:MemberName")
    public boolean isValid;
    @SuppressWarnings("checkstyle:MemberName")
    public double value;

    public ResultPairArm(boolean isValidInput, double valueInput) {
      isValid = isValidInput;
      value = valueInput;
    }
  }

  private DoubleSupplier m_stringUnspooledLenSupplier;
  private DutyCycleEncoderSim m_winchAbsoluteEncoderSim;
  private double m_currentSignedDegrees;
  private boolean m_isCurrentSignedDegreesSet = false;
  private double m_topSignedDegreesLimitFinal;
  private double m_bottomSignedDegreesLimitFinal;
  private double m_grabberBreaksIfOpenBelowSignedDegreesLimit;
  private double m_encoderRotationsOffset;
  private BooleanSupplier m_grabberOpenSupplier = null;
  private boolean m_isBroken;
  private CalcArmAngleHelper m_calcArmAngleHelper;

  /**
   * Constructor.
   */
  public ArmSimulation(DoubleSupplier stringUnspooledLenSupplier,
      DutyCycleEncoderSim winchAbsoluteEncoderSim,
      ArmSimulationParams armParams) {

    if (stringUnspooledLenSupplier == null) {
      throw new IllegalArgumentException("stringUnspooledLenSupplier");
    }

    if (armParams.armLengthFromEdgeToPivot < armParams.armLengthFromEdgeToPivotMin) {
      throw new IllegalArgumentException("armLengthFromEdgeToPivot needs to be at least "
          + armParams.armLengthFromEdgeToPivotMin + " meters, otherwise the arm cant be pivoted");
    }

    if (!UnitConversions.isRotationValid(armParams.encoderRotationsOffset)) {
      throw new IllegalArgumentException("encoderRotationsOffset must be between 0 and 1");
    }

    double topSignedDegreesLimitFinal = armParams.topSignedDegreesLimit
        + armParams.deltaDegreesBeforeBroken;

    double bottomSignedDegreesLimitFinal = armParams.bottomSignedDegreesLimit
        - armParams.deltaDegreesBeforeBroken;

    if (!UnitConversions.isInRightHalfPlane(topSignedDegreesLimitFinal)) {
      throw new IllegalArgumentException("topSignedDegreesLimitFinal must be between -90 and 90");
    }

    if (!UnitConversions.isInRightHalfPlane(bottomSignedDegreesLimitFinal)) {
      throw new IllegalArgumentException(
          "bottomSignedDegreesLimitFinal must be between -90 and 90");
    }

    if (!UnitConversions.isInRightHalfPlane(armParams.grabberSignedDegreesLimit)) {
      throw new IllegalArgumentException(
          "grabberBreaksIfOpenBelowSignedDegreesLimit must be between -90 and 90");
    }

    if (topSignedDegreesLimitFinal <= bottomSignedDegreesLimitFinal) {
      throw new IllegalArgumentException(
          "topSignedDegreesLimitFinal must be > bottomSignedDegreesLimitFinal");
    }

    double grabberLimit = armParams.grabberSignedDegreesLimit;
    if (grabberLimit >= topSignedDegreesLimitFinal
        || grabberLimit <= bottomSignedDegreesLimitFinal) {
      throw new IllegalArgumentException(
          "grabberBreaksIfOpenBelowSignedDegreesLimit must be between "
              + "topSignedDegreesLimit and bottomSignedDegreesLimit");
    }

    // Copy into member variables
    m_grabberBreaksIfOpenBelowSignedDegreesLimit = grabberLimit;
    m_topSignedDegreesLimitFinal = topSignedDegreesLimitFinal;
    m_bottomSignedDegreesLimitFinal = bottomSignedDegreesLimitFinal;
    m_stringUnspooledLenSupplier = stringUnspooledLenSupplier;
    m_winchAbsoluteEncoderSim = winchAbsoluteEncoderSim;
    m_encoderRotationsOffset = armParams.encoderRotationsOffset;
    m_isBroken = false;
    m_calcArmAngleHelper = new CalcArmAngleHelper(armParams.heightFromWinchToPivotPoint,
        armParams.armLengthFromEdgeToPivot);

    // Forces the absolute encoder to show the correct position
    updateAbsoluteEncoderPosition();

  }

  public boolean getIsBroken() {
    return m_isBroken;
  }

  private boolean isInGrabberBreakRange(double positionSignedDegrees) {
    return UnitConversions.lessThanButNotEqualDouble(positionSignedDegrees,
        m_grabberBreaksIfOpenBelowSignedDegreesLimit);
  }

  private ResultPairArm checkIfArmBroken(double oldSignedDegrees,
      boolean isOldSignedDegreesSet,
      double newSignedDegrees,
      boolean isGrabberOpen) {

    boolean isValid = true;
    double resetPositionTo = newSignedDegrees;

    if (isOldSignedDegreesSet && isGrabberOpen && isInGrabberBreakRange(newSignedDegrees)
        && isInGrabberBreakRange(oldSignedDegrees)) {

      // If the arm is ALREADY below a certain level, and grabber is open, arm is broken
      System.out.println("ARM: Grabber is open while arm is in breakable range");
      isValid = false;

      // Note that we don't let the arm move from where it was
      resetPositionTo = oldSignedDegrees;
    }

    if (newSignedDegrees > m_topSignedDegreesLimitFinal) {
      System.out.println("ARM: Angle is above top limit of " + m_topSignedDegreesLimitFinal);
      resetPositionTo = m_topSignedDegreesLimitFinal;
      isValid = false;
    }

    if (newSignedDegrees < m_bottomSignedDegreesLimitFinal) {
      System.out.println("ARM: Angle is below limit of " + m_bottomSignedDegreesLimitFinal);
      resetPositionTo = m_bottomSignedDegreesLimitFinal;
      isValid = false;
    }

    return new ResultPairArm(isValid, resetPositionTo);
  }

  private ResultPairArm checkIfArmStuck(double oldSignedDegrees,
      boolean isOldSignedDegreesSet,
      double newSignedDegrees,
      boolean isGrabberOpen) {

    boolean isValid = true;
    double resetPositionTo = newSignedDegrees;

    if (isOldSignedDegreesSet && isGrabberOpen && isInGrabberBreakRange(newSignedDegrees)
        && !isInGrabberBreakRange(oldSignedDegrees)) {

      // If the arm is ABOUT to go into the breakable range with the grabber open, the arm gets
      // stuck but doesn't break
      System.out.println("ARM: Grabber is open while try to move arm to ground");
      isValid = false;

      // With grabber open, arm is STUCK and not able to go lower than a certain point
      resetPositionTo = m_grabberBreaksIfOpenBelowSignedDegreesLimit;
    }

    return new ResultPairArm(isValid, resetPositionTo);
  }

  private void updateAbsoluteEncoderPosition() {
    // If the arm is broken, there's nothing to update
    if (m_isBroken) {
      return;
    }

    boolean isGrabberOpen = getGrabberOpen();

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
        newAbsoluteEncoderSignedDegrees,
        isGrabberOpen);

    if (resultPairStuck != null && !resultPairStuck.isValid) {
      newAbsoluteEncoderSignedDegrees = resultPairStuck.value;
    }

    ResultPairArm resultPairBroken = checkIfArmBroken(m_currentSignedDegrees,
        m_isCurrentSignedDegreesSet,
        newAbsoluteEncoderSignedDegrees,
        isGrabberOpen);

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

  public void setGrabberOpenSupplier(BooleanSupplier grabberOpenSupplier) {
    m_grabberOpenSupplier = grabberOpenSupplier;
  }

  /**
   * Returns true if grabber is open.
   * Uses the booleanSupplier passed to armSimulation from grabber system.
   */
  public boolean getGrabberOpen() {
    boolean result = false;

    if (m_grabberOpenSupplier != null) {
      result = m_grabberOpenSupplier.getAsBoolean();
    }

    return result;
  }

  public void simulationPeriodic() {
    updateAbsoluteEncoderPosition();
  }
}
