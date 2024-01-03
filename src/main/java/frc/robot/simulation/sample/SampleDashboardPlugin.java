package frc.robot.simulation.sample;

import frc.robot.shuffle.MultiType;
import frc.robot.simulation.framework.DashboardPluginInterface;

/**
 * For Sample Sim Model, exposes the parameters we show on Shuffleboard dashboard.
 */
public class SampleDashboardPlugin implements DashboardPluginInterface<Integer, Integer> {

  @Override
  public String[] queryListOfParameters() {
    return new String[] {
        "Accumulator"
    };
  }

  @Override
  public MultiType[] getDashboardParamsFromInputOutput(Integer input, Integer output) {
    throw new UnsupportedOperationException(
        "Unimplemented method 'getDashboardParamsFromInputOutput'");
  }
}
