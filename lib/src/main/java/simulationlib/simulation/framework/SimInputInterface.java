package simulationlib.simulation.framework;

/**
 * Interface to get the input of a device. SimManager will call this method to get the input.
 */
public interface SimInputInterface<T> {
  T getInput();
}
