package frc.robot.simulation.framework;

import frc.robot.shuffle.MultiType;

/**
 * Holds two values:
 * 1. The name of the Property, as a String
 * 2. Value (of type MultiType)
 */
public class DashboardItem {
  private final String m_propertyName;
  private final MultiType m_value;

  public DashboardItem(String propertyName, MultiType value) {
    m_propertyName = propertyName;
    m_value = value;
  }

  public String getPropertyName() {
    return m_propertyName;
  }

  public MultiType getValue() {
    return m_value;
  }
}
