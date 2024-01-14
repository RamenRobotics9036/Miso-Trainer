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
  private final ExtenderParams m_extenderParams;
  private boolean m_isBroken;
  private double m_initialMotorRotations = 0;
  private double m_currentExtendedLen = 0;
  private boolean m_initialMotorRotationsSet = false;

  /**
   * Constructs a new ExtenderSimulation instance with the provided parameters.
   */
  public ExtenderSimModel(double initialMotorRotations, ExtenderParams extenderParams) {

    // Sanity checks
    if (extenderParams.cylinderDiameterMeters <= 0) {
      throw new IllegalArgumentException("CylinderDiameterMeters must be >0");
    }

    if (extenderParams.totalExtenderLengthMeters <= 0) {
      throw new IllegalArgumentException("TotalExtenderLengthMeters must be >0");
    }

    if (extenderParams.initialExtendedLen < 0) {
      throw new IllegalArgumentException("InitialExtendedLen must be >=0");
    }

    if (extenderParams.initialExtendedLen > extenderParams.totalExtenderLengthMeters) {
      throw new IllegalArgumentException("InitialExtendedLen must be <= TotalExtenderLengthMeters");
    }

    m_extenderParams = extenderParams;

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
    return getExtendedLen() / m_extenderParams.totalExtenderLengthMeters;
  }

  // $TODO - This should go away
  public boolean getIsBroken() {
    return m_isBroken;
  }

  private double calcExtenderLen(double newRotationsWithoutPolarity) {
    // How much has the motor turned since extender initialized?
    double motorPolarity = m_extenderParams.invertMotor ? -1 : 1;
    double newRotationsWithPolarity = newRotationsWithoutPolarity * motorPolarity;
    double initialRotationsWithPolarity = m_initialMotorRotations * motorPolarity;
    double deltaRotations = newRotationsWithPolarity - initialRotationsWithPolarity;

    double deltaLenMeters = deltaRotations * (Math.PI * m_extenderParams.cylinderDiameterMeters);

    return m_extenderParams.initialExtendedLen + deltaLenMeters;
  }

  private boolean isExtenderLenOutsideBounds(double len) {
    return len > m_extenderParams.totalExtenderLengthMeters || len < 0;
  }

  private double clampExtenderLen(double len) {
    if (len > m_extenderParams.totalExtenderLengthMeters) {
      return m_extenderParams.totalExtenderLengthMeters;
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
  // $TODO - This should become private
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
    result.setExtendedPercent(newLen / m_extenderParams.totalExtenderLengthMeters);

    return result;
  }

  @Override
  public boolean isModelBroken() {
    return m_isBroken;
  }
}
