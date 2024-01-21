package frc.robot.subsystems;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.RobotState;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import frc.robot.Constants;
import frc.robot.helpers.DefaultLayout;
import frc.robot.helpers.DefaultLayout.Widget;
import frc.robot.simulation.drive.DriveSimModel;
import frc.robot.simulation.drive.DriveState;

import java.util.Map;

/**
 * Subclass of TankDriveSystem that is used for simulation. Note that this code isn't run if
 * the robot is not running in simulation mode.
 */
public class TankDriveSystemSim extends TankDriveSystem {
  private DriveSimModel m_driveSimulation = null;
  private DefaultLayout m_defaultLayout = new DefaultLayout();

  /**
   * Factory method to create a TankDriveSystemSim or TankDriveSystem object.
   */
  public static TankDriveSystem createTankDriveSystemInstance(XboxController controller) {
    TankDriveSystem result;

    if (RobotBase.isSimulation()) {
      result = new TankDriveSystemSim(controller);

      // System.out.println("TANKDRIVESYSTEM: **** Simulation ****");

    }
    else {
      result = new TankDriveSystem(controller);

      // System.out.println("TANKDRIVESYSTEM: Physical Robot version");
    }

    return result;
  }

  /**
   * Constructor.
   */
  public TankDriveSystemSim(XboxController controller) {
    // FIRST, we call superclass
    super(controller);

    // This entire class should only be instantiated when we're under simulation.
    // But just in-case someone tries to instantiate it otherwise, we do an extra
    // check here.
    if (RobotBase.isSimulation()) {
      m_driveSimulation = new DriveSimModel(
          Constants.OperatorConstants.kWheelDiameterMetersDrive / 2);
      resetSimulationRobotPosition();
    }

    // $LATER - 1) This should be called from initDashboard, 2) move the widget code into
    // TankDriveSystemSimWithWidgets
    addShuffleboardWidgets();
  }

  /**
   * Add widgets to Shuffleboard.
   */
  private void addShuffleboardWidgets() {
    Widget pos = m_defaultLayout.getWidgetPosition("Field");
    Shuffleboard.getTab("Simulation").add("Field", m_driveSimulation.getField())
        .withWidget(BuiltInWidgets.kField).withPosition(pos.x, pos.y)
        .withSize(pos.width, pos.height);

    pos = m_defaultLayout.getWidgetPosition("Heading");
    Shuffleboard.getTab("Simulation").add("Heading", m_driveSimulation.getGyro())
        .withWidget(BuiltInWidgets.kGyro).withPosition(pos.x, pos.y).withSize(pos.width, pos.height)
        .withProperties(Map.of("Starting angle", 90));
  }

  private boolean isRobotEnabled() {
    return RobotState.isEnabled();
  }

  @Override
  public void periodic() {
    super.periodic();

    // When Robot is disabled, the entire simulation freezes
    if (isRobotEnabled()) {
      DriveState driveState = m_driveSimulation.simulationPeriodic();
      m_driveSimulation.drawRobotOnField(driveState.getPose());
    }
  }

  @Override
  public void simulationPeriodic() {
    super.simulationPeriodic();
  }

  @Override
  public void resetEncoders() {
    super.resetEncoders();

    m_driveSimulation.resetRelativeEncoders();
  }

  @Override
  public double getGyroYaw() {
    // $TODO - This is wrong
    return m_driveSimulation.getHeading();
  }

  // $TODO - This can go away 100%. Instead, pass in an initial x=2, y=2, heading=0 to the
  // constructor of the drive
  private void resetSimulationRobotPosition() {
    Pose2d initialPosition = new Pose2d(2, 2, new Rotation2d());
    m_driveSimulation.resetOdometry(initialPosition);
  }

  // RETURN SIMULATED VALUE: Overrides physical encoder value in parent class
  @Override
  public double getLeftEncoder() {
    // Note that our relativeEncoder returns distance the SIMULATED robot moved on
    // the field in meters.
    // But we want to return number of MOTOR rotations that our PHYSICAL robot would
    // have had to take to move that distance in real life.
    return m_driveSimulation.getRelativeDistanceLeft() * m_gearBoxRatio
        / (m_wheelDiameterMeters * Math.PI);
  }

  // RETURN SIMULATED VALUE: Overrides physical encoder value in parent class
  @Override
  public double getRightEncoder() {
    // Note that our relativeEncoder returns distance the SIMULATED robot moved on
    // the field in meters.
    // But we want to return number of MOTOR rotations that our PHYSICAL robot would
    // have had to take to move that distance in real life.
    return m_driveSimulation.getRelativeDistanceRight() * m_gearBoxRatio
        / (m_wheelDiameterMeters * Math.PI);
  }

  @Override
  public void arcadeDrive(double xspeed, double zrotation, boolean squareInputs) {
    super.arcadeDrive(xspeed, zrotation, squareInputs);

    // When Robot is disabled, the entire simulation freezes
    if (isRobotEnabled()) {
      m_driveSimulation.arcadeDrive(xspeed, zrotation, squareInputs);
    }
  }

  @Override
  public void tankDrive(double leftSpeed, double rightSpeed, boolean squareInputs) {
    super.tankDrive(leftSpeed, rightSpeed, squareInputs);

    // When Robot is disabled, the entire simulation freezes
    if (isRobotEnabled()) {
      m_driveSimulation.tankDrive(leftSpeed, rightSpeed, squareInputs);
    }
  }
}
