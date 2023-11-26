package frc.robot.simulation.simplearm.ramenarmlogic;

import edu.wpi.first.wpilibj.simulation.DutyCycleEncoderSim;
import frc.robot.helpers.UnitConversions;
import frc.robot.simulation.simplearm.ArmSimLogicInterface;
import frc.robot.simulation.simplearm.ArmSimulation;
import frc.robot.simulation.simplearm.ArmSimulationParams;
import frc.robot.simulation.simplearm.CreateArmResult;
import frc.robot.simulation.simplearm.ResultPairArm;
import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;

/**
 * Robot-specific logic for the arm simulation.
 */
public class RamenArmSimLogic implements ArmSimLogicInterface {
  private final double m_grabberBreaksIfOpenBelowSignedDegreesLimit;
  private BooleanSupplier m_grabberOpenSupplier = null;

  /**
   * Constructor.
   */
  public RamenArmSimLogic(double grabberBreaksIfOpenBelowSignedDegreesLimit,
      ArmSimulationParams armParams) {

    if (!UnitConversions.isInRightHalfPlane(grabberBreaksIfOpenBelowSignedDegreesLimit)) {
      throw new IllegalArgumentException(
          "grabberBreaksIfOpenBelowSignedDegreesLimit must be between -90 and 90");
    }

    if (grabberBreaksIfOpenBelowSignedDegreesLimit >= armParams.topSignedDegreesBreak
        || grabberBreaksIfOpenBelowSignedDegreesLimit <= armParams.bottomSignedDegreesBreak) {
      throw new IllegalArgumentException(
          "grabberBreaksIfOpenBelowSignedDegreesLimit must be between "
              + "topSignedDegreesBreak and bottomSignedBreak");
    }

    m_grabberBreaksIfOpenBelowSignedDegreesLimit = grabberBreaksIfOpenBelowSignedDegreesLimit;
  }

  /**
   * Create ArmSimulation, but with additional robot-specific logic
   * from Ramen bot.
   */
  public static CreateArmResult createRamenArmSimulation(DoubleSupplier stringUnspooledLenSupplier,
      DutyCycleEncoderSim winchAbsoluteEncoderSim,
      ArmSimulationParams armParams,
      double grabberBreaksIfOpenBelowSignedDegreesLimit) {

    RamenArmSimLogic ramenArmLogic = new RamenArmSimLogic(
        grabberBreaksIfOpenBelowSignedDegreesLimit, armParams);

    ArmSimulation armSimulation = new ArmSimulation(stringUnspooledLenSupplier,
        winchAbsoluteEncoderSim, armParams, ramenArmLogic);

    return new CreateArmResult(armSimulation, ramenArmLogic);
  }

  /**
   * Check robot-specific logic for whether arm is BROKEN.
   */
  public ResultPairArm checkIfArmBroken(double oldSignedDegrees,
      boolean isOldSignedDegreesSet,
      double newSignedDegrees) {

    boolean isValid = true;
    double resetPositionTo = newSignedDegrees;

    boolean isGrabberOpen = getGrabberOpen();

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

    boolean isGrabberOpen = getGrabberOpen();

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

  private boolean isInGrabberBreakRange(double positionSignedDegrees) {
    return UnitConversions.lessThanButNotEqualDouble(positionSignedDegrees,
        m_grabberBreaksIfOpenBelowSignedDegreesLimit);
  }
}
