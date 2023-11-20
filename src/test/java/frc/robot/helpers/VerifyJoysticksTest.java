package frc.robot.helpers;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test VerifyJoysticks class.
 */
public class VerifyJoysticksTest {
  /**
   * Holds the expected joystick configuration values, just for these test cases.
   */
  private static JoystickConfig[] getTestJoystickConfigs() {
    return new JoystickConfig[] {
        new JoystickConfig(1, // port
            6, // expectedAxisCount
            16, // expectedButtonCount
            1, // expectedPOVCountt
            "Controller (Xbox One For Windows)", 1) // expectedJoystickType
    };
  }

  private static JoystickConfig[] getTwoTestJoysticksConfigs() {
    return new JoystickConfig[] {
        new JoystickConfig(1, // port
            6, // expectedAxisCount
            16, // expectedButtonCount
            1, // expectedPOVCountt
            "Controller (Xbox One For Windows)", 1), // expectedJoystickType
        new JoystickConfig(2, // port
            5, // expectedAxisCount
            4, // expectedButtonCount
            8, // expectedPOVCountt
            "Joystick 2", 20) // expectedJoystickType
    };
  }

  @BeforeEach
  public void setUp() {
  }

  @Test
  public void createVerifyJoysticksShouldSucceed() {
    VerifyJoysticks verifyTemp = new VerifyJoysticks(getTestJoystickConfigs(),
        new DriverStationFunctions(), 0);

    assertTrue(verifyTemp != null);
  }

  @Test
  public void createVerifyJoysticksWithNullJoystickConfigsShouldFail() {
    assertThrows(IllegalArgumentException.class,
        () -> new VerifyJoysticks(null, new DriverStationFunctions(), 0));
  }

  @Test
  public void createVerifyJoysticksWithNullDriverStationFunctionsShouldFail() {
    assertThrows(IllegalArgumentException.class,
        () -> new VerifyJoysticks(getTestJoystickConfigs(), null, 0));
  }

  @Test
  void joystickUnpluggedShouldShowAsUnplugged() {
    int port = 1;

    DriverStationFunctions mockDriverStationFunctions = mock(DriverStationFunctions.class);
    when(mockDriverStationFunctions.isJoystickConnected(port)).thenReturn(false);

    VerifyJoysticks verifyTemp = new VerifyJoysticks(getTestJoystickConfigs(),
        mockDriverStationFunctions, 0);

    assertTrue(verifyTemp != null);
    assertTrue(verifyTemp.getAreAllJoysticksHealth());

    verifyTemp.verifyJoysticksPeriodically();
    assertTrue(!verifyTemp.getAreAllJoysticksHealth());
    assertTrue(!verifyTemp.getIsJoystickConnectedCorrect(0));
  }

  @Test
  void joystickPluggedInShouldShowAsPluggedIn() {
    int port = 1;

    DriverStationFunctions mockDriverStationFunctions = mock(DriverStationFunctions.class);

    when(mockDriverStationFunctions.isJoystickConnected(port)).thenReturn(true);
    when(mockDriverStationFunctions.getStickAxisCount(port)).thenReturn(6);
    when(mockDriverStationFunctions.getStickButtonCount(port)).thenReturn(16);
    when(mockDriverStationFunctions.getStickPovCount(port)).thenReturn(1);
    when(mockDriverStationFunctions.getJoystickName(port))
        .thenReturn("Controller (Xbox One For Windows)");
    when(mockDriverStationFunctions.getJoystickType(port)).thenReturn(1);

    VerifyJoysticks verifyTemp = new VerifyJoysticks(getTestJoystickConfigs(),
        mockDriverStationFunctions, 0);

    assertTrue(verifyTemp != null);
    assertTrue(verifyTemp.getAreAllJoysticksHealth());

    verifyTemp.verifyJoysticksPeriodically();
    assertTrue(verifyTemp.getAreAllJoysticksHealth());
    assertTrue(verifyTemp.getIsJoystickConnectedCorrect(0));
  }

