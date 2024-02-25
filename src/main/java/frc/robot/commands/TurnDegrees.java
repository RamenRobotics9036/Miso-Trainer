package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.TankDriveSystem;

/**
 * Command to turn the robot a certain number of degrees.
 */
public class TurnDegrees extends Command {
  private double m_percentOutput;
  private double m_degrees;
  private double m_initialHeading;
  private TankDriveSystem m_drive;

  /**
   * Constructor.
   */
  public TurnDegrees(TankDriveSystem drive, double percentOutput, double degrees) {
    m_percentOutput = percentOutput;
    m_degrees = degrees;
    m_drive = drive;

    addRequirements(drive);
  }

  @Override
  public void initialize() {
    m_initialHeading = m_drive.getGyroYaw();
  }

  @Override
  public void execute() {
    // Calculate the error
    double error = m_degrees - (m_drive.getGyroYaw() - m_initialHeading);

    // Wrap error to be within -180 to 180 degrees
    error = ((error + 180) % 360) - 180;
    if (error < -180) {
      error += 360;
    }

    double direction = Math.signum(error);
    m_drive.tankDrive(-1 * direction * m_percentOutput, direction * m_percentOutput, true);
  }

  @Override
  public boolean isFinished() {
    // Check if the robot is within an acceptable error range (e.g., 2 degrees)
    return Math.abs(m_degrees - (m_drive.getGyroYaw() - m_initialHeading)) < 2;
  }

  @Override
  public void end(boolean interrupted) {
    m_drive.tankDrive(0, 0, false);
  }
}
