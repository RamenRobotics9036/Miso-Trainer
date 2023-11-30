package frc.robot.simulation.winch;

import frc.robot.simulation.framework.SimManagerBase;
import frc.robot.simulation.winch.WinchSimModel.WindingOrientation;

/**
 * Simulates a winch. The winch depends on a motor (not part of this particular simulation),
 * and outputs the length of the string.
 */
public class WinchSimManager extends SimManagerBase<Double, WinchState> {
  private final WinchSimModel m_model;

  /**
   * Constructor.
   */
  public WinchSimManager(boolean enableTestMode,
      double spoolDiameterMeters,
      double totalStringLenMeters,
      double initialLenSpooled,
      WindingOrientation initialWindingOrientation,
      boolean invertMotor) {

    super(enableTestMode);

    m_model = new WinchSimModel(spoolDiameterMeters, totalStringLenMeters, initialLenSpooled,
        initialWindingOrientation, invertMotor);
  }

  @Override
  protected WinchState doSimulation(Double winchMotorEncoderRotations) {
    m_model.updateNewLenSpooled(winchMotorEncoderRotations);

    WinchState result = new WinchState(m_model.getTotalStringLenMeters());
    result.setStringUnspooledLen(m_model.getStringUnspooledLen());
    result.setWindingOrientation(m_model.getWindingOrientation());
    result.setIsBroken(m_model.getIsBroken());

    return result;
  }
}
