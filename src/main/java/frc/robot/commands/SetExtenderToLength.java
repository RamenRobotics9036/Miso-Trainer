package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.ArmSystem;

/**
 * Command to set extender to a certain length.
 */
public class SetExtenderToLength extends Command {
  private ArmSystem m_armSystem;
  private double m_rotations;
  private double m_speed;
  private int m_inverse;

  /**
   * Constructor.
   */
  public SetExtenderToLength(ArmSystem armSystem, double rotations, double speed) {
    m_armSystem = armSystem;
    addRequirements(armSystem);

    m_rotations = rotations;
    m_speed = speed;
  }

  @Override
  public void initialize() {
    if (m_armSystem.getExtenderEncoder() > m_rotations) {
      m_inverse = -1;
      //System.out.println("inversed");
    }
    else {
      m_inverse = 1;
      //System.out.println("not inversed");
    }
  }

  @Override
  public void execute() {
    m_armSystem.setExtenderSpeed(m_inverse * m_speed);
  }

  @Override
  public boolean isFinished() {

    if (m_inverse == 1 && m_armSystem.getExtenderEncoder() >= m_rotations) {
      return true;
    }
    else if (m_inverse == -1 && m_armSystem.getExtenderEncoder() <= m_rotations) {
      return true;
    }
    return false;
  }

  @Override
  public void end(boolean interrupted) {
    m_armSystem.setExtenderSpeed(0);
  }
}
