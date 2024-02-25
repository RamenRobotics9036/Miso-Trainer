package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.ArmSystem;

/**
 * Command to retract the arm fully.
 */
public class RetractArmCommand extends Command {
  ArmSystem m_armSystem;

  public RetractArmCommand(ArmSystem armSystem) {
    m_armSystem = armSystem;
    addRequirements(armSystem);
  }

  @Override
  public void initialize() {
  }

  @Override
  public void execute() {
    m_armSystem.setExtenderSpeed(0.7);
  }

  @Override
  public boolean isFinished() {
    if (!m_armSystem.getDigitalSensor()) {
      return true;
    }

    return false;
  }

  @Override
  public void end(boolean interrupted) {
    m_armSystem.setExtenderSpeed(0);
    m_armSystem.resetExtenderEncoder();
  }
}
