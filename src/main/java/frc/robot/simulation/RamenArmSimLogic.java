package frc.robot.simulation;

import frc.robot.helpers.UnitConversions;

/**
 * Robot-specific logic for the arm simulation.
 */
public class RamenArmSimLogic implements ArmSimLogicInterface {
  /**
   * Check robot-specific logic for whether arm is BROKEN.
   */
  public ResultPairArm checkIfArmBroken(double oldSignedDegrees,
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

    return isValid ? null : new ResultPairArm(isValid, resetPositionTo);

  }

  /**
   * Check robot-specific logic for whether arm is STUCK.
   */
  public ResultPairArm checkIfArmStuck(double oldSignedDegrees,
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
      resetPositionTo = 0; // $TODOm_grabberBreaksIfOpenBelowSignedDegreesLimit;
    }

    return isValid ? null : new ResultPairArm(isValid, resetPositionTo);

  }

  private boolean isInGrabberBreakRange(double positionSignedDegrees) {
    return UnitConversions.lessThanButNotEqualDouble(positionSignedDegrees, 0);
    // $TODO m_grabberBreaksIfOpenBelowSignedDegreesLimit);
  }
}
