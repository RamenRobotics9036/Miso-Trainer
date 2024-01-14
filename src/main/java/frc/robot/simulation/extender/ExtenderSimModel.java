package frc.robot.simulation.extender;

/**
 * This class represents a simulation of an extender.
 * <p>
 * It maintains state of the extender including the motor encoder simulation, cylinder diameter,
 * total extender length, the current extended length, and whether the extender is broken or not.
 * </p>
 */
public class ExtenderSimModel {
  private double m_totalExtenderLengthMeters;
  private double m_minExtendLength = 0;
  private double m_cylinderDiameterMeters;
  private double m_currentExtendedLen; // $TODO - Get rid of this
  private boolean m_isBroken;
  private double m_currentMotorRotations = 0;
  private double m_initialMotorRotations = 0;
  private boolean m_initialMotorRotationsSet = false;
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
  public ExtenderSimModel(double initialMotorRotations,
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
  public double updateNewExtendedLen(double newMotorRotations) {
    // Snapshot the initial motor rotations
    if (!m_initialMotorRotationsSet) {
      m_currentMotorRotations = m_initialMotorRotations = newMotorRotations;
      m_initialMotorRotationsSet = true;
    }

    // If the extender is broken, there's nothing to update
    if (m_isBroken) {
      newMotorRotations = m_currentMotorRotations;
    }

    // Save value
    m_currentMotorRotations = newMotorRotations;

    // How much has the motor turned since extender initialized?
    double newRotationsWithPolarity = newMotorRotations * m_motorPolarity;
    double initialRotationsWithPolarity = m_initialMotorRotations * m_motorPolarity;
    double deltaRotations = newRotationsWithPolarity - initialRotationsWithPolarity;

    double deltaLenMeters = deltaRotations * (Math.PI * m_cylinderDiameterMeters);
    double newLen = m_initialExtendedLen + deltaLenMeters;

    // Check for bounds
    if (newLen > m_totalExtenderLengthMeters) {
      newLen = m_totalExtenderLengthMeters;
      m_isBroken = true;
    }
    else if (newLen < m_minExtendLength) {
      newLen = m_minExtendLength;
      m_isBroken = true;
    }

    m_currentExtendedLen = newLen;

    return m_currentExtendedLen;
  }
}
