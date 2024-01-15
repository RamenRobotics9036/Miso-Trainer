package frc.robot.shuffle;

import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;
import java.util.function.Supplier;

/**
 * Helper class for populating Shuffleboard.
 */
public class ShuffleboardHelpers {
  PrefixedConcurrentMap<Supplier<MultiType>> m_globalMap;

  /**
   * Constructor.
   */
  public ShuffleboardHelpers(PrefixedConcurrentMap<Supplier<MultiType>> globalMap) {
    m_globalMap = globalMap;
  }

  /**
   * We have a global map of all properties that can be displayed in Shuffleboard.
   * These properties are stored as Supplier for MultiType objects. This
   * method converts the supplier to a DoubleSupplier/BooleanSupplier/etc and returns it.
   */
  public DoubleSupplier getDoubleSupplier(String key) {
    Supplier<MultiType> supplier = m_globalMap.get(key);

    if (supplier == null) {
      throw new IllegalArgumentException("Key missing: " + key);
    }

    if (!supplier.get().getType().equals("Double")) {
      throw new IllegalArgumentException("Key wrong type: " + key);
    }

    return () -> supplier.get().getDouble().orElse(0.0);
  }

  /**
   * Converts Supplier for MultiType to BooleanSupplier.
   */
  public BooleanSupplier getBooleanSupplier(String key) {
    Supplier<MultiType> supplier = m_globalMap.get(key);

    if (supplier == null) {
      throw new IllegalArgumentException("Key missing: " + key);
    }

    if (!supplier.get().getType().equals("Boolean")) {
      throw new IllegalArgumentException("Key wrong type: " + key);
    }

    return () -> supplier.get().getBoolean().orElse(false);
  }
}
