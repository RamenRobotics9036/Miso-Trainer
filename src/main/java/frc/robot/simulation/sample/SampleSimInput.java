package frc.robot.simulation.sample;

import frc.robot.simulation.framework.SimInputInterface;
import java.util.function.Supplier;

/**
 * Helper class to implement input interface.
 */
public class SampleSimInput implements SimInputInterface<Integer> {
  private Supplier<Integer> m_numSupplier;

  /**
   * Constructor.
   */
  public SampleSimInput(Supplier<Integer> numSupplier) {
    if (numSupplier == null) {
      throw new IllegalArgumentException("numSupplier cannot be null");
    }

    m_numSupplier = numSupplier;
  }

  @Override
  public Integer getInput() {
    return m_numSupplier.get();
  }
}
