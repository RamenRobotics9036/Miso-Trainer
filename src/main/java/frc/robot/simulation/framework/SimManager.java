package frc.robot.simulation.framework;

import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.RobotState;
import java.util.function.Supplier;

/**
 * Partially implements SimManagerInterface.
 */
public class SimManager<InputT, OutputT> {

  private SimModelInterface<InputT, OutputT> m_simModelFunc;
  private SimInputInterface<InputT> m_inputHandler = null;
  private SimOutputInterface<OutputT> m_outputHandler = null;
  private boolean m_outputInitialized = false;
  private Supplier<Boolean> m_isRobotEnabled;

  /**
   * Constructor.
   */
  public SimManager(SimModelInterface<InputT, OutputT> simModelFunc, boolean enableTestMode) {
    if (simModelFunc == null) {
      throw new IllegalArgumentException("simModelFunc cannot be null");
    }

    // This entire class should only be instantiated when we're under simulation.
    // But just in-case someone tries to instantiate it otherwise, we do an extra
    // check here.
    if (!RobotBase.isSimulation()) {
      throw new IllegalStateException("SimManager should only be instantiated when in simulation");
    }

    m_simModelFunc = simModelFunc;

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
  public SimManager(SimModelInterface<InputT, OutputT> simModelFunc,
      Supplier<Boolean> isRobotEnabledFunc) {
    this(simModelFunc, true);

    if (isRobotEnabledFunc == null) {
      throw new IllegalArgumentException("isRobotEnabledFunc cannot be null");
    }
    m_isRobotEnabled = isRobotEnabledFunc;
  }

  public void setInputHandler(SimInputInterface<InputT> inputHandler) {
    m_inputHandler = inputHandler;
    tryInitializeOutput();
  }

  public void setOutputHandler(SimOutputInterface<OutputT> outputHandler) {
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

  private void doSimulationWrapper() {
    if (m_inputHandler != null && m_outputHandler != null) {
      // Step 1: Get the input from the input handler
      InputT input = m_inputHandler.getInput();

      // Step 2: Do simulation
      OutputT result = m_simModelFunc.updateSimulation(input);

      // Step 3: Write the output to the output handler
      m_outputHandler.setOutput(result);
    }
  }

  // This returns the isBroken state from the SimModel
  public boolean isBroken() {
    return m_simModelFunc.isModelBroken();
  }

  /**
   * Called every 20ms.
   */
  public void simulationPeriodic() {
    // Yet another safety check just to be sure
    if (!RobotBase.isSimulation()) {
      throw new IllegalStateException("SimManager should only be instantiated when in simulation");
    }

    // When Robot is disabled, the entire simulation freezes
    if (isRobotEnabled()) {
      doSimulationWrapper();
    }
  }
}
