package frc.robot.simulation.winch;

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
public class WinchSimModel {
  /**
   * The WindingOrientation enum represents the orientation of the string.
   * If the string is towards the back of the robot, then we represent as BackOfRobot.
   * If the string is towards the front of the robot, then we represent as FrontOfRobot.
   */
  public enum WindingOrientation {
    BackOfRobot, FrontOfRobot
  }

  private double m_spoolDiameterMeters;
  private double m_totalStringLenMeters;
  private double m_currentLenSpooled;
  private boolean m_isBroken;
  private double m_initialMotorRotations;
  private boolean m_isInitialMotorRotationsSet;
  private double m_initialLenSpooled;
  private double m_motorPolarity;

  /**
   * Constructs a new WinchSimulation.
   *
   * @param spoolDiameterMeters       the diameter of the spool, in meters
   * @param totalStringLenMeters      the total length of the string, in meters
   * @param initialLenSpooled         the initial length of string spooled, in meters
   * @param initialWindingOrientation the initial winding orientation of the string
   * @param invertMotor               whether to invert the motor (true for inverted, false for not)
   * @throws IllegalArgumentException if any of the parameters are invalid
   */
  public WinchSimModel(double spoolDiameterMeters,
      double totalStringLenMeters,
      double initialLenSpooled,
      WindingOrientation initialWindingOrientation,
      boolean invertMotor) {

    // Sanity checks
    if (spoolDiameterMeters <= 0) {
      throw new IllegalArgumentException("SpoolDiameterMeters must be >0");
    }
    if (totalStringLenMeters <= 0) {
      throw new IllegalArgumentException("TotalStringLenMeters must be >0");
    }
    if (initialLenSpooled < 0 || initialLenSpooled > totalStringLenMeters) {
      throw new IllegalArgumentException(
          "InitialLenSpooled must be between 0 and TotalStringLenMeters");
    }

    // Initialize fields
    m_spoolDiameterMeters = spoolDiameterMeters;
    m_totalStringLenMeters = totalStringLenMeters;
    m_motorPolarity = invertMotor ? -1 : 1;

    // If the string is towards the back of the robot, then we represent the length
    // of string pooled as a NEGATIVE number
    // See diagram above
    m_initialLenSpooled = (initialWindingOrientation == WindingOrientation.BackOfRobot)
        ? -1 * initialLenSpooled
        : initialLenSpooled;
    m_currentLenSpooled = m_initialLenSpooled;
    m_isBroken = false;
    m_initialMotorRotations = 0;
    m_isInitialMotorRotationsSet = false;
  }

  public double getTotalStringLenMeters() {
    return m_totalStringLenMeters;
  }

  /**
   * Returns the length of string that is currently unspooled.
   *
   * @return the length of string that is currently unspooled, in meters
   */
  // $LATER All these getters can go away
  public double getStringUnspooledLen() {
    return m_totalStringLenMeters - Math.abs(m_currentLenSpooled);
  }

  /**
   * Returns the proportion of string that is currently unspooled.
   *
   * @return the proportion of string that is currently unspooled, as a decimal
   */
  public double getStringUnspooledPercent() {
    return getStringUnspooledLen() / m_totalStringLenMeters;
  }

  /**
   * Returns the current winding orientation.
   *
   * @return the current winding orientation
   */
  public WindingOrientation getWindingOrientation() {
    // We define 0 as string orientation: back
    return (m_currentLenSpooled <= 0) ? WindingOrientation.BackOfRobot
        : WindingOrientation.FrontOfRobot;
  }

  /**
   * Returns the name of the current winding orientation.
   *
   * @return the name of the current winding orientation
   */
  public String getWindingOrientationName() {
    return getWindingOrientation().name();
  }

  /**
   * Returns whether the string is currently broken.
   *
   * @return true if the string is currently broken, false otherwise
   */
  public boolean getIsBroken() {
    return m_isBroken;
  }

  private double getDeltaRotations(double currentRotationsWithPolarity) {
    if (!m_isInitialMotorRotationsSet) {
      m_initialMotorRotations = currentRotationsWithPolarity;
      m_isInitialMotorRotationsSet = true;
    }

    return currentRotationsWithPolarity - m_initialMotorRotations;
  }

  /**
   * Updates the current length of string spooled. This method is called periodically
   * during simulation to update the state of the winch.
   */
  public void updateNewLenSpooled(double currentRotations) {
    double currentRotationsWithPolarity = currentRotations * m_motorPolarity;
    double deltaRotations;

    // If the winch is broken, there's nothing to update
    if (m_isBroken) {
      return;
    }

    // How much has the motor turned since winch initialized?
    deltaRotations = getDeltaRotations(currentRotationsWithPolarity);

    // How much sting-length (in meters) has been spooled or unspooled?
    double deltaStringLenMeters = deltaRotations * (Math.PI * m_spoolDiameterMeters);
    double newCurrentLenSpooled = m_initialLenSpooled + deltaStringLenMeters;

    // Check for bounds
    if (newCurrentLenSpooled > m_totalStringLenMeters) {
      newCurrentLenSpooled = m_totalStringLenMeters;
      m_isBroken = true;
    }
    else if (newCurrentLenSpooled < -1 * m_totalStringLenMeters) {
      newCurrentLenSpooled = -1 * m_totalStringLenMeters;
      m_isBroken = true;
    }

    m_currentLenSpooled = newCurrentLenSpooled;
  }
}
