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
  private DoubleSupplier m_stringUnspooledLenSupplier;
  private DutyCycleEncoderSim m_winchAbsoluteEncoderSim;
  private double m_currentSignedDegrees;
  private boolean m_isCurrentSignedDegreesSet = false;
  private double m_topSignedDegreesLimit;
  private double m_bottomSignedDegreesLimit;
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

    // $TODO - Just store armParams as an instance variable, rather than copying all the
    // values out of it
    if (armParams.m_armLengthFromEdgeToPivot < armParams.m_armLengthFromEdgeToPivotMin) {
      throw new IllegalArgumentException("armLengthFromEdgeToPivot needs to be at least "
          + armParams.m_armLengthFromEdgeToPivotMin + " meters, otherwise the arm cant be pivoted");
    }

    if (armParams.m_encoderRotationsOffset < 0 || armParams.m_encoderRotationsOffset >= 1) {
      throw new IllegalArgumentException("encoderRotationsOffset must be between 0 and 1");
    }

    m_topSignedDegreesLimit = UnitConversions.rotationToSignedDegrees(armParams.m_topRotationsLimit
        + armParams.m_deltaRotationsBeforeBroken - armParams.m_encoderRotationsOffset);

    m_bottomSignedDegreesLimit = UnitConversions
        .rotationToSignedDegrees(armParams.m_bottomRotationsLimit
            - armParams.m_deltaRotationsBeforeBroken - armParams.m_encoderRotationsOffset);

    m_grabberBreaksIfOpenBelowSignedDegreesLimit = UnitConversions.rotationToSignedDegrees(
        armParams.m_grabberBreaksIfOpenBelowThisLimit - armParams.m_encoderRotationsOffset);

    if (!UnitConversions.isInRightHalfPlane(m_topSignedDegreesLimit)) {
      throw new IllegalArgumentException("m_topSignedDegreesLimit must be between -90 and 90");
    }

    if (!UnitConversions.isInRightHalfPlane(m_bottomSignedDegreesLimit)) {
      throw new IllegalArgumentException("m_bottomSignedDegreesLimit must be between -90 and 90");
    }

    if (!UnitConversions.isInRightHalfPlane(m_grabberBreaksIfOpenBelowSignedDegreesLimit)) {
      throw new IllegalArgumentException(
          "m_grabberBreaksIfOpenBelowSignedDegreesLimit must be between -90 and 90");
    }

    if (m_topSignedDegreesLimit <= m_bottomSignedDegreesLimit) {
      throw new IllegalArgumentException(
          "m_topSignedDegreesLimit must be > m_bottomSignedDegreesLimit");
    }

    if (m_grabberBreaksIfOpenBelowSignedDegreesLimit >= m_topSignedDegreesLimit
        || m_grabberBreaksIfOpenBelowSignedDegreesLimit <= m_bottomSignedDegreesLimit) {
      throw new IllegalArgumentException(
          "m_grabberBreaksIfOpenBelowSignedDegreesLimit must be between "
              + "m_topSignedDegreesLimit and m_bottomSignedDegreesLimit");
    }

    m_stringUnspooledLenSupplier = stringUnspooledLenSupplier;
    m_winchAbsoluteEncoderSim = winchAbsoluteEncoderSim;
    m_encoderRotationsOffset = armParams.m_encoderRotationsOffset;
    m_isBroken = false;

    m_calcArmAngleHelper = new CalcArmAngleHelper(armParams.m_heightFromWinchToPivotPoint,
        armParams.m_armLengthFromEdgeToPivot);

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

    if (isGrabberOpen && m_isCurrentSignedDegreesSet
        && isInGrabberBreakRange(newAbsoluteEncoderSignedDegrees)) {

      if (!isInGrabberBreakRange(m_currentSignedDegrees)) {

        // If the arm is ABOUT to go into the breakable range with the grabber open, the arm gets
        // stuck but doesn't break
        System.out.println("ARM: Grabber is open while try to move arm to ground");

        // With grabber open, arm is STUCK and not able to go lower than a certain point
        newAbsoluteEncoderSignedDegrees = m_grabberBreaksIfOpenBelowSignedDegreesLimit;
      }
      else {

        // If the arm is ALREADY below a certain level, and grabber is broken, arm is broken
        System.out.println("ARM: Grabber is open while arm is in breakable range");
        m_isBroken = true;

        // Note that we don't let the arm move from where it was
        newAbsoluteEncoderSignedDegrees = m_currentSignedDegrees;
      }
    }

    if (newAbsoluteEncoderSignedDegrees > m_topSignedDegreesLimit) {
      System.out.println("ARM: Angle is above top limit of " + m_topSignedDegreesLimit);
      newAbsoluteEncoderSignedDegrees = m_topSignedDegreesLimit;
      m_isBroken = true;
    }

    if (newAbsoluteEncoderSignedDegrees < m_bottomSignedDegreesLimit) {
      System.out.println("ARM: Angle is below limit of " + m_bottomSignedDegreesLimit);
      newAbsoluteEncoderSignedDegrees = m_bottomSignedDegreesLimit;
      m_isBroken = true;
    }

    // Update the current position
    m_currentSignedDegrees = newAbsoluteEncoderSignedDegrees;
    m_isCurrentSignedDegreesSet = true;

    double newAbsoluteEncoderPosition = UnitConversions
        .signedDegreesToRotation(newAbsoluteEncoderSignedDegrees);

    // Add arm offset position back
    double newOffsetAbsoluteEncoderPosition = UnitConversions
        .clampRotation(newAbsoluteEncoderPosition + m_encoderRotationsOffset);

    m_winchAbsoluteEncoderSim.set(newOffsetAbsoluteEncoderPosition);
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
