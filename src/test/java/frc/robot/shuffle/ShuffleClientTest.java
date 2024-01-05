package frc.robot.shuffle;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import frc.robot.shuffle.PrefixedConcurrentMap.Client;
import frc.robot.simulation.framework.DashboardItem;
import frc.robot.simulation.framework.DashboardPluginInterface;
import frc.robot.simulation.framework.SimManager;
import frc.robot.simulation.framework.inputoutputs.LambdaSimInput;
import frc.robot.simulation.framework.inputoutputs.LambdaSimOutput;
import frc.robot.simulation.sample.SampleDashboardPlugin;
import frc.robot.simulation.sample.SampleSimModel;
import java.util.function.Supplier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test querying each sim model for the properties it shows on the Shuffleboard dash.
 */
public class ShuffleClientTest {
  PrefixedConcurrentMap<Supplier<MultiType>> m_globalMap = SupplierMapFactory.getGlobalInstance();
  private final Integer m_numDefaultProperties = 1;

  @BeforeEach
  public void setUp() {
    // Reset the global cache of dashboard items before each test
    m_globalMap.clear();
  }

  @Test
  public void creatingSimManagerWithNullShuffleClientShouldSucceed() {
    int ratio = 2;

    SimManager<Integer, Integer> sampleSimManager = new SimManager<Integer, Integer>(
        new SampleSimModel(ratio), null, null, () -> true);
    assertTrue(sampleSimManager != null);
  }

  @Test
  public void creatingSimManagerWithNonNullShuffleClientShouldSucceed() {
    int ratio = 2;

    Client<Supplier<MultiType>> shuffleClient = m_globalMap.getClientWithPrefix("Sample sim");

    SimManager<Integer, Integer> sampleSimManager = new SimManager<Integer, Integer>(
        new SampleSimModel(ratio), shuffleClient, null, () -> true);
    assertTrue(sampleSimManager != null);
  }

  @Test
  public void creatingSimManagerShouldHaveExpectedAcumulatorProperty() {
    int ratio = 2;

    Client<Supplier<MultiType>> shuffleClient = m_globalMap.getClientWithPrefix("Sample sim");
    DashboardPluginInterface<Integer, Integer> plugin = new SampleDashboardPlugin();

    SimManager<Integer, Integer> sampleSimManager = new SimManager<Integer, Integer>(
        new SampleSimModel(ratio), shuffleClient, plugin, () -> true);
    assertTrue(sampleSimManager != null);

    // We expect exactly 1 property to be in the global hashmap
    assertEquals(1 + m_numDefaultProperties, m_globalMap.getAllEntries().size());

    // For the sample sim, we expect to see the following properties on the Shuffleboard:
    // Accumulator
    Supplier<MultiType> accumulatorSupplier = m_globalMap.get("Sample sim/Accumulator");
    assertNotNull(accumulatorSupplier);
  }

  @Test
  public void creatingSimManagerShouldNotHaveBogusProperty() {
    int ratio = 2;

    Client<Supplier<MultiType>> shuffleClient = m_globalMap.getClientWithPrefix("Sample sim");
    DashboardPluginInterface<Integer, Integer> plugin = new SampleDashboardPlugin();

    SimManager<Integer, Integer> sampleSimManager = new SimManager<Integer, Integer>(
        new SampleSimModel(ratio), shuffleClient, plugin, () -> true);
    assertTrue(sampleSimManager != null);

    // We expect exactly 1 property to be in the global hashmap
    assertEquals(1 + m_numDefaultProperties, m_globalMap.getAllEntries().size());

    // We do NOT expect to have a bogus property Accumulator2
    Supplier<MultiType> accumulatorSupplier = m_globalMap.get("Sample sim/Accumulator2");
    assertNull(accumulatorSupplier);
  }

  @Test
  public void creatingSimManagerShouldReturnCorrectToString() {
    int ratio = 2;

    Client<Supplier<MultiType>> shuffleClient = m_globalMap.getClientWithPrefix("Sample sim");
    DashboardPluginInterface<Integer, Integer> plugin = new SampleDashboardPlugin();

    SimManager<Integer, Integer> sampleSimManager = new SimManager<Integer, Integer>(
        new SampleSimModel(ratio), shuffleClient, plugin, () -> true);
    assertTrue(sampleSimManager != null);

    // We expect exactly 1 property to be in the global hashmap
    assertEquals(1 + m_numDefaultProperties, m_globalMap.getAllEntries().size());

    String actualString = m_globalMap.toString();
    String expectedString = "[Sample sim/IsBroken, Sample sim/Accumulator]";
    assertEquals(expectedString, actualString);
  }

