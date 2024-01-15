package frc.robot.simulation.winch;

import frc.robot.simulation.framework.inputoutputs.CopyableInterface;
import frc.robot.simulation.winch.WinchSimModel.WindingOrientation;

/**
 * Holds the outputed state of the winch.
 */
public class WinchState implements CopyableInterface<WinchState> {
  private double m_stringUnspooledLen;
  private double m_unspooledPercent;
  private WindingOrientation m_windingOrientation;
  private boolean m_isBroken;

  /**
   * Constructor.
   */
  public WinchState() {
    m_stringUnspooledLen = 0;
    m_unspooledPercent = 0;
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
    return m_unspooledPercent;
  }

  public void setStringUnspooledPercent(double unspooledPercent) {
    m_unspooledPercent = unspooledPercent;
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

  /**
   * Copy to another instance of WinchState.
   */
  public void copyFrom(WinchState other) {
    if (other == null) {
      throw new IllegalArgumentException("other cannot be null");
    }

    m_stringUnspooledLen = other.m_stringUnspooledLen;
    m_unspooledPercent = other.m_unspooledPercent;
    m_windingOrientation = other.m_windingOrientation;
    m_isBroken = other.m_isBroken;
  }
}
