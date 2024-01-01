package frc.robot.shuffle;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Stores a map of key-value pairs, where the key is prefixed with a string.
 */
public class PrefixedConcurrentMap {
  private final ConcurrentHashMap<String, Double> m_map = new ConcurrentHashMap<>();

  // Method to add a value with a prefixed key
  private void add(String prefix, String key, Double value) {
    m_map.put(prefix + "_" + key, value);
  }

  // Method to retrieve a value by its full key
  public Double get(String key) {
    return m_map.get(key);
  }

  // Method to retrieve all entries in the map
  public Map<String, Double> getAllEntries() {
    return m_map;
  }

  /**
   * Client interface for adding items.
   */
  public interface Client {
    void addItem(String key, Double value);
  }

  /**
   * Method to create a client with a specific prefix.
   */
  public Client getClientWithPrefix(String prefix) {
    return new Client() {
      @Override
      public void addItem(String key, Double value) {
        add(prefix, key, value);
      }
    };
  }
}
