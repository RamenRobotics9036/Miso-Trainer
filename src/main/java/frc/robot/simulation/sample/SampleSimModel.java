package frc.robot.simulation.sample;

/**
 * Does the real-world simulation for the sample (which just accumulates integer values as a sum).
 */
public class SampleSimModel {
  private int m_accumulator;
  private final int m_ratio;

  /**
   * Constructor.
   */
  public SampleSimModel(int ratio) {
    m_ratio = ratio;
    m_accumulator = 0;
  }

  public int updateTotal(int numValue) {
    m_accumulator += (numValue * m_ratio);
    return m_accumulator;
  }
}
