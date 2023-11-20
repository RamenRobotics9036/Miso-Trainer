package frc.robot.helpers;

import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import java.time.Instant;
import java.util.function.Supplier;

/**
 * VerifyJoysticks class is used to verify that the joysticks are connected and have the expected
 * number of buttons, axes, etc. This is done by running a series of tests, each of which is
 * implemented as a lambda. The lambda returns null if the test passes, or an error message if the
 * test fails.
 */
public class VerifyJoysticks {
  private class JoystickTest {
    private boolean m_lastSuccess;
    private final String m_joystickTestName;
    private final Supplier<String> m_testLambda;
    private final boolean m_stopRestOfTestsIfFailed;

    // Constructor
    public JoystickTest(String joystickTestName,
        boolean stopRestOfTestsIfFailed,
        Supplier<String> testLambda) {
      m_joystickTestName = joystickTestName;
      m_testLambda = testLambda;
      m_stopRestOfTestsIfFailed = stopRestOfTestsIfFailed;
      m_lastSuccess = true;
    }

    private void printJoystickWarning(String msg) {
      // Only print if physical Robot is running, not in Simulation
      if (!RobotBase.isSimulation()) {
        System.out.println(msg);
      }
    }

    public boolean runTest(int port) {
      String errorMsg = m_testLambda.get();
      boolean newSuccess = errorMsg == null;

      // NOTE: We only print errors or successes when there's a change
      if (!newSuccess && m_lastSuccess) {
        printJoystickWarning(
            String.format("Joystick test [%s] FAILED: %s", m_joystickTestName, errorMsg));
      }
      else if (newSuccess && !m_lastSuccess) {
        printJoystickWarning(
            String.format("Joystick test [%s] passed on port=%d", m_joystickTestName, port));
      }

      m_lastSuccess = newSuccess;

      return newSuccess;
    }

    public boolean wasLastTestSuccessful() {
      return m_lastSuccess;
    }

    public boolean is_stopRestOfTestsIfFailed() {
      return m_stopRestOfTestsIfFailed;
    }

    public void setTestAsSucceeded() {
      m_lastSuccess = true;
    }
  }

  // Member variables
  private DriverStationFunctions m_driverStationFunctions;
  private boolean m_lastResult;
  private boolean m_firstCall;
  private Instant m_recordedTime = Instant.now();
  private final JoystickConfig[] m_joystickConfigs;
  private JoystickTest[][] m_tests;
  private final int m_periodSeconds;

  /**
   * Constructor.
   */
  public VerifyJoysticks(JoystickConfig[] joystickConfigs,
      DriverStationFunctions driverStationFunctions,
      int periodSeconds) {
    // Check params
    if (joystickConfigs == null) {
      throw new IllegalArgumentException("joystickConfigs cannot be null");
    }

    if (driverStationFunctions == null) {
      throw new IllegalArgumentException("driverStationFunctions cannot be null");
    }

    m_periodSeconds = periodSeconds;

    m_driverStationFunctions = driverStationFunctions;
    m_joystickConfigs = joystickConfigs;
    m_tests = createAllJoystickTests(m_joystickConfigs);

    m_firstCall = true;
    m_lastResult = true;
  }

  /**
   * Returns the default hardcoded default configuration used by the Ramen robotics Miso
   * robot.
   */
  public static JoystickConfig[] getDefaultJoystickConfigs() {
    // Hardcoded expected joystick values
    return new JoystickConfig[] {
        new JoystickConfig(1, // port
            6, // expectedAxisCount
            16, // expectedButtonCount
            1, // expectedPOVCountt
            "Controller (Xbox One For Windows)", 1), // expectedJoystickType
        new JoystickConfig(0, 6, 16, 1, "Controller (Xbox One For Windows)", 1)
    };
  }

