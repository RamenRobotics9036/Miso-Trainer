package frc.robot.shuffle;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
  public void testGetWrongTypeString() {
    MultiType doubleType = MultiType.of(123.45);
    assertEquals(Optional.empty(), doubleType.getString());
  }
}