  @Test
  public void validDashboardPluginShouldSucceed() {
    class ValidDashboardPlugin implements DashboardPluginInterface<Integer, Integer> {
      @Override
      public DashboardItem[] queryListOfDashboardPropertiesWithInitValues() {
        return new DashboardItem[] {
            new DashboardItem("Accumulator", MultiType.of(0))
        };
      }

      @Override
      public MultiType[] getDashboardPropertiesFromInputOutput(Integer input, Integer output) {
        return new MultiType[] {
            MultiType.of(0)
        };
      }
    }

    int ratio = 2;

    Client<Supplier<MultiType>> shuffleClient = m_globalMap.getClientWithPrefix("Sample sim");
    DashboardPluginInterface<Integer, Integer> plugin = new ValidDashboardPlugin();

    SimManager<Integer, Integer> sampleSimManager = new SimManager<Integer, Integer>(
        new SampleSimModel(ratio), shuffleClient, plugin, () -> true);
    assertTrue(sampleSimManager != null);

    // We expect exactly 1 property to be in the global hashmap
    assertEquals(1 + m_numDefaultProperties, m_globalMap.getAllEntries().size());

    String actualString = m_globalMap.toString();
    String expectedString = "[Sample sim/IsBroken, Sample sim/Accumulator]";
    assertEquals(expectedString, actualString);
  }

  @Test
  public void dashboardPluginReturningNullShouldSucceed() {
    class DashboardPluginReturnsNull implements DashboardPluginInterface<Integer, Integer> {
      @Override
      public DashboardItem[] queryListOfDashboardPropertiesWithInitValues() {
        return null;
      }

      @Override
      public MultiType[] getDashboardPropertiesFromInputOutput(Integer input, Integer output) {
        return new MultiType[] {
            MultiType.of(0)
        };
      }
    }

    int ratio = 2;

    Client<Supplier<MultiType>> shuffleClient = m_globalMap.getClientWithPrefix("Sample sim");
    DashboardPluginInterface<Integer, Integer> plugin = new DashboardPluginReturnsNull();

    SimManager<Integer, Integer> sampleSimManager = new SimManager<Integer, Integer>(
        new SampleSimModel(ratio), shuffleClient, plugin, () -> true);

    assertTrue(sampleSimManager != null);

    // We expect exactly 0 properties to be in the global hashmap
    assertEquals(0 + m_numDefaultProperties, m_globalMap.getAllEntries().size());
  }

  @Test
  public void dashboardPluginReturningNullMultitypeShouldThrowException() {
    class DashboardPluginReturnsNullMultitype
        implements DashboardPluginInterface<Integer, Integer> {
      @Override
      public DashboardItem[] queryListOfDashboardPropertiesWithInitValues() {
        return new DashboardItem[] {
            new DashboardItem("Accumulator", null)
        };
      }

      @Override
      public MultiType[] getDashboardPropertiesFromInputOutput(Integer input, Integer output) {
        return new MultiType[] {
            MultiType.of(0)
        };
      }
    }

    int ratio = 2;

    Client<Supplier<MultiType>> shuffleClient = m_globalMap.getClientWithPrefix("Sample sim");
    DashboardPluginInterface<Integer, Integer> plugin = new DashboardPluginReturnsNullMultitype();

    assertThrows(IllegalArgumentException.class, () -> {
      @SuppressWarnings("unused")
      SimManager<Integer, Integer> sampleSimManager = new SimManager<Integer, Integer>(
          new SampleSimModel(ratio), shuffleClient, plugin, () -> true);
    });
  }

  @Test
  public void mismatchedPropertiesAndMultitypeArrayCountsShouldSucceed() {
    class DashboardPluginMismatchedArrayCounts
        implements DashboardPluginInterface<Integer, Integer> {
      @Override
      public DashboardItem[] queryListOfDashboardPropertiesWithInitValues() {
        return new DashboardItem[] {
            new DashboardItem("Accumulator", MultiType.of(0))
        };
      }

      @Override
      public MultiType[] getDashboardPropertiesFromInputOutput(Integer input, Integer output) {
        return new MultiType[] {
            MultiType.of(0), MultiType.of(1)
        };
      }
    }

    int ratio = 2;

    Client<Supplier<MultiType>> shuffleClient = m_globalMap.getClientWithPrefix("Sample sim");
    DashboardPluginInterface<Integer, Integer> plugin = new DashboardPluginMismatchedArrayCounts();

    SimManager<Integer, Integer> sampleSimManager = new SimManager<Integer, Integer>(
        new SampleSimModel(ratio), shuffleClient, plugin, () -> true);
    assertTrue(sampleSimManager != null);

    // We expect exactly 1 property to be in the global hashmap
    assertEquals(1 + m_numDefaultProperties, m_globalMap.getAllEntries().size());

    String actualString = m_globalMap.toString();
    String expectedString = "[Sample sim/IsBroken, Sample sim/Accumulator]";
    assertEquals(expectedString, actualString);
  }