  private JoystickTest[][] createAllJoystickTests(JoystickConfig[] joystickConfigs) {
    JoystickTest[][] allTests = new JoystickTest[joystickConfigs.length][];

    for (int i = 0; i < joystickConfigs.length; i++) {
      allTests[i] = createInitializedJoystickTest(joystickConfigs[i].m_port,
          joystickConfigs[i].m_expectedAxisCount,
          joystickConfigs[i].m_expectedButtonCount,
          joystickConfigs[i].m_expectedPovCount,
          joystickConfigs[i].m_expectedJoystickName,
          joystickConfigs[i].m_expectedJoystickType);
    }

    return allTests;
  }

  /**
   * Returns whether the last test of whether joystick is connected
   * returned the expected value.
   */
  public boolean getIsJoystickConnectedCorrect(int index) {
    if (index >= m_joystickConfigs.length) {
      throw new IllegalArgumentException("index is out of range");
    }
    return m_tests[index][0].wasLastTestSuccessful();
  }

  /**
   * Returns whether the last test of whether the joystick has the expected number of axes
   * returned the expected value.
   */
  public boolean getIsAxisCountCorrect(int index) {
    if (index >= m_joystickConfigs.length) {
      throw new IllegalArgumentException("index is out of range");
    }
    return m_tests[index][1].wasLastTestSuccessful();
  }

  /**
   * Returns whether the last test of whether the joystick has the expected number of buttons
   * returned the expected value.
   */
  public boolean getIsTestButtonCountCorrect(int index) {
    if (index >= m_joystickConfigs.length) {
      throw new IllegalArgumentException("index is out of range");
    }
    return m_tests[index][2].wasLastTestSuccessful();
  }

  /**
   * Returns whether the last test of whether the joystick has the expected number of POVs
   * returned the expected value.
   */
  public boolean getIsPovCountCorrect(int index) {
    if (index >= m_joystickConfigs.length) {
      throw new IllegalArgumentException("index is out of range");
    }
    return m_tests[index][3].wasLastTestSuccessful();
  }

  /**
   * Returns whether the last test of whether the joystick has the expected name
   * returned the expected value.
   */
  public boolean getIsJoystickNameCorrect(int index) {
    if (index >= m_joystickConfigs.length) {
      throw new IllegalArgumentException("index is out of range");
    }
    return m_tests[index][4].wasLastTestSuccessful();
  }

  /**
   * Returns whether the last test of whether the joystick has the expected type
   * returned the expected value.
   */
  public boolean getIsJoystickTypeCorrect(int index) {
    if (index >= m_joystickConfigs.length) {
      throw new IllegalArgumentException("index is out of range");
    }
    return m_tests[index][5].wasLastTestSuccessful();
  }

  public boolean getAreAllJoysticksHealth() {
    return m_lastResult;
  }

  private JoystickTest[] createInitializedJoystickTest(int port,
      int expectedAxisCount,
      int expectedButtonCount,
      int expectedPovCount,
      String expectedJoystickName,
      int expectedJoystickType) {

    JoystickTest[] tests = new JoystickTest[] {
        new JoystickTest("isJoystickConnected", true, () -> testIsJoystickConnected(port)),
        new JoystickTest("TestAxisCount", false, () -> testAxisCount(port, expectedAxisCount)),
        new JoystickTest("TestButtonCount", false,
            () -> testButtonCount(port, expectedButtonCount)),
        new JoystickTest("TestPOVCount", false, () -> testPovCount(port, expectedPovCount)),
        new JoystickTest("TestJoystickName", false,
            () -> testJoystickName(port, expectedJoystickName)),
        new JoystickTest("TestJoystickType", false,
            () -> testJoystickType(port, expectedJoystickType))
    };

    return tests;
  }

  /**
   * Call this every 20ms from the robot periodic function. Checks whether
   * the joysticks are connected and have the expected number of axes, buttons, and POVs.
   */
  public boolean verifyJoysticksPeriodically() {
    Instant currentTime = Instant.now();
    boolean allSuccess = true;

    if (m_firstCall
        || (currentTime.getEpochSecond() - m_recordedTime.getEpochSecond()) >= m_periodSeconds) {

      for (int i = 0; i < m_joystickConfigs.length; i++) {
        if (!verifySingleJoystick(m_joystickConfigs[i].m_port,
            m_joystickConfigs[i].m_expectedAxisCount,
            m_joystickConfigs[i].m_expectedButtonCount,
            m_joystickConfigs[i].m_expectedPovCount,
            m_joystickConfigs[i].m_expectedJoystickName,
            m_joystickConfigs[i].m_expectedJoystickType,
            m_tests[i])) {

          allSuccess = false;
        }
      }

      m_lastResult = allSuccess;
      m_recordedTime = currentTime;
      m_firstCall = false;

      // We only update the dashboard every few seconds
      updateDashboard();
    }

    return m_lastResult;
  }

