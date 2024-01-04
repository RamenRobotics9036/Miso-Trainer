package frc.robot.simulation.framework;

import frc.robot.shuffle.MultiType;
import java.util.function.Supplier;

/**
 * Class that holds two values:
 * 1. The name of the Property, as a String
 * 2. A supplier for a MultiType object that holds the value of the Property
 */
public class DashboardSupplierItem {
  private final String m_propertyName;
  private final Supplier<MultiType> m_supplier;

  public DashboardSupplierItem(String propertyName, Supplier<MultiType> supplier) {
    m_propertyName = propertyName;
    m_supplier = supplier;
  }

  public String getPropertyName() {
    return m_propertyName;
  }

  public Supplier<MultiType> getSupplier() {
    return m_supplier;
  }
}
