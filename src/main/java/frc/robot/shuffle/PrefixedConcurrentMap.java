package frc.robot.shuffle;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Stores a map of key-value pairs, where the key is prefixed with a string path
 * and the value can be of any type.
 */
public class PrefixedConcurrentMap<T> {
  private final ConcurrentHashMap<String, T> m_map = new ConcurrentHashMap<>();

  // Method to add a value with a prefixed key
  private void add(String prefix, String key, T value) {
    m_map.put(prefix + (prefix.isEmpty() ? "" : "/") + key, value);
  }

  // Method to retrieve a value by its full key
  public T get(String key) {
    return m_map.get(key);
  }

  // Method to retrieve all entries in the map
  public Map<String, T> getAllEntries() {
    return m_map;
  }

  /**
   * Client interface for adding items.
   */
  public interface Client<T> {
    void addItem(String key, T value);

    Client<T> getSubdirectoryClient(String subdirectory);
  }

  /**
   * Method to create a client with a specific prefix.
   */
  public Client<T> getClientWithPrefix(String initialPrefix) {
    return new Client<T>() {
      private String m_prefix = initialPrefix;

      @Override
      public void addItem(String key, T value) {
        add(m_prefix, key, value);
      }

      @Override
      public Client<T> getSubdirectoryClient(String subdirectory) {
        return new Client<T>() {
          private final String m_extendedPrefix = m_prefix + (m_prefix.isEmpty() ? "" : "/")
              + subdirectory;

          @Override
          public void addItem(String key, T value) {
            add(m_extendedPrefix, key, value);
          }

          @Override
          public Client<T> getSubdirectoryClient(String furtherSubdirectory) {
            return getClientWithPrefix(
                m_extendedPrefix + (m_extendedPrefix.isEmpty() ? "" : "/") + furtherSubdirectory);
          }
        };
      }
    };
  }
}
