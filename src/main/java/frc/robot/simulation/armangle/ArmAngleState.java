package frc.robot.simulation.armangle;

import frc.robot.simulation.framework.inputoutputs.CopyableInterface;

// $LATER - Note that we're now only returning a single DOUBLE, so no point in all this complexity
/**
 * Holds the angle that arm is currently positioned.
 */
public class ArmAngleState implements CopyableInterface<ArmAngleState> {
  private double m_angleSignedDegrees;

  /**
   * Constructor.
   */
  public ArmAngleState() {
    m_angleSignedDegrees = 0;
  }

  // Implement getters and setters for fields
  public double getAngleSignedDegrees() {
    return m_angleSignedDegrees;
  }

  public void setAngleSignedDegrees(double angleSignedDegrees) {
    m_angleSignedDegrees = angleSignedDegrees;
  }

  /**
   * Copy to another instance of ArmAngleState.
   */
  public void copyFrom(ArmAngleState other) {
    if (other == null) {
      throw new IllegalArgumentException("other cannot be null");
    }

    m_angleSignedDegrees = other.m_angleSignedDegrees;
  }
}
