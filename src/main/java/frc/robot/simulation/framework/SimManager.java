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
  private final DashboardPluginInterface<InputT, OutputT> m_dashboardPlugin;
  private MultiType[] m_dashboardMultiTypeStorage = null;
  private MultiType[] m_defaultDashStorage = null;
  private boolean m_pluginDashItemsInitialized = false;
  private SimInputInterface<InputT> m_inputHandler = null;
  private SimOutputInterface<OutputT> m_outputHandler = null;
  private boolean m_outputInitialized = false;
  private Supplier<Boolean> m_isRobotEnabled;

  /**
   * Constructor.
   */
  public SimManager(SimModelInterface<InputT, OutputT> simModelFunc,
      Client<Supplier<MultiType>> shuffleClient,
      DashboardPluginInterface<InputT, OutputT> dashboardPlugin,
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
    m_dashboardPlugin = dashboardPlugin;

    // When the robot is in test mode, we act as if the robot is ALWAYS enabled.
    // Otherwise, we'd get odd results when unit-testing.
    if (enableTestMode) {
      m_isRobotEnabled = () -> true;
    }
    else {
      m_isRobotEnabled = () -> RobotState.isEnabled();
    }

    // No dashboard items are added globally if shuffleClient wasnt passed into
    // constructor
    if (m_shuffleClient != null) {
      DashboardItem[] dashboardItems = getListOfDashboardPropertiesFromPlugin();
      if (dashboardItems != null) {
        m_dashboardMultiTypeStorage = allocateDashboardMultiTypes(dashboardItems);
        addPropertiesToGlobalHashMap(dashboardItems, m_dashboardMultiTypeStorage);
        m_pluginDashItemsInitialized = true;
      }
      addDefaultDashboardItems();
    }
  }

  /**
   * Optional Constructor that allows the user to specify a custom function to
   * determine if the robot is enabled.
   */
  public SimManager(SimModelInterface<InputT, OutputT> simModelFunc,
      Client<Supplier<MultiType>> shuffleClient,
      DashboardPluginInterface<InputT, OutputT> dashboardPlugin,
      Supplier<Boolean> isRobotEnabledFunc) {
    this(simModelFunc, shuffleClient, dashboardPlugin, true);

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

  private Boolean checkIfDashboardItemsValid(DashboardItem[] dashboardItems) {
    if (dashboardItems == null) {
      return false;
    }

    for (DashboardItem dashboardItem : dashboardItems) {
      if (dashboardItem == null) {
        return false;
      }
    }

    return true;
  }

  private MultiType[] allocateDashboardMultiTypes(DashboardItem[] dashboardItems) {
    if (dashboardItems == null || dashboardItems.length == 0) {
      throw new IllegalArgumentException("dashboardItems cannot be null or empty");
    }

    // Allocate new array with the same len as dashboardItems,
    // and copy the MultiType objects from dashboardItems into it
    MultiType[] result = new MultiType[dashboardItems.length];
    for (int i = 0; i < dashboardItems.length; i++) {
      result[i] = dashboardItems[i].getValue();
    }

    return result;
  }

  private void addPropertiesToGlobalHashMap(DashboardItem[] dashboardItems,
      MultiType[] multiTypes) {
    if (dashboardItems == null || dashboardItems.length == 0) {
      throw new IllegalArgumentException("dashboardItems cannot be null or empty");
    }

    if (multiTypes == null || multiTypes.length == 0) {
      throw new IllegalArgumentException("multiTypes cannot be null or empty");
    }

    if (dashboardItems.length != multiTypes.length) {
      throw new IllegalArgumentException("dashboardItems and multiTypes must have the same length");
    }

    for (int i = 0; i < dashboardItems.length; i++) {
      MultiType multiType = multiTypes[i];
      m_shuffleClient.addItem(dashboardItems[i].getPropertyName(), () -> multiType);
    }
  }

  private void addDefaultDashboardItems() {
    // We store one default value: IsBroken
    m_defaultDashStorage = new MultiType[] {
        MultiType.of(false)
    };

    DashboardItem[] defaultDashItems = new DashboardItem[] {
        new DashboardItem("IsBroken", MultiType.of(false))
    };

    addPropertiesToGlobalHashMap(defaultDashItems, m_defaultDashStorage);
  }

  private DashboardItem[] getListOfDashboardPropertiesFromPlugin() {
    // If no dashboard plugin was passed into the constructor, then we do nothing
    if (m_dashboardPlugin == null) {
      return null;
    }

    // Query the dashboard plugin for the list of properties it wants to display
    DashboardItem[] result = m_dashboardPlugin.queryListOfDashboardPropertiesWithInitValues();
    if (!checkIfDashboardItemsValid(result)) {
      System.out.println("WARNING: Dashboard plugin returned null or invalid list of properties");
      return null;
    }

    return result;
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

  private void updateDashboardValues(MultiType[] newValues) {
    if (newValues == null) {
      System.out.println("WARNING: getDashboardPropertiesFromInputOutput() return null");
      return;
    }

    // The length of newValues should be the same as the length of the
    // m_dashboardMultiTypeStorage array
    if (newValues.length != m_dashboardMultiTypeStorage.length) {
      System.out.println("WARNING: getDashboardPropertiesFromInputOutput() returned array of "
          + "different length than expected");
      return;
    }

    // Copy the new values into the m_dashboardMultiTypeStorage array
    for (int i = 0; i < newValues.length; i++) {
      // We copy into the existing allocated MultiType, so that the lambda that
      // was passed to Shuffleboard continues to work
      newValues[i].copyTo(m_dashboardMultiTypeStorage[i]);
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

      // Step 4: Update the dashboard
      if (m_pluginDashItemsInitialized) {
        updateDashboardValues(
            m_dashboardPlugin.getDashboardPropertiesFromInputOutput(input, result));
      }
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
