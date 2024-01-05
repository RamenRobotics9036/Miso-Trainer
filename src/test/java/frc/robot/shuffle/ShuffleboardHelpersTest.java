package frc.robot.shuffle;

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
    throw new RuntimeException("Not implemented");
  }

  @Test
  public void getBooleanDoubleSupplierShouldSucceed() {
    throw new RuntimeException("Not implemented");
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
    throw new RuntimeException("Not implemented");
  }

  @Test
  public void booleanValueSetShouldBeCorrectValue() {
    throw new RuntimeException("Not implemented");
  }
}
