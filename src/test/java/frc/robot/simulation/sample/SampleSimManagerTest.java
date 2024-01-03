package frc.robot.simulation.sample;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import frc.robot.simulation.framework.SimManager;
import frc.robot.simulation.framework.inputoutputs.LambdaSimInput;
import frc.robot.simulation.framework.inputoutputs.LambdaSimOutput;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test the simulation framework: The framework takes an input, runs
 * it through the simulation model, and returns an output.
 */
public class SampleSimManagerTest {

  /**
   * Runs before each test.
   */
  @BeforeEach
  public void setUp() {
  }

  @Test
  public void creatingSampleSimManagerShouldSucceed() {
    int ratio = 2;

    // Note: Many of these tests run with isRobotEnabled = () -> true. This is because
    // we want all the testing to concretely run the simulation, and not skip it.
    SimManager<Integer, Integer> sampleSimManager = new SimManager<Integer, Integer>(
        new SampleSimModel(ratio), null, () -> true);
    assertTrue(sampleSimManager != null);
  }

  @Test
  public void creatingSimInputWithoutSupplierShouldThrowException() {
    assertThrows(IllegalArgumentException.class, () -> {

      @SuppressWarnings("unused")
      LambdaSimInput<Integer> sampleSimInput = new LambdaSimInput<Integer>(null);
    });
  }

  @Test
  public void creatingSimOutputWithoutConsumerShouldThrowException() {
    assertThrows(IllegalArgumentException.class, () -> {

      @SuppressWarnings("unused")
      LambdaSimOutput<Integer> sampleSimOutput = new LambdaSimOutput<Integer>(null);
    });
  }

  @Test
  public void creatingSimManagerWithNullIsRobotEnabledParamShouldThrowException() {
    assertThrows(IllegalArgumentException.class, () -> {
      int ratio = 2;

      @SuppressWarnings("unused")
      SimManager<Integer, Integer> sampleSimManager = new SimManager<Integer, Integer>(
          new SampleSimModel(ratio), null, null);
    });
  }

  private void runSimulationTenTimes(SimManager<Integer, Integer> sampleSimManager) {
    if (sampleSimManager == null) {
      throw new IllegalArgumentException("sampleSimManager cannot be null");
    }

    for (int i = 0; i < 10; i++) {
      sampleSimManager.simulationPeriodic();
    }
  }

  @Test
  public void creatingSimManagerWithNoInputOutputsAndRunningPeriodicShouldSucceed() {
    int ratio = 2;
    SimManager<Integer, Integer> sampleSimManager = new SimManager<Integer, Integer>(
        new SampleSimModel(ratio), null, () -> true);

    runSimulationTenTimes(sampleSimManager);
  }

  @Test
  public void creatingSimManagerWithOnlyOutputAndRunningPeriodicShouldNotUpdateOutputValue() {
    int ratio = 2;
    final int[] outputVariable = {
        5
    };
    int expectedVallue = outputVariable[0];

    SimManager<Integer, Integer> sampleSimManager = new SimManager<Integer, Integer>(
        new SampleSimModel(ratio), null, () -> true);
    sampleSimManager.setOutputHandler(new LambdaSimOutput<Integer>((numOutput) -> {
      outputVariable[0] = numOutput;
    }));

    runSimulationTenTimes(sampleSimManager);

    assertEquals(expectedVallue, outputVariable[0]);
  }

  private int testSimulation_Helper(int ratio,
      int initialOutputValue,
      int inputValue,
      boolean isEnabledDuringInit,
      boolean isEnabledDuringSimulation,
      boolean doSimulationTenTimes) {

    final int[] inputVariable = {
        inputValue
    };

    final int[] outputVariable = {
        initialOutputValue
    };

    final boolean[] isRobotEnabled = {
        isEnabledDuringInit
    };

    SimManager<Integer, Integer> sampleSimManager = new SimManager<Integer, Integer>(
        new SampleSimModel(ratio), null, () -> isRobotEnabled[0]);

    sampleSimManager.setInputHandler(new LambdaSimInput<Integer>(() -> {
      return inputVariable[0];
    }));

    sampleSimManager.setOutputHandler(new LambdaSimOutput<Integer>((numOutput) -> {
      outputVariable[0] = numOutput;
    }));

    isRobotEnabled[0] = isEnabledDuringSimulation;
    if (doSimulationTenTimes) {
      runSimulationTenTimes(sampleSimManager);
    }

    return outputVariable[0];
  }

