package frc.robot.simulation.sample;

import frc.robot.shuffle.MultiType;
import frc.robot.simulation.framework.DashboardSupplierItem;
import frc.robot.simulation.framework.SimModelInterface;

/**
 * Does the real-world simulation for the sample (which just accumulates integer values as a sum).
 */
public class SampleSimModel implements SimModelInterface<Integer, Integer> {
  private int m_accumulator;
  private final int m_ratio;
  private final MultiType m_dashAccumulator = MultiType.of(0);

  /**
   * Constructor.
   */
  public SampleSimModel(int ratio) {
    m_ratio = ratio;
    m_accumulator = 0;
  }

  // $TODO - Need a unit test to check that the sample dashboard value is actually properly updated
  // in lambda returned from getDashboardItems().
  /**
   * Returns parameters to display in Shuffleboard.
   */
  public DashboardSupplierItem[] getDashboardItems() {
    return new DashboardSupplierItem[] {
        new DashboardSupplierItem("Accumulator", () -> m_dashAccumulator)
    };
  }

  public boolean isModelBroken() {
    // Sample doesn't break in this simulation
    return false;
  }

  /**
   * Runs the simulation.
   */
  public Integer updateSimulation(Integer numValue) {
    m_accumulator += (numValue * m_ratio);

    // Set the dashboard value too
    m_dashAccumulator.setInteger(m_accumulator);

    return m_accumulator;
  }
}
