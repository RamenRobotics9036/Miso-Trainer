package frc.robot.shuffle;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * This class is a factory for the global singleton instance of ConcurrentHashMap.
 */
public class SupplierMapFactory {
  /**
   * This is the global singleton instance of ConcurrentHashMap.
   */
  @SuppressWarnings("LineLengthCheck")
  private static final ConcurrentHashMap<String, Supplier<MultiType>> globalMap = new ConcurrentHashMap<>();

  private SupplierMapFactory() {
    // Private constructor to prevent instantiation
  }

  // Method to get the global singleton instance of ConcurrentHashMap
  public static ConcurrentHashMap<String, Supplier<MultiType>> getGlobalInstance() {
    return globalMap;
  }
}
