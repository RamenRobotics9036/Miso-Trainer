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
  private final double m_totalExtenderLengthMeters;
  private final double m_cylinderDiameterMeters;
  private boolean m_isBroken;
  private double m_initialMotorRotations = 0;
  private double m_currentMotorRotations = 0;
  private boolean m_initialMotorRotationsSet = false;
  private final double m_initialExtendedLen;
  private final double m_motorPolarity;

  /**
   * Constructs a new ExtenderSimulation instance with the provided parameters.
   */
  public ExtenderSimModel(ExtenderParams params) {

    // Sanity checks
    if (params.cylinderDiameterMeters <= 0) {
      throw new IllegalArgumentException("CylinderDiameterMeters must be >0");
    }

    if (params.totalExtenderLengthMeters <= 0) {
      throw new IllegalArgumentException("TotalExtenderLengthMeters must be >0");
    }

    if (params.initialExtendedLen < 0) {
      throw new IllegalArgumentException("InitialExtendedLen must be >=0");
    }

    if (params.initialExtendedLen > params.totalExtenderLengthMeters) {
      throw new IllegalArgumentException("InitialExtendedLen must be <= TotalExtenderLengthMeters");
    }

    m_cylinderDiameterMeters = params.cylinderDiameterMeters;
    m_totalExtenderLengthMeters = params.totalExtenderLengthMeters;
    m_initialExtendedLen = params.initialExtendedLen;
    m_motorPolarity = params.invertMotor ? -1 : 1;

    m_isBroken = false;
  }

  @Override
  public boolean isModelBroken() {
    return m_isBroken;
  }

  @Override
  public ExtenderState updateSimulation(Double newMotorRotations) {
    // Snapshot the initial motor rotations
    if (!m_initialMotorRotationsSet) {
      m_initialMotorRotations = m_currentMotorRotations = newMotorRotations;
      m_initialMotorRotationsSet = true;
    }

    // If the extender is broken, there's nothing to update
    if (m_isBroken) {
      newMotorRotations = m_currentMotorRotations;
    }

    // How much has the motor turned since extender initialized?
    newMotorRotations *= m_motorPolarity;
    double deltaRotations = newMotorRotations - m_initialMotorRotations;

    double deltaLenMeters = deltaRotations * (Math.PI * m_cylinderDiameterMeters);
    double newCurrentLen = m_initialExtendedLen + deltaLenMeters;

    // Check for bounds
    double minExtendLength = 0;
    if (newCurrentLen > m_totalExtenderLengthMeters) {
      newCurrentLen = m_totalExtenderLengthMeters;
      m_isBroken = true;
    }
    else if (newCurrentLen < minExtendLength) {
      newCurrentLen = minExtendLength;
      m_isBroken = true;
    }

    m_currentMotorRotations = newMotorRotations;

    ExtenderState extenderStateResult = new ExtenderState();
    extenderStateResult.setExtendedLen(newCurrentLen);
    extenderStateResult.setExtendedPercent(newCurrentLen / m_totalExtenderLengthMeters);

    return extenderStateResult;
  }
}
