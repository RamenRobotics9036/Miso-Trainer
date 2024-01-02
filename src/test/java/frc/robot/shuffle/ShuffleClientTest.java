package frc.robot.shuffle;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.function.Supplier;

import frc.robot.shuffle.PrefixedConcurrentMap.Client;
import frc.robot.simulation.framework.SimManager;
import frc.robot.simulation.sample.SampleSimModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test querying each sim model for the properties it shows on the Shuffleboard dash.
 */
public class ShuffleClientTest {
  PrefixedConcurrentMap<Supplier<MultiType>> m_globalMap = SupplierMapFactory.getGlobalInstance();

  @BeforeEach
  public void setUp() {
    // Reset the global cache of dashboard items before each test
    m_globalMap.clear();
  }

  @Test
  public void creatingSimManagerWithNullShuffleClientShouldSucceed() {
    int ratio = 2;

    SimManager<Integer, Integer> sampleSimManager = new SimManager<Integer, Integer>(
        new SampleSimModel(ratio), null, () -> true);
    assertTrue(sampleSimManager != null);
  }

  @Test
  public void creatingSimManagerWithNonNullShuffleClientShouldSucceed() {
    int ratio = 2;

    Client<Supplier<MultiType>> shuffleClient = m_globalMap.getClientWithPrefix("Sample sim");

    SimManager<Integer, Integer> sampleSimManager = new SimManager<Integer, Integer>(
        new SampleSimModel(ratio), shuffleClient, () -> true);
    assertTrue(sampleSimManager != null);
  }

  @Test
  public void creatingSimManagerShouldHaveExpectedAcumulatorProperty() {
    int ratio = 2;

    Client<Supplier<MultiType>> shuffleClient = m_globalMap.getClientWithPrefix("Sample sim");

    SimManager<Integer, Integer> sampleSimManager = new SimManager<Integer, Integer>(
        new SampleSimModel(ratio), shuffleClient, () -> true);
    assertTrue(sampleSimManager != null);

    // We expect exactly 1 property to be in the global hashmap
    assertEquals(1, m_globalMap.getAllEntries().size());

    // For the sample sim, we expect to see the following properties on the Shuffleboard:
    // Accumulator
    Supplier<MultiType> accumulatorSupplier = m_globalMap.get("Sample sim/Accumulator");
    assertNotNull(accumulatorSupplier);
  }

  @Test
  public void creatingSimManagerShouldNotHaveBogusProperty() {
    int ratio = 2;

    Client<Supplier<MultiType>> shuffleClient = m_globalMap.getClientWithPrefix("Sample sim");

    SimManager<Integer, Integer> sampleSimManager = new SimManager<Integer, Integer>(
        new SampleSimModel(ratio), shuffleClient, () -> true);
    assertTrue(sampleSimManager != null);

    // We expect exactly 1 property to be in the global hashmap
    assertEquals(1, m_globalMap.getAllEntries().size());

    // We do NOT expect to have a bogus property Accumulator2
    Supplier<MultiType> accumulatorSupplier = m_globalMap.get("Sample sim/Accumulator2");
    assertNull(accumulatorSupplier);
  }

  @Test
  public void creatingSimManagerShouldReturnCorrectToString() {
    int ratio = 2;

    Client<Supplier<MultiType>> shuffleClient = m_globalMap.getClientWithPrefix("Sample sim");

    SimManager<Integer, Integer> sampleSimManager = new SimManager<Integer, Integer>(
        new SampleSimModel(ratio), shuffleClient, () -> true);
    assertTrue(sampleSimManager != null);

    // We expect exactly 1 property to be in the global hashmap
    assertEquals(1, m_globalMap.getAllEntries().size());

    String actualString = m_globalMap.toString();
    String expectedString = "[Sample sim/Accumulator]";
    assertEquals(expectedString, actualString);
  }

  // $TODO - Verify that the initial VALUE of the property when Supplier is queried is as expected
  // $TODO - Verify that after the value is CHANGED, querying the Supplier again returns the new
  // value

  // $TODO - Get rid of this
  // public int testSimulation_Helper(int ratio,
  // int initialOutputValue,
  // int inputValue,
  // boolean isEnabledDuringInit,
  // boolean isEnabledDuringSimulation,
  // boolean doSimulationTenTimes) {

  // final int[] inputVariable = {
  // inputValue
  // };

  // final int[] outputVariable = {
  // initialOutputValue
  // };

  // final boolean[] isRobotEnabled = {
  // isEnabledDuringInit
  // };

  // SimManager<Integer, Integer> sampleSimManager = new SimManager<Integer, Integer>(
  // new SampleSimModel(ratio), null, () -> isRobotEnabled[0]);

  // sampleSimManager.setInputHandler(new LambdaSimInput<Integer>(() -> {
  // return inputVariable[0];
  // }));

  // sampleSimManager.setOutputHandler(new LambdaSimOutput<Integer>((numOutput) -> {
  // outputVariable[0] = numOutput;
  // }));

  // isRobotEnabled[0] = isEnabledDuringSimulation;
  // if (doSimulationTenTimes) {
  // runSimulationTenTimes(sampleSimManager);
  // }

  // return outputVariable[0];
  // }
}
