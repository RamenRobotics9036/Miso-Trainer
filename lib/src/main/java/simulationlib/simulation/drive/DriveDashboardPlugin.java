package simulationlib.simulation.drive;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import simulationlib.shuffle.MultiType;
import simulationlib.simulation.framework.DashboardItem;
import simulationlib.simulation.framework.DashboardPluginInterface;

/**
 * For Sample Sim Model, exposes the properties we show on Shuffleboard dashboard.
 */
public class DriveDashboardPlugin implements DashboardPluginInterface<DriveInputState, DriveState> {

  @Override
  public DashboardItem[] queryListOfDashboardPropertiesWithInitValues() {
    return new DashboardItem[] {
        new DashboardItem("GyroHeadingDegrees", MultiType.of(0.0)),
        new DashboardItem("RobotPose", MultiType.of(new Pose2d(0, 0, new Rotation2d()))),
    };
  }

  @Override
  public MultiType[] getDashboardPropertiesFromInputOutput(DriveInputState input,
      DriveState output) {
    MultiType[] result = new MultiType[2];

    result[0] = MultiType.of(output.getGyroHeadingDegrees());
    result[1] = MultiType.of(output.getPhysicalWorldPose());

    return result;
  }
}
