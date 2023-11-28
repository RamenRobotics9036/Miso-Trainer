package frc.robot.simulation.armangle;

/**
 * Holds the angle that arm is currently positioned.
 */
public class ArmAngleState {
  private double m_angleSignedDegrees;
  private boolean m_isBroken;

  /**
   * Constructor.
   */
  public ArmAngleState() {
    m_angleSignedDegrees = 0;
    m_isBroken = false;
  }

  // Implement getters and setters for fields
  public double getAngleSignedDegrees() {
    return m_angleSignedDegrees;
  }

  public void setAngleSignedDegrees(double angleSignedDegrees) {
    m_angleSignedDegrees = angleSignedDegrees;
  }

  public boolean getIsBroken() {
    return m_isBroken;
  }

  public void setIsBroken(boolean isBroken) {
    m_isBroken = isBroken;
  }

  /**
   * Copy to another instance of WinchState.
   */
  public void copyFrom(ArmAngleState other) {
    if (other == null) {
      throw new IllegalArgumentException("other cannot be null");
    }

    m_angleSignedDegrees = other.m_angleSignedDegrees;
    m_isBroken = other.m_isBroken;
  }
}
