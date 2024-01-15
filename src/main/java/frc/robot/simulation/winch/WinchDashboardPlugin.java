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
        // $TODO - Replace with UnspooledPercent AND winding orientation name
        new DashboardItem("Accumulator", MultiType.of(0))
    };
  }

  @Override
  public MultiType[] getDashboardPropertiesFromInputOutput(Double input, WinchState output) {
    MultiType[] result = new MultiType[1];

    // $TODO - Replace with UnspooledPercent AND winding orientation name
    // result[0] = MultiType.of(output);

    return result;
  }
}
