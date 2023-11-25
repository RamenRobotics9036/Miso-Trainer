package frc.robot.simulation;

import java.util.function.DoubleSupplier;

import edu.wpi.first.wpilibj.simulation.DutyCycleEncoderSim;
import frc.robot.Constants;
import frc.robot.helpers.UnitConversions;

/**
 * Robot-specific logic for the arm simulation.
 */
public class RamenArmSimLogic implements ArmSimLogicInterface {
  private final double m_grabberBreaksIfOpenBelowSignedDegreesLimit;

  /**
   * Constructor.
   */
  public RamenArmSimLogic(double grabberBreaksIfOpenBelowSignedDegreesLimit) {
    m_grabberBreaksIfOpenBelowSignedDegreesLimit = grabberBreaksIfOpenBelowSignedDegreesLimit;
  }

  /**
   * Create ArmSimulation, but with additional robot-specific logic
   * from Ramen bot.
   */
  public static ArmSimulation createRamenArmSimulation(DoubleSupplier stringUnspooledLenSupplier,
      DutyCycleEncoderSim winchAbsoluteEncoderSim,
      ArmSimulationParams armParams,
      double grabberBreaksIfOpenBelowSignedDegreesLimit) {

    ArmSimLogicInterface ramenArmLogic = new RamenArmSimLogic(UnitConversions
        .rotationToSignedDegrees(Constants.SimConstants.kgrabberBreaksIfOpenBelowThisLimit
            - Constants.SimConstants.karmEncoderRotationsOffset));

    ArmSimulation armSimulation = new ArmSimulation(stringUnspooledLenSupplier,
        winchAbsoluteEncoderSim, armParams, ramenArmLogic);

    return armSimulation;
  }

  /**
   * Check robot-specific logic for whether arm is BROKEN.
   */
  public ResultPairArm checkIfArmBroken(double oldSignedDegrees,
      boolean isOldSignedDegreesSet,
      double newSignedDegrees) {

    boolean isValid = true;
    double resetPositionTo = newSignedDegrees;

    boolean isGrabberOpen = false; // $TODO

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
      double newSignedDegrees) {

    boolean isValid = true;
    double resetPositionTo = newSignedDegrees;

    boolean isGrabberOpen = false; // $TODO

    if (isOldSignedDegreesSet && isGrabberOpen && isInGrabberBreakRange(newSignedDegrees)
        && !isInGrabberBreakRange(oldSignedDegrees)) {

      // If the arm is ABOUT to go into the breakable range with the grabber open, the arm gets
      // stuck but doesn't break
      System.out.println("ARM: Grabber is open while try to move arm to ground");
      isValid = false;

      // With grabber open, arm is STUCK and not able to go lower than a certain point
      resetPositionTo = m_grabberBreaksIfOpenBelowSignedDegreesLimit;
    }

    return isValid ? null : new ResultPairArm(isValid, resetPositionTo);

  }

  private boolean isInGrabberBreakRange(double positionSignedDegrees) {
    return UnitConversions.lessThanButNotEqualDouble(positionSignedDegrees,
        m_grabberBreaksIfOpenBelowSignedDegreesLimit);
  }
}
