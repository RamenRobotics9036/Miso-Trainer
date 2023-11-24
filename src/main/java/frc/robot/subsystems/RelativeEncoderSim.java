package frc.robot.subsystems;

import com.revrobotics.RelativeEncoder;
import edu.wpi.first.wpilibj.RobotBase;

/**
 * Rev robotics doesnt have a simulation class for RelativeEncoder. However,
 * a common pattern in FRC is to only set the RelativeEncoder's current position
 * by setting it on the RelativeEncoderSim object. This way, the simulation never
 * directly touches the RelativeEncoder object, which is a bit cleaner.
 */
// $TODOLATER - Is testmode even needed anymore?
public class RelativeEncoderSim {
  private boolean m_testMode;
  private double m_testModePosition;
  private RelativeEncoder m_relEncoder = null;

  /**
   * Constructor.
   */
  public RelativeEncoderSim(RelativeEncoder relEncoder) {
    // This entire class should only be instantiated when we're under simulation.
    // But just in-case someone tries to instantiate it otherwise, we do an extra check here.
    if (RobotBase.isSimulation()) {
      m_relEncoder = relEncoder;
    }

    m_testMode = false;
  }

  /**
   * Constructor for test mode.
   */
  public RelativeEncoderSim(RelativeEncoder relEncoder, boolean testMode) {
    this(relEncoder);

    m_testMode = testMode;
    m_testModePosition = 0;
  }

  /**
   * Get the current position of the encoder.
   */
  public double getPosition() {
    if (!m_testMode) {
      return m_relEncoder.getPosition();
    }
    else {
      return m_testModePosition;
    }
  }

  /**
   * Set the current position of the encoder.
   */
  public void setPosition(double position) {
    if (!m_testMode) {
      m_relEncoder.setPosition(position);
    }
    else {
      m_testModePosition = position;
    }
  }
}
