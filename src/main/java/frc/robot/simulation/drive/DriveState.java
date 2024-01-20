package frc.robot.simulation.drive;

import frc.robot.simulation.framework.inputoutputs.CopyableInterface;

/**
 * Holds output state of DriveSimModel.
 */
public class DriveState implements CopyableInterface<DriveState> {
  private double m_leftEncoderDistance;
  private double m_leftEncoderRate;

  /**
   * Constructor.
   */
  public DriveState() {
    m_leftEncoderDistance = 0;
    m_leftEncoderRate = 0;
  }

  // Implement getters and setters for fields
  public double getLeftEncoderDistance() {
    return m_leftEncoderDistance;
  }

  public void setLeftEncoderDistance(double encoderDistance) {
    m_leftEncoderDistance = encoderDistance;
  }

  public double getLeftEncoderRate() {
    return m_leftEncoderRate;
  }

  public void setLeftEncoderRate(double encoderRate) {
    m_leftEncoderRate = encoderRate;
  }

  /**
   * Copy to another instance of ArmAngleState.
   */
  public void copyFrom(DriveState other) {
    if (other == null) {
      throw new IllegalArgumentException("other cannot be null");
    }

    m_leftEncoderDistance = other.m_leftEncoderDistance;
    m_leftEncoderRate = other.m_leftEncoderRate;
  }
}
