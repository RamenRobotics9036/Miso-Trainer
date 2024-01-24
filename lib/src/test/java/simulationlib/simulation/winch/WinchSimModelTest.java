package simulationlib.simulation.winch;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.function.Supplier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import simulationlib.helpers.UnitConversions;
import simulationlib.simulation.framework.SimManager;
import simulationlib.simulation.framework.inputoutputs.CopySimOutput;
import simulationlib.simulation.framework.inputoutputs.LambdaSimInput;
import simulationlib.simulation.winch.WinchSimModel.WindingOrientation;

/**
 * Test the WinchSimulation class. These are more advanced
 * tests that run a cycle of the simulation and check the results.
 */
public class WinchSimModelTest {

  /**
   * Runs before each test.
   */
  @BeforeEach
  public void setUp() {
  }

  private double getTestingSpoolDiameter() {
    return 0.01; // (1 centimeter)
  }

  private double getTestingStringLen() {
    return 5;
  }

  private double getTestingInitialLenSpooled() {
    return 1;
  }

  private SimManager<Double, WinchState> createTestSimManager(WindingOrientation stringOrientation,
      boolean flipWinchPolarity,
      Supplier<Double> winchInputSupplier,
      WinchState winchState) {

    WinchParams winchParams = new WinchParams(
        getTestingSpoolDiameter(), new WinchCable(getTestingStringLen(),
            getTestingStringLen() - getTestingInitialLenSpooled(), stringOrientation),
        flipWinchPolarity);

    SimManager<Double, WinchState> winchSimManager = new SimManager<Double, WinchState>(
        new WinchSimModel(winchParams), null, null, true);
    winchSimManager.setInputHandler(new LambdaSimInput<Double>(winchInputSupplier));
    winchSimManager.setOutputHandler(new CopySimOutput<WinchState>(winchState));

    return winchSimManager;
  }

  private void testWinchMove(WindingOrientation stringOrientation,
      double lenToGrowString,
      boolean flipWinchPolarity,
      double expectedResult,
      boolean expectIsBroken) {

    double[] currentWinchRotations = {
        0
    };
    Supplier<Double> winchInputSupplier = () -> {
      return currentWinchRotations[0];
    };

    WinchState tempWinchState = new WinchState();

    // Create SimManager
    SimManager<Double, WinchState> winchSimManager = createTestSimManager(stringOrientation,
        flipWinchPolarity,
        winchInputSupplier,
        tempWinchState);

    // Initialize the number of rotations
    currentWinchRotations[0] = 0.0;
    winchSimManager.simulationPeriodic();

    // Rotate the motor such that string gets 0.5 meters longer
    double spoolCircumference = getTestingSpoolDiameter() * Math.PI;
    double numRotations = lenToGrowString / spoolCircumference;

    currentWinchRotations[0] = numRotations;
    winchSimManager.simulationPeriodic();

    double result = tempWinchState.getStringUnspooledLen();
    assertEquals(result, expectedResult, UnitConversions.kAngleTolerance);
    assertTrue(winchSimManager.isBroken() == expectIsBroken);

    // Now check that tempWinchState.getStringUnspooledPercent() is correct too
    double expectedPercent = tempWinchState.getStringUnspooledPercent();
    assertEquals(expectedPercent,
        expectedResult / getTestingStringLen(),
        UnitConversions.kAngleTolerance);
  }

  @Test
  public void whenStringOnBackAndMotorTurnsClockwiseThenStringShouldGetLonger() {
    testWinchMove(WindingOrientation.BackOfRobot,
        0.5, // lenToGrowString
        false, // flipWinchPolarity
        4.5, // expectedResult
        false); // expectedIsBroken
  }

  @Test
  public void whenStringOnBackAndMotorTurnsCounterClockwiseThenStringShouldGetShorter() {
    testWinchMove(WindingOrientation.BackOfRobot,
        -0.5, // lenToGrowString
        false, // flipWinchPolarity
        3.5, // expectedResult
        false); // expectedIsBroken
  }

  @Test
  public void winchShouldMoveOtherDirectionWhenPolarityInverted() {
    testWinchMove(WindingOrientation.BackOfRobot,
        0.5, // lenToGrowString
        true, // flipWinchPolarity
        3.5, // expectedResult
        false); // expectedIsBroken
  }

  @Test
  public void winchShouldSpoolOtherDirectionIfStringExtendedTooLong() {
    testWinchMove(WindingOrientation.BackOfRobot,
        1.5, // lenToGrowString
        false, // flipWinchPolarity
        4.5, // expectedResult
        false); // expectedIsBroken
  }

  @Test
  public void winchShouldntBreakIfEntirelySpooled() {
    testWinchMove(WindingOrientation.BackOfRobot,
        5.99, // lenToGrowString
        false, // flipWinchPolarity
        0.01, // expectedResult
        false); // expectedIsBroken
  }

  @Test
  public void winchShouldBreakIfOverSpooled() {
    testWinchMove(WindingOrientation.BackOfRobot,
        6.01, // lenToGrowString
        false, // flipWinchPolarity
        0.0, // expectedResult
        true); // expectedIsBroken
  }

  @Test
  public void twoMotorMovesShouldMoveStringCumulatively() {
    double[] currentWinchRotations = {
        0
    };
    Supplier<Double> winchInputSupplier = () -> {
      return currentWinchRotations[0];
    };

    WinchState tempWinchState = new WinchState();

    // Create SimManager
    SimManager<Double, WinchState> winchSimManager = createTestSimManager(
        WindingOrientation.BackOfRobot,
        false,
        winchInputSupplier,
        tempWinchState);

    // Initialize the number of rotations
    currentWinchRotations[0] = 0.0;
    winchSimManager.simulationPeriodic();

    // Rotate the motor such that string gets a bit longer
    double spoolCircumference = getTestingSpoolDiameter() * Math.PI;
    double numRotations = 0.2 / spoolCircumference;

    currentWinchRotations[0] = numRotations;
    winchSimManager.simulationPeriodic();

    // Rotate again
    currentWinchRotations[0] = numRotations * 2;
    winchSimManager.simulationPeriodic();

    double result = tempWinchState.getStringUnspooledLen();

    double expectedResult = 4.4;
    assertEquals(result, expectedResult, UnitConversions.kAngleTolerance);
    assertTrue(!winchSimManager.isBroken());

    // Now check that tempWinchState.getStringUnspooledPercent() is correct too
    double expectedPercent = tempWinchState.getStringUnspooledPercent();
    assertEquals(expectedPercent,
        expectedResult / getTestingStringLen(),
        UnitConversions.kAngleTolerance);
  }
}
