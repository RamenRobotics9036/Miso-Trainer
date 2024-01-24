package simulationlib.shuffle;

import java.util.function.Supplier;
import simulationlib.shuffle.PrefixedConcurrentMap.Client;

/**
 * Returns a singleton instance to a dictionary mapping a string ID of a component
 * to the lambda that returns live information about that component (which can
 * then be shown in Shuffleboard).
 * <p>
 * Do not use this singleton in Unit Tests, since the tests should not share
 * global state.
 * </p>
 */
public class SupplierMapFactory {

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
   * Returns a Client to query information about a component, for showing in Shuffleboard.
   */
  public static Client<Supplier<MultiType>> getGlobalSimClient() {
    PrefixedConcurrentMap<Supplier<MultiType>> globalMap = SupplierMapFactory.getGlobalInstance();

    return globalMap.getClientWithPrefix("sim");
  }
}
