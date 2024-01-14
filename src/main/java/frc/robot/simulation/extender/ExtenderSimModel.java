package frc.robot.simulation.extender;

import frc.robot.simulation.framework.SimModelInterface;

/**
 * This class represents a simulation of an extender.
 * <p>
 * It maintains state of the extender including the motor encoder simulation, cylinder diameter,
 * total extender length, the current extended length, and whether the extender is broken or not.
 * </p>
 */
public class ExtenderSimModel implements SimModelInterface<Double, ExtenderState> {
  private double m_totalExtenderLengthMeters;
  private double m_cylinderDiameterMeters;
  private boolean m_isBroken;
  private double m_initialMotorRotations = 0;
  private double m_currentExtendedLen = 0;
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

  // $TODO - This should go away
  public double getExtendedLen() {
    return m_currentExtendedLen;
  }

  // $TODO - This should go away
  public double getExtendedPercent() {
    return getExtendedLen() / m_totalExtenderLengthMeters;
  }

  // $TODO - This should go away
  public boolean getIsBroken() {
    return m_isBroken;
  }

  private double calcExtenderLen(double newRotationsWithoutPolarity) {
    // How much has the motor turned since extender initialized?
    double newRotationsWithPolarity = newRotationsWithoutPolarity * m_motorPolarity;
    double initialRotationsWithPolarity = m_initialMotorRotations * m_motorPolarity;
    double deltaRotations = newRotationsWithPolarity - initialRotationsWithPolarity;

    double deltaLenMeters = deltaRotations * (Math.PI * m_cylinderDiameterMeters);

    return m_initialExtendedLen + deltaLenMeters;
  }

  private boolean isExtenderLenOutsideBounds(double len) {
    return len > m_totalExtenderLengthMeters || len < 0;
  }

  private double clampExtenderLen(double len) {
    if (len > m_totalExtenderLengthMeters) {
      return m_totalExtenderLengthMeters;
    }
    else if (len < 0) {
      return 0;
    }
    else {
      return len;
    }
  }

  /**
   * Updates the current extended length of the extender based on the current motor rotations.
   */
  public double updateNewExtendedLen(double newMotorRotations) {
    // Snapshot the initial motor rotations
    if (!m_initialMotorRotationsSet) {
      m_initialMotorRotations = newMotorRotations;
      m_initialMotorRotationsSet = true;
    }

    // If the extender is broken, we return the LAST valid extender len.
    // We don't recalculate it since the extender should just stop moving once broken.
    if (m_isBroken) {
      return m_currentExtendedLen;
    }

    // How much has the motor turned since extender initialized?
    double newLen = calcExtenderLen(newMotorRotations);

    // Check for bounds
    if (isExtenderLenOutsideBounds(newLen)) {
      m_isBroken = true;
      newLen = clampExtenderLen(newLen);
    }

    // Save value
    m_currentExtendedLen = newLen;

    return m_currentExtendedLen;
  }

  @Override
  public ExtenderState updateSimulation(Double inputMotorRotations) {
    double newLen = updateNewExtendedLen(inputMotorRotations);

    ExtenderState result = new ExtenderState();
    result.setExtendedLen(newLen);
    result.setExtendedPercent(newLen / m_totalExtenderLengthMeters);

    return result;
  }

  @Override
  public boolean isModelBroken() {
    return m_isBroken;
  }
}
