package frc.robot.simulation.framework.inputoutputs;

import frc.robot.simulation.framework.SimOutputInterface;
import java.util.function.Consumer;

/**
 * Helper class to implement output interface.
 */
public class LambdaSimOutput<T> implements SimOutputInterface<T> {
  private Consumer<T> m_valueConsumer;

  /**
   * Constructor.
   */
  public LambdaSimOutput(Consumer<T> valueConsumer) {
    if (valueConsumer == null) {
      throw new IllegalArgumentException("valueConsumer cannot be null");
    }

    m_valueConsumer = valueConsumer;
  }

  @Override
  public void setOutput(T valueOutput) {
    m_valueConsumer.accept(valueOutput);
  }
}
