package simulationlib.simulation.extender;

import simulationlib.shuffle.MultiType;
import simulationlib.simulation.framework.DashboardItem;
import simulationlib.simulation.framework.DashboardPluginInterface;

/**
 * For Extender Sim Model, exposes the properties we show on Shuffleboard dashboard.
 */
public class ExtenderDashboardPlugin implements DashboardPluginInterface<Double, ExtenderState> {

  @Override
  public DashboardItem[] queryListOfDashboardPropertiesWithInitValues() {
    return new DashboardItem[] {
        new DashboardItem("PercentExtended", MultiType.of(0.0))
    };
  }

  @Override
  public MultiType[] getDashboardPropertiesFromInputOutput(Double input, ExtenderState output) {
    MultiType[] result = new MultiType[1];

    result[0] = MultiType.of(output.getExtendedPercent());

    return result;
  }
}
