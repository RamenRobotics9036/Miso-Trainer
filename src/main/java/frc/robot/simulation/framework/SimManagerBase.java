package frc.robot.simulation.framework;

import edu.wpi.first.wpilibj.RobotState;
import java.util.function.Supplier;

/**
 * Partially implements SimManagerInterface.
 */
public abstract class SimManagerBase<InputT, OutputT>
    implements SimManagerInterface<InputT, OutputT> {

  private SimInputInterface<InputT> m_inputHandler = null;
  private SimOutputInterface<OutputT> m_outputHandler = null;
  private boolean m_outputInitialized = false;
  private Supplier<Boolean> m_isRobotEnabled;

  /**
   * Constructor.
   */
  public SimManagerBase(boolean enableTestMode) {
    // When the robot is in test mode, we act as if the robot is ALWAYS enabled.
    // Otherwise, we'd get odd results when unit-testing.
    if (enableTestMode) {
      m_isRobotEnabled = () -> true;
    }
    else {
      m_isRobotEnabled = () -> RobotState.isEnabled();
    }
  }

  /**
   * Optional Constructor that allows the user to specify a custom function to
   * determine if the robot is enabled.
   */
  public SimManagerBase(Supplier<Boolean> isRobotEnabledFunc) {
    this(true);

    if (isRobotEnabledFunc == null) {
      throw new IllegalArgumentException("isRobotEnabledFunc cannot be null");
    }
    m_isRobotEnabled = isRobotEnabledFunc;
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
    return m_isRobotEnabled.get();
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
