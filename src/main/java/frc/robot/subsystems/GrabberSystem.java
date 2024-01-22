package frc.robot.subsystems;

import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.PneumaticHub;
import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.RobotState;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

/**
 * The class is responsible for managing the grabber subsystem.
 */
public class GrabberSystem extends SubsystemBase {
  private XboxController m_controller;
  private final Compressor m_compressor = new Compressor(PneumaticsModuleType.REVPH);
  protected PneumaticHub m_pneumaticHub;
  protected DoubleSolenoid m_solenoid;

  /**
   * Constructor.
   */
  public GrabberSystem(XboxController controller) {
    m_pneumaticHub = new PneumaticHub();
    m_compressor.enableDigital();

    m_solenoid = m_pneumaticHub.makeDoubleSolenoid(
        Constants.OperatorConstants.kGrabberForwardChannel,
        Constants.OperatorConstants.kGrabberBackwardChannel);

    m_controller = controller;
  }

  public void initDashBoard() {
  }

  public void updateDashOnRobotPeriodic() {
  }

  @Override
  public void periodic() {
    if (!RobotState.isAutonomous()) {
      if (m_controller.getLeftBumperReleased()) {
        m_solenoid.set(Value.kForward);
      }
      else if (m_controller.getRightBumperReleased()) {
        m_solenoid.set(Value.kReverse);
      }
    }
  }

  @Override
  public void simulationPeriodic() {
  }

  public void openGrabber() {
    m_solenoid.set(Value.kForward);
  }

  public void closeGrabber() {
    m_solenoid.set(Value.kReverse);
  }
}
