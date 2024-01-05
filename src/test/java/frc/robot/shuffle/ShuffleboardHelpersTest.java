package frc.robot.shuffle;

import java.util.function.Supplier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for ShuffleboardHelpers.
 */
public class ShuffleboardHelpersTest {
  private PrefixedConcurrentMap<Supplier<MultiType>> m_mapMock;
  private ShuffleboardHelpers m_helpers;

  @BeforeEach
  public void setUp() {
    m_helpers = new ShuffleboardHelpers(m_mapMock);
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
}
