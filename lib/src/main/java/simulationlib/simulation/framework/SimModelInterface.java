package simulationlib.simulation.framework;

/**
 * updateSimulation() is called ever 20ms.
 */
public interface SimModelInterface<InputT, OutputT> {
  OutputT updateSimulation(InputT input);

  boolean isModelBroken();
}
