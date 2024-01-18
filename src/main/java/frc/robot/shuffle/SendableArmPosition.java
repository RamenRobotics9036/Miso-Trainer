package frc.robot.shuffle;

import edu.wpi.first.util.sendable.Sendable;
import edu.wpi.first.util.sendable.SendableBuilder;
import frc.robot.Constants;
import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;

/**
 * Datatype used to describe Shuffleboard widget animation's arm position.
 */
public class SendableArmPosition implements Sendable {
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
