package frc.robot.simulation.sample;

import frc.robot.shuffle.MultiType;
import frc.robot.simulation.framework.DashboardPluginInterface;

/**
 * For Sample Sim Model, exposes the properties we show on Shuffleboard dashboard.
 */
public class SampleDashboardPlugin implements DashboardPluginInterface<Integer, Integer> {

  @Override
  public String[] queryListOfDashboardProperties() {
    return new String[] {
        "Accumulator"
    };
  }

  @Override
  public MultiType[] getDashboardPropertiesFromInputOutput(Integer input, Integer output) {
    MultiType[] result = new MultiType[1];

    result[0] = MultiType.of(output);

    return result;
  }
}
