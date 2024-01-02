package frc.robot.shuffle;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.function.Supplier;

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
