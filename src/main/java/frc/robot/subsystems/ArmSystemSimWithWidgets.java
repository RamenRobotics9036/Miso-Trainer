package frc.robot.subsystems;

import edu.wpi.first.util.sendable.Sendable;
import edu.wpi.first.util.sendable.SendableBuilder;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import frc.robot.Constants;
import frc.robot.commands.ArmExtendFully;
import frc.robot.commands.ArmToGround;
import frc.robot.commands.ArmToMiddleNodeCone;
import frc.robot.commands.RetractArmCommand;
import frc.robot.helpers.DefaultLayout;
import frc.robot.helpers.DefaultLayout.Widget;
import frc.robot.shuffle.MultiType;
import frc.robot.shuffle.PrefixedConcurrentMap;
import frc.robot.shuffle.SupplierMapFactory;
import java.util.Map;
import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;
import java.util.function.Supplier;

/**
 * This adds shuffleboard widgets to the ArmSystemSim class.
 */
public class ArmSystemSimWithWidgets extends ArmSystemSim {
  private DefaultLayout m_defaultLayout = new DefaultLayout();
  PrefixedConcurrentMap<Supplier<MultiType>> m_globalMap = SupplierMapFactory.getGlobalInstance();

  private static class SendableArmPosition implements Sendable {
    private DoubleSupplier m_percentRaisedSupplier;
    private DoubleSupplier m_percentExtendedSupplier;
    private BooleanSupplier m_clawOpenSupplier;

    /**
     * Constructor.
     */
    public SendableArmPosition(DoubleSupplier percentRaisedSupplier,
        DoubleSupplier percentExtendedSupplier,
        BooleanSupplier clawOpenSupplier) {

      m_percentRaisedSupplier = percentRaisedSupplier;
      m_percentExtendedSupplier = percentExtendedSupplier;
      m_clawOpenSupplier = clawOpenSupplier;
    }

    @Override
    public void initSendable(SendableBuilder builder) {
      builder.setSmartDashboardType(Constants.SimConstants.kAnimatedArmWidget);
      builder.addDoubleProperty("percentRaised", m_percentRaisedSupplier, null);
      builder.addDoubleProperty("percentExtended", m_percentExtendedSupplier, null);
      builder.addBooleanProperty("isClawOpen", m_clawOpenSupplier, null);
    }
  }

  /**
   * Constructor.
   */
  public ArmSystemSimWithWidgets(XboxController controller) {

    // FIRST, we call superclass
    super(controller);
  }

  // $TODO - Command buttons can now be moved over to the new shuffleboard population code
  private void addCommandButtons() {
    // Move to to middle node cone
    Widget pos = m_defaultLayout.getWidgetPosition("Arm Middle node");
    Shuffleboard.getTab("Simulation").add("Arm Middle node", new ArmToMiddleNodeCone(this))
        .withWidget(BuiltInWidgets.kCommand).withPosition(pos.x, pos.y)
        .withSize(pos.width, pos.height);

    // Lower arm to ground
    pos = m_defaultLayout.getWidgetPosition("Arm to ground");
    Shuffleboard.getTab("Simulation").add("Arm to ground", new ArmToGround(this))
        .withWidget(BuiltInWidgets.kCommand).withPosition(pos.x, pos.y)
        .withSize(pos.width, pos.height);

    // Extend arm
    pos = m_defaultLayout.getWidgetPosition("Extend");
    Shuffleboard.getTab("Simulation").add("Extend", new ArmExtendFully(this))
        .withWidget(BuiltInWidgets.kCommand).withPosition(pos.x, pos.y)
        .withSize(pos.width, pos.height);

    // Retract extender
    pos = m_defaultLayout.getWidgetPosition("Retract extender");
    Shuffleboard.getTab("Simulation").add("Retract extender", new RetractArmCommand(this))
        .withWidget(BuiltInWidgets.kCommand).withPosition(pos.x, pos.y)
        .withSize(pos.width, pos.height);
  }

