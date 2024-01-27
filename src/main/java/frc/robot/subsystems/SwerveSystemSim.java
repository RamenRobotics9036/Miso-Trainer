package frc.robot.subsystems;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj.RobotState;
import edu.wpi.first.wpilibj.XboxController;
import frc.robot.Constants;
import simulationlib.shuffle.PrefixedConcurrentMap;
import simulationlib.simulation.drive.ArcadeInputParams;
import simulationlib.simulation.drive.DriveDashboardPlugin;
import simulationlib.simulation.drive.DriveInputState;
import simulationlib.simulation.drive.DriveSimModel;
import simulationlib.simulation.drive.DriveState;
import simulationlib.simulation.framework.SimManager;
import simulationlib.simulation.framework.inputoutputs.LambdaSimInput;
import simulationlib.simulation.framework.inputoutputs.LambdaSimOutput;

/**
 * Subclass of TankDriveSystem that is used for simulation. Note that this code isn't run if
 * the robot is not running in simulation mode.
 */
public class SwerveSystemSim extends TankDriveSystem {
  /**
   * Constructor.
   */
  public SwerveSystemSim(XboxController controller) {
    // FIRST, we call superclass
    super(controller);

  }

  private boolean isRobotEnabled() {
    return RobotState.isEnabled();
  }

  @Override
  public void periodic() {
    super.periodic();
  }

  @Override
  public void simulationPeriodic() {
    super.simulationPeriodic();

    if (isRobotEnabled()) {
    }
  }

  @Override
  public void resetEncoders() {
    super.resetEncoders();
  }

  @Override
  public double getGyroYaw() {
    return 0;
  }

  // RETURN SIMULATED VALUE: Overrides physical encoder value in parent class
  @Override
  public double getLeftEncoder() {
    // Note that our relativeEncoder returns distance the SIMULATED robot moved on
    // the field in meters.
    // But we want to return number of MOTOR rotations that our PHYSICAL robot would
    // have had to take to move that distance in real life.
    return 0;
  }

  // RETURN SIMULATED VALUE: Overrides physical encoder value in parent class
  @Override
  public double getRightEncoder() {
    // Note that our relativeEncoder returns distance the SIMULATED robot moved on
    // the field in meters.
    // But we want to return number of MOTOR rotations that our PHYSICAL robot would
    // have had to take to move that distance in real life.
    return 0;
  }

  @Override
  public void arcadeDrive(double xspeed, double zrotation, boolean squareInputs) {
    super.arcadeDrive(xspeed, zrotation, squareInputs);
  }

  @Override
  public void tankDrive(double leftSpeed, double rightSpeed, boolean squareInputs) {
    super.tankDrive(leftSpeed, rightSpeed, squareInputs);
  }
}
