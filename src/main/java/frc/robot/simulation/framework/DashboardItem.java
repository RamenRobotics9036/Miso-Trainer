package frc.robot.simulation.framework;

import frc.robot.shuffle.MultiType;
import java.util.function.Supplier;

/**
 * Class that holds two values:
 * 1. The name of the Parameter, as a String
 * 2. A supplier for a MultiType object that holds the value of the Parameter
 */
public class DashboardItem {
  private final String m_paramName;
  private final Supplier<MultiType> m_supplier;

  public DashboardItem(String paramName, Supplier<MultiType> supplier) {
    m_paramName = paramName;
    m_supplier = supplier;
  }

  public String getParamName() {
    return m_paramName;
  }

  public Supplier<MultiType> getSupplier() {
    return m_supplier;
  }
}
