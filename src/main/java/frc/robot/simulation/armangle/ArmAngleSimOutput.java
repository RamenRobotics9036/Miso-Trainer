package frc.robot.simulation.armangle;

import frc.robot.simulation.framework.SimOutputInterface;
import java.util.function.Consumer;

/**
 * Helper class to implement output interface.
 */
public class ArmAngleSimOutput implements SimOutputInterface<Double> {
  private Consumer<Double> m_armAngleConsumer;

  /**
   * Constructor.
   */
  public ArmAngleSimOutput(Consumer<Double> armAngleConsumer) {
    if (armAngleConsumer == null) {
      throw new IllegalArgumentException("armAngleConsumer cannot be null");
    }

    m_armAngleConsumer = armAngleConsumer;
  }

  @Override
  public void setOutput(Double numOutput) {
    m_armAngleConsumer.accept(numOutput);
  }
}
