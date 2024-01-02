package frc.robot.simulation.framework;

import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.RobotState;
import frc.robot.shuffle.MultiType;
import frc.robot.shuffle.PrefixedConcurrentMap.Client;
import java.util.function.Supplier;

/**
 * Partially implements SimManagerInterface.
 */
public class SimManager<InputT, OutputT> {

  private final SimModelInterface<InputT, OutputT> m_simModelFunc;
  private final Client<Supplier<MultiType>> m_shuffleClient;
  private SimInputInterface<InputT> m_inputHandler = null;
  private SimOutputInterface<OutputT> m_outputHandler = null;
  private boolean m_outputInitialized = false;
  private Supplier<Boolean> m_isRobotEnabled;

  // $TODO - Need a few unit tests for dashboard
  // 1) That passing in null for shuffleClient is OK
  // 2) That if shuffleclient is NON-null, and the sample sim returns null DashboardItems, that the
  // shuffleboard global hashmap is properly empty
  // 3) If shuffleclient is NON-null, and the sample sim returns DashboardItems, that they properly
  // show-up in shuffleboard global hashmap

  /**
   * Constructor.
   */
  public SimManager(SimModelInterface<InputT, OutputT> simModelFunc,
      Client<Supplier<MultiType>> shuffleClient,
      boolean enableTestMode) {
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
    m_shuffleClient = shuffleClient;

    // When the robot is in test mode, we act as if the robot is ALWAYS enabled.
    // Otherwise, we'd get odd results when unit-testing.
    if (enableTestMode) {
      m_isRobotEnabled = () -> true;
    }
    else {
      m_isRobotEnabled = () -> RobotState.isEnabled();
    }

    queryAndSetDashboardItems();
  }

  /**
   * Optional Constructor that allows the user to specify a custom function to
   * determine if the robot is enabled.
   */
  public SimManager(SimModelInterface<InputT, OutputT> simModelFunc,
      Client<Supplier<MultiType>> shuffleClient,
      Supplier<Boolean> isRobotEnabledFunc) {
    this(simModelFunc, shuffleClient, true);

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

  // Add items to the global hashmap, for every dashboard parameter exposed
  // by the SimModel.
  private void queryAndSetDashboardItems() {
    // No dashboard items are added globally if shuffleClient wasnt passed into
    // constructor
    if (m_shuffleClient == null) {
      return;
    }

    DashboardItem[] dashboardItems = m_simModelFunc.getDashboardItems();

    // A particular SimModel may return null for getDashboardItems(),
    // in which case we do nothing.
    if (dashboardItems == null) {
      return;
    }

    for (DashboardItem dashboardItem : dashboardItems) {
      m_shuffleClient.addItem("Ido was here", dashboardItem.getSupplier());
    }
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