  @Test
  void joystickWrongAxisCountShowsAsBroken() {
    int port = 1;

    DriverStationFunctions mockDriverStationFunctions = mock(DriverStationFunctions.class);

    when(mockDriverStationFunctions.isJoystickConnected(port)).thenReturn(true);
    when(mockDriverStationFunctions.getStickAxisCount(port)).thenReturn(7); // this value is changed
    when(mockDriverStationFunctions.getStickButtonCount(port)).thenReturn(16);
    when(mockDriverStationFunctions.getStickPovCount(port)).thenReturn(1);
    when(mockDriverStationFunctions.getJoystickName(port))
        .thenReturn("Controller (Xbox One For Windows)");
    when(mockDriverStationFunctions.getJoystickType(port)).thenReturn(1);

    VerifyJoysticks verifyTemp = new VerifyJoysticks(getTestJoystickConfigs(),
        mockDriverStationFunctions, 0);

    assertTrue(verifyTemp != null);
    assertTrue(verifyTemp.getAreAllJoysticksHealth());

    verifyTemp.verifyJoysticksPeriodically();
    assertTrue(!verifyTemp.getAreAllJoysticksHealth());
    assertTrue(!verifyTemp.getIsAxisCountCorrect(0));
  }

  @Test
  void joystickWrongButtonCountShowsAsBroken() {
    int port = 1;

    DriverStationFunctions mockDriverStationFunctions = mock(DriverStationFunctions.class);

    when(mockDriverStationFunctions.isJoystickConnected(port)).thenReturn(true);
    when(mockDriverStationFunctions.getStickAxisCount(port)).thenReturn(6);
    when(mockDriverStationFunctions.getStickButtonCount(port)).thenReturn(17);
    when(mockDriverStationFunctions.getStickPovCount(port)).thenReturn(1);
    when(mockDriverStationFunctions.getJoystickName(port))
        .thenReturn("Controller (Xbox One For Windows)");
    when(mockDriverStationFunctions.getJoystickType(port)).thenReturn(1);

    VerifyJoysticks verifyTemp = new VerifyJoysticks(getTestJoystickConfigs(),
        mockDriverStationFunctions, 0);

    assertTrue(verifyTemp != null);
    assertTrue(verifyTemp.getAreAllJoysticksHealth());

    verifyTemp.verifyJoysticksPeriodically();
    assertTrue(!verifyTemp.getAreAllJoysticksHealth());
    assertTrue(!verifyTemp.getIsTestButtonCountCorrect(0));
  }

  @Test
  void joystickWrongPovCountShowsAsBroken() {
    int port = 1;

    DriverStationFunctions mockDriverStationFunctions = mock(DriverStationFunctions.class);

    when(mockDriverStationFunctions.isJoystickConnected(port)).thenReturn(true);
    when(mockDriverStationFunctions.getStickAxisCount(port)).thenReturn(6);
    when(mockDriverStationFunctions.getStickButtonCount(port)).thenReturn(16);
    when(mockDriverStationFunctions.getStickPovCount(port)).thenReturn(2); // this value is changed
    when(mockDriverStationFunctions.getJoystickName(port))
        .thenReturn("Controller (Xbox One For Windows)");
    when(mockDriverStationFunctions.getJoystickType(port)).thenReturn(1);

    VerifyJoysticks verifyTemp = new VerifyJoysticks(getTestJoystickConfigs(),
        mockDriverStationFunctions, 0);

    assertTrue(verifyTemp != null);
    assertTrue(verifyTemp.getAreAllJoysticksHealth());

    verifyTemp.verifyJoysticksPeriodically();
    assertTrue(!verifyTemp.getAreAllJoysticksHealth());
    assertTrue(!verifyTemp.getIsPovCountCorrect(0));
  }

  @Test
  void joystickWrongJoystickNameShowsAsBroken() {
    int port = 1;

    DriverStationFunctions mockDriverStationFunctions = mock(DriverStationFunctions.class);

    when(mockDriverStationFunctions.isJoystickConnected(port)).thenReturn(true);
    when(mockDriverStationFunctions.getStickAxisCount(port)).thenReturn(6);
    when(mockDriverStationFunctions.getStickButtonCount(port)).thenReturn(16);
    when(mockDriverStationFunctions.getStickPovCount(port)).thenReturn(1);
    when(mockDriverStationFunctions.getJoystickName(port)).thenReturn("Controller (For Windows)");
    when(mockDriverStationFunctions.getJoystickType(port)).thenReturn(1);

    VerifyJoysticks verifyTemp = new VerifyJoysticks(getTestJoystickConfigs(),
        mockDriverStationFunctions, 0);

    assertTrue(verifyTemp != null);
    assertTrue(verifyTemp.getAreAllJoysticksHealth());

    verifyTemp.verifyJoysticksPeriodically();
    assertTrue(!verifyTemp.getAreAllJoysticksHealth());
    assertTrue(!verifyTemp.getIsJoystickNameCorrect(0));
  }

