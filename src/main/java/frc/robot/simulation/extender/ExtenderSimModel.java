package frc.robot.simulation.extender;

import edu.wpi.first.math.Pair;
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

    // Save value
    m_currentMotorRotations = newMotorRotations;

    double newMotorRotationsWithPolarity = newMotorRotations * m_motorPolarity;

    // How much has the motor turned since extender initialized?
    double deltaRotations = newMotorRotationsWithPolarity - m_initialMotorRotations;

    double deltaLenMeters = deltaRotations * (Math.PI * m_cylinderDiameterMeters);
    if (isExtenderAboutToBreak(m_initialExtendedLen + deltaLenMeters)) {
      m_isBroken = true;
    }

    double newExtenderLen = getExtenderLen(m_initialExtendedLen + deltaLenMeters);
    ExtenderState extenderStateResult = new ExtenderState();
    extenderStateResult.setExtendedLen(newExtenderLen);
    extenderStateResult.setExtendedPercent(newExtenderLen / m_totalExtenderLengthMeters);

    return extenderStateResult;
  }

  private double getExtenderLen(double lenMeters) {
    return getExtenderLenAndStatus(lenMeters).getFirst();
  }

  private boolean isExtenderAboutToBreak(double lenMeters) {
    return getExtenderLenAndStatus(lenMeters).getSecond();
  }

  private Pair<Double, Boolean> getExtenderLenAndStatus(double lenMeters) {
    Boolean aboutToBreak = false;

    // Check for bounds
    double minExtendLength = 0;
    if (lenMeters > m_totalExtenderLengthMeters) {
      lenMeters = m_totalExtenderLengthMeters;
      aboutToBreak = true;
    }
    else if (lenMeters < minExtendLength) {
      lenMeters = minExtendLength;
      aboutToBreak = true;
    }
    return new Pair<Double, Boolean>(lenMeters, aboutToBreak);
  }
}
