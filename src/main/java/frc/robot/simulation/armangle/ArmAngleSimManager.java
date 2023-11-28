package frc.robot.simulation.armangle;

import frc.robot.simulation.framework.SimManagerBase;
import java.util.function.Supplier;

/**
 * Simulation manager for a simple motor, AND an encoder that reads that motor position.
 */
public class ArmAngleSimManager extends SimManagerBase<Double, ArmAngleState> {
  private final ArmAngleSimModel m_model;

  /**
   * Constructor.
   */
  public ArmAngleSimManager(ArmAngleParams armAngleParams) {
    super();

    m_model = new ArmAngleSimModel(armAngleParams);
  }

  @Override
  protected Double doSimulation(Double stringLength) {
    return m_model.updateTotal(stringLength);
  }
}
