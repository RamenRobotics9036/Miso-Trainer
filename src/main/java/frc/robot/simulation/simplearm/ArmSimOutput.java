package frc.robot.simulation.simplearm;

import edu.wpi.first.wpilibj.simulation.DutyCycleEncoderSim;
import frc.robot.helpers.DutyCycleEncoderSim2;
import frc.robot.simulation.framework.SimOutputInterface;

/**
 * Helper class to implement OutputDoubleInterface.
 */
public class ArmSimOutput implements SimOutputInterface<Double> {
  private final DutyCycleEncoderSim m_absoluteEncoderRealWrapper;

  /**
   * Constructor. Note that the wrapper object (RelativeEncoderSim) is passed in,
   * not the real object. This is because the simulation never writes to the
   * real object (RelativeEncoder).
   */
  public ArmSimOutput(DutyCycleEncoderSim absoluteEncoderRealWrapper) {
    if (absoluteEncoderRealWrapper == null) {
      throw new IllegalArgumentException("absoluteEncoderRealWrapper cannot be null");
    }

    m_absoluteEncoderRealWrapper = absoluteEncoderRealWrapper;
  }

  @Override
  public void setOutput(Double outputRotations) {
    // Sets the absolute encoder position in rotations.
    m_absoluteEncoderRealWrapper.set(outputRotations);
  }
}
