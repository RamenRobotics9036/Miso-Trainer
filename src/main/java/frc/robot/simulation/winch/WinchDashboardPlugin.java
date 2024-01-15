package frc.robot.simulation.winch;

import frc.robot.shuffle.MultiType;
import frc.robot.simulation.framework.DashboardItem;
import frc.robot.simulation.framework.DashboardPluginInterface;

/**
 * For Winch Sim Model, exposes the properties we show on Shuffleboard dashboard.
 */
public class WinchDashboardPlugin implements DashboardPluginInterface<Double, WinchState> {

  @Override
  public DashboardItem[] queryListOfDashboardPropertiesWithInitValues() {
    return new DashboardItem[] {
        new DashboardItem("UnspooledLen", MultiType.of(0.0)),
        new DashboardItem("UnspooledPercent", MultiType.of(0.0))
    };
  }

  @Override
  public MultiType[] getDashboardPropertiesFromInputOutput(Double input, WinchState output) {
    MultiType[] result = new MultiType[2];

    result[0] = MultiType.of(output.getStringUnspooledLen());
    result[1] = MultiType.of(output.getStringUnspooledPercent());

    return result;
  }
}
