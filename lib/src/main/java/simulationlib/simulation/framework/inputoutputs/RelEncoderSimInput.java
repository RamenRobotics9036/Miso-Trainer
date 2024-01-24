package simulationlib.simulation.framework.inputoutputs;

import simulationlib.simulation.framework.SimInputInterface;
import simulationlib.simulation.framework.customwrappers.RelativeEncoderSim;

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
