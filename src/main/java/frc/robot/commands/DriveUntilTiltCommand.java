package frc.robot.commands;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.TankDriveSystem;

/**
 * Command to drive forward until the robot gyro indicates that the robot is
 * tilted.
 */
public class DriveUntilTiltCommand extends Command {
  private TankDriveSystem m_driveSystem;
  private double m_percentOutput;

  /**
   * Constructor.
   */
  public DriveUntilTiltCommand(TankDriveSystem driveSystem, double percentOutput) {
    m_percentOutput = percentOutput;
    m_driveSystem = driveSystem;

    addRequirements(driveSystem);
  }

  @Override
  public void initialize() {

  }

  @Override
  public void execute() {
    m_driveSystem.tankDrive(m_percentOutput, m_percentOutput, false);
  }

  @Override
  public boolean isFinished() {
    if (MathUtil.applyDeadband(m_driveSystem.getGyroAngle() + 2, 2.5) > 0) {
      return true;
    }

    return false;
  }

  @Override
  public void end(boolean interrupted) {
    m_driveSystem.tankDrive(0, 0, false);
  }
}
