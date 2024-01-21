package frc.robot.subsystems;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.RobotState;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.smartdashboard.Field2d;
import frc.robot.Constants;
import frc.robot.helpers.DefaultLayout;
import frc.robot.helpers.DefaultLayout.Widget;
import frc.robot.simulation.drive.ArcadeInputParams;
import frc.robot.simulation.drive.DriveInputState;
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
  private final DriveState m_driveState = new DriveState();
  private final Field2d m_fieldSim = new Field2d();
  private boolean m_resetRelativeEncodersOnNextCycle = false;
  private ArcadeInputParams m_arcadeInputParamsNextCycle = new ArcadeInputParams(0, 0, false);

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
      Pose2d initialPosition = new Pose2d(2, 2, new Rotation2d());

      m_driveSimulation = new DriveSimModel(initialPosition,
          Constants.OperatorConstants.kWheelDiameterMetersDrive / 2);

      // $TODO - This can go away later when we use SimManager
      force_periodic();
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
    Shuffleboard.getTab("Simulation").add("Field", m_fieldSim).withWidget(BuiltInWidgets.kField)
        .withPosition(pos.x, pos.y).withSize(pos.width, pos.height);

    // $TODO - Move this into the populate widget class
    pos = m_defaultLayout.getWidgetPosition("Heading");
    Shuffleboard.getTab("Simulation")
        .addDouble("Heading", () -> m_driveState.getGyroHeadingDegrees())
        .withWidget(BuiltInWidgets.kGyro).withPosition(pos.x, pos.y).withSize(pos.width, pos.height)
        .withProperties(Map.of("Starting angle", 90));
  }

  private void drawRobotOnField(Pose2d pose) {
    m_fieldSim.setRobotPose(pose);
  }

  private boolean isRobotEnabled() {
    return RobotState.isEnabled();
  }

  // $TODO - This can go away later when we use SimManager
  private void force_periodic() {
    DriveInputState inputState = new DriveInputState(m_resetRelativeEncodersOnNextCycle,
        m_arcadeInputParamsNextCycle);

    // Reset one-shot
    m_resetRelativeEncodersOnNextCycle = false;

    DriveState driveState = m_driveSimulation.simulationPeriodicForDrive(inputState);
    m_driveState.copyFrom(driveState);

    drawRobotOnField(m_driveState.getPhysicalWorldPose());
  }

  @Override
  public void periodic() {
    super.periodic();

    // When Robot is disabled, the entire simulation freezes
    if (isRobotEnabled()) {
      force_periodic();
    }
  }

  @Override
  public void simulationPeriodic() {
    super.simulationPeriodic();
  }

  @Override
  public void resetEncoders() {
    super.resetEncoders();

    m_resetRelativeEncodersOnNextCycle = true;
  }

  @Override
  public double getGyroYaw() {
    return m_driveState.getGyroHeadingDegrees();
  }

  // RETURN SIMULATED VALUE: Overrides physical encoder value in parent class
  @Override
  public double getLeftEncoder() {
    // Note that our relativeEncoder returns distance the SIMULATED robot moved on
    // the field in meters.
    // But we want to return number of MOTOR rotations that our PHYSICAL robot would
    // have had to take to move that distance in real life.
    return m_driveState.getLeftRelativeEncoderDistance() * m_gearBoxRatio
        / (m_wheelDiameterMeters * Math.PI);
  }

  // RETURN SIMULATED VALUE: Overrides physical encoder value in parent class
  @Override
  public double getRightEncoder() {
    // Note that our relativeEncoder returns distance the SIMULATED robot moved on
    // the field in meters.
    // But we want to return number of MOTOR rotations that our PHYSICAL robot would
    // have had to take to move that distance in real life.
    return m_driveState.getRightRelativeEncoderDistance() * m_gearBoxRatio
        / (m_wheelDiameterMeters * Math.PI);
  }

  private void simArcadeDrive(double xspeed, double zrotation, boolean squareInputs) {
    // When Robot is disabled, the entire simulation freezes
    if (isRobotEnabled()) {
      m_arcadeInputParamsNextCycle = new ArcadeInputParams(xspeed, zrotation, squareInputs);
    }
  }

  @Override
  public void arcadeDrive(double xspeed, double zrotation, boolean squareInputs) {
    super.arcadeDrive(xspeed, zrotation, squareInputs);

    simArcadeDrive(xspeed, zrotation, squareInputs);
  }

  @Override
  public void tankDrive(double leftSpeed, double rightSpeed, boolean squareInputs) {
    super.tankDrive(leftSpeed, rightSpeed, squareInputs);

    double xforward = (leftSpeed + rightSpeed) / 2;
    double zrotation = (leftSpeed - rightSpeed) / 2;

    simArcadeDrive(xforward, zrotation, squareInputs);
  }
}
