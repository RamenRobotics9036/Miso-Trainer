package frc.robot.simulation.winch;

import frc.robot.simulation.framework.SimModelInterface;

/**
 * Simulates a winch with a spool and a cable. The cable can be extended and retracted,
 * and it can be positioned either at the front or the back of a robot.
 * The simulation takes into account the diameter of the spool and the length of the cable,
 * as well as the current position of the motor. It can simulate the cable breaking
 * if it's extended or retracted beyond its total length.
 * <p>
 * Terminology:
 * Total cable length - The length of the cable in meters
 * Spool - The cylinder that the cable is wrapped around
 * Length Spooled - The amount of cable that is wrapped around the spool
 * Length Unspooled - How far the cable is extended from the spool
 * Top of spool - The cable is coming off the top of the spool
 * Bottom of spool - The cable is coming off the bottom of the spool
 * Winding orientation - Whether cable is coming off the top of the spool or the bottom
 * </p>
 */
public class WinchSimModel implements SimModelInterface<Double, WinchState> {
  /**
   * The WindingOrientation enum represents the orientation of the cable.
   * If the cable is towards the back of the robot, then we represent as BackOfRobot.
   * If the cable is towards the front of the robot, then we represent as FrontOfRobot.
   */
  public enum WindingOrientation {
    BackOfRobot, FrontOfRobot
  }

  private double m_spoolDiameterMeters;
  private WinchCable m_winchCable;
  private WinchCable m_initialWinchCable;
  private boolean m_isBroken;
  private double m_initialMotorRotations;
  private boolean m_isInitialMotorRotationsSet;
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
    if (winchParams.winchCable.getTotalLenMeters() <= 0) {
      throw new IllegalArgumentException("TotalCableLenMeters must be >0");
    }
    if (winchParams.winchCable.calcSpooledLenMeters() < 0
        || winchParams.winchCable.calcSpooledLenMeters() > winchParams.winchCable.getTotalLenMeters() {
      throw new IllegalArgumentException(
          "InitialLenSpooled must be between 0 and TotalCableLenMeters");
    }

    // Initialize fields
    m_spoolDiameterMeters = winchParams.spoolDiameterMeters;

    m_winchCable = new WinchCable(winchParams.winchCable.getTotalLenMeters(),
        winchParams.winchCable.getTotalLenMeters() - winchParams.winchCable.calcSpooledLenMeters(),
        winchParams.winchCable.getWindingOrientation());
    m_initialWinchCable = new WinchCable(m_winchCable);

    m_motorPolarity = winchParams.invertMotor ? -1 : 1;

    m_isBroken = false;
    m_initialMotorRotations = 0;
    m_isInitialMotorRotationsSet = false;
  }

  /**
   * Calculates the length of cable that is spooled, in meters.
   * If the cable is towards the front of the robot, then we represent the length
   * of cable as a POSITIVE number.
   * If the cable is towards the back of the robot, then we represent the length
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

    return new WinchCable(getTotalLenMeters(), getTotalLenMeters() - absSpooledLen,
        windingOrientation);
  }

  private double getTotalLenMeters() {
    return m_winchCable.getTotalLenMeters();
  }

  private double getCableUnspooledLen() {
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
   * Updates the current length of cable spooled. This method is called periodically
   * during simulation to update the state of the winch.
   */
  public WinchState updateSimulation(Double currentRotations) {
    WinchState winchStateResult = new WinchState(getTotalLenMeters());
    double currentRotationsWithPolarity = currentRotations * m_motorPolarity;
    double deltaRotations;

    // If the winch is broken, there's nothing to update
    if (m_isBroken) {
      winchStateResult.setCableUnspooledLen(getCableUnspooledLen());
      winchStateResult.setWindingOrientation(getWindingOrientation());
      winchStateResult.setIsBroken(true);
      return winchStateResult;
    }

    // How much has the motor turned since winch initialized?
    deltaRotations = getDeltaRotations(currentRotationsWithPolarity);

    // How much sting-length (in meters) has been spooled or unspooled?
    double deltaCableLenMeters = deltaRotations * (Math.PI * m_spoolDiameterMeters);
    double newCurrentSignedLenSpooled = calcSignedCableSpooledLen(m_initialWinchCable)
        + deltaCableLenMeters;

    // Check for bounds
    if (newCurrentSignedLenSpooled > getTotalLenMeters()) {
      newCurrentSignedLenSpooled = getTotalLenMeters();
      m_isBroken = true;
    }
    else if (newCurrentSignedLenSpooled < -1 * getTotalLenMeters()) {
      newCurrentSignedLenSpooled = -1 * getTotalLenMeters();
      m_isBroken = true;
    }

    m_winchCable = calcWinchCableFromSignedSpooledLen(newCurrentSignedLenSpooled);

    winchStateResult.setCableUnspooledLen(getCableUnspooledLen());
    winchStateResult.setWindingOrientation(getWindingOrientation());
    winchStateResult.setIsBroken(m_isBroken);

    return winchStateResult;
  }
}
