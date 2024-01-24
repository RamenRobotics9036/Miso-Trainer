package simulationlib.simulation.framework;

/**
 * Interface to set the output of a device. SimManager will call this method to set the output.
 */
public interface SimOutputInterface<T> {
  void setOutput(T output);
}
