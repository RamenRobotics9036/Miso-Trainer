package frc.robot.simulation.winch;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import edu.wpi.first.hal.HAL;
import frc.robot.helpers.UnitConversions;
import frc.robot.simulation.winch.WinchSimModel.WindingOrientation;
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

    WinchSimModel tempWinchSimulation = new WinchSimModel(m_spoolDiameterMeters,
        m_totalStringLenMeters, m_initialLenSpooled, stringOrientation, flipWinchPolarity);

    // Initialize the number of rotations
    tempWinchSimulation.updateSimulation(0.0);

    // Rotate the motor such that string gets 0.5 meters longer
    double spoolCircumference = m_spoolDiameterMeters * Math.PI;
    double numRotations = lenToGrowString / spoolCircumference;

    tempWinchSimulation.updateSimulation(numRotations);
    double result = tempWinchSimulation.getStringUnspooledLen();

    assertEquals(result, expectedResult, UnitConversions.kAngleTolerance);
    // $TODO - Shouldnt be stashing winchSimulation!
    assertTrue(tempWinchSimulation.isModelBroken() == expectIsBroken);
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
    WinchSimModel tempWinchSimulation = new WinchSimModel(m_spoolDiameterMeters,
        m_totalStringLenMeters, m_initialLenSpooled, WindingOrientation.BackOfRobot, false);

    // Initialize the number of rotations
    tempWinchSimulation.updateSimulation(0.0);

    // Rotate the motor such that string gets a bit longer
    double spoolCircumference = m_spoolDiameterMeters * Math.PI;
    double numRotations = 0.2 / spoolCircumference;
    tempWinchSimulation.updateSimulation(numRotations);

    // Rotate again
    numRotations = 0.2 / spoolCircumference;
    tempWinchSimulation.updateSimulation(numRotations * 2);

    double result = tempWinchSimulation.getStringUnspooledLen();

    double expectedResult = 4.4;
    assertEquals(result, expectedResult, UnitConversions.kAngleTolerance);
    // $TODO - Shouldnt be stashing winchSimulation!
    assertTrue(!tempWinchSimulation.isModelBroken());

  }
}
