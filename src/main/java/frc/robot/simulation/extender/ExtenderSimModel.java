package frc.robot.simulation.extender;

import frc.robot.helpers.RelativeEncoderSim;
import frc.robot.simulation.framework.SimModelInterface;

/**
 * This class represents a simulation of an extender.
 * <p>
 * It maintains state of the extender including the motor encoder simulation, cylinder diameter,
 * total extender length, the current extended length, and whether the extender is broken or not.
 * </p>
 */
public class ExtenderSimModel implements SimModelInterface<Double, ExtenderState> {
  // $TODO - Remove motorEncoderSim
  private RelativeEncoderSim m_motorEncoderSim;
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
   * @param motorEncoderSim           The encoder simulation for the motor. $TODO goes away
   * @param cylinderDiameterMeters    The diameter of the cylinder in meters.
   * @param totalExtenderLengthMeters The total length of the extender in meters.
   * @param initialExtendedLen        The initial length of the extender.
   * @param invertMotor               Whether the motor should be inverted.
   *
   * @throws IllegalArgumentException If any input parameter does not meet the requirements.
   */
  // $TODO - Remove motorEncoderSim
  public ExtenderSimModel(RelativeEncoderSim motorEncoderSim,
      double cylinderDiameterMeters,
      double totalExtenderLengthMeters,
      double initialExtendedLen,
      boolean invertMotor) {

    // Sanity checks
    if (motorEncoderSim == null) {
      throw new IllegalArgumentException("motorEncoderSim is null");
    }

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

    m_motorEncoderSim = motorEncoderSim;
    m_cylinderDiameterMeters = cylinderDiameterMeters;
    m_totalExtenderLengthMeters = totalExtenderLengthMeters;
    m_initialExtendedLen = initialExtendedLen;
    m_motorPolarity = invertMotor ? -1 : 1;

    m_isBroken = false;

    // Take a snapshot of current DCMotor position
    m_initialMotorRotations = m_motorEncoderSim.getPosition();

    // $TODO - Shouldnt need to call this - Call this to initialize m_currentExtendedLen
    m_currentExtendedLen = updateNewExtendedLen(m_motorEncoderSim.getPosition());
  }

  // $TODO - Not needed?
  public double getExtendedLen() {
    return m_currentExtendedLen;
  }

  // $TODO - Not needed?
  public double getExtendedPercent() {
    return getExtendedLen() / m_totalExtenderLengthMeters;
  }

  // $TODO - This can go away
  public boolean getIsBroken() {
    return m_isBroken;
  }

  @Override
  public boolean isModelBroken() {
    return getIsBroken();
  }

  private double updateNewExtendedLen(double currentRotations) {
    // If the extender is broken, there's nothing to update
    if (m_isBroken) {
      return m_currentExtendedLen;
    }

    // How much has the motor turned since extender initialized?
    currentRotations *= m_motorPolarity;
    double deltaRotations = currentRotations - m_initialMotorRotations;

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

    return newCurrentLen;
  }

  // $TODO - This goes away
  public void simulationPeriodic() {
    m_currentExtendedLen = updateNewExtendedLen(m_motorEncoderSim.getPosition());
  }

  @Override
  public ExtenderState updateSimulation(Double currentMotorRotations) {
    throw new UnsupportedOperationException("Not implemented");
  }
}