  /**
   * This test validates that the output is updated once during robot initialization.
   * This happens even if Periodic is never called.
   * In this particular test, the robot is ENABLED the entire time.
   */
  @Test
  public void creatingSimManagerWithEnabledRobotAlwaysUpdatesOutputOnceDuringInitialization() {
    int ratio = 4;
    int initialOutputValue = -1;
    int inputValue = 5;
    boolean isRobotEnabledDuringInit = true;
    boolean isRobotEnabledDuringSimulation = true;
    boolean doSimulationTenTimes = false;
    int expectedVallue = 1 * (inputValue * ratio);

    int result = testSimulation_Helper(ratio,
        initialOutputValue,
        inputValue,
        isRobotEnabledDuringInit,
        isRobotEnabledDuringSimulation,
        doSimulationTenTimes);

    assertEquals(expectedVallue, result);
  }

  /**
   * This test validates that the output is updated once during robot initialization.
   * This happens even if Periodic is never called.
   * In this particular test, the robot is DISABLED the entire time.
   */
  @Test
  public void creatingSimManagerWithDisabledRobotAlwaysUpdatesOutputOnceDuringInitialization() {
    int ratio = 4;
    int initialOutputValue = -1;
    int inputValue = 5;
    boolean isRobotEnabledDuringInit = false;
    boolean isRobotEnabledDuringSimulation = false;
    boolean doSimulationTenTimes = false;
    int expectedVallue = 1 * (inputValue * ratio);

    int result = testSimulation_Helper(ratio,
        initialOutputValue,
        inputValue,
        isRobotEnabledDuringInit,
        isRobotEnabledDuringSimulation,
        doSimulationTenTimes);

    assertEquals(expectedVallue, result);
  }

  /**
   * When the Periodic is called 10 times, the output should be updated 10 times.
   * However, since the output is also updated once during initialization, the
   * total number of times output is updated is actually 11.
   * In this particular test, the robot was DISABLED during initialization, but
   * was ENABLED while the 10 Periodic calls were made.
   */
  @Test
  public void runningSimulationWithRobotDisabledDuringInit() {
    int ratio = 4;
    int initialOutputValue = -1;
    int inputValue = 5;
    boolean isRobotEnabledDuringInit = false;
    boolean isRobotEnabledDuringSimulation = true;
    boolean doSimulationTenTimes = true;
    int expectedVallue = 11 * (inputValue * ratio);

    int result = testSimulation_Helper(ratio,
        initialOutputValue,
        inputValue,
        isRobotEnabledDuringInit,
        isRobotEnabledDuringSimulation,
        doSimulationTenTimes);

    assertEquals(expectedVallue, result);
  }

  /**
   * When the Periodic is called 10 times, the output should be updated 10 times.
   * However, since the output is also updated once during initialization, the
   * total number of times output is updated is actually 11.
   * In this particular test, the robot was ENABLED during the ENTIRE test.
   */
  @Test
  public void runningSimulationWithRobotEnabledDuringInit() {
    int ratio = 4;
    int initialOutputValue = -1;
    int inputValue = 5;
    boolean isRobotEnabledDuringInit = true;
    boolean isRobotEnabledDuringSimulation = true;
    boolean doSimulationTenTimes = true;
    int expectedVallue = 11 * (inputValue * ratio);

    int result = testSimulation_Helper(ratio,
        initialOutputValue,
        inputValue,
        isRobotEnabledDuringInit,
        isRobotEnabledDuringSimulation,
        doSimulationTenTimes);

    assertEquals(expectedVallue, result);
  }

  /**
   * In this test, Periodic is called 10 times. However, the robot is DISABLED
   * during each of these calls. Therefore, the output should only be updated
   * once during initialization.
   */
  @Test
  public void runningSimulationTenTimesWithRobotDisabledTheEntireTime() {
    int ratio = 4;
    int initialOutputValue = -1;
    int inputValue = 5;
    boolean isRobotEnabledDuringInit = false;
    boolean isRobotEnabledDuringSimulation = false;
    boolean doSimulationTenTimes = true;
    int expectedVallue = 1 * (inputValue * ratio);

    int result = testSimulation_Helper(ratio,
        initialOutputValue,
        inputValue,
        isRobotEnabledDuringInit,
        isRobotEnabledDuringSimulation,
        doSimulationTenTimes);

    assertEquals(expectedVallue, result);
  }
}
