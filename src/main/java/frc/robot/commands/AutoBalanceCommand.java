package frc.robot.commands;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.TankDriveSystem;

/**
 * This class represents a command for an automated balance control system for a tank drive system.
 * It makes use of a gyroscope to perform balance corrections at a defined rate.
 */
public class AutoBalanceCommand extends Command {
  private TankDriveSystem m_driveSystem;
  private double m_rate;
  private Timer m_timer = new Timer();
  private double m_cycle = 1;

  /**
   * Constructs a new AutoBalanceCommand with the given tank drive system and rate.
   *
   * @param driveSystem the tank drive system to control
   * @param rate        the rate at which balance corrections should be applied
   */
  public AutoBalanceCommand(TankDriveSystem driveSystem, double rate) {
    m_driveSystem = driveSystem;
    m_rate = rate;
    addRequirements(driveSystem);
  }

  @Override
  public void initialize() {
    m_timer.start();
  }

  /*
   * This function is called every 20ms until the command is completed
   */
  @Override
  public void execute() {
    double executeRate = m_rate / m_cycle;

    if (MathUtil.applyDeadband(m_driveSystem.getGyroRate(), 3) > 0) {
      m_driveSystem.tankDrive(0, 0, false);
      if (m_timer.get() >= 0.5) {
        m_cycle += 0.5;
        m_timer.reset();
      }
    }
    else if (MathUtil.applyDeadband(m_driveSystem.getGyroAngle() + 2.5, 2) > 0) {
      m_driveSystem.tankDrive(executeRate, executeRate, false);
    }
    else if (MathUtil.applyDeadband(m_driveSystem.getGyroAngle() + 2.5, 2) < 0) {
      m_driveSystem.tankDrive(-executeRate, -executeRate, false);
    }
    else {
      m_driveSystem.tankDrive(0, 0, false);
      if (m_timer.get() >= 0.5) {
        m_cycle += 0.5; // Changed from 0.5
        m_timer.reset();
      }
    }

    m_driveSystem.setRate();
    // new WaitCommand(0.5).schedule();
  }

  @Override
  public boolean isFinished() {
    return false;
  }

  @Override
  public void end(boolean interrupted) {
    m_driveSystem.tankDrive(0, 0, false);
  }
}
