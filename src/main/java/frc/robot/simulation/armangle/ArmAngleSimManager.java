package frc.robot.simulation.armangle;

import frc.robot.simulation.framework.SimManagerBase;

/**
 * Simulation manager for a simple motor, AND an encoder that reads that motor position.
 */
public class ArmAngleSimManager extends SimManagerBase<Double, ArmAngleState> {
  private final ArmAngleSimModel m_model;

  /**
   * Constructor.
   */
  public ArmAngleSimManager(boolean enableTestMode, ArmAngleParams armAngleParams) {
    super(enableTestMode);

    m_model = new ArmAngleSimModel(armAngleParams);
  }

  @Override
  protected ArmAngleState doSimulation(Double stringLength) {
    m_model.updateArmAngle(stringLength);

    ArmAngleState result = new ArmAngleState();
    result.setAngleSignedDegrees(m_model.getAngleSignedDegrees());
    result.setIsBroken(m_model.getIsBroken());

    return result;
  }
}
