package simulationlib.shuffle;

import edu.wpi.first.util.sendable.Sendable;
import edu.wpi.first.util.sendable.SendableBuilder;
import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;
import simulationlib.Constants;

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
    builder.setSmartDashboardType(Constants.kAnimatedArmWidget);
    builder.addDoubleProperty("percentRaised", m_percentRaisedSupplier, null);
    builder.addDoubleProperty("percentExtended", m_percentExtendedSupplier, null);
    builder.addBooleanProperty("isClawOpen", m_clawOpenSupplier, null);
  }
}
