package frc.robot.helpers;

/**
 * This class is used to store the configuration for a joystick. This is used
 * to ensure that the joystick is configured correctly.
 */
public class JoystickConfig {
  public int m_port;
  public int m_expectedAxisCount;
  public int m_expectedButtonCount;
  public int m_expectedPovCount;
  public String m_expectedJoystickName;
  public int m_expectedJoystickType;

  /**
   * Constructor.
   */
  public JoystickConfig(int port,
      int expectedAxisCount,
      int expectedButtonCount,
      int expectedPovCount,
      String expectedJoystickName,
      int expectedJoystickType) {

    this.m_port = port;
    this.m_expectedAxisCount = expectedAxisCount;
    this.m_expectedButtonCount = expectedButtonCount;
    this.m_expectedPovCount = expectedPovCount;
    this.m_expectedJoystickName = expectedJoystickName;
    this.m_expectedJoystickType = expectedJoystickType;
  }
}
