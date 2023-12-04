package frc.robot.simulation.simplearm;

/**
 * Interface for robot-specific logic for the arm simulation.
 */
public interface ExtendArmInterface {
  ResultPairArm checkIfArmBroken(double oldSignedDegrees,
      boolean isOldSignedDegreesSet,
      double newSignedDegrees);

  ResultPairArm checkIfArmStuck(double oldSignedDegrees,
      boolean isOldSignedDegreesSet,
      double newSignedDegrees);
}
