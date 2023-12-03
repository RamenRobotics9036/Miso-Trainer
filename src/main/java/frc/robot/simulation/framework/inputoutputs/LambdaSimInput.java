package frc.robot.simulation.framework.inputoutputs;

import frc.robot.simulation.framework.SimInputInterface;
import java.util.function.Supplier;

/**
 * Helper class to implement input interface.
 */
public class LambdaSimInput<T> implements SimInputInterface<T> {
  private Supplier<T> m_valueSupplier;

  /**
   * Constructor.
   */
  public LambdaSimInput(Supplier<T> valueSupplier) {
    if (valueSupplier == null) {
      throw new IllegalArgumentException("valueSupplier cannot be null");
    }

    m_valueSupplier = valueSupplier;
  }

  @Override
  public T getInput() {
    return m_valueSupplier.get();
  }
}
