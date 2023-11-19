package frc.robot.simulation.sample;

import frc.robot.simulation.framework.SimOutputInterface;
import java.util.function.Consumer;

/**
 * Helper class to implement output interface.
 */
public class SampleSimOutput implements SimOutputInterface<Integer> {
  private Consumer<Integer> m_numConsumer;

  /**
   * Constructor.
   */
  public SampleSimOutput(Consumer<Integer> numConsumer) {
    if (numConsumer == null) {
      throw new IllegalArgumentException("numConsumer cannot be null");
    }

    m_numConsumer = numConsumer;
  }

  @Override
  public void setOutput(Integer numOutput) {
    m_numConsumer.accept(numOutput);
  }
}
