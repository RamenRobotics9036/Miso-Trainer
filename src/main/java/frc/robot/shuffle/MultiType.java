package frc.robot.shuffle;

import edu.wpi.first.math.geometry.Pose2d;
import java.util.Optional;

/**
 * A class that can store a value of one of three types: Boolean, Double, or
 * String. Thanks to ChatGPT!
 */
public class MultiType {
  private Boolean m_booleanValue;
  private Double m_doubleValue;
  private Integer m_integerValue;
  private String m_stringValue;
  private Pose2d m_poseValue;

  // Private constructor
  private MultiType(Boolean booleanValue,
      Double doubleValue,
      Integer integerValue,
      String stringValue,
      Pose2d poseValue) {
    this.m_booleanValue = booleanValue;
    this.m_doubleValue = doubleValue;
    this.m_integerValue = integerValue;
    this.m_stringValue = stringValue;
    this.m_poseValue = poseValue;
  }

  /**
   * Factory for Boolean.
   */
  public static MultiType of(Boolean value) {
    if (value == null) {
      throw new IllegalArgumentException("Null value not allowed for Boolean type");
    }

    return new MultiType(value, null, null, null, null);
  }

  /**
   * Factory for Double.
   */
  public static MultiType of(Double value) {
    if (value == null) {
      throw new IllegalArgumentException("Null value not allowed for Double type");
    }

    return new MultiType(null, value, null, null, null);
  }

  /**
   * Factory for Integer.
   */
  public static MultiType of(Integer value) {
    if (value == null) {
      throw new IllegalArgumentException("Null value not allowed for Integer type");
    }

    return new MultiType(null, null, value, null, null);
  }

  /**
   * Factory for String.
   */
  public static MultiType of(String value) {
    if (value == null) {
      throw new IllegalArgumentException("Null value not allowed for String type");
    }

    return new MultiType(null, null, null, value, null);
  }

  /**
   * Factory for Pose2d.
   */
  public static MultiType of(Pose2d value) {
    if (value == null) {
      throw new IllegalArgumentException("Null value not allowed for Pose2d type");
    }

    return new MultiType(null, null, null, null, value);
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
   * Change value of Integer.
   */
  public void setInteger(Integer value) {
    if (m_integerValue == null) {
      throw new IllegalStateException("Cannot change type of value");
    }

    if (value == null) {
      throw new IllegalArgumentException("Null value not allowed for Integer type");
    }

    m_integerValue = value;
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

  /**
   * Change value of Pose2d.
   */
  public void setPose2d(Pose2d value) {
    if (m_poseValue == null) {
      throw new IllegalStateException("Cannot change type of value");
    }

    if (value == null) {
      throw new IllegalArgumentException("Null value not allowed for Pose2d type");
    }

    m_poseValue = value;
  }

  // Methods to safely retrieve the value
  public Optional<Boolean> getBoolean() {
    return Optional.ofNullable(m_booleanValue);
  }

  public Optional<Double> getDouble() {
    return Optional.ofNullable(m_doubleValue);
  }

  public Optional<Integer> getInteger() {
    return Optional.ofNullable(m_integerValue);
  }

  public Optional<String> getString() {
    return Optional.ofNullable(m_stringValue);
  }

  public Optional<Pose2d> getPose2d() {
    return Optional.ofNullable(m_poseValue);
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
    else if (m_integerValue != null) {
      return "Integer";
    }
    else if (m_stringValue != null) {
      return "String";
    }
    else if (m_poseValue != null) {
      return "Pose2d";
    }
    else {
      return "None";
    }
  }

  /**
   * CopyTo method.
   */
  public void copyTo(MultiType other) {
    if (other == null) {
      throw new IllegalArgumentException("Null value not allowed for copyTo target");
    }

    if (m_booleanValue != null) {
      other.setBoolean(m_booleanValue);
    }

    if (m_doubleValue != null) {
      other.setDouble(m_doubleValue);
    }

    if (m_integerValue != null) {
      other.setInteger(m_integerValue);
    }

    if (m_stringValue != null) {
      other.setString(m_stringValue);
    }

    if (m_poseValue != null) {
      other.setPose2d(m_poseValue);
    }
  }
}