  @Test
  void joystickWrongJoystickTypeShowsAsBroken() {
    int port = 1;

    DriverStationFunctions mockDriverStationFunctions = mock(DriverStationFunctions.class);

    when(mockDriverStationFunctions.isJoystickConnected(port)).thenReturn(true);
    when(mockDriverStationFunctions.getStickAxisCount(port)).thenReturn(6);
    when(mockDriverStationFunctions.getStickButtonCount(port)).thenReturn(16);
    when(mockDriverStationFunctions.getStickPovCount(port)).thenReturn(1);
    when(mockDriverStationFunctions.getJoystickName(port))
        .thenReturn("Controller (Xbox One For Windows)");
    when(mockDriverStationFunctions.getJoystickType(port)).thenReturn(2); // this value is changed

    VerifyJoysticks verifyTemp = new VerifyJoysticks(getTestJoystickConfigs(),
        mockDriverStationFunctions, 0);

    assertTrue(verifyTemp != null);
    assertTrue(verifyTemp.getAreAllJoysticksHealth());

    verifyTemp.verifyJoysticksPeriodically();
    assertTrue(!verifyTemp.getAreAllJoysticksHealth());
    assertTrue(!verifyTemp.getIsJoystickTypeCorrect(0));

    // For good measure, lets verify that the other settings are healthy
    assertTrue(verifyTemp.getIsJoystickConnectedCorrect(0));
    assertTrue(verifyTemp.getIsAxisCountCorrect(0));
    assertTrue(verifyTemp.getIsTestButtonCountCorrect(0));
    assertTrue(verifyTemp.getIsPovCountCorrect(0));
    assertTrue(verifyTemp.getIsJoystickNameCorrect(0));
  }

  @Test
  void testingTwoJoysticksShouldWork() {
    DriverStationFunctions mockDriverStationFunctions = mock(DriverStationFunctions.class);

    // Joystick on port 1
    when(mockDriverStationFunctions.isJoystickConnected(1)).thenReturn(true);
    when(mockDriverStationFunctions.getStickAxisCount(1)).thenReturn(6);
    when(mockDriverStationFunctions.getStickButtonCount(1)).thenReturn(16);
    when(mockDriverStationFunctions.getStickPovCount(1)).thenReturn(1);
    when(mockDriverStationFunctions.getJoystickName(1))
        .thenReturn("Controller (Xbox One For Windows)");
    when(mockDriverStationFunctions.getJoystickType(1)).thenReturn(1);

    // Joystick on port 2
    when(mockDriverStationFunctions.isJoystickConnected(2)).thenReturn(true);
    when(mockDriverStationFunctions.getStickAxisCount(2)).thenReturn(5);
    when(mockDriverStationFunctions.getStickButtonCount(2)).thenReturn(4);
    when(mockDriverStationFunctions.getStickPovCount(2)).thenReturn(8);
    when(mockDriverStationFunctions.getJoystickName(2)).thenReturn("Joystick 2");
    when(mockDriverStationFunctions.getJoystickType(2)).thenReturn(20);

    VerifyJoysticks verifyTemp = new VerifyJoysticks(getTwoTestJoysticksConfigs(),
        mockDriverStationFunctions, 0);

    assertTrue(verifyTemp != null);
    assertTrue(verifyTemp.getAreAllJoysticksHealth());

    verifyTemp.verifyJoysticksPeriodically();
    assertTrue(verifyTemp.getAreAllJoysticksHealth());

    // Verify joystick on port 1
    assertTrue(verifyTemp.getIsJoystickConnectedCorrect(0));
    assertTrue(verifyTemp.getIsAxisCountCorrect(0));
    assertTrue(verifyTemp.getIsTestButtonCountCorrect(0));
    assertTrue(verifyTemp.getIsPovCountCorrect(0));
    assertTrue(verifyTemp.getIsJoystickNameCorrect(0));
    assertTrue(verifyTemp.getIsJoystickTypeCorrect(0));

    // Verify joystick on port 2
    assertTrue(verifyTemp.getIsJoystickConnectedCorrect(1));
    assertTrue(verifyTemp.getIsAxisCountCorrect(1));
    assertTrue(verifyTemp.getIsTestButtonCountCorrect(1));
    assertTrue(verifyTemp.getIsPovCountCorrect(1));
    assertTrue(verifyTemp.getIsJoystickNameCorrect(1));
    assertTrue(verifyTemp.getIsJoystickTypeCorrect(1));
  }

