package frc.robot.simulation.framework;

/**
 * updateSimulation() is called ever 20ms.
 */
public interface SimModelInterface<InputT, OutputT> {
  DashboardItem[] getDashboardItems();

  OutputT updateSimulation(InputT input);

  boolean isModelBroken();
}
