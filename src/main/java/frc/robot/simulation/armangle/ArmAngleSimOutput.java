package frc.robot.simulation.armangle;

import frc.robot.simulation.framework.SimOutputInterface;

/**
 * Helper class to implement output interface.
 */
public class ArmAngleSimOutput implements SimOutputInterface<ArmAngleState> {
  private ArmAngleState m_targetArmAngleState;

  /**
   * Constructor.
   */
  public ArmAngleSimOutput(ArmAngleState targetArmAngleState) {
    if (targetArmAngleState == null) {
      throw new IllegalArgumentException("targetArmAngleState cannot be null");
    }

    m_targetArmAngleState = targetArmAngleState;
  }

  @Override
  public void setOutput(ArmAngleState newArmAngleState) {
    // Copy from output to target.
    m_targetArmAngleState.copyFrom(newArmAngleState);
  }
}
