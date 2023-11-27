package frc.robot.subsystems;

import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.RobotState;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.simulation.DoubleSolenoidSim;
import edu.wpi.first.wpilibj.simulation.REVPHSim;
import frc.robot.Constants;
import frc.robot.commands.CloseGrabberCommand;
import frc.robot.commands.GrabberOpenCommand;
import frc.robot.helpers.DefaultLayout;
import frc.robot.helpers.DefaultLayout.Widget;

import java.util.Map;
import java.util.function.BooleanSupplier;

/**
 * Subclass of GrabberSystem that is used for simulation. Note that this code isn't run if
 * the robot is not running in simulation mode.
 */
public class GrabberSystemSim extends GrabberSystem {
  private Value m_solenoidStatus;
  private boolean m_grabberPhysicallyOpened;
  private REVPHSim m_penumaticSim;
  private DoubleSolenoidSim m_solenoidSim;
  private DefaultLayout m_defaultLayout = new DefaultLayout();

  /**
   * Factory method to create a GrabberSystemSim or GrabberSystem object.
   */
  public static GrabberSystem createGrabberSystemInstance(XboxController controller) {
    GrabberSystem result;

    if (RobotBase.isSimulation()) {
      result = new GrabberSystemSim(controller);

      // System.out.println("GRABBERSYSTEM: **** Simulation ****");

    }
    else {
      result = new GrabberSystem(controller);

      // System.out.println("GRABBERSYSTEM: Physical Robot version");
    }

    return result;
  }

  // $LATER - Move widgets into GrabberSystemSimWithWidgets
  /**
   * Constructor.
   */
  public GrabberSystemSim(XboxController controller) {
    // FIRST, we call superclass
    super(controller);

    // This entire class should only be instantiated when we're under simulation.
    // But just in-case someone tries to instantiate it otherwise, we do an extra
    // check here.
    if (!RobotBase.isSimulation()) {
      return;
    }

    // Create simulated pneumatic hub
    m_penumaticSim = new REVPHSim(m_pneumaticHub);

    // Create simulated solenoid
    m_solenoidSim = new DoubleSolenoidSim(m_penumaticSim,
        Constants.OperatorConstants.kGrabberForwardChannel,
        Constants.OperatorConstants.kGrabberBackwardChannel);

    m_solenoidStatus = Value.kOff;
    m_grabberPhysicallyOpened = Constants.SimConstants.kgrabberInitiallyOpened;
  }

  private void addCommandButtons() {
    Widget pos = m_defaultLayout.getWidgetPosition("Open Grabber");
    // Open grabber
    Shuffleboard.getTab("Simulation").add("Open Grabber", new GrabberOpenCommand(this))
        .withWidget(BuiltInWidgets.kCommand).withPosition(pos.x, pos.y)
        .withSize(pos.width, pos.height);

    // Close grabber
    pos = m_defaultLayout.getWidgetPosition("Close Grabber");
    Shuffleboard.getTab("Simulation").add("Close Grabber", new CloseGrabberCommand(this))
        .withWidget(BuiltInWidgets.kCommand).withPosition(pos.x, pos.y)
        .withSize(pos.width, pos.height);

    // Grabber commands
    pos = m_defaultLayout.getWidgetPosition("Grabber System Commands");
    Shuffleboard.getTab("Simulation").add("Grabber System Commands", this)
        .withPosition(pos.x, pos.y).withSize(pos.width, pos.height);
  }

  public BooleanSupplier getGrabberOpenSupplier() {
    return () -> m_grabberPhysicallyOpened;
  }

  private String getGrabberStatusText() {
    String solenoidStatusText;
    String physicalGrabberText;

    switch (m_solenoidStatus) {
      case kForward:
        solenoidStatusText = "Forward";
        break;
      case kReverse:
        solenoidStatusText = "Reverse";
        break;
      case kOff:
        solenoidStatusText = "Off";
        break;
      default:
        solenoidStatusText = "Unknown";
        break;
    }

    physicalGrabberText = m_grabberPhysicallyOpened ? "Open" : "Closed";

    return String.format("%s (Sol: %s)", physicalGrabberText, solenoidStatusText);
  }

  private void addShuffleboardWidgets() {
    // Grabber functional
    Widget pos = m_defaultLayout.getWidgetPosition("Grabber Functional");
    Shuffleboard.getTab("Simulation").addBoolean("Grabber Functional", () -> true)
        .withWidget(BuiltInWidgets.kBooleanBox)
        .withProperties(Map.of("colorWhenTrue", "#C0FBC0", "colorWhenFalse", "#8B0000"))
        .withPosition(pos.x, pos.y).withSize(pos.width, pos.height);

    // Grabber open/closed
    pos = m_defaultLayout.getWidgetPosition("Grabber");
    Shuffleboard.getTab("Simulation").addString("Grabber", () -> getGrabberStatusText())
        .withWidget(BuiltInWidgets.kTextView).withPosition(pos.x, pos.y)
        .withSize(pos.width, pos.height);
  }

  private boolean isRobotEnabled() {
    return RobotState.isEnabled();
  }

  @Override
  public void initDashBoard() {
    super.initDashBoard();

    addShuffleboardWidgets();
    addCommandButtons();
  }

  @Override
  public void periodic() {
    super.periodic();
  }

  @Override
  public void simulationPeriodic() {
    super.simulationPeriodic();

    // When Robot is disabled, the entire simulation freezes
    if (isRobotEnabled()) {
      m_solenoidStatus = m_solenoidSim.get();

      // If the solenoid is on, update the physicalGrabber as opened or closed
      if (m_solenoidStatus == Value.kForward) {
        m_grabberPhysicallyOpened = true;
      }
      else if (m_solenoidStatus == Value.kReverse) {
        m_grabberPhysicallyOpened = false;
      }
    }
  }
}