  private void updateDashboard() {
    // $TODO - This should be in init or update DashBoard? THIS IS JUST FOR JOYSTICKS?
    SmartDashboard.putBoolean("Joystick health", m_lastResult);
  }

  private String testIsJoystickConnected(int port) {
    if (!m_driverStationFunctions.isJoystickConnected(port)) {
      return "No joystick plugged into port " + port;
    }

    return null;
  }

  private String testAxisCount(int port, int expectedAxisCount) {
    int actualAxisCount = m_driverStationFunctions.getStickAxisCount(port);
    if (expectedAxisCount != actualAxisCount) {
      return String.format("Port=%d, Expected AxisCount=%d, Actual AxisCount=%d, ",
          port,
          expectedAxisCount,
          actualAxisCount);
    }

    return null;
  }

  private String testButtonCount(int port, int expectedButtonCount) {
    int actualButtonCount = m_driverStationFunctions.getStickButtonCount(port);
    if (expectedButtonCount != actualButtonCount) {
      return String.format("Port=%d, Expected ButtonCount=%d, Actual ButtonCount=%d",
          port,
          expectedButtonCount,
          actualButtonCount);
    }

    return null;
  }

  private String testPovCount(int port, int expectedPovCount) {
    int actualPovCount = m_driverStationFunctions.getStickPovCount(port);
    if (expectedPovCount != actualPovCount) {
      return String.format("Port=%d, Expected PovCount=%d, Actual PovCount=%d",
          port,
          expectedPovCount,
          actualPovCount);
    }

    return null;
  }

  private String testJoystickName(int port, String expectedJoystickName) {
    String actualJoystickName = m_driverStationFunctions.getJoystickName(port);
    if (!expectedJoystickName.equals(actualJoystickName)) {
      return String.format("Port=%d, Expected JoystickName=%s, Actual JoystickName=%s",
          port,
          expectedJoystickName,
          actualJoystickName);
    }

    return null;
  }

  private String testJoystickType(int port, int expectedJoystickType) {
    int actualJoystickType = m_driverStationFunctions.getJoystickType(port);
    if (expectedJoystickType != actualJoystickType) {
      return String.format("Port=%d, Expected JoystickType=%d, Actual JoystickType=%d",
          port,
          expectedJoystickType,
          actualJoystickType);
    }

    return null;
  }

  private static void resetAllTestsToPassedExceptJoystickUnplugged(JoystickTest[] tests) {
    for (int i = 0; i < tests.length; i++) {
      if (!tests[i].is_stopRestOfTestsIfFailed()) {
        tests[i].setTestAsSucceeded();
      }
    }
  }

  private boolean verifySingleJoystick(int port,
      int expectedAxisCount,
      int expectedButtonCount,
      int expectedPovCount,
      String expectedJoystickName,
      int expectedJoystickType,
      JoystickTest[] tests) {

    boolean result = true;

    // Loop through the tests, and run each
    for (int i = 0; i < tests.length; i++) {
      if (!tests[i].runTest(port)) {
        result = false;

        if (tests[i].is_stopRestOfTestsIfFailed()) {
          // When the joystick is UNPLUGGED, we reset all the OTHER joystick test results
          // to PASSED. That way, when a new joystick is plugged in again,
          // all those other tests will rerun even if they were already failed on the
          // previously plugged-in Joystick. That will cause this system to print ALL
          // the unexpected configurations on the NEW joystick
          resetAllTestsToPassedExceptJoystickUnplugged(tests);
          break;
        }
      }
    }

    return result;
  }
}
