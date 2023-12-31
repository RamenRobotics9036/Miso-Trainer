package frc.robot.simulation.winch;

import frc.robot.simulation.framework.inputoutputs.Copyable;
import frc.robot.simulation.winch.WinchSimModel.WindingOrientation;

/**
 * Holds the outputed state of the winch.
 */
public class WinchState implements Copyable<WinchState> {
  private final double m_totalStringLenMeters;
  private double m_stringUnspooledLen;
  private WindingOrientation m_windingOrientation;
  private boolean m_isBroken;

  /**
   * Constructor.
   */
  public WinchState(double totalStringLenMeters) {
    if (totalStringLenMeters <= 0) {
      throw new IllegalArgumentException("totalStringLenMeters must be greater than 0");
    }

    m_totalStringLenMeters = totalStringLenMeters;
    m_stringUnspooledLen = 0;
    m_windingOrientation = WindingOrientation.BackOfRobot;
    m_isBroken = false;
  }

  // Implement getters and setters for all 4 fields
  public double getStringUnspooledLen() {
    return m_stringUnspooledLen;
  }

  public void setCableUnspooledLen(double stringUnspooledLen) {
    m_stringUnspooledLen = stringUnspooledLen;
  }

  public double getStringUnspooledPercent() {
    return getStringUnspooledLen() / m_totalStringLenMeters;
  }

  public WindingOrientation getWindingOrientation() {
    return m_windingOrientation;
  }

  public void setWindingOrientation(WindingOrientation windingOrientation) {
    m_windingOrientation = windingOrientation;
  }

  public String getWindingOrientationName() {
    return m_windingOrientation.name();
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
  public void copyFrom(WinchState other) {
    if (other == null) {
      throw new IllegalArgumentException("other cannot be null");
    }

    m_stringUnspooledLen = other.m_stringUnspooledLen;
    m_windingOrientation = other.m_windingOrientation;
    m_isBroken = other.m_isBroken;
  }
}
