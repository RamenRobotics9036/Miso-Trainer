package frc.robot.simulation.framework.inputoutputs;

import frc.robot.simulation.framework.SimOutputInterface;

/**
 * Copies the data OUT into an object that is passed in Constructor.
 * This makes it easy for caller, since lifetime of the object
 * is managed by the caller.
 */
public class CopySimOutput<T extends CopyableInterface<T>> implements SimOutputInterface<T> {
  private final T m_targetState;

  /**
   * Constructor.
   */
  public CopySimOutput(T targetState) {
    if (targetState == null) {
      throw new IllegalArgumentException("targetState cannot be null");
    }

    m_targetState = targetState;
  }

  @Override
  public void setOutput(T newState) {
    // Copy from output to target.
    m_targetState.copyFrom(newState);
  }
}
