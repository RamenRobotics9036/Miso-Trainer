package frc.robot.subsystems;

import edu.wpi.first.util.sendable.Sendable;
import edu.wpi.first.util.sendable.SendableBuilder;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import frc.robot.Constants;
import frc.robot.shuffle.MultiType;
import frc.robot.shuffle.PrefixedConcurrentMap;
import frc.robot.shuffle.SupplierMapFactory;
import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;
import java.util.function.Supplier;

/**
 * This adds shuffleboard widgets to the ArmSystemSim class.
 */
// $TODO - Break this into a separate class in shuffle directory
public class ArmSystemSimWithWidgets extends ArmSystemSim {
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

  private double getArmPercentRaised() {
    double lowerLimit = Constants.OperatorConstants.kWinchEncoderLowerLimit;
    double upperLimit = Constants.OperatorConstants.kWinchEncoderUpperLimit;
    double currentPosition = m_winchAbsoluteEncoder.getAbsolutePosition();

    return (currentPosition - lowerLimit) / (upperLimit - lowerLimit);
  }

  private void addShuffleboardWidgets() {
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
