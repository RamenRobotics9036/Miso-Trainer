package frc.robot.simulation.sample;

import frc.robot.simulation.framework.SimModelInterface;
import simulationlib.shuffle.MultiType;

/**
 * Does the real-world simulation for the sample (which just accumulates integer values as a sum).
 */
public class SampleSimModel implements SimModelInterface<Integer, Integer> {
  private int m_accumulator;
  private final int m_ratio;
  private final MultiType m_dashAccumulator = MultiType.of(0);

  /**
   * Constructor.
   */
  public SampleSimModel(int ratio) {
    m_ratio = ratio;
    m_accumulator = 0;
  }

  public boolean isModelBroken() {
    // Sample doesn't break in this simulation
    return false;
  }

  /**
   * Runs the simulation.
   */
  public Integer updateSimulation(Integer numValue) {
    m_accumulator += (numValue * m_ratio);

    // Set the dashboard value too
    m_dashAccumulator.setInteger(m_accumulator);

    return m_accumulator;
  }
}
