package frc.robot.simulation.sample;

import frc.robot.simulation.framework.SimManagerBase;

/**
 * Simulation manager for a simple motor, AND an encoder that reads that motor position.
 */
public class SampleSimManager extends SimManagerBase<Integer, Integer> {
  private final SampleSimModel m_model;

  /**
   * Constructor.
   */
  public SampleSimManager(int ratio) {
    m_model = new SampleSimModel(ratio);
  }

  @Override
  protected Integer doSimulation(Integer numValue) {
    return m_model.updateTotal(numValue);
  }
}
