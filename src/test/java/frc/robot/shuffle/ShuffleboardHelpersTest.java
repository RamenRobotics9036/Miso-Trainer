package frc.robot.shuffle;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import frc.robot.shuffle.PrefixedConcurrentMap.Client;

import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;
import java.util.function.Supplier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for ShuffleboardHelpers.
 */
public class ShuffleboardHelpersTest {
  PrefixedConcurrentMap<Supplier<MultiType>> m_globalMap = SupplierMapFactory.getGlobalInstance();
  private ShuffleboardHelpers m_helpers;

  /**
   * Constructor.
   */
  public ShuffleboardHelpersTest() {
    m_helpers = new ShuffleboardHelpers(m_globalMap);
  }

  @BeforeEach
  public void setUp() {
    // Reset the global cache of dashboard items before each test
    m_globalMap.clear();
  }

  @Test
  public void getDoubleSupplierShouldSucceed() {
    Client<Supplier<MultiType>> shuffleClient = m_globalMap.getClientWithPrefix("Test");
    Double value = 2.0;
    shuffleClient.addItem("DoubleKey", () -> MultiType.of(value));

    DoubleSupplier result = m_helpers.getDoubleSupplier("Test/DoubleKey");
    assertNotNull(result);
    assertEquals(2.0, result.getAsDouble());
  }

  @Test
  public void getBooleanDoubleSupplierShouldSucceed() {
    Client<Supplier<MultiType>> shuffleClient = m_globalMap.getClientWithPrefix("Test");
    Boolean value = true;
    shuffleClient.addItem("BooleanKey", () -> MultiType.of(value));

    BooleanSupplier result = m_helpers.getBooleanSupplier("Test/BooleanKey");
    assertNotNull(result);
    assertEquals(true, result.getAsBoolean());
  }

  @Test
  public void getmissingDoubleKeyShouldThrow() {
    throw new RuntimeException("Not implemented");
  }

  @Test
  public void getmissingBooleanKeyShouldThrow() {
    throw new RuntimeException("Not implemented");
  }

  @Test
  public void getWrongTypeDoubleKeyShouldThrow() {
    throw new RuntimeException("Not implemented");
  }

  @Test
  public void getWrongTypeBooleanKeyShouldThrow() {
    throw new RuntimeException("Not implemented");
  }

  @Test
  public void doubleValueSetShouldBeCorrectValue() {
    Client<Supplier<MultiType>> shuffleClient = m_globalMap.getClientWithPrefix("Test");
    Double[] valueArray = {
        2.0
    };
    shuffleClient.addItem("DoubleKey", () -> MultiType.of(valueArray[0]));

    DoubleSupplier result = m_helpers.getDoubleSupplier("Test/DoubleKey");
    assertNotNull(result);
    assertEquals(2.0, result.getAsDouble());

    valueArray[0] = 4.0;
    assertEquals(4.0, result.getAsDouble());
  }

  @Test
  public void booleanValueSetShouldBeCorrectValue() {
    Client<Supplier<MultiType>> shuffleClient = m_globalMap.getClientWithPrefix("Test");
    Boolean[] valueArray = {
        true
    };
    shuffleClient.addItem("BooleanKey", () -> MultiType.of(valueArray[0]));

    BooleanSupplier result = m_helpers.getBooleanSupplier("Test/BooleanKey");
    assertNotNull(result);
    assertEquals(true, result.getAsBoolean());

    valueArray[0] = false;
    assertEquals(false, result.getAsBoolean());
  }
}
