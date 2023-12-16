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
  private final double m_spoolDiameterMeters = 0.01; // (1 centimeter)
  private final double m_totalStringLenMeters = 5;
  private final WindingOrientation m_initialStringOrientation = WindingOrientation.BackOfRobot;
  private final boolean m_invertMotor = false;

  /**
   * Runs before each test.
   */
  @BeforeEach
  public void setUp() {
    assert HAL.initialize(500, 0); // initialize the HAL, crash if failed
  }

  @Test
  public void initialLenSpooledLessThanTotalStringLenShouldSucceed() {
    WinchParams winchParams = new WinchParams(m_spoolDiameterMeters, 5, 4,
        m_initialStringOrientation, m_invertMotor);

    WinchSimModel tempWinchSimulation = new WinchSimModel(winchParams);
    assertTrue(tempWinchSimulation != null);
  }

  @Test
  public void initialLenSpooledLenEqualToTotalStringLenShouldSucceed() {
    WinchParams winchParams = new WinchParams(m_spoolDiameterMeters, 5, 5,
        m_initialStringOrientation, m_invertMotor);

    WinchSimModel tempWinchSimulation = new WinchSimModel(winchParams);
    assertTrue(tempWinchSimulation != null);
  }

  @Test
  public void initialLenSpooledLenGreaterThanTotalStringLenShouldFail() {
    assertThrows(IllegalArgumentException.class, () -> {
      WinchParams winchParams = new WinchParams(m_spoolDiameterMeters, 5, 5.1,
          m_initialStringOrientation, m_invertMotor);

      WinchSimModel tempWinchSimulation = new WinchSimModel(winchParams);
      assertTrue(tempWinchSimulation != null);
    });
  }

  @Test
  public void initialLenSpooledLenZeroShouldSucceed() {
    WinchParams winchParams = new WinchParams(m_spoolDiameterMeters, 5, 0,
        m_initialStringOrientation, m_invertMotor);

    WinchSimModel tempWinchSimulation = new WinchSimModel(winchParams);
    assertTrue(tempWinchSimulation != null);
  }

  @Test
  public void initialLenSpooledLenLessThanZeroShouldFail() {
    assertThrows(IllegalArgumentException.class, () -> {
      WinchParams winchParams = new WinchParams(m_spoolDiameterMeters, 5, -1,
          m_initialStringOrientation, m_invertMotor);

      WinchSimModel tempWinchSimulation = new WinchSimModel(winchParams);
      assertTrue(tempWinchSimulation != null);
    });
  }

  @Test
  public void queryingStringLenExtendedShouldReturnCorrectValueWhenStringOnBack() {
    double totalStringLen = 5;
    double stringLenSpooled = 1;
    WindingOrientation stringOrientation = WindingOrientation.BackOfRobot;

    WinchParams winchParams = new WinchParams(m_spoolDiameterMeters, totalStringLen,
        stringLenSpooled, stringOrientation, m_invertMotor);

    WinchSimModel tempWinchSimulation = new WinchSimModel(winchParams);
    assertTrue(tempWinchSimulation.getStringUnspooledLen() == totalStringLen - stringLenSpooled);
  }

  @Test
  public void queryingStringLenExtendedShouldReturnCorrectValueWhenStringOnFront() {
    double totalStringLen = 5;
    double stringLenSpooled = 1;
    WindingOrientation stringOrientation = WindingOrientation.FrontOfRobot;

    WinchParams winchParams = new WinchParams(m_spoolDiameterMeters, totalStringLen,
        stringLenSpooled, stringOrientation, m_invertMotor);

    WinchSimModel tempWinchSimulation = new WinchSimModel(winchParams);
    assertTrue(tempWinchSimulation.getStringUnspooledLen() == totalStringLen - stringLenSpooled);
  }

  @Test
  public void queryingStringOrientationFrontShouldReturnCorrectValue() {
    double initialLenSpooled = 1;
    WindingOrientation stringOrientation = WindingOrientation.FrontOfRobot;

    WinchParams winchParams = new WinchParams(m_spoolDiameterMeters, m_totalStringLenMeters,
        initialLenSpooled, stringOrientation, m_invertMotor);

    WinchSimModel tempWinchSimulation = new WinchSimModel(winchParams);
    assertTrue(tempWinchSimulation.getWindingOrientation() == stringOrientation);
  }

  @Test
  public void queryingStringOrientationBackShouldReturnCorrectValue() {
    double initialLenSpooled = 1;
    WindingOrientation stringOrientation = WindingOrientation.BackOfRobot;

    WinchParams winchParams = new WinchParams(m_spoolDiameterMeters, m_totalStringLenMeters,
        initialLenSpooled, stringOrientation, m_invertMotor);

    WinchSimModel tempWinchSimulation = new WinchSimModel(winchParams);
    assertTrue(tempWinchSimulation.getWindingOrientation() == stringOrientation);
  }

  @Test
  public void queryingStringOrientationShouldReturnBackWhenSpooledAmountIsZero() {
    WindingOrientation stringOrientation = WindingOrientation.FrontOfRobot;
    double initialLenSpooled = 0;

    WinchParams winchParams = new WinchParams(m_spoolDiameterMeters, m_totalStringLenMeters,
        initialLenSpooled, stringOrientation, m_invertMotor);

    WinchSimModel tempWinchSimulation = new WinchSimModel(winchParams);
    assertTrue(tempWinchSimulation.getWindingOrientation() == WindingOrientation.BackOfRobot);
  }
}