  @Test
  void whenJoystickGetsUnpluggedRestOfParamsShouldShowAsHealthy() {
    int port = 1;

    DriverStationFunctions mockDriverStationFunctions = mock(DriverStationFunctions.class);

    when(mockDriverStationFunctions.isJoystickConnected(port)).thenReturn(true).thenReturn(false);
    when(mockDriverStationFunctions.getStickAxisCount(port)).thenReturn(6);
    when(mockDriverStationFunctions.getStickButtonCount(port)).thenReturn(16);
    when(mockDriverStationFunctions.getStickPovCount(port)).thenReturn(1);
    when(mockDriverStationFunctions.getJoystickName(port))
        .thenReturn("Controller (Xbox One For Windows)");
    when(mockDriverStationFunctions.getJoystickType(port)).thenReturn(2); // this value is changed

    VerifyJoysticks verifyTemp = new VerifyJoysticks(getTestJoystickConfigs(),
        mockDriverStationFunctions, 0);

    assertTrue(verifyTemp != null);
    assertTrue(verifyTemp.getAreAllJoysticksHealth());

    verifyTemp.verifyJoysticksPeriodically();

    // On the first round, JoystickType should be unhealthy
    assertTrue(!verifyTemp.getAreAllJoysticksHealth());
    assertTrue(!verifyTemp.getIsJoystickTypeCorrect(0));
    assertTrue(verifyTemp.getIsJoystickConnectedCorrect(0));
    assertTrue(verifyTemp.getIsAxisCountCorrect(0));
    assertTrue(verifyTemp.getIsTestButtonCountCorrect(0));
    assertTrue(verifyTemp.getIsPovCountCorrect(0));
    assertTrue(verifyTemp.getIsJoystickNameCorrect(0));

    verifyTemp.verifyJoysticksPeriodically();

    // Now, the joystick was unplugged. All other params should be healthy
    assertTrue(!verifyTemp.getAreAllJoysticksHealth());
    assertTrue(!verifyTemp.getIsJoystickConnectedCorrect(0));
    assertTrue(verifyTemp.getIsAxisCountCorrect(0));
    assertTrue(verifyTemp.getIsTestButtonCountCorrect(0));
    assertTrue(verifyTemp.getIsPovCountCorrect(0));
    assertTrue(verifyTemp.getIsJoystickNameCorrect(0));
    assertTrue(verifyTemp.getIsJoystickTypeCorrect(0));
  }

  @Test
  void whenElapsedTimerUsedThenQuicklyRequeringShouldGiveOldResult() {
    int port = 1;
    int timerSeconds = 60 * 60 * 24;

    DriverStationFunctions mockDriverStationFunctions = mock(DriverStationFunctions.class);

    when(mockDriverStationFunctions.isJoystickConnected(port)).thenReturn(true).thenReturn(false);
    when(mockDriverStationFunctions.getStickAxisCount(port)).thenReturn(6);
    when(mockDriverStationFunctions.getStickButtonCount(port)).thenReturn(16);
    when(mockDriverStationFunctions.getStickPovCount(port)).thenReturn(1);
    when(mockDriverStationFunctions.getJoystickName(port))
        .thenReturn("Controller (Xbox One For Windows)");
    when(mockDriverStationFunctions.getJoystickType(port)).thenReturn(2); // this value is changed

    VerifyJoysticks verifyTemp = new VerifyJoysticks(getTestJoystickConfigs(),
        mockDriverStationFunctions, timerSeconds);

    assertTrue(verifyTemp != null);
    assertTrue(verifyTemp.getAreAllJoysticksHealth());

    verifyTemp.verifyJoysticksPeriodically();

    // On the first round, JoystickType should be unhealthy
    assertTrue(!verifyTemp.getAreAllJoysticksHealth());
    assertTrue(!verifyTemp.getIsJoystickTypeCorrect(0));
    assertTrue(verifyTemp.getIsJoystickConnectedCorrect(0));
    assertTrue(verifyTemp.getIsAxisCountCorrect(0));
    assertTrue(verifyTemp.getIsTestButtonCountCorrect(0));
    assertTrue(verifyTemp.getIsPovCountCorrect(0));
    assertTrue(verifyTemp.getIsJoystickNameCorrect(0));

    verifyTemp.verifyJoysticksPeriodically();

    // Since timer is set to 24 hours, status on second query shouldnt change
    assertTrue(!verifyTemp.getAreAllJoysticksHealth());
    assertTrue(!verifyTemp.getIsJoystickTypeCorrect(0));
    assertTrue(verifyTemp.getIsJoystickConnectedCorrect(0));
    assertTrue(verifyTemp.getIsAxisCountCorrect(0));
    assertTrue(verifyTemp.getIsTestButtonCountCorrect(0));
    assertTrue(verifyTemp.getIsPovCountCorrect(0));
    assertTrue(verifyTemp.getIsJoystickNameCorrect(0));
  }
}
