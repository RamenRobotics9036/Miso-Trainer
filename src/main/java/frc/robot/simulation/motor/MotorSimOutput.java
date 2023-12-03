package frc.robot.simulation.motor;

import frc.robot.helpers.RelativeEncoderSim;
import frc.robot.simulation.framework.SimOutputInterface;

/**
 * Helper class to implement OutputDoubleInterface.
 */
public class MotorSimOutput implements SimOutputInterface<Double> {
  private final RelativeEncoderSim m_encoderRealWrapper;

  /**
   * Constructor. Note that the wrapper object (RelativeEncoderSim) is passed in,
   * not the real object. This is because the simulation never writes to the
   * real object (RelativeEncoder).
   */
  public MotorSimOutput(RelativeEncoderSim encoderRealWrapper) {
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
