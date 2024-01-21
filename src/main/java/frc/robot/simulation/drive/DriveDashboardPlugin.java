package frc.robot.simulation.drive;

import frc.robot.shuffle.MultiType;
import frc.robot.simulation.framework.DashboardItem;
import frc.robot.simulation.framework.DashboardPluginInterface;

/**
 * For Sample Sim Model, exposes the properties we show on Shuffleboard dashboard.
 */
public class DriveDashboardPlugin implements DashboardPluginInterface<DriveInputState, DriveState> {

  @Override
  public DashboardItem[] queryListOfDashboardPropertiesWithInitValues() {
    return new DashboardItem[] {
        new DashboardItem("GyroHeadingDegrees", MultiType.of(0.0))
    };
  }

  @Override
  public MultiType[] getDashboardPropertiesFromInputOutput(DriveInputState input,
      DriveState output) {
    MultiType[] result = new MultiType[1];

    result[0] = MultiType.of(output.getGyroHeadingDegrees());

    return result;
  }
}
