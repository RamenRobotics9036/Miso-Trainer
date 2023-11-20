package frc.robot.helpers;

import edu.wpi.first.wpilibj.DriverStation;

/**
 * Holds methods used to query joystick information. Injected into
 * VerifyJoysticksTest.java, which is run as a unit test.
 */
public class DriverStationFunctions {
  public boolean isJoystickConnected(int port) {
    return DriverStation.isJoystickConnected(port);
  }

  public int getStickAxisCount(int port) {
    return DriverStation.getStickAxisCount(port);
  }

  public int getStickButtonCount(int port) {
    return DriverStation.getStickButtonCount(port);
  }

  public int getStickPovCount(int port) {
    return DriverStation.getStickPOVCount(port);
  }

  public String getJoystickName(int port) {
    return DriverStation.getJoystickName(port);
  }

  public int getJoystickType(int port) {
    return DriverStation.getJoystickType(port);
  }
}
