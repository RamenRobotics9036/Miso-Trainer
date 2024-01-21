package frc.robot.simulation.drive;

import edu.wpi.first.math.geometry.Pose2d;
import frc.robot.simulation.framework.inputoutputs.CopyableInterface;

/**
 * Holds output state.
 */
public class DriveState implements CopyableInterface<DriveState> {
  private Pose2d m_pose;

  /**
   * Constructor.
   */
  public DriveState() {
    m_pose = new Pose2d();
  }

  private static Pose2d copyPose(Pose2d other) {
    return new Pose2d(other.getTranslation(), other.getRotation());
  }

  // Implement getters and setters for fields
  public Pose2d getPose() {
    return copyPose(m_pose);
  }

  public void setPose(Pose2d newPose) {
    m_pose = copyPose(newPose);
  }

  /**
   * Copy to another instance.
   */
  public void copyFrom(DriveState other) {
    if (other == null) {
      throw new IllegalArgumentException("other cannot be null");
    }

    this.setPose(other.m_pose);
  }
}
