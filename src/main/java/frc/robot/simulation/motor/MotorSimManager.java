package frc.robot.simulation.motor;

import frc.robot.simulation.framework.SimManagerBase;

/**
 * Simulation manager for a simple motor, AND an encoder that reads that motor position.
 */
public class MotorSimManager extends SimManagerBase<Double, Double> {
  private final MotorSimModel m_model;

  /**
   * Constructor.
   */
  public MotorSimManager(boolean enableTestMode, double gearRatio) {
    super(enableTestMode);

    m_model = new MotorSimModel(gearRatio);
  }

  @Override
  protected Double doSimulation(Double motorPowerPercentage) {
    return m_model.updateMotorPosition(motorPowerPercentage);
  }
}
