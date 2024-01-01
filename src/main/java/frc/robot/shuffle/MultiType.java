package frc.robot.shuffle;

import java.util.Optional;

/**
 * A class that can store a value of one of three types: Boolean, Double, or
 * String. Thanks to ChatGPT!
 */
public class MultiType {
  private Boolean m_booleanValue;
  private Double m_doubleValue;
  private String m_stringValue;

  // Private constructor
  private MultiType(Boolean booleanValue, Double doubleValue, String stringValue) {
    this.m_booleanValue = booleanValue;
    this.m_doubleValue = doubleValue;
    this.m_stringValue = stringValue;
  }

  /**
   * Factory for Boolean.
   */
  public static MultiType of(Boolean value) {
    if (value == null) {
      throw new IllegalArgumentException("Null value not allowed for Boolean type");
    }

    return new MultiType(value, null, null);
  }

  /**
   * Factory for Double.
   */
  public static MultiType of(Double value) {
    if (value == null) {
      throw new IllegalArgumentException("Null value not allowed for Double type");
    }

    return new MultiType(null, value, null);
  }

  /**
   * Factory for String.
   */
  public static MultiType of(String value) {
    if (value == null) {
      throw new IllegalArgumentException("Null value not allowed for String type");
    }

    return new MultiType(null, null, value);
  }

  /**
   * Change value of Boolean.
   */
  public void setBoolean(Boolean value) {
    if (m_booleanValue == null) {
      throw new IllegalStateException("Cannot change type of value");
    }

    if (value == null) {
      throw new IllegalArgumentException("Null value not allowed for Boolean type");
    }

    m_booleanValue = value;
  }

  /**
   * Change value of Double.
   */
  public void setDouble(Double value) {
    if (m_doubleValue == null) {
      throw new IllegalStateException("Cannot change type of value");
    }

    if (value == null) {
      throw new IllegalArgumentException("Null value not allowed for Double type");
    }

    m_doubleValue = value;
  }

  /**
   * Change value of String.
   */
  public void setString(String value) {
    if (m_stringValue == null) {
      throw new IllegalStateException("Cannot change type of value");
    }

    if (value == null) {
      throw new IllegalArgumentException("Null value not allowed for String type");
    }

    m_stringValue = value;
  }

  // Methods to safely retrieve the value
  public Optional<Boolean> getBoolean() {
    return Optional.ofNullable(m_booleanValue);
  }

  public Optional<Double> getDouble() {
    return Optional.ofNullable(m_doubleValue);
  }

  public Optional<String> getString() {
    return Optional.ofNullable(m_stringValue);
  }

  /**
   * Method to determine the type of the value.
   */
  public String getType() {
    if (m_booleanValue != null) {
      return "Boolean";
    }
    else if (m_doubleValue != null) {
      return "Double";
    }
    else if (m_stringValue != null) {
      return "String";
    }
    else {
      return "None";
    }
  }
}
