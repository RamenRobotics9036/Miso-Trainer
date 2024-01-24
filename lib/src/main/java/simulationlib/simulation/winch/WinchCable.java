package simulationlib.simulation.winch;

import simulationlib.simulation.winch.WinchSimModel.WindingOrientation;

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

  public double getTotalLenMeters() {
    return m_totalLenMeters;
  }

  public double getUnspooledLenMeters() {
    return m_unspooledLenMeters;
  }

  public WindingOrientation getWindingOrientation() {
    return m_windingOrientation;
  }

  public double calcSpooledLenMeters() {
    return m_totalLenMeters - m_unspooledLenMeters;
  }
}
