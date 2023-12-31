package frc.robot.simulation.winch;

import frc.robot.simulation.framework.SimModelInterface;

/**
 * Simulates a winch with a spool and a string. The string can be extended and retracted,
 * and it can be positioned either at the front or the back of a robot.
 * The simulation takes into account the diameter of the spool and the length of the string,
 * as well as the current position of the motor. It can simulate the string breaking
 * if it's extended or retracted beyond its total length.
 * <p>
 * Terminology:
 * Total string length - The length of the string in meters
 * Spool - The cylinder that the string is wrapped around
 * Length Spooled - The amount of string that is wrapped around the spool
 * Length Unspooled - How far the string is extended from the spool
 * Top of spool - The string is coming off the top of the spool
 * Bottom of spool - The string is coming off the bottom of the spool
 * Winding orientation - Whether string is coming off the top of the spool or the bottom
 * </p>
 */
public class WinchSimModel implements SimModelInterface<Double, WinchState> {
  /**
   * The WindingOrientation enum represents the orientation of the string.
   * If the string is towards the back of the robot, then we represent as BackOfRobot.
   * If the string is towards the front of the robot, then we represent as FrontOfRobot.
   */
  public enum WindingOrientation {
    BackOfRobot, FrontOfRobot
  }

  private double m_spoolDiameterMeters;
  private WinchCable m_winchCable;
  private WinchCable m_initialWinchCable;
  // $TODO - Remove m_totalStringLenMeters and m_currentLenSpooled
  private double m_totalStringLenMeters;
  private double m_currentSignedLenSpooled;
  private boolean m_isBroken;
  private double m_initialMotorRotations;
  private boolean m_isInitialMotorRotationsSet;
  private double m_initialSignedLenSpooled;
  private double m_motorPolarity;

  /**
   * Constructs a new WinchSimulation.
   *
   */
  public WinchSimModel(WinchParams winchParams) {

    // Sanity checks
    if (winchParams.spoolDiameterMeters <= 0) {
      throw new IllegalArgumentException("SpoolDiameterMeters must be >0");
    }
    if (winchParams.totalStringLenMeters <= 0) {
      throw new IllegalArgumentException("TotalStringLenMeters must be >0");
    }
    if (winchParams.initialLenSpooled < 0
        || winchParams.initialLenSpooled > winchParams.totalStringLenMeters) {
      throw new IllegalArgumentException(
          "InitialLenSpooled must be between 0 and TotalStringLenMeters");
    }

    // Initialize fields
    m_spoolDiameterMeters = winchParams.spoolDiameterMeters;
    m_totalStringLenMeters = winchParams.totalStringLenMeters;

    // $TODO - winchParams should pass a WinchCable object instead of the string length
    m_winchCable = new WinchCable(winchParams.totalStringLenMeters,
        winchParams.totalStringLenMeters - winchParams.initialLenSpooled,
        winchParams.initialWindingOrientation);
    m_initialWinchCable = new WinchCable(m_winchCable);

    m_motorPolarity = winchParams.invertMotor ? -1 : 1;

    m_initialSignedLenSpooled = calcSignedCableSpooledLen(m_initialWinchCable);
    m_currentSignedLenSpooled = m_initialSignedLenSpooled;
    m_isBroken = false;
    m_initialMotorRotations = 0;
    m_isInitialMotorRotationsSet = false;
  }

  /**
   * Calculates the length of cable that is spooled, in meters.
   * If the string is towards the front of the robot, then we represent the length
   * of cable as a POSITIVE number.
   * If the string is towards the back of the robot, then we represent the length
   * of cable as a NEGATIVE number.
   */
  private double calcSignedCableSpooledLen(WinchCable winchCable) {
    return (winchCable.getWindingOrientation() == WindingOrientation.BackOfRobot)
        ? -1 * winchCable.calcSpooledLenMeters()
        : winchCable.calcSpooledLenMeters();
  }

  private WinchCable calcWinchCableFromSignedSpooledLen(double signedSpooledLen) {
    WindingOrientation windingOrientation = (signedSpooledLen <= 0) ? WindingOrientation.BackOfRobot
        : WindingOrientation.FrontOfRobot;
    double absSpooledLen = Math.abs(signedSpooledLen);

    return new WinchCable(m_totalStringLenMeters, m_totalStringLenMeters - absSpooledLen,
        windingOrientation);
  }

  private double getStringUnspooledLen() {
    return m_winchCable.getUnspooledLenMeters();
  }

  private WindingOrientation getWindingOrientation() {
    return m_winchCable.getWindingOrientation();
  }

  private double getDeltaRotations(double currentRotationsWithPolarity) {
    if (!m_isInitialMotorRotationsSet) {
      m_initialMotorRotations = currentRotationsWithPolarity;
      m_isInitialMotorRotationsSet = true;
    }

    return currentRotationsWithPolarity - m_initialMotorRotations;
  }

  public boolean isModelBroken() {
    return m_isBroken;
  }

  /**
   * Updates the current length of string spooled. This method is called periodically
   * during simulation to update the state of the winch.
   */
  public WinchState updateSimulation(Double currentRotations) {
    WinchState winchStateResult = new WinchState(m_totalStringLenMeters);
    double currentRotationsWithPolarity = currentRotations * m_motorPolarity;
    double deltaRotations;

    // If the winch is broken, there's nothing to update
    if (m_isBroken) {
      winchStateResult.setStringUnspooledLen(getStringUnspooledLen());
      winchStateResult.setWindingOrientation(getWindingOrientation());
      winchStateResult.setIsBroken(true);
      return winchStateResult;
    }

    // How much has the motor turned since winch initialized?
    deltaRotations = getDeltaRotations(currentRotationsWithPolarity);

    // How much sting-length (in meters) has been spooled or unspooled?
    double deltaStringLenMeters = deltaRotations * (Math.PI * m_spoolDiameterMeters);
    double newCurrentSignedLenSpooled = m_initialSignedLenSpooled + deltaStringLenMeters;

    // Check for bounds
    if (newCurrentSignedLenSpooled > m_totalStringLenMeters) {
      newCurrentSignedLenSpooled = m_totalStringLenMeters;
      m_isBroken = true;
    }
    else if (newCurrentSignedLenSpooled < -1 * m_totalStringLenMeters) {
      newCurrentSignedLenSpooled = -1 * m_totalStringLenMeters;
      m_isBroken = true;
    }

    m_currentSignedLenSpooled = newCurrentSignedLenSpooled;
    m_winchCable = calcWinchCableFromSignedSpooledLen(m_currentSignedLenSpooled);

    winchStateResult.setStringUnspooledLen(getStringUnspooledLen());
    winchStateResult.setWindingOrientation(getWindingOrientation());
    winchStateResult.setIsBroken(m_isBroken);

    return winchStateResult;
  }
}
