package frc.robot.simulation.winch;

import frc.robot.simulation.winch.WinchSimModel.WindingOrientation;

/**
 * Describes the winch's cable.
 */
public class WinchCable {
  private final double m_totalLenMeters;
  private double m_unspooledLenMeters;
  private WindingOrientation m_windingOrientation;

  /**
   * Constructor.
   */
  public WinchCable(double totalLenMeters,
      double unspooledLenMeters,
      WindingOrientation windingOrientation) {

    if (totalLenMeters <= 0) {
      throw new IllegalArgumentException("totalLenMeters must be greater than 0");
    }
    if (unspooledLenMeters < 0 || unspooledLenMeters > totalLenMeters) {
      throw new IllegalArgumentException("unspooledLenMeters must be between 0 and totalLenMeters");
    }

    m_totalLenMeters = totalLenMeters;
    m_unspooledLenMeters = unspooledLenMeters;
    m_windingOrientation = windingOrientation;
  }

  /**
   * Copy constructor.
   */
  public WinchCable(WinchCable other) {
    m_totalLenMeters = other.m_totalLenMeters;
    m_unspooledLenMeters = other.m_unspooledLenMeters;
    m_windingOrientation = other.m_windingOrientation;
  }

  public double getUnspooledLenMeters() {
    return m_unspooledLenMeters;
  }

  /**
   * Sets the length of the cable that is unspooled.
   */
  public void setUnspooledLenMeters(double unspooledLenMeters) {
    if (unspooledLenMeters < 0 || unspooledLenMeters > m_totalLenMeters) {
      throw new IllegalArgumentException("unspooledLenMeters must be between 0 and totalLenMeters");
    }

    m_unspooledLenMeters = unspooledLenMeters;
  }

  public WindingOrientation getWindingOrientation() {
    return m_windingOrientation;
  }

  public void setWindingOrientation(WindingOrientation windingOrientation) {
    m_windingOrientation = windingOrientation;
  }

  public double calcSpooledLenMeters() {
    return m_totalLenMeters - m_unspooledLenMeters;
  }
}
