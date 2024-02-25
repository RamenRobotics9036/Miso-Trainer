package frc.robot.commands;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Constants;
import frc.robot.subsystems.ArmSystem;

/**
 * Command to set the winch to a certain angle.
 */
public class SetWinchToAngle extends Command {
  private ArmSystem m_armSystem;
  private double m_angle;
  private double m_speed;
  private int m_inverse;

  /**
   * Constructor.
   */
  public SetWinchToAngle(ArmSystem armSystem, double angle, double speed) {
    m_armSystem = armSystem;
    addRequirements(armSystem);

    m_angle = angle;
    m_speed = speed;
  }

  @Override
  public void initialize() {
    double currentRotations = m_armSystem.getWinchAbsoluteEncoder();
    if (currentRotations > m_angle) {
      m_inverse = -1;
      //System.out.println("inversed");
    }
    else {
      m_inverse = 1;
      //System.out.println("not inversed");
    }
    //System.out.println("Command initialized with enoder at " + currentRotations);
    //System.out.println("Command initialized with speed at " + m_speed);
  }

  @Override
  public void execute() {
    m_armSystem.setWinchSpeed(m_inverse * m_speed);
    //System.out.println("Command executed " + m_armSystem.getWinchAbsoluteEncoder());
  }

  @Override
  public boolean isFinished() {
    if (MathUtil.applyDeadband(m_armSystem.getLeftAxis(),
        Constants.OperatorConstants.kDeadband) != 0) {
      return true;

    }
    //System.out.println("ENCODER" + m_armSystem.getWinchAbsoluteEncoder());
    //System.out.println("ANGLE" + m_angle);
    if (m_inverse == 1 && m_armSystem.getWinchAbsoluteEncoder() >= m_angle) {
      //System.out.println("Is finished with encoder at " + m_armSystem.getWinchAbsoluteEncoder());
      return true;
    }
    else if (m_inverse == -1 && m_armSystem.getWinchAbsoluteEncoder() <= m_angle) {
      //System.out.println("Is finished with encoder at " + m_armSystem.getWinchAbsoluteEncoder());
      return true;
    }
    return false;
  }

  @Override
  public void end(boolean interrupted) {
    //System.out.println("Ended with encoder at " + m_armSystem.getWinchAbsoluteEncoder());
    m_armSystem.setWinchSpeed(0);
  }
}
