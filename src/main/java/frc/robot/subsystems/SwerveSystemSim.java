package frc.robot.subsystems;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotState;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.smartdashboard.Field2d;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import simulationlib.simulation.swerve.SwerveDrive;
import simulationlib.simulation.swerve.SwerveSimConstants.Usb;

/**
 * Subclass of TankDriveSystem that is used for simulation. Note that this code isn't run if
 * the robot is not running in simulation mode.
 */
public class SwerveSystemSim extends TankDriveSystem {
  private static Joystick m_leftJoystick = new Joystick(Usb.leftJoystick);
  private final Field2d m_field2d = new Field2d();
  private SwerveDrive m_swerveDrive;

  /**
   * Constructor.
   */
  public SwerveSystemSim(XboxController controller) {
    // FIRST, we call superclass
    super(controller);

    m_swerveDrive = new SwerveDrive();
  }

  private void updateRobotPoses() {
    m_field2d.setRobotPose(m_swerveDrive.getPoseMeters());
  }

  @Override
  public void periodic() {
    super.periodic();
  }

  @Override
  public void simulationPeriodic() {
    super.simulationPeriodic();

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

    // Create command to drive
    // $TODO
    // new SetSwerveDrive(m_robotDrive, () -> leftJoystick.getX(),
    // () -> leftJoystick.getY() * -1.0, () -> leftJoystick.getZ(), true));
  }

  @Override
  public void tankDrive(double leftSpeed, double rightSpeed, boolean squareInputs) {
    super.tankDrive(leftSpeed, rightSpeed, squareInputs);
  }
}
