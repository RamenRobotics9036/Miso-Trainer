package frc.robot.shuffle;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
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
  void testAddAndGetSingleItem() {
    PrefixedConcurrentMap.Client<String> client = m_map.getClientWithPrefix("Test");
    client.addItem("Key1", "Value1");

    String value = m_map.get("Test/Key1");
    assertEquals("Value1", value, "Retrieved value should match the added value.");
  }

  @Test
  @DisplayName("Test subdirectory functionality")
  void testSubdirectory() {
    PrefixedConcurrentMap.Client<String> client = m_map.getClientWithPrefix("Test");
    PrefixedConcurrentMap.Client<String> subClient = client.getSubdirectoryClient("SubTest");

    subClient.addItem("Key2", "Value2");

    String value = m_map.get("Test/SubTest/Key2");
    assertEquals("Value2", value, "Retrieved value should match the added value in subdirectory.");
  }

  @Test
  @DisplayName("Test multiple levels of subdirectories")
  void testMultipleSubdirectories() {
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
  void testGetAllEntries() {
    PrefixedConcurrentMap.Client<String> client = m_map.getClientWithPrefix("Test");
    client.addItem("Key1", "Value1");
    client.addItem("Key2", "Value2");

    Map<String, String> entries = m_map.getAllEntries();
    assertEquals(2, entries.size(), "Map should contain exactly 2 entries.");
    assertTrue(entries.containsKey("Test/Key1"), "Map should contain key Test/Key1.");
    assertTrue(entries.containsKey("Test/Key2"), "Map should contain key Test/Key2.");
  }

  @Test
  @DisplayName("Test that duplicate keys cannot be added")
  void testNoDuplicateKeysAllowed() {
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
  void testGetClientWithNullInitialPrefix() {
    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
      m_map.getClientWithPrefix(null);
    });

    assertEquals("Initial prefix cannot be null or empty.",
        exception.getMessage(),
        "Exception message should indicate null or empty initial prefix.");
  }

  @Test
  @DisplayName("Test getClientWithPrefix with empty initialPrefix throws IllegalArgumentException")
  void testGetClientWithEmptyInitialPrefix() {
    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
      m_map.getClientWithPrefix("");
    });

    assertEquals("Initial prefix cannot be null or empty.",
        exception.getMessage(),
        "Exception message should indicate null or empty initial prefix.");
  }

  @Test
  @DisplayName("Test getSubdirectoryClient with null subdirectory throws IllegalArgumentException")
  void testGetSubdirectoryClientWithNullSubdirectory() {
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
  void testGetSubdirectoryClientWithEmptySubdirectory() {
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
  void testAddWithNullKey() {
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
  void testAddWithEmptyKey() {
    PrefixedConcurrentMap.Client<String> client = m_map.getClientWithPrefix("Test");

    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
      client.addItem("", "Value");
    });

    assertEquals("Key cannot be null or empty.",
        exception.getMessage(),
        "Exception message should indicate null or empty key.");
  }
}
