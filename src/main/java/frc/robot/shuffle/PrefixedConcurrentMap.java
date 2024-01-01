package frc.robot.shuffle;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Stores a map of key-value pairs, where the key is prefixed with a string path.
 */
public class PrefixedConcurrentMap {
  private final ConcurrentHashMap<String, Double> m_map = new ConcurrentHashMap<>();

  // Method to add a value with a prefixed key
  private void add(String prefix, String key, Double value) {
    m_map.put(prefix + (prefix.isEmpty() ? "" : "/") + key, value);
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

    Client getSubdirectoryClient(String subdirectory);
  }

  /**
   * Method to create a client with a specific prefix.
   */
  public Client getClientWithPrefix(String initialPrefix) {
    return new Client() {
      private String m_prefix = initialPrefix;

      @Override
      public void addItem(String key, Double value) {
        add(m_prefix, key, value);
      }

      @Override
      public Client getSubdirectoryClient(String subdirectory) {
        return new Client() {
          private final String m_extendedPrefix = m_prefix + (m_prefix.isEmpty() ? "" : "/")
              + subdirectory;

          @Override
          public void addItem(String key, Double value) {
            add(m_extendedPrefix, key, value);
          }

          @Override
          public Client getSubdirectoryClient(String furtherSubdirectory) {
            return getClientWithPrefix(
                m_extendedPrefix + (m_extendedPrefix.isEmpty() ? "" : "/") + furtherSubdirectory);
          }
        };
      }
    };
  }
}
