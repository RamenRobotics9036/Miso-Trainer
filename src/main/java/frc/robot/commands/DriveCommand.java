package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.TankDriveSystem;

/**
 * Command to drive the robot a certain distance.
 */
public class DriveCommand extends Command {
  private double m_distance;
  private double m_gearBoxRatio;
  private double m_percentOutput;
  private double m_wheelCircumference;

  TankDriveSystem m_drive;

  /**
   * Constructor.
   */
  public DriveCommand(TankDriveSystem drive,
      double distance,
      double gearBoxRatio,
      double percentOutput,
      double wheelCircumference) {

    m_distance = distance;
    m_gearBoxRatio = gearBoxRatio;
    m_percentOutput = -percentOutput;
    m_wheelCircumference = wheelCircumference;

    m_drive = drive;
    addRequirements(drive);
  }

  @Override
  public void initialize() {
    m_drive.resetEncoders();
  }

  @Override
  public void execute() {
    m_drive.tankDrive(-m_percentOutput, -m_percentOutput, false);
  }

  @Override
  public boolean isFinished() {
    if (m_distance <= m_drive.getAverageEncoderPosition() / m_gearBoxRatio * m_wheelCircumference) {
      return true;
    }
    return false;
  }

  @Override
  public void end(boolean interrupted) {
    m_drive.tankDrive(0, 0, false);
  }
}
