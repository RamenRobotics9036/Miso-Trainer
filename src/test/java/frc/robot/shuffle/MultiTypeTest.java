package frc.robot.shuffle;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import org.junit.jupiter.api.Test;

/**
 * Tests the MultiType class.
 */
public class MultiTypeTest {

  @Test
  public void testCreateBooleanType() {
    MultiType booleanType = MultiType.of(true);
    assertEquals("Boolean", booleanType.getType());
    assertEquals(Optional.of(true), booleanType.getBoolean());
  }

  @Test
  public void testCreateDoubleType() {
    MultiType doubleType = MultiType.of(10.5);
    assertEquals("Double", doubleType.getType());
    assertEquals(Optional.of(10.5), doubleType.getDouble());
  }

  @Test
  public void testCreateIntegerType() {
    MultiType integerType = MultiType.of(10);
    assertEquals("Integer", integerType.getType());
    assertEquals(Optional.of(10), integerType.getInteger());
  }

  @Test
  public void testCreateStringType() {
    MultiType stringType = MultiType.of("Hello");
    assertEquals("String", stringType.getType());
    assertEquals(Optional.of("Hello"), stringType.getString());
  }

  @Test
  public void testNullBooleanCreation() {
    assertThrows(IllegalArgumentException.class, () -> MultiType.of((Boolean) null));
  }

  @Test
  public void testNullDoubleCreation() {
    assertThrows(IllegalArgumentException.class, () -> MultiType.of((Double) null));
  }

  @Test
  public void testNullIntegerCreation() {
    assertThrows(IllegalArgumentException.class, () -> MultiType.of((Integer) null));
  }

  @Test
  public void testNullStringCreation() {
    assertThrows(IllegalArgumentException.class, () -> MultiType.of((String) null));
  }

  @Test
  public void testGetWrongTypeBoolean() {
    MultiType stringType = MultiType.of("Test");
    assertEquals(Optional.empty(), stringType.getBoolean());
  }

  @Test
  public void testGetWrongTypeDouble() {
    MultiType booleanType = MultiType.of(false);
    assertEquals(Optional.empty(), booleanType.getDouble());
  }

  @Test
  public void testGetWrongTypeInteger() {
    MultiType stringType = MultiType.of("Test");
    assertEquals(Optional.empty(), stringType.getInteger());
  }

  @Test
  public void testGetWrongTypeString() {
    MultiType doubleType = MultiType.of(123.45);
    assertEquals(Optional.empty(), doubleType.getString());
  }

  @Test
  void testSetBoolean() {
    MultiType booleanMultiType = MultiType.of(true);

    // Test setting a new boolean value
    booleanMultiType.setBoolean(false);
    assertTrue(false == booleanMultiType.getBoolean().orElseThrow());

    // Test setting null (should throw IllegalArgumentException)
    assertThrows(IllegalArgumentException.class, () -> booleanMultiType.setBoolean(null));

    // Test setting boolean on a double type (should throw IllegalStateException)
    MultiType doubleMultiType = MultiType.of(1.0);
    assertThrows(IllegalStateException.class, () -> doubleMultiType.setBoolean(true));
  }

  @Test
  void testSetDouble() {
    MultiType doubleMultiType = MultiType.of(1.0);

    // Test setting a new double value
    doubleMultiType.setDouble(2.0);
    assertEquals(2.0, doubleMultiType.getDouble().orElseThrow());

    // Test setting null (should throw IllegalArgumentException)
    assertThrows(IllegalArgumentException.class, () -> doubleMultiType.setDouble(null));

    // Test setting double on a string type (should throw IllegalStateException)
    MultiType stringMultiType = MultiType.of("test");
    assertThrows(IllegalStateException.class, () -> stringMultiType.setDouble(3.0));
  }

  @Test
  void testSetInteger() {
    MultiType integerMultiType = MultiType.of(1);

    // Test setting a new integer value
    integerMultiType.setInteger(2);
    assertEquals(2, integerMultiType.getInteger().orElseThrow());

    // Test setting null (should throw IllegalArgumentException)
    assertThrows(IllegalArgumentException.class, () -> integerMultiType.setInteger(null));

    // Test setting integer on a string type (should throw IllegalStateException)
    MultiType stringMultiType = MultiType.of("test");
    assertThrows(IllegalStateException.class, () -> stringMultiType.setInteger(3));
  }

  @Test
  void testSetString() {
    MultiType stringMultiType = MultiType.of("test");

    // Test setting a new string value
    stringMultiType.setString("new test");
    assertEquals("new test", stringMultiType.getString().orElseThrow());

    // Test setting null (should throw IllegalArgumentException)
    assertThrows(IllegalArgumentException.class, () -> stringMultiType.setString(null));

    // Test setting string on a boolean type (should throw IllegalStateException)
    MultiType booleanMultiType = MultiType.of(true);
    assertThrows(IllegalStateException.class, () -> booleanMultiType.setString("false"));
  }
}
