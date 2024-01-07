package frc.robot.simulation.extender;

import frc.robot.shuffle.MultiType;
import frc.robot.simulation.framework.DashboardSupplierItem;
import frc.robot.simulation.framework.SimModelInterface;

/**
 * Does the real-world simulation for the extender.
 */
public class ExtenderSimModel implements SimModelInterface<Integer, Integer> {
  private int m_accumulator;
  private final int m_ratio;
  private final MultiType m_dashAccumulator = MultiType.of(0);

  /**
   * Constructor.
   */
  public ExtenderSimModel(int ratio) {
    m_ratio = ratio;
    m_accumulator = 0;
  }

  /**
   * Returns properties to display in Shuffleboard.
   */
  public DashboardSupplierItem[] getDashboardSupplierItems() {
    return new DashboardSupplierItem[] {
        new DashboardSupplierItem("Accumulator", () -> m_dashAccumulator)
    };
  }

  public boolean isModelBroken() {
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
