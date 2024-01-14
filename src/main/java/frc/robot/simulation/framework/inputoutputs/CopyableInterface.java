package frc.robot.simulation.framework.inputoutputs;

/**
 * Specifies that the fields of the object can be copied from another object instance.
 */
public interface CopyableInterface<T> {
  void copyFrom(T other);
}
