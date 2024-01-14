package frc.robot.simulation.extender;

import frc.robot.simulation.framework.inputoutputs.CopyableInterface;

/**
 * Holds output state of ExtenderSimModel.
 */
public class ExtenderState implements CopyableInterface<ExtenderState> {
  private double m_extendedLen;
  private double m_extendedPercent;

  /**
   * Constructor.
   */
  public ExtenderState() {
    m_extendedLen = 0;
    m_extendedPercent = 0;
  }

  // Implement getters and setters for fields
  public double getExtendedLen() {
    return m_extendedLen;
  }

  public void setExtendedLen(double extendedLen) {
    m_extendedLen = extendedLen;
  }

  public double getExtendedPercent() {
    return m_extendedPercent;
  }

  public void setExtendedPercent(double extendedPercent) {
    m_extendedPercent = extendedPercent;
  }

  /**
   * Copy to another instance of ArmAngleState.
   */
  public void copyFrom(ExtenderState other) {
    if (other == null) {
      throw new IllegalArgumentException("other cannot be null");
    }

    m_extendedLen = other.m_extendedLen;
    m_extendedPercent = other.m_extendedPercent;
  }
}
