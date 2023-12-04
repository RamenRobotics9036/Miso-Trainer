package frc.robot.simulation.simplearm;

import edu.wpi.first.math.Pair;

/**
 * Interface for robot-specific logic for the arm simulation.
 */
public interface ExtendArmInterface {
  Pair<Boolean, Double> checkIfArmBroken(double oldSignedDegrees,
      boolean isOldSignedDegreesSet,
      double newSignedDegrees);

  Pair<Boolean, Double> checkIfArmStuck(double oldSignedDegrees,
      boolean isOldSignedDegreesSet,
      double newSignedDegrees);
}
