package simulationlib.simulation.framework.inputoutputs;

import com.revrobotics.CANSparkMax;

import simulationlib.simulation.framework.SimInputInterface;

/**
 * Helper class to implement SimInputDoubleInterface.
 */
public class MotorSparkMaxSimInput implements SimInputInterface<Double> {
  private CANSparkMax m_motorReal;

  /**
   * Constructor.
   */
  public MotorSparkMaxSimInput(CANSparkMax motorReal) {
    if (motorReal == null) {
      throw new IllegalArgumentException("motorReal cannot be null");
    }

    m_motorReal = motorReal;
  }

  @Override
  public Double getInput() {
    return m_motorReal.get();
  }
}
