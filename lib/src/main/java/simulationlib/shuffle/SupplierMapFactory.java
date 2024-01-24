package simulationlib.shuffle;

import java.util.function.Supplier;
import simulationlib.shuffle.PrefixedConcurrentMap.Client;

/**
 * This class is a factory for the global singleton instance of PrefixedConcurrentMap.
 */
public class SupplierMapFactory {
  /**
   * This is the global singleton instance of ConcurrentHashMap.
   */
  @SuppressWarnings("LineLengthCheck")
  private static final PrefixedConcurrentMap<Supplier<MultiType>> globalMap = new PrefixedConcurrentMap<>();

  private SupplierMapFactory() {
    // Private constructor to prevent instantiation
  }

  // Method to get the global singleton instance of ConcurrentHashMap
  public static PrefixedConcurrentMap<Supplier<MultiType>> getGlobalInstance() {
    return globalMap;
  }

  /**
   * Returns a client for the global map with the specified prefix.
   * In Simulation, all SupplierMaps are prefixed with "sim".
   */
  public static Client<Supplier<MultiType>> getGlobalSimClient() {
    PrefixedConcurrentMap<Supplier<MultiType>> globalMap = SupplierMapFactory.getGlobalInstance();

    return globalMap.getClientWithPrefix("sim");
  }
}