  @Test
  public void nullMultiTypeArrayShouldSucceed() {
    class DashboardPluginNullMultitypeArray implements DashboardPluginInterface<Integer, Integer> {
      @Override
      public DashboardItem[] queryListOfDashboardPropertiesWithInitValues() {
        return new DashboardItem[] {
            new DashboardItem("Accumulator", MultiType.of(0))
        };
      }

      @Override
      public MultiType[] getDashboardPropertiesFromInputOutput(Integer input, Integer output) {
        return null;
      }
    }

    int ratio = 2;

    Client<Supplier<MultiType>> shuffleClient = m_globalMap.getClientWithPrefix("Sample sim");
    DashboardPluginInterface<Integer, Integer> plugin = new DashboardPluginNullMultitypeArray();

    SimManager<Integer, Integer> sampleSimManager = new SimManager<Integer, Integer>(
        new SampleSimModel(ratio), shuffleClient, plugin, () -> true);

    assertTrue(sampleSimManager != null);

    // We expect exactly 1 property to be in the global hashmap
    assertEquals(1 + m_numDefaultProperties, m_globalMap.getAllEntries().size());

    String actualString = m_globalMap.toString();
    String expectedString = "[Sample sim/IsBroken, Sample sim/Accumulator]";
    assertEquals(expectedString, actualString);
  }

  @Test
  public void nullMultiTypeItemShouldSucceed() {
    class DashboardPluginNullMultitypeItem implements DashboardPluginInterface<Integer, Integer> {
      @Override
      public DashboardItem[] queryListOfDashboardPropertiesWithInitValues() {
        return new DashboardItem[] {
            new DashboardItem("Accumulator", MultiType.of(0))
        };
      }

      @Override
      public MultiType[] getDashboardPropertiesFromInputOutput(Integer input, Integer output) {
        return new MultiType[] {
            MultiType.of(0), MultiType.of(1), null, MultiType.of(2)
        };
      }
    }

    int ratio = 2;

    Client<Supplier<MultiType>> shuffleClient = m_globalMap.getClientWithPrefix("Sample sim");
    DashboardPluginInterface<Integer, Integer> plugin = new DashboardPluginNullMultitypeItem();

    SimManager<Integer, Integer> sampleSimManager = new SimManager<Integer, Integer>(
        new SampleSimModel(ratio), shuffleClient, plugin, () -> true);

    assertTrue(sampleSimManager != null);

    // We expect exactly 1 property to be in the global hashmap
    assertEquals(1 + m_numDefaultProperties, m_globalMap.getAllEntries().size());

    String actualString = m_globalMap.toString();
    String expectedString = "[Sample sim/IsBroken, Sample sim/Accumulator]";
    assertEquals(expectedString, actualString);
  }

  @Test
  public void changingParametersShouldChangeDashboardValues() {

    int ratio = 1;

    final int[] inputVariable = {
        1
    };

    final int[] outputVariable = {
        0
    };

    Client<Supplier<MultiType>> shuffleClient = m_globalMap.getClientWithPrefix("Sample sim");
    DashboardPluginInterface<Integer, Integer> plugin = new SampleDashboardPlugin();

    SimManager<Integer, Integer> sampleSimManager = new SimManager<Integer, Integer>(
        new SampleSimModel(ratio), shuffleClient, plugin, () -> true);

    sampleSimManager.setInputHandler(new LambdaSimInput<Integer>(() -> {
      return inputVariable[0];
    }));

    sampleSimManager.setOutputHandler(new LambdaSimOutput<Integer>((numOutput) -> {
      outputVariable[0] = numOutput;
    }));

    // Get the dashboardSupplier
    Supplier<MultiType> dashboardSupplier = m_globalMap.get("Sample sim/Accumulator");

    // After initializing the input and output handlers, value of output should be 1
    int expected = 1;
    assertEquals(expected, outputVariable[0]);

    int actual = dashboardSupplier.get().getInteger().orElseThrow();
    assertEquals(expected, actual);

    // Simulate another iteration
    sampleSimManager.simulationPeriodic();

    expected = 2;
    assertEquals(expected, outputVariable[0]);

    actual = dashboardSupplier.get().getInteger().orElseThrow();
    assertEquals(expected, actual);
  }
}
