package frc.robot.simulation.framework;

/**
 * Manages simulation of a particular device. It calls an input Supplier to periodically
 * query inputs to device, and calls the output Consumer to set the new state of the device.
 * It leverages a Model class to do the actual real-world simulation.
 */
// $TODO - No need for this interface to exist.
public interface SimManagerInterface<InputT, OutputT> {
  void setInputHandler(SimInputInterface<InputT> inputHandler);

  void setOutputHandler(SimOutputInterface<OutputT> outputHandler);

  void simulationPeriodic();
}
