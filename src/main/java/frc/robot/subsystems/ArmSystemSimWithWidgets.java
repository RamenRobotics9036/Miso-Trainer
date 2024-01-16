package frc.robot.subsystems;

import edu.wpi.first.util.sendable.Sendable;
import edu.wpi.first.util.sendable.SendableBuilder;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import frc.robot.Constants;
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
  }

  private double getArmPercentRaised() {
    double lowerLimit = Constants.OperatorConstants.kWinchEncoderLowerLimit;
    double upperLimit = Constants.OperatorConstants.kWinchEncoderUpperLimit;
    double currentPosition = m_winchAbsoluteEncoder.getAbsolutePosition();

    return (currentPosition - lowerLimit) / (upperLimit - lowerLimit);
  }

  private void addShuffleboardWidgets() {
    addShuffleboardArmList();

    // Add Robot Arm widget
    // $LATER Don't hardcode name of the widget and location
    Shuffleboard.getTab("Simulation").add("Happy",
        new SendableArmPosition(() -> getArmPercentRaised(),
            () -> m_extenderState.getExtendedPercent(), () -> m_ramenArmSimLogic.getGrabberOpen()))
        .withWidget(Constants.SimConstants.kAnimatedArmWidget).withPosition(7, 0).withSize(3, 3);
  }

  @Override
  public void initDashBoard() {
    super.initDashBoard();

    addShuffleboardWidgets();
  }
}
