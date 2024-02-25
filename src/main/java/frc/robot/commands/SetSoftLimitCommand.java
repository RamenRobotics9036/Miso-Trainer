package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.ArmSystem;

/**
 * Command to set the soft limit of the arm.
 */
public class SetSoftLimitCommand extends Command {
  ArmSystem m_armSystem;

  public SetSoftLimitCommand(ArmSystem armSystem) {
    m_armSystem = armSystem;
    addRequirements(armSystem);
  }

  @Override
  public void initialize() {
    m_armSystem.setSoftLimit();
  }

  @Override
  public void execute() {
  }

  @Override
  public boolean isFinished() {
    return true;
  }

  @Override
  public void end(boolean interrupted) {
    m_armSystem.setExtenderSpeed(0);
  }
}
