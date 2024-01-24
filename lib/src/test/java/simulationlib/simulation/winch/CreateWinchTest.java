package simulationlib.simulation.winch;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import edu.wpi.first.hal.HAL;
import simulationlib.simulation.winch.WinchCable;
import simulationlib.simulation.winch.WinchParams;
import simulationlib.simulation.winch.WinchSimModel;
import simulationlib.simulation.winch.WinchState;
import simulationlib.simulation.winch.WinchSimModel.WindingOrientation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test creating the WinchSimModel class with various inputs.
 */
public class CreateWinchTest {

  /**
   * Runs before each test.
   */
  @BeforeEach
  public void setUp() {
    assert HAL.initialize(500, 0); // initialize the HAL, crash if failed
  }

  private double getTestingSpoolDiameter() {
    return 0.01; // (1 centimeter)
  }

  private double getTestingStringLen() {
    return 5;
  }

  private WinchSimModel helperCreatingWinchWithParams(double spoolDiameterMeters,
      double totalStringLenMeters,
      double lenSpooled,
      WindingOrientation windingOrientation,
      boolean invertMotor) {

    WinchParams winchParams = new WinchParams(spoolDiameterMeters,
        new WinchCable(totalStringLenMeters, totalStringLenMeters - lenSpooled, windingOrientation),
        invertMotor);

    WinchSimModel tempWinchSimulation = new WinchSimModel(winchParams);
    assertTrue(tempWinchSimulation != null);

    return tempWinchSimulation;
  }

  @Test
  public void initialLenSpooledLessThanTotalStringLenShouldSucceed() {
    helperCreatingWinchWithParams(getTestingSpoolDiameter(),
        5,
        4,
        WindingOrientation.BackOfRobot,
        false);
  }

  @Test
  public void initialLenSpooledLenEqualToTotalStringLenShouldSucceed() {
    helperCreatingWinchWithParams(getTestingSpoolDiameter(),
        5,
        5,
        WindingOrientation.BackOfRobot,
        false);
  }

  @Test
  public void initialLenSpooledLenGreaterThanTotalStringLenShouldFail() {
    assertThrows(IllegalArgumentException.class, () -> {
      helperCreatingWinchWithParams(getTestingSpoolDiameter(),
          5,
          5.1,
          WindingOrientation.BackOfRobot,
          false);
    });
  }

  @Test
  public void initialLenSpooledLenZeroShouldSucceed() {
    helperCreatingWinchWithParams(getTestingSpoolDiameter(),
        5,
        0,
        WindingOrientation.BackOfRobot,
        false);
  }

  @Test
  public void initialLenSpooledLenLessThanZeroShouldFail() {
    assertThrows(IllegalArgumentException.class, () -> {
      helperCreatingWinchWithParams(getTestingSpoolDiameter(),
          5,
          -1,
          WindingOrientation.BackOfRobot,
          false);
    });
  }

  @Test
  public void queryingStringLenExtendedShouldReturnCorrectValueWhenStringOnBack() {
    double totalStringLen = 5;
    double stringLenSpooled = 1;
    WindingOrientation stringOrientation = WindingOrientation.BackOfRobot;

    WinchSimModel tempWinchSimulation = helperCreatingWinchWithParams(getTestingSpoolDiameter(),
        totalStringLen,
        stringLenSpooled,
        stringOrientation,
        false);

    // Call updateSimulation with 0 rotations as a way to get back current winchState
    WinchState winchState = tempWinchSimulation.updateSimulation(0.0);
    assertTrue(winchState.getStringUnspooledLen() == totalStringLen - stringLenSpooled);
  }

  @Test
  public void queryingStringLenExtendedShouldReturnCorrectValueWhenStringOnFront() {
    double totalStringLen = 5;
    double stringLenSpooled = 1;
    WindingOrientation stringOrientation = WindingOrientation.FrontOfRobot;

    WinchSimModel tempWinchSimulation = helperCreatingWinchWithParams(getTestingSpoolDiameter(),
        totalStringLen,
        stringLenSpooled,
        stringOrientation,
        false);

    // Call updateSimulation with 0 rotations as a way to get back current winchState
    WinchState winchState = tempWinchSimulation.updateSimulation(0.0);
    assertTrue(winchState.getStringUnspooledLen() == totalStringLen - stringLenSpooled);
  }

  @Test
  public void queryingStringOrientationFrontShouldReturnCorrectValue() {
    double initialLenSpooled = 1;
    WindingOrientation stringOrientation = WindingOrientation.FrontOfRobot;

    WinchSimModel tempWinchSimulation = helperCreatingWinchWithParams(getTestingSpoolDiameter(),
        getTestingStringLen(),
        initialLenSpooled,
        stringOrientation,
        false);

    // Call updateSimulation with 0 rotations as a way to get back current winchState
    WinchState winchState = tempWinchSimulation.updateSimulation(0.0);
    assertTrue(winchState.getWindingOrientation() == stringOrientation);
  }

  @Test
  public void queryingStringOrientationBackShouldReturnCorrectValue() {
    double initialLenSpooled = 1;
    WindingOrientation stringOrientation = WindingOrientation.BackOfRobot;

    WinchSimModel tempWinchSimulation = helperCreatingWinchWithParams(getTestingSpoolDiameter(),
        getTestingStringLen(),
        initialLenSpooled,
        stringOrientation,
        false);

    // Call updateSimulation with 0 rotations as a way to get back current winchState
    WinchState winchState = tempWinchSimulation.updateSimulation(0.0);
    assertTrue(winchState.getWindingOrientation() == stringOrientation);
  }

  @Test
  public void queryingStringOrientationShouldReturnBackWhenSpooledAmountIsZero() {
    WindingOrientation stringOrientation = WindingOrientation.FrontOfRobot;
    double initialLenSpooled = 0;

    WinchSimModel tempWinchSimulation = helperCreatingWinchWithParams(getTestingSpoolDiameter(),
        getTestingStringLen(),
        initialLenSpooled,
        stringOrientation,
        false);

    // Call updateSimulation with 0 rotations as a way to get back current winchState
    WinchState winchState = tempWinchSimulation.updateSimulation(0.0);
    assertTrue(winchState.getWindingOrientation() == WindingOrientation.BackOfRobot);
  }
}
