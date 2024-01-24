package simulationlib.shuffle;

// import java.util.function.Supplier;
// import simulationlib.shuffle.PrefixedConcurrentMap.Client;

// $TODO - This singleton is causing race-conditions in the tests. Also,
// if multiple tests are running simultaneously, they will interfere with each other.
/*
 * $TODO - Remove this
 * public class SupplierMapFactory_old {
 * 
 * @SuppressWarnings("LineLengthCheck")
 * private static final PrefixedConcurrentMap<Supplier<MultiType>> globalMap = new
 * PrefixedConcurrentMap<>();
 * 
 * private SupplierMapFactory() {
 * // Private constructor to prevent instantiation
 * }
 * 
 * // Method to get the global singleton instance of ConcurrentHashMap
 * public static PrefixedConcurrentMap<Supplier<MultiType>> getGlobalInstance() {
 * return globalMap;
 * }
 * 
 * public static Client<Supplier<MultiType>> getGlobalSimClient() {
 * PrefixedConcurrentMap<Supplier<MultiType>> globalMap = SupplierMapFactory.getGlobalInstance();
 * 
 * return globalMap.getClientWithPrefix("sim");
 * }
 * }
 */
