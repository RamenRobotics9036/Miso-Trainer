package frc.robot.simulation.winch;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import edu.wpi.first.hal.HAL;
import frc.robot.helpers.UnitConversions;
import frc.robot.simulation.framework.SimManager;
import frc.robot.simulation.framework.inputoutputs.CopySimOutput;
import frc.robot.simulation.framework.inputoutputs.LambdaSimInput;
import frc.robot.simulation.winch.WinchSimModel.WindingOrientation;
import java.util.function.Supplier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test the WinchSimulation class. These are more advanced
 * tests that run a cycle of the simulation and check the results.
 */
public class WinchSimModelTest {
  private final double m_spoolDiameterMeters = 0.01; // (1 centimeter)
  private final double m_totalStringLenMeters = 5;
  private final double m_initialLenSpooled = 1;

  /**
   * Runs before each test.
   */
  @BeforeEach
  public void setUp() {
    assert HAL.initialize(500, 0); // initialize the HAL, crash if failed
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

    WinchState tempWinchState = new WinchState(m_totalStringLenMeters);

    WinchParams winchParams = new WinchParams(m_spoolDiameterMeters, m_totalStringLenMeters,
        m_initialLenSpooled, stringOrientation, flipWinchPolarity);

    // Create SimManager
    SimManager<Double, WinchState> winchSimManager = new SimManager<Double, WinchState>(
        new WinchSimModel(winchParams), true);
    winchSimManager.setInputHandler(new LambdaSimInput<Double>(winchInputSupplier));
    winchSimManager.setOutputHandler(new CopySimOutput<WinchState>(tempWinchState));

    // Initialize the number of rotations
    currentWinchRotations[0] = 0.0;
    winchSimManager.simulationPeriodic();

    // Rotate the motor such that string gets 0.5 meters longer
    double spoolCircumference = m_spoolDiameterMeters * Math.PI;
    double numRotations = lenToGrowString / spoolCircumference;

    currentWinchRotations[0] = numRotations;
    winchSimManager.simulationPeriodic();

    double result = tempWinchState.getStringUnspooledLen();

    assertEquals(result, expectedResult, UnitConversions.kAngleTolerance);
    assertTrue(winchSimManager.isBroken() == expectIsBroken);
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

    WinchState tempWinchState = new WinchState(m_totalStringLenMeters);

    WinchParams winchParams = new WinchParams(m_spoolDiameterMeters, m_totalStringLenMeters,
        m_initialLenSpooled, WindingOrientation.BackOfRobot, false);

    // Create SimManager
    SimManager<Double, WinchState> winchSimManager = new SimManager<Double, WinchState>(
        new WinchSimModel(winchParams), true);
    winchSimManager.setInputHandler(new LambdaSimInput<Double>(winchInputSupplier));
    winchSimManager.setOutputHandler(new CopySimOutput<WinchState>(tempWinchState));

    // Initialize the number of rotations
    currentWinchRotations[0] = 0.0;
    winchSimManager.simulationPeriodic();

    // Rotate the motor such that string gets a bit longer
    double spoolCircumference = m_spoolDiameterMeters * Math.PI;
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
  }
}
