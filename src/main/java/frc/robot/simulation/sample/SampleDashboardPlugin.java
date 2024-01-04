package frc.robot.simulation.sample;

import frc.robot.shuffle.MultiType;
import frc.robot.simulation.framework.DashboardItem;
import frc.robot.simulation.framework.DashboardPluginInterface;

/**
 * For Sample Sim Model, exposes the properties we show on Shuffleboard dashboard.
 */
public class SampleDashboardPlugin implements DashboardPluginInterface<Integer, Integer> {

  @Override
  public DashboardItem[] queryListOfDashboardPropertiesWithInitValues() {
    return new DashboardItem[] {
        new DashboardItem("Accumulator", MultiType.of(0))
    };
  }

  @Override
  public MultiType[] getDashboardPropertiesFromInputOutput(Integer input, Integer output) {
    MultiType[] result = new MultiType[1];

    result[0] = MultiType.of(output);

    return result;
  }
}
