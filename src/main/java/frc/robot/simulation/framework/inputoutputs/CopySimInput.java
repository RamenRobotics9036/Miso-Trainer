package frc.robot.simulation.framework.inputoutputs;

import frc.robot.simulation.framework.SimInputInterface;

/**
 * Helper class to implement input interface.
 * Note that the SimManagers gets a reference to the input value, NOT a copy.
 * This isnt ideal: It means SimManager needs to be careful not to update the
 * input value.
 */
public class CopySimInput<T> implements SimInputInterface<T> {
  private final T m_inputValue;

  /**
   * Constructor.
   */
  public CopySimInput(T inputValue) {
    if (inputValue == null) {
      throw new IllegalArgumentException("inputValue cannot be null");
    }

    m_inputValue = inputValue;
  }

  @Override
  public T getInput() {
    return m_inputValue;
  }
}
