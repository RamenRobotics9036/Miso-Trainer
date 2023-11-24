package frc.robot.simulation;

/**
 * Interface for robot-specific logic for the arm simulation.
 */
public interface ArmSimLogicInterface {
  ResultPairArm checkIfArmBroken(double oldSignedDegrees,
      boolean isOldSignedDegreesSet,
      double newSignedDegrees,
      boolean isGrabberOpen);

  ResultPairArm checkIfArmStuck(double oldSignedDegrees,
      boolean isOldSignedDegreesSet,
      double newSignedDegrees,
      boolean isGrabberOpen);
}
