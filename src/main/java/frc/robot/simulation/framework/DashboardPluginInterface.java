package frc.robot.simulation.framework;

import frc.robot.shuffle.MultiType;

/**
 * Interface for Simulation Models to specify which properties to show
 * on the Shuffleboard dashboard.
 * <P>
 * The way this works is that the SimModel updates the simulation every 20ms.
 * In updateSimulation(), it takes an input of type InputT, and then
 * returns an output of type OutputT. Now, we introduce a new plugin
 * called the DashboardPlugin that will also be called every 20ms, and it
 * will have both the input and output values passed in. The plugin
 * can choose to *tap* any parts of the input/output, and show them in the
 * Shuffleboard dashboard.
 * </P>
 * <P>
 * To do this, the plugin returns a list of MultiType objects, which
 * hold the value. The SimManager will eventually put these values into
 * into a global dictionary that can be used to add widgets to
 * Shuffleboard.
 * </P>
 */
public interface DashboardPluginInterface<InputT, OutputT> {
  public String[] queryListOfDashboardProperties();

  public MultiType[] getDashboardPropertiesFromInputOutput(InputT input, OutputT output);
}
