package frc.robot.simulation.armangle;

import frc.robot.simulation.framework.SimInputInterface;
import java.util.function.Supplier;

/**
 * Helper class to implement input interface.
 */
public class ArmAngleSimInput implements SimInputInterface<Double> {
  private Supplier<Double> m_stringLengthSupplier;

  /**
   * Constructor.
   */
  public ArmAngleSimInput(Supplier<Double> stringLengthSupplier) {
    if (stringLengthSupplier == null) {
      throw new IllegalArgumentException("stringLengthSupplier cannot be null");
    }

    m_stringLengthSupplier = stringLengthSupplier;
  }

  @Override
  public Double getInput() {
    return m_stringLengthSupplier.get();
  }
}
