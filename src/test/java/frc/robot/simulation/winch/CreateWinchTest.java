package frc.robot.simulation.winch;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import edu.wpi.first.hal.HAL;
import frc.robot.simulation.winch.WinchSimModel.WindingOrientation;
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

  private double getSpoolDiameter() {
    return 0.01; // (1 centimeter)
  }

  private double getStringLen() {
    return 5;
  }

  private WinchSimModel helperCreatingWinchWithParams(double spoolDiameterMeters,
      double totalStringLenMeters,
      double lenSpooled,
      WindingOrientation windingOrientation,
      boolean invertMotor) {

    WinchParams winchParams = new WinchParams(spoolDiameterMeters, totalStringLenMeters, lenSpooled,
        windingOrientation, invertMotor);

    WinchSimModel tempWinchSimulation = new WinchSimModel(winchParams);
    assertTrue(tempWinchSimulation != null);

    return tempWinchSimulation;
  }

  @Test
  public void initialLenSpooledLessThanTotalStringLenShouldSucceed() {
    helperCreatingWinchWithParams(getSpoolDiameter(), 5, 4, WindingOrientation.BackOfRobot, false);
  }

  @Test
  public void initialLenSpooledLenEqualToTotalStringLenShouldSucceed() {
    helperCreatingWinchWithParams(getSpoolDiameter(), 5, 5, WindingOrientation.BackOfRobot, false);
  }

  @Test
  public void initialLenSpooledLenGreaterThanTotalStringLenShouldFail() {
    assertThrows(IllegalArgumentException.class, () -> {
      helperCreatingWinchWithParams(getSpoolDiameter(),
          5,
          5.1,
          WindingOrientation.BackOfRobot,
          false);
    });
  }

  @Test
  public void initialLenSpooledLenZeroShouldSucceed() {
    helperCreatingWinchWithParams(getSpoolDiameter(), 5, 0, WindingOrientation.BackOfRobot, false);
  }

  @Test
  public void initialLenSpooledLenLessThanZeroShouldFail() {
    assertThrows(IllegalArgumentException.class, () -> {
      helperCreatingWinchWithParams(getSpoolDiameter(),
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

    WinchSimModel tempWinchSimulation = helperCreatingWinchWithParams(getSpoolDiameter(),
        totalStringLen,
        stringLenSpooled,
        stringOrientation,
        false);
    assertTrue(tempWinchSimulation.getStringUnspooledLen() == totalStringLen - stringLenSpooled);
  }

  @Test
  public void queryingStringLenExtendedShouldReturnCorrectValueWhenStringOnFront() {
    double totalStringLen = 5;
    double stringLenSpooled = 1;
    WindingOrientation stringOrientation = WindingOrientation.FrontOfRobot;

    WinchSimModel tempWinchSimulation = helperCreatingWinchWithParams(getSpoolDiameter(),
        totalStringLen,
        stringLenSpooled,
        stringOrientation,
        false);
    assertTrue(tempWinchSimulation.getStringUnspooledLen() == totalStringLen - stringLenSpooled);
  }

  @Test
  public void queryingStringOrientationFrontShouldReturnCorrectValue() {
    double initialLenSpooled = 1;
    WindingOrientation stringOrientation = WindingOrientation.FrontOfRobot;

    WinchSimModel tempWinchSimulation = helperCreatingWinchWithParams(getSpoolDiameter(),
        getStringLen(),
        initialLenSpooled,
        stringOrientation,
        false);
    assertTrue(tempWinchSimulation.getWindingOrientation() == stringOrientation);
  }

  @Test
  public void queryingStringOrientationBackShouldReturnCorrectValue() {
    double initialLenSpooled = 1;
    WindingOrientation stringOrientation = WindingOrientation.BackOfRobot;

    WinchSimModel tempWinchSimulation = helperCreatingWinchWithParams(getSpoolDiameter(),
        getStringLen(),
        initialLenSpooled,
        stringOrientation,
        false);
    assertTrue(tempWinchSimulation.getWindingOrientation() == stringOrientation);
  }

  @Test
  public void queryingStringOrientationShouldReturnBackWhenSpooledAmountIsZero() {
    WindingOrientation stringOrientation = WindingOrientation.FrontOfRobot;
    double initialLenSpooled = 0;

    WinchSimModel tempWinchSimulation = helperCreatingWinchWithParams(getSpoolDiameter(),
        getStringLen(),
        initialLenSpooled,
        stringOrientation,
        false);
    assertTrue(tempWinchSimulation.getWindingOrientation() == WindingOrientation.BackOfRobot);
  }
}
