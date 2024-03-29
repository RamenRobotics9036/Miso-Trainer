package simulationlib.simulation.motor;

import simulationlib.shuffle.MultiType;
import simulationlib.simulation.framework.DashboardItem;
import simulationlib.simulation.framework.DashboardPluginInterface;

/**
 * For Motor Sim Model, exposes the properties we show on Shuffleboard dashboard.
 */
public class MotorDashboardPlugin implements DashboardPluginInterface<Double, Double> {

  /**
   * Constructor.
   */
  public MotorDashboardPlugin() {
  }

  @Override
  public DashboardItem[] queryListOfDashboardPropertiesWithInitValues() {
    return new DashboardItem[] {
        new DashboardItem("InputPower", MultiType.of(0.0)),
        new DashboardItem("Rotations", MultiType.of(0.0)),
    };
  }

  @Override
  public MultiType[] getDashboardPropertiesFromInputOutput(Double input, Double output) {
    MultiType[] result = new MultiType[2];

    result[0] = MultiType.of(input);
    result[1] = MultiType.of(output);

    return result;
  }
}
