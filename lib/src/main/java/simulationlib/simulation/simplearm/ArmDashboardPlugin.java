package simulationlib.simulation.simplearm;

import simulationlib.shuffle.MultiType;
import simulationlib.simulation.framework.DashboardItem;
import simulationlib.simulation.framework.DashboardPluginInterface;

/**
 * For Arm Sim Model, exposes the properties we show on Shuffleboard dashboard.
 */
public class ArmDashboardPlugin implements DashboardPluginInterface<Double, Double> {

  @Override
  public DashboardItem[] queryListOfDashboardPropertiesWithInitValues() {
    return new DashboardItem[] {
        new DashboardItem("ArmPosition", MultiType.of(0.0))
    };
  }

  @Override
  public MultiType[] getDashboardPropertiesFromInputOutput(Double input, Double output) {
    MultiType[] result = new MultiType[1];

    result[0] = MultiType.of(output);

    return result;
  }
}
