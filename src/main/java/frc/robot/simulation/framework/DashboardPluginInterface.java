package frc.robot.simulation.framework;

import frc.robot.shuffle.MultiType;

/**
 * Interface for Simulation Models to specify which parameters to show
 * on the Shuffleboard dashboard.
 */
public interface DashboardPluginInterface<TInput, TOutput> {
  public String[] queryListOfParameters();

  public MultiType[] getDashboardParamsFromInputOutput(TInput input, TOutput output);
}
