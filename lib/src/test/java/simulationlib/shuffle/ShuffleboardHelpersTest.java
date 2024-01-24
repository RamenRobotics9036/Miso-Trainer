package simulationlib.shuffle;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;
import java.util.function.Supplier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import simulationlib.shuffle.PrefixedConcurrentMap.Client;

/**
 * Unit tests for ShuffleboardHelpers.
 */
public class ShuffleboardHelpersTest {
  PrefixedConcurrentMap<Supplier<MultiType>> m_globalMap;
  private ShuffleboardHelpers m_helpers;

  /**
   * Constructor.
   */
  public ShuffleboardHelpersTest() {
  }

  @BeforeEach
  public void setUp() {
    m_globalMap = new PrefixedConcurrentMap<>();
    m_helpers = new ShuffleboardHelpers(m_globalMap);
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
  public void getBooleanSupplierShouldSucceed() {
    Client<Supplier<MultiType>> shuffleClient = m_globalMap.getClientWithPrefix("Test");
    Boolean value = true;
    shuffleClient.addItem("BooleanKey", () -> MultiType.of(value));

    BooleanSupplier result = m_helpers.getBooleanSupplier("Test/BooleanKey");
    assertNotNull(result);
    assertEquals(true, result.getAsBoolean());
  }

  @Test
  public void getmissingDoubleKeyShouldThrow() {
    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
      m_helpers.getDoubleSupplier("Test/BogusKey");
    });

    String expectedMessage = "Key missing: Test/BogusKey";
    String actualMessage = exception.getMessage();

    assertEquals(expectedMessage, actualMessage);
  }

  @Test
  public void getmissingBooleanKeyShouldThrow() {
    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
      m_helpers.getBooleanSupplier("Test/BogusKey");
    });

    String expectedMessage = "Key missing: Test/BogusKey";
    String actualMessage = exception.getMessage();

    assertEquals(expectedMessage, actualMessage);
  }

  @Test
  public void getWrongTypeDoubleKeyShouldThrow() {
    Client<Supplier<MultiType>> shuffleClient = m_globalMap.getClientWithPrefix("Test");
    Double value = 2.0;
    shuffleClient.addItem("DoubleKey", () -> MultiType.of(value));

    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
      m_helpers.getBooleanSupplier("Test/DoubleKey");
    });

    String expectedMessage = "Key wrong type: Test/DoubleKey";
    String actualMessage = exception.getMessage();

    assertEquals(expectedMessage, actualMessage);
  }

  @Test
  public void getWrongTypeBooleanKeyShouldThrow() {
    Client<Supplier<MultiType>> shuffleClient = m_globalMap.getClientWithPrefix("Test");
    Boolean value = true;
    shuffleClient.addItem("BooleanKey", () -> MultiType.of(value));

    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
      m_helpers.getDoubleSupplier("Test/BooleanKey");
    });

    String expectedMessage = "Key wrong type: Test/BooleanKey";
    String actualMessage = exception.getMessage();

    assertEquals(expectedMessage, actualMessage);
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
