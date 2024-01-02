package frc.robot.shuffle;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Stores a map of key-value pairs, where the key is prefixed with a string path
 * and the value can be of any type.
 */
public class PrefixedConcurrentMap<T> {
  private final ConcurrentHashMap<String, T> m_map = new ConcurrentHashMap<>();

  // Method to add a value with a prefixed key
  private void add(String prefix, String key, T value) {
    // Check for null or empty prefix and key
    if (prefix == null || prefix.isEmpty()) {
      throw new IllegalArgumentException("Prefix cannot be null or empty.");
    }
    if (key == null || key.isEmpty()) {
      throw new IllegalArgumentException("Key cannot be null or empty.");
    }

    String fullKey = prefix + (prefix.isEmpty() ? "" : "/") + key;

    if (m_map.containsKey(fullKey)) {
      throw new IllegalArgumentException("Duplicate key: " + fullKey);
    }

    m_map.put(fullKey, value);
  }

  // Method to retrieve a value by its full key
  public T get(String key) {
    return m_map.get(key);
  }

  // Returns a READ-ONLY set of all entries in the map
  public Set<Map.Entry<String, T>> getAllEntries() {
    return Collections.unmodifiableSet(m_map.entrySet());
  }

  public void clear() {
    m_map.clear();
  }

  public String toString() {
    return m_map.keySet().toString();
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
    if (initialPrefix == null || initialPrefix.isEmpty()) {
      throw new IllegalArgumentException("Initial prefix cannot be null or empty.");
    }

    return new Client<T>() {
      private String m_prefix = initialPrefix;

      @Override
      public void addItem(String key, T value) {
        add(m_prefix, key, value);
      }

      @Override
      public Client<T> getSubdirectoryClient(String subdirectory) {
        if (subdirectory == null || subdirectory.isEmpty()) {
          throw new IllegalArgumentException("Subdirectory cannot be null or empty.");
        }

        return new Client<T>() {
          private final String m_extendedPrefix = m_prefix + "/" + subdirectory;

          @Override
          public void addItem(String key, T value) {
            add(m_extendedPrefix, key, value);
          }

          @Override
          public Client<T> getSubdirectoryClient(String furtherSubdirectory) {
            return getClientWithPrefix(m_extendedPrefix + "/" + furtherSubdirectory);
          }
        };
      }
    };
  }
}
