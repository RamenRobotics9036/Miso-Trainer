package frc.robot.simulation.framework.inputoutputs;

import frc.robot.helpers.RelativeEncoderSim;
import frc.robot.simulation.framework.SimInputInterface;

/**
 * Input is the number of rotations on relative motor encoder.
 */
public class RelEncoderSimInput implements SimInputInterface<Double> {
  private final RelativeEncoderSim m_encoderRealWrapper;

  /**
   * Constructor.
   */
  public RelEncoderSimInput(RelativeEncoderSim encoderRealWrapper) {
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
