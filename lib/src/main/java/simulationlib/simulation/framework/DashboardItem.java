package simulationlib.simulation.framework;

import simulationlib.shuffle.MultiType;

/**
 * Holds two values:
 * 1. The name of the Property, as a String
 * 2. Value (of type MultiType)
 */
public class DashboardItem {
  private final String m_propertyName;
  private final MultiType m_value;

  /**
   * Constructor.
   */
  public DashboardItem(String propertyName, MultiType value) {
    if (propertyName == null) {
      throw new IllegalArgumentException("DashboardItem propertyName cannot be null");
    }

    if (value == null) {
      throw new IllegalArgumentException("DashboardItem value cannot be null");
    }

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
