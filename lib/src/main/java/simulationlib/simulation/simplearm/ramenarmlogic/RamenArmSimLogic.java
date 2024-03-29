package simulationlib.simulation.simplearm.ramenarmlogic;

import edu.wpi.first.math.Pair;
import edu.wpi.first.wpilibj.simulation.DutyCycleEncoderSim;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;
import simulationlib.helpers.UnitConversions;
import simulationlib.shuffle.MultiType;
import simulationlib.shuffle.PrefixedConcurrentMap.Client;
import simulationlib.simulation.framework.SimManager;
import simulationlib.simulation.framework.inputoutputs.AbsEncoderSimOutput;
import simulationlib.simulation.framework.inputoutputs.LambdaSimInput;
import simulationlib.simulation.simplearm.ArmDashboardPlugin;
import simulationlib.simulation.simplearm.ArmSimModel;
import simulationlib.simulation.simplearm.ArmSimParams;
import simulationlib.simulation.simplearm.ExtendArmInterface;

/**
 * Robot-specific logic for the arm simulation.
 */
public class RamenArmSimLogic implements ExtendArmInterface {
  private final double m_grabberBreaksIfOpenBelowSignedDegreesLimit;
  private BooleanSupplier m_grabberOpenSupplier = null;

  /**
   * Constructor.
   */
  public RamenArmSimLogic(double grabberBreaksIfOpenBelowSignedDegreesLimit,
      ArmSimParams armParams) {

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
  public static Pair<SimManager<Double, Double>, RamenArmSimLogic> createRamenArmSimulation(
      Client<Supplier<MultiType>> shuffleClient,
      Supplier<Double> desiredArmAngleSupplier,
      DutyCycleEncoderSim winchAbsoluteEncoderSim,
      ArmSimParams armParams,
      double grabberBreaksIfOpenBelowSignedDegreesLimit,
      boolean enableTestMode) {

    RamenArmSimLogic ramenArmLogic = new RamenArmSimLogic(
        grabberBreaksIfOpenBelowSignedDegreesLimit, armParams);

    Client<Supplier<MultiType>> armClient = (shuffleClient != null)
        ? shuffleClient.getSubdirectoryClient("Arm")
        : null;

    SimManager<Double, Double> armSimManager = new SimManager<Double, Double>(
        new ArmSimModel(armParams, ramenArmLogic), armClient, new ArmDashboardPlugin(),
        enableTestMode);
    armSimManager.setInputHandler(new LambdaSimInput<Double>(desiredArmAngleSupplier));
    armSimManager.setOutputHandler(new AbsEncoderSimOutput(winchAbsoluteEncoderSim));

    return new Pair<SimManager<Double, Double>, RamenArmSimLogic>(armSimManager, ramenArmLogic);
  }

  /**
   * Check robot-specific logic for whether arm is BROKEN.
   */
  public Pair<Boolean, Double> checkIfArmBroken(double oldSignedDegrees,
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

    return isValid ? null : new Pair<Boolean, Double>(isValid, resetPositionTo);
  }

  /**
   * Check robot-specific logic for whether arm is STUCK.
   */
  public Pair<Boolean, Double> checkIfArmStuck(double oldSignedDegrees,
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

    return isValid ? null : new Pair<Boolean, Double>(isValid, resetPositionTo);
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
