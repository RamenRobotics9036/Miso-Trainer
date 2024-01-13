package frc.robot.simulation.winch.extender;

/**
 * This class represents a simulation of an extender.
 * <p>
 * It maintains state of the extender including the motor encoder simulation, cylinder diameter,
 * total extender length, the current extended length, and whether the extender is broken or not.
 * </p>
 */
public class ExtenderSimulation {
  private double m_totalExtenderLengthMeters = 0.5;
  private double m_minExtendLength = 0;
  private double m_cylinderDiameterMeters;
  private double m_currentExtendedLen;
  private boolean m_isBroken;
  private double m_initialMotorRotations;
  private double m_initialExtendedLen;
  private double m_motorPolarity;

  /**
   * Constructs a new ExtenderSimulation instance with the provided parameters.
   *
   * @param initialMotorRotations     The initial encoder position for motor.
   * @param cylinderDiameterMeters    The diameter of the cylinder in meters.
   * @param totalExtenderLengthMeters The total length of the extender in meters.
   * @param initialExtendedLen        The initial length of the extender.
   * @param invertMotor               Whether the motor should be inverted.
   *
   * @throws IllegalArgumentException If any input parameter does not meet the requirements.
   */
  public ExtenderSimulation(double initialMotorRotations,
      double cylinderDiameterMeters,
      double totalExtenderLengthMeters,
      double initialExtendedLen,
      boolean invertMotor) {

    // Sanity checks
    if (cylinderDiameterMeters <= 0) {
      throw new IllegalArgumentException("CylinderDiameterMeters must be >0");
    }

    if (totalExtenderLengthMeters <= 0) {
      throw new IllegalArgumentException("TotalExtenderLengthMeters must be >0");
    }

    if (initialExtendedLen < 0) {
      throw new IllegalArgumentException("InitialExtendedLen must be >=0");
    }

    if (initialExtendedLen > totalExtenderLengthMeters) {
      throw new IllegalArgumentException("InitialExtendedLen must be <= TotalExtenderLengthMeters");
    }

    m_cylinderDiameterMeters = cylinderDiameterMeters;
    m_totalExtenderLengthMeters = totalExtenderLengthMeters;
    m_initialExtendedLen = initialExtendedLen;
    m_motorPolarity = invertMotor ? -1 : 1;

    m_isBroken = false;

    // Take a snapshot of current DCMotor position
    m_initialMotorRotations = initialMotorRotations;

    // Call this to initialize m_currentExtendedLen
    updateNewExtendedLen(initialMotorRotations);
  }

  public double getExtendedLen() {
    return m_currentExtendedLen;
  }

  public double getExtendedPercent() {
    return getExtendedLen() / m_totalExtenderLengthMeters;
  }

  public boolean getIsBroken() {
    return m_isBroken;
  }

  /**
   * Updates the current extended length of the extender based on the current motor rotations.
   */
  public double updateNewExtendedLen(double currentRotations) {
    // If the extender is broken, there's nothing to update
    if (m_isBroken) {
      return m_currentExtendedLen;
    }

    // How much has the motor turned since extender initialized?
    double currentRotationsWithPolarity = currentRotations * m_motorPolarity;
    double deltaRotations = currentRotationsWithPolarity - m_initialMotorRotations;

    double deltaLenMeters = deltaRotations * (Math.PI * m_cylinderDiameterMeters);
    double newCurrentLen = m_initialExtendedLen + deltaLenMeters;

    // Check for bounds
    if (newCurrentLen > m_totalExtenderLengthMeters) {
      newCurrentLen = m_totalExtenderLengthMeters;
      m_isBroken = true;
    }
    else if (newCurrentLen < m_minExtendLength) {
      newCurrentLen = m_minExtendLength;
      m_isBroken = true;
    }

    m_currentExtendedLen = newCurrentLen;

    return m_currentExtendedLen;
  }
}
