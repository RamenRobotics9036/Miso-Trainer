package frc.robot.subsystems;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.RobotState;
import edu.wpi.first.wpilibj.XboxController;
import frc.robot.Constants;
import frc.robot.simulation.drive.ArcadeInputParams;
import frc.robot.simulation.drive.DriveDashboardPlugin;
import frc.robot.simulation.drive.DriveInputState;
import frc.robot.simulation.drive.DriveSimModel;
import frc.robot.simulation.drive.DriveState;
import frc.robot.simulation.framework.SimManager;
import frc.robot.simulation.framework.inputoutputs.LambdaSimInput;
import frc.robot.simulation.framework.inputoutputs.LambdaSimOutput;
import simulationlib.shuffle.PrefixedConcurrentMap;

/**
 * Subclass of TankDriveSystem that is used for simulation. Note that this code isn't run if
 * the robot is not running in simulation mode.
 */
public class TankDriveSystemSim extends TankDriveSystem {
  private final Pose2d m_initialPosition = new Pose2d(2, 2, new Rotation2d());
  private SimManager<DriveInputState, DriveState> m_driveSimManager;
  private DriveState m_driveState = new DriveState();
  private final DriveInputState m_driveInputState = new DriveInputState(false,
      new ArcadeInputParams(0, 0, false));

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

    m_driveSimManager = new SimManager<DriveInputState, DriveState>(
        new DriveSimModel(m_initialPosition,
            Constants.OperatorConstants.kWheelDiameterMetersDrive / 2),
        PrefixedConcurrentMap.createShuffleboardClientForSubsystem("DriveSystem"),
        new DriveDashboardPlugin(), false);

    m_driveSimManager.setInputHandler(new LambdaSimInput<DriveInputState>(() -> m_driveInputState));
    m_driveSimManager.setOutputHandler(new LambdaSimOutput<DriveState>((stateOutput) -> {
      m_driveState = stateOutput;
    }));
  }

  private boolean isRobotEnabled() {
    return RobotState.isEnabled();
  }

  @Override
  public void periodic() {
    super.periodic();
  }

  @Override
  public void simulationPeriodic() {
    super.simulationPeriodic();

    if (isRobotEnabled()) {
      m_driveSimManager.simulationPeriodic();

      // Reset one-shot
      m_driveInputState.resetRelativeEncoders = false;
    }
  }

  @Override
  public void resetEncoders() {
    super.resetEncoders();

    m_driveInputState.resetRelativeEncoders = true;
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
      m_driveInputState.arcadeParams = new ArcadeInputParams(xspeed, zrotation, squareInputs);
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