  private void addShuffleboardExtenderList() {
    // Extender functional
    // $TODO - Hiding old widget for now
    // Widget pos = m_defaultLayout.getWidgetPosition("Extender Functional");
    // Shuffleboard.getTab("Simulation")
    // .addBoolean("Extender Functional", () -> !m_extenderSimulation.getIsBroken())
    // .withWidget(BuiltInWidgets.kBooleanBox)
    // .withProperties(Map.of("colorWhenTrue", "#C0FBC0", "colorWhenFalse", "#8B0000"))
    // .withPosition(pos.x, pos.y).withSize(pos.width, pos.height);

    // Extender percent extended
    // $TODO - Hiding old widget for now
    // pos = m_defaultLayout.getWidgetPosition("Extender % Extended");
    // Shuffleboard.getTab("Simulation")
    // .addDouble("Extender % Extended", () -> m_extenderSimulation.getExtendedPercent())
    // .withWidget(BuiltInWidgets.kNumberBar)
    // .withProperties(Map.of("min", 0.0, "max", 1.0, "show text", false))
    // .withPosition(pos.x, pos.y).withSize(pos.width, pos.height);

    // Extender sensor display
    Widget pos = m_defaultLayout.getWidgetPosition("Extender Sensor");
    Shuffleboard.getTab("Simulation").addBoolean("Extender Sensor", () -> !m_sensorSim.getValue())
        .withWidget(BuiltInWidgets.kBooleanBox)
        .withProperties(Map.of("colorWhenTrue", "#C0FBC0", "colorWhenFalse", "#FFFFFF"))
        .withPosition(pos.x, pos.y).withSize(pos.width, pos.height);
  }

  private void addShuffleboardArmList() {
    // Arm functional display
    Widget pos = m_defaultLayout.getWidgetPosition("Arm Functional");
    Shuffleboard.getTab("Simulation").addBoolean("Arm Functional", () -> !getIsStringOrArmBroken())
        .withWidget(BuiltInWidgets.kBooleanBox)
        .withProperties(Map.of("colorWhenTrue", "#C0FBC0", "colorWhenFalse", "#8B0000"))
        .withPosition(pos.x, pos.y).withSize(pos.width, pos.height);

    // Arm position
    pos = m_defaultLayout.getWidgetPosition("Arm position");
    Shuffleboard.getTab("Simulation")
        .addDouble("Arm position", () -> m_winchAbsoluteEncoder.getAbsolutePosition())
        .withWidget(BuiltInWidgets.kTextView).withPosition(pos.x, pos.y)
        .withSize(pos.width, pos.height);

    // Arm commands
    pos = m_defaultLayout.getWidgetPosition("Arm System Commands");
    Shuffleboard.getTab("Simulation").add("Arm System Commands", this).withPosition(pos.x, pos.y)
        .withSize(pos.width, pos.height);
  }

  private void addShuffleboardWinchList() {
    // Winch String % extended
    Widget pos = m_defaultLayout.getWidgetPosition("Winch String % Extended");
    Shuffleboard.getTab("Simulation")
        .addDouble("Winch String % Extended", () -> m_winchState.getStringUnspooledPercent())
        .withWidget(BuiltInWidgets.kNumberBar)
        .withProperties(Map.of("min", 0.0, "max", 1.0, "show text", false))
        .withPosition(pos.x, pos.y).withSize(pos.width, pos.height);

    // Winch string location
    pos = m_defaultLayout.getWidgetPosition("Winch string location");
    Shuffleboard.getTab("Simulation")
        .addString("Winch string location", () -> m_winchState.getWindingOrientationName())
        .withWidget(BuiltInWidgets.kTextView).withPosition(pos.x, pos.y)
        .withSize(pos.width, pos.height);
  }

  private double getArmPercentRaised() {
    double lowerLimit = Constants.OperatorConstants.kWinchEncoderLowerLimit;
    double upperLimit = Constants.OperatorConstants.kWinchEncoderUpperLimit;
    double currentPosition = m_winchAbsoluteEncoder.getAbsolutePosition();

    return (currentPosition - lowerLimit) / (upperLimit - lowerLimit);
  }

  private void addShuffleboardWidgets() {
    addShuffleboardWinchList();
    addShuffleboardExtenderList();
    addShuffleboardArmList();

    // Add Robot Arm widget
    // $LATER Don't hardcode name of the widget and location
    Shuffleboard.getTab("Simulation")
        .add("Happy",
            new SendableArmPosition(() -> getArmPercentRaised(),
                () -> m_extenderSimulation.getExtendedPercent(),
                () -> m_ramenArmSimLogic.getGrabberOpen()))
        .withWidget(Constants.SimConstants.kAnimatedArmWidget).withPosition(7, 0).withSize(3, 3);
  }

  @Override
  public void initDashBoard() {
    super.initDashBoard();

    addShuffleboardWidgets();
    addCommandButtons();
  }
}
