package frc.robot.subsystems;

import static simulationlib.simulation.swerve.SwerveSimConstants.Swerve.kModuleTranslations;

import java.util.function.Supplier;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.smartdashboard.Field2d;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import simulationlib.shuffle.MultiType;
import simulationlib.shuffle.PrefixedConcurrentMap;
import simulationlib.shuffle.PrefixedConcurrentMap.Client;
import simulationlib.simulation.swerve.SwerveDrive;
import simulationlib.simulation.swerve.SwerveSimConstants.Usb;

/**
 * Subclass of TankDriveSystem that is used for simulation. Note that this code isn't run if
 * the robot is not running in simulation mode.
 */
public class SwerveSystemSim extends TankDriveSystem {
  private static Joystick m_leftJoystick = new Joystick(Usb.leftJoystick);
  private SwerveDrive m_swerveDrive;
  private final Field2d m_field2d = new Field2d();
  private final Pose2d[] m_robotPose = {
      new Pose2d()
  };
  private final Pose2d[] m_swerveModulePoses = {
      new Pose2d(), new Pose2d(), new Pose2d(), new Pose2d()
  };

  /**
   * Constructor.
   */
  public SwerveSystemSim(XboxController controller) {
    // FIRST, we call superclass
    super(controller);

    m_swerveDrive = new SwerveDrive();

    // $TODO - This can go away later. For now, expose pose for Shuffleboard
    Client<Supplier<MultiType>> shuffleClient = PrefixedConcurrentMap
        .createShuffleboardClientForSubsystem("SwerveSystem");
    shuffleClient.addItem("RobotPose", () -> MultiType.of(m_robotPose[0]));
  }

  private void updateRobotPoses() {
    m_robotPose[0] = m_swerveDrive.getPoseMeters();
    m_field2d.setRobotPose(m_robotPose[0]);

    for (int i = 0; i < kModuleTranslations.length; i++) {
      Translation2d updatedPositions = kModuleTranslations[i]
          .rotateBy(m_swerveDrive.getPoseMeters().getRotation())
          .plus(m_swerveDrive.getPoseMeters().getTranslation());
      m_swerveModulePoses[i] = new Pose2d(updatedPositions, m_swerveDrive.getSwerveModule(i)
          .getHeadingRotation2d().plus(m_swerveDrive.getHeadingRotation2d()));
    }

    m_field2d.getObject("Swerve Modules").setPoses(m_swerveModulePoses);
  }

  private void joystickDrive() {
    double joyX = m_leftJoystick.getX();
    double joyY = m_leftJoystick.getY();
    double joyZ = m_leftJoystick.getZ();

    double throttle = Math.abs(joyX) > 0.05 ? joyX : 0;
    double strafe = Math.abs(joyY * -1.0) > 0.05 ? joyY * -1.0 : 0;
    double rotation = Math.abs(joyZ) > 0.05 ? joyZ : 0;

    // Forward/Back
    // Trottle,
    // Left/Right Strafe,
    // Left/Right Turn
    m_swerveDrive.drive(throttle, strafe, rotation, true, false);
  }

  @Override
  public void periodic() {
    super.periodic();

    // $TODO - Should only be doing this in teleop
    joystickDrive();

    m_swerveDrive.periodic();
  }

  @Override
  public void simulationPeriodic() {
    super.simulationPeriodic();

    m_swerveDrive.simulationPeriodic();

    updateRobotPoses();
    SmartDashboard.putData("Field2d", m_field2d);
  }

  @Override
  public void resetEncoders() {
    super.resetEncoders();
  }

  @Override
  public double getGyroYaw() {
    return 0;
  }

  // RETURN SIMULATED VALUE: Overrides physical encoder value in parent class
  @Override
  public double getLeftEncoder() {
    // Note that our relativeEncoder returns distance the SIMULATED robot moved on
    // the field in meters.
    // But we want to return number of MOTOR rotations that our PHYSICAL robot would
    // have had to take to move that distance in real life.
    return 0;
  }

  // RETURN SIMULATED VALUE: Overrides physical encoder value in parent class
  @Override
  public double getRightEncoder() {
    // Note that our relativeEncoder returns distance the SIMULATED robot moved on
    // the field in meters.
    // But we want to return number of MOTOR rotations that our PHYSICAL robot would
    // have had to take to move that distance in real life.
    return 0;
  }

  @Override
  public void arcadeDrive(double xspeed, double zrotation, boolean squareInputs) {
    super.arcadeDrive(xspeed, zrotation, squareInputs);
  }

  @Override
  public void tankDrive(double leftSpeed, double rightSpeed, boolean squareInputs) {
    super.tankDrive(leftSpeed, rightSpeed, squareInputs);
  }
}
