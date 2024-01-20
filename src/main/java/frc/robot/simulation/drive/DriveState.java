package frc.robot.simulation.drive;

import frc.robot.simulation.framework.inputoutputs.CopyableInterface;

/**
 * Holds output state of DriveSimModel.
 */
public class DriveState implements CopyableInterface<DriveState> {
  private double m_leftEncoderDistance;
  private double m_leftEncoderRate;
  private double m_rightEncoderDistance;
  private double m_rightEncoderRate;
  private double m_robotHeadingDegrees;

  /**
   * Constructor.
   */
  public DriveState() {
    m_leftEncoderDistance = 0;
    m_leftEncoderRate = 0;
    m_rightEncoderDistance = 0;
    m_rightEncoderRate = 0;
    m_robotHeadingDegrees = 0;
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

  public double getRightEncoderDistance() {
    return m_rightEncoderDistance;
  }

  public void setRightEncoderDistance(double encoderDistance) {
    m_rightEncoderDistance = encoderDistance;
  }

  public double getRightEncoderRate() {
    return m_rightEncoderRate;
  }

  public void setRightEncoderRate(double encoderRate) {
    m_rightEncoderRate = encoderRate;
  }

  public double getRobotHeadingDegrees() {
    return m_robotHeadingDegrees;
  }

  public void setRobotHeadingDegrees(double robotHeadingDegrees) {
    m_robotHeadingDegrees = robotHeadingDegrees;
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
    m_rightEncoderDistance = other.m_rightEncoderDistance;
    m_rightEncoderRate = other.m_rightEncoderRate;
    m_robotHeadingDegrees = other.m_robotHeadingDegrees;
  }
}
