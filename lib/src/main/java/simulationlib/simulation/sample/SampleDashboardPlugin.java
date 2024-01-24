package simulationlib.simulation.sample;

import simulationlib.shuffle.MultiType;
import simulationlib.simulation.framework.DashboardItem;
import simulationlib.simulation.framework.DashboardPluginInterface;

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
