package frc.robot.simulation.sample;

import frc.robot.simulation.framework.SimManagerBase;
import java.util.function.Supplier;

/**
 * Simulation manager for a simple motor, AND an encoder that reads that motor position.
 */
public class SampleSimManager extends SimManagerBase<Integer, Integer> {
  private final SampleSimModel m_model;

  /**
   * Constructor requires the user to specify a custom function to
   * determine if the robot is enabled. This is so that unit-testing
   * for this SAMPLE can be done with and without the robot enabled
   * in simulation.
   */
  public SampleSimManager(int ratio, Supplier<Boolean> isRobotEnabledFunc) {
    super(isRobotEnabledFunc);
    m_model = new SampleSimModel(ratio);
  }

  @Override
  protected Integer doSimulation(Integer numValue) {
    return m_model.updateSimulation(numValue);
  }
}
