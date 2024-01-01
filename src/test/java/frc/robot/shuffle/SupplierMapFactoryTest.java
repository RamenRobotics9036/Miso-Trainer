package frc.robot.shuffle;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SupplierMapFactoryTest {

  @Test
  @DisplayName("Test that getGlobalInstance returns a ConcurrentHashMap instance")
  void testGetGlobalInstanceReturnsMap() {
    ConcurrentHashMap<String, Supplier<MultiType>> map = SupplierMapFactory.getGlobalInstance();
    assertNotNull(map, "getGlobalInstance should return a non-null ConcurrentHashMap instance.");
  }

  @Test
  @DisplayName("Test that getGlobalInstance always returns the same ConcurrentHashMap instance")
  void testGetGlobalInstanceSingletonProperty() {
    ConcurrentHashMap<String, Supplier<MultiType>> map1 = SupplierMapFactory.getGlobalInstance();
    ConcurrentHashMap<String, Supplier<MultiType>> map2 = SupplierMapFactory.getGlobalInstance();

    assertSame(map1,
        map2,
        "getGlobalInstance should return the same ConcurrentHashMap instance on multiple calls.");
  }
}
