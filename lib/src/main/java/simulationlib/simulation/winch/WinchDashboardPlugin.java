package simulationlib.simulation.winch;

import simulationlib.shuffle.MultiType;
import simulationlib.simulation.framework.DashboardItem;
import simulationlib.simulation.framework.DashboardPluginInterface;

/**
 * For Winch Sim Model, exposes the properties we show on Shuffleboard dashboard.
 */
public class WinchDashboardPlugin implements DashboardPluginInterface<Double, WinchState> {

  @Override
  public DashboardItem[] queryListOfDashboardPropertiesWithInitValues() {
    return new DashboardItem[] {
        new DashboardItem("UnspooledLen", MultiType.of(0.0)),
        new DashboardItem("UnspooledPercent", MultiType.of(0.0)),
        new DashboardItem("WindingOrientation", MultiType.of(""))
    };
  }

  @Override
  public MultiType[] getDashboardPropertiesFromInputOutput(Double input, WinchState output) {
    MultiType[] result = new MultiType[3];

    result[0] = MultiType.of(output.getStringUnspooledLen());
    result[1] = MultiType.of(output.getStringUnspooledPercent());
    result[2] = MultiType.of(output.getWindingOrientationName());

    return result;
  }
}
