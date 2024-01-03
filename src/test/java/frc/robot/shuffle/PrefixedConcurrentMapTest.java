package frc.robot.shuffle;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.AbstractMap;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PrefixedConcurrentMapTest {

  private PrefixedConcurrentMap<String> m_map;

  @BeforeEach
  void setUp() {
    m_map = new PrefixedConcurrentMap<>();
  }

  @Test
  @DisplayName("Test adding and retrieving a single item")
  public void testAddAndGetSingleItem() {
    PrefixedConcurrentMap.Client<String> client = m_map.getClientWithPrefix("Test");
    client.addItem("Key1", "Value1");

    String value = m_map.get("Test/Key1");
    assertEquals("Value1", value, "Retrieved value should match the added value.");
  }

  @Test
  @DisplayName("Test subdirectory functionality")
  public void testSubdirectory() {
    PrefixedConcurrentMap.Client<String> client = m_map.getClientWithPrefix("Test");
    PrefixedConcurrentMap.Client<String> subClient = client.getSubdirectoryClient("SubTest");

    subClient.addItem("Key2", "Value2");

    String value = m_map.get("Test/SubTest/Key2");
    assertEquals("Value2", value, "Retrieved value should match the added value in subdirectory.");
  }

  @Test
  @DisplayName("Test multiple levels of subdirectories")
  public void testMultipleSubdirectories() {
    PrefixedConcurrentMap.Client<String> client = m_map.getClientWithPrefix("Level1");
    PrefixedConcurrentMap.Client<String> subClient1 = client.getSubdirectoryClient("Level2");
    PrefixedConcurrentMap.Client<String> subClient2 = subClient1.getSubdirectoryClient("Level3");

    subClient2.addItem("Key3", "Value3");

    String value = m_map.get("Level1/Level2/Level3/Key3");
    assertEquals("Value3",
        value,
        "Retrieved value should match the added value in nested subdirectories.");
  }

  @Test
  @DisplayName("Test retrieving all entries")
  public void testGetAllEntries() {
    PrefixedConcurrentMap.Client<String> client = m_map.getClientWithPrefix("Test");
    client.addItem("Key1", "Value1");
    client.addItem("Key2", "Value2");

    Set<Map.Entry<String, String>> entries = m_map.getAllEntries();
    assertEquals(2, entries.size(), "Map should contain exactly 2 entries.");

    boolean foundKey1 = false;
    boolean foundKey2 = false;
    for (Map.Entry<String, String> entry : entries) {
      if ("Test/Key1".equals(entry.getKey()) && "Value1".equals(entry.getValue())) {
        foundKey1 = true;
      }
      else if ("Test/Key2".equals(entry.getKey()) && "Value2".equals(entry.getValue())) {
        foundKey2 = true;
      }
    }

    assertTrue(foundKey1, "Map should contain key Test/Key1 with correct value.");
    assertTrue(foundKey2, "Map should contain key Test/Key2 with correct value.");
  }

  @Test
  @DisplayName("Test that returned set of entries is read-only")
  public void testReadOnlyEntries() {
    PrefixedConcurrentMap.Client<String> client = m_map.getClientWithPrefix("Test");
    client.addItem("Key1", "Value1");

    Set<Map.Entry<String, String>> entries = m_map.getAllEntries();

    assertThrows(UnsupportedOperationException.class, () -> {
      // Trying to modify the set should throw an exception
      entries.add(new AbstractMap.SimpleEntry<>("Test/Key2", "Value2"));
    });
  }

  @Test
  @DisplayName("Test set reflects current map state")
  public void testSetReflectsMapState() {
    PrefixedConcurrentMap.Client<String> client = m_map.getClientWithPrefix("Test");
    client.addItem("Key1", "Value1");

    Set<Map.Entry<String, String>> entries = m_map.getAllEntries();
    assertEquals(1, entries.size(), "Initial set should contain 1 entry.");

    // Add another item and check if the set reflects this change
    client.addItem("Key2", "Value2");
    assertEquals(2, m_map.getAllEntries().size(), "Set should reflect the new state of the map.");
  }

  @Test
  @DisplayName("Test that duplicate keys cannot be added")
  public void testNoDuplicateKeysAllowed() {
    PrefixedConcurrentMap.Client<String> client = m_map.getClientWithPrefix("Test");
    client.addItem("Key1", "Value1");

    // Attempt to add another item with the same key
    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
      client.addItem("Key1", "Value2");
    });

    String expectedMessage = "Duplicate key: Test/Key1";
    String actualMessage = exception.getMessage();
    assertTrue(actualMessage.contains(expectedMessage),
        "Exception message should indicate duplicate key.");

    // Verify the original value is still present
    String value = m_map.get("Test/Key1");
    assertEquals("Value1", value, "Original value should remain unchanged.");
  }

  @Test
  @DisplayName("Test getClientWithPrefix with null initialPrefix throws IllegalArgumentException")
  public void testGetClientWithNullInitialPrefix() {
    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
      m_map.getClientWithPrefix(null);
    });

    assertEquals("Initial prefix cannot be null or empty.",
        exception.getMessage(),
        "Exception message should indicate null or empty initial prefix.");
  }

  @Test
  @DisplayName("Test getClientWithPrefix with empty initialPrefix throws IllegalArgumentException")
  public void testGetClientWithEmptyInitialPrefix() {
    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
      m_map.getClientWithPrefix("");
    });

    assertEquals("Initial prefix cannot be null or empty.",
        exception.getMessage(),
        "Exception message should indicate null or empty initial prefix.");
  }

  @Test
  @DisplayName("Test getSubdirectoryClient with null subdirectory throws IllegalArgumentException")
  public void testGetSubdirectoryClientWithNullSubdirectory() {
    PrefixedConcurrentMap.Client<String> client = m_map.getClientWithPrefix("Test");

    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
      client.getSubdirectoryClient(null);
    });

    assertEquals("Subdirectory cannot be null or empty.",
        exception.getMessage(),
        "Exception message should indicate null or empty subdirectory.");
  }

  @Test
  @DisplayName("Test getSubdirectoryClient with empty subdirectory throws IllegalArgumentException")
  public void testGetSubdirectoryClientWithEmptySubdirectory() {
    PrefixedConcurrentMap.Client<String> client = m_map.getClientWithPrefix("Test");

    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
      client.getSubdirectoryClient("");
    });

    assertEquals("Subdirectory cannot be null or empty.",
        exception.getMessage(),
        "Exception message should indicate null or empty subdirectory.");
  }

  @Test
  @DisplayName("Test add with null key throws IllegalArgumentException")
  public void testAddWithNullKey() {
    PrefixedConcurrentMap.Client<String> client = m_map.getClientWithPrefix("Test");

    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
      client.addItem(null, "Value");
    });

    assertEquals("Key cannot be null or empty.",
        exception.getMessage(),
        "Exception message should indicate null or empty key.");
  }

  @Test
  @DisplayName("Test add with empty key throws IllegalArgumentException")
  public void testAddWithEmptyKey() {
    PrefixedConcurrentMap.Client<String> client = m_map.getClientWithPrefix("Test");

    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
      client.addItem("", "Value");
    });

    assertEquals("Key cannot be null or empty.",
        exception.getMessage(),
        "Exception message should indicate null or empty key.");
  }

  @Test
  @DisplayName("Test clearing all entries")
  public void testClear() {
    PrefixedConcurrentMap.Client<String> client = m_map.getClientWithPrefix("Test");
    client.addItem("Key1", "Value1");
    client.addItem("Key2", "Value2");

    m_map.clear();

    assertEquals(0, m_map.getAllEntries().size(), "Map should be empty after clearing it.");
  }

  @Test
  @DisplayName("Test toString returns correct string")
  public void testToString() {
    PrefixedConcurrentMap.Client<String> client = m_map.getClientWithPrefix("Test");
    client.addItem("Key1", "Value1");
    client.addItem("Key2", "Value2");

    String expectedString = "[Test/Key2, Test/Key1]";
    assertEquals(expectedString, m_map.toString(), "toString should return correct string.");
  }

  @Test
  @DisplayName("Test toString returns empty string when map is empty")
  public void testToStringEmpty() {
    String expectedString = "[]";
    assertEquals(expectedString, m_map.toString(), "toString should return empty string.");
  }
}
