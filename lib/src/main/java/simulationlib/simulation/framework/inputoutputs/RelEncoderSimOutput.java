package simulationlib.simulation.framework.inputoutputs;

import simulationlib.simulation.framework.SimOutputInterface;
import simulationlib.simulation.framework.customwrappers.RelativeEncoderSim;

/**
 * Helper class to implement output to relative encoder.
 */
public class RelEncoderSimOutput implements SimOutputInterface<Double> {
  private final RelativeEncoderSim m_encoderRealWrapper;

  /**
   * Constructor. Note that the wrapper object (RelativeEncoderSim) is passed in,
   * not the real object. This is because the simulation never writes to the
   * real object (RelativeEncoder).
   */
  public RelEncoderSimOutput(RelativeEncoderSim encoderRealWrapper) {
    if (encoderRealWrapper == null) {
      throw new IllegalArgumentException("encoderRealWrapper cannot be null");
    }

    m_encoderRealWrapper = encoderRealWrapper;
  }

  @Override
  public void setOutput(Double outputRotations) {
    // Sets the encoder position in rotations.
    m_encoderRealWrapper.setPosition(outputRotations);
  }
}
