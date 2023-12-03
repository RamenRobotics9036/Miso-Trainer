package frc.robot.simulation.winch;

import frc.robot.helpers.RelativeEncoderSim;
import frc.robot.simulation.framework.SimInputInterface;

/**
 * Winch input is the number of rotations on winch motor encoder.
 */
public class WinchSimInput implements SimInputInterface<Double> {
  private final RelativeEncoderSim m_encoderRealWrapper;

  /**
   * Constructor.
   */
  public WinchSimInput(RelativeEncoderSim encoderRealWrapper) {
    if (encoderRealWrapper == null) {
      throw new IllegalArgumentException("encoderRealWrapper cannot be null");
    }

    m_encoderRealWrapper = encoderRealWrapper;
  }

  @Override
  public Double getInput() {
    return m_encoderRealWrapper.getPosition();
  }
}
