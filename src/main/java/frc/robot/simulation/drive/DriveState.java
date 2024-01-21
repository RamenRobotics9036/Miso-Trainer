package frc.robot.simulation.drive;

import edu.wpi.first.math.geometry.Pose2d;
import frc.robot.simulation.framework.inputoutputs.CopyableInterface;

/**
 * Holds output state.
 */
public class DriveState implements CopyableInterface<DriveState> {
  private Pose2d m_pose;
  private double m_gyroHeadingDegrees;
  private double m_leftRelativeEncoderDistance;
  private double m_rightRelativeEncoderDistance;

  /**
   * Constructor.
   */
  public DriveState() {
    m_pose = new Pose2d();
    m_gyroHeadingDegrees = 0.0;
    m_leftRelativeEncoderDistance = 0.0;
    m_rightRelativeEncoderDistance = 0.0;
  }

  private static Pose2d copyPose(Pose2d other) {
    return new Pose2d(other.getTranslation(), other.getRotation());
  }

  // Implement getters and setters for fields
  public Pose2d getPose() {
    return m_pose;
  }

  public void setPose(Pose2d newPose) {
    // Make a copy when initially setting value
    m_pose = copyPose(newPose);
  }

  public double getGyroHeadingDegrees() {
    return m_gyroHeadingDegrees;
  }

  public void setGyroHeadingDegrees(double newGyroHeading) {
    m_gyroHeadingDegrees = newGyroHeading;
  }

  public double getLeftRelativeEncoderDistance() {
    return m_leftRelativeEncoderDistance;
  }

  public void setLeftRelativeEncoderDistance(double newLeftRelativeEncoderDistance) {
    m_leftRelativeEncoderDistance = newLeftRelativeEncoderDistance;
  }

  public double getRightRelativeEncoderDistance() {
    return m_rightRelativeEncoderDistance;
  }

  public void setRightRelativeEncoderDistance(double newRightRelativeEncoderDistance) {
    m_rightRelativeEncoderDistance = newRightRelativeEncoderDistance;
  }

  /**
   * Copy to another instance.
   */
  public void copyFrom(DriveState other) {
    if (other == null) {
      throw new IllegalArgumentException("other cannot be null");
    }

    this.setPose(other.getPose());
    this.m_gyroHeadingDegrees = other.m_gyroHeadingDegrees;
    this.m_leftRelativeEncoderDistance = other.m_leftRelativeEncoderDistance;
    this.m_rightRelativeEncoderDistance = other.m_rightRelativeEncoderDistance;
  }
}
