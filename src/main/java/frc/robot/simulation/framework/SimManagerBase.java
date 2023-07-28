package frc.robot.simulation.framework;

import edu.wpi.first.wpilibj.RobotState;

/**
 * Partially implements SimManagerInterface.
 */
public abstract class SimManagerBase<InputT, OutputT>
    implements SimManagerInterface<InputT, OutputT> {

  private SimInputInterface<InputT> m_inputHandler = null;
  private SimOutputInterface<OutputT> m_outputHandler = null;
  private boolean m_outputInitialized = false;

  /**
   * Constructor.
   */
  public SimManagerBase() {
  }

  @Override
  public final void setInputHandler(SimInputInterface<InputT> inputHandler) {
    m_inputHandler = inputHandler;
    tryInitializeOutput();
  }

  @Override
  public final void setOutputHandler(SimOutputInterface<OutputT> outputHandler) {
    m_outputHandler = outputHandler;
    tryInitializeOutput();
  }

  private boolean isRobotEnabled() {
    return RobotState.isEnabled();
  }

  // Once the input and output handler are both setup, we want to do one run of the
  // simulation just to properly set the output. This is done even if the Robot
  // is in diabled state.
  private void tryInitializeOutput() {
    if (!m_outputInitialized && m_inputHandler != null && m_outputHandler != null) {
      doSimulationWrapper();
      m_outputInitialized = true;
    }
  }

  // Must be implemented by derived class
  protected abstract OutputT doSimulation(InputT input);

  private void doSimulationWrapper() {
    if (m_inputHandler != null && m_outputHandler != null) {
      // Step 1: Get the input from the input handler
      InputT input = m_inputHandler.getInput();

      // Step 2: Do simulation
      OutputT result = this.doSimulation(input);

      // Step 3: Write the output to the output handler
      m_outputHandler.setOutput(result);
    }
  }

  // The following method cannot be further overriden by derived class
  @Override
  public final void simulationPeriodic() {
    // When Robot is disabled, the entire simulation freezes
    if (isRobotEnabled()) {
      doSimulationWrapper();
    }
  }
}
