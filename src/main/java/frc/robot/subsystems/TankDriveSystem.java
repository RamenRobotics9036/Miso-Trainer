package frc.robot.subsystems;

import com.ctre.phoenix.sensors.Pigeon2;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.revrobotics.RelativeEncoder;
import edu.wpi.first.math.filter.SlewRateLimiter;
import edu.wpi.first.wpilibj.RobotState;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.motorcontrol.MotorControllerGroup;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

/**
 * This class represents a tank drive system for a robot. The drive system uses
 * Xbox controller input (or autonomous drive) to control four motors.
 * The system also has features such as slew rate limiting and turbo mode.
 */
public class TankDriveSystem extends SubsystemBase {
  private XboxController m_controller;

  private boolean m_squareInputs;
  private double m_maxOutput;

  private SlewRateLimiter m_slewLimiter1;
  private SlewRateLimiter m_slewLimiter2;
  private SlewRateLimiter m_turboLimiter1;
  private SlewRateLimiter m_turboLimiter2;

  private double m_slewLimit;

  private CANSparkMax m_leftMotor1;
  private CANSparkMax m_leftMotor2;
  private CANSparkMax m_rightMotor1;
  private CANSparkMax m_rightMotor2;

  private MotorControllerGroup m_leftMotors;
  private MotorControllerGroup m_rightMotors;

  private DifferentialDrive m_drive;
  private RelativeEncoder m_leftEncoder;
  private RelativeEncoder m_rightEncoder;

  private final Pigeon2 m_gyro = new Pigeon2(7);
  private double m_prevRate = 0;

  protected double m_wheelDiameterMeters;
  protected double m_gearBoxRatio;

  /**
   * Constructor for the TankDriveSystem.
   */
  public TankDriveSystem(XboxController controller) {
    m_gearBoxRatio = Constants.OperatorConstants.kGearBoxRatioDrive;
    m_wheelDiameterMeters = Constants.OperatorConstants.kWheelDiameterMetersDrive;

    m_leftMotor1 = new CANSparkMax(Constants.OperatorConstants.kLeftMotorBackChannel,
        MotorType.kBrushless);
    m_leftMotor2 = new CANSparkMax(Constants.OperatorConstants.kLeftMotorForwardChannel,
        MotorType.kBrushless);
    m_rightMotor1 = new CANSparkMax(Constants.OperatorConstants.kRightMotorForwardChannel,
        MotorType.kBrushless);
    m_rightMotor2 = new CANSparkMax(Constants.OperatorConstants.kRightMotorBackChannel,
        MotorType.kBrushless);

    m_leftMotors = new MotorControllerGroup(m_leftMotor1, m_leftMotor2);
    m_rightMotors = new MotorControllerGroup(m_rightMotor1, m_rightMotor2);
    m_rightMotors.setInverted(true);
    m_drive = new DifferentialDrive(m_leftMotors, m_rightMotors);

    m_drive.setMaxOutput(Constants.OperatorConstants.kMaxOutputDrive);
    m_drive.setDeadband(Constants.OperatorConstants.kDeadband);

    m_leftEncoder = m_leftMotor1.getEncoder();
    m_rightEncoder = m_rightMotor1.getEncoder();

    m_controller = controller;

    m_squareInputs = Constants.OperatorConstants.kSquareInputsDrive;
    m_maxOutput = Constants.OperatorConstants.kMaxOutputDrive;

    m_slewLimit = Constants.OperatorConstants.kSlewLimit;
    if (m_slewLimit > 0) {
      m_slewLimiter1 = new SlewRateLimiter(Constants.OperatorConstants.kSlewLimit);
      m_slewLimiter2 = new SlewRateLimiter(Constants.OperatorConstants.kSlewLimit);
    }

    m_turboLimiter1 = new SlewRateLimiter(Constants.OperatorConstants.kTurboSlew);
    m_turboLimiter2 = new SlewRateLimiter(Constants.OperatorConstants.kTurboSlew);

    initDashBoard();
  }

  public void initDashBoard() {
  }

  public void updateDashBoard() {
  }

  /**
   * Returns the default drive command, which in this case is Joystick input.
   *
   * @return The default drive command.
   */
  public CommandBase getDefaultDriveCommand() {
    return run(() -> {
      processJoystickInput();
    });
  }

  public void resetEncoders() {
    m_leftEncoder.setPosition(0);
    m_rightEncoder.setPosition(0);
  }

  public double getLeftEncoder() {
    return m_leftEncoder.getPosition();
  }

  public double getRightEncoder() {
    return m_rightEncoder.getPosition();
  }

  public double getAverageEncoderPosition() {
    return (Math.abs(getLeftEncoder()) + Math.abs(getRightEncoder())) / 2;
  }

  private void processJoystickInput() {
    // In autonomous, the default command is to tell the robot to STOP. Recall
    // that we have to send a voltage-level to the motors EVERY 20ms, otherwise
    // the watchdog trips
    double leftAxisY = RobotState.isAutonomous() ? 0 : m_controller.getLeftY();
    double rightAxisY = RobotState.isAutonomous() ? 0 : m_controller.getRightY();

    double xspeed = (leftAxisY + rightAxisY) / 2;
    double zrotation = (leftAxisY - rightAxisY) / 2;

    if (m_controller.getRightTriggerAxis() > Constants.OperatorConstants.kDeadband) {
      m_drive.setMaxOutput(1);
      double slewLimit1 = m_turboLimiter1.calculate(xspeed);
      double slewLimit2 = m_turboLimiter2.calculate(zrotation);

      arcadeDrive(slewLimit1,
          slewLimit2 * Constants.OperatorConstants.kRotationDilation,
          m_squareInputs);
      m_slewLimiter1.reset(slewLimit1);
      m_slewLimiter2.reset(slewLimit2);
    }
    else {

      m_drive.setMaxOutput(m_maxOutput);
      double slewLimit1 = m_slewLimiter1.calculate(xspeed);
      double slewLimit2 = m_slewLimiter2.calculate(zrotation);

      arcadeDrive(slewLimit1,
          slewLimit2 * Constants.OperatorConstants.kRotationDilation,
          m_squareInputs);
      m_turboLimiter1.reset(slewLimit1);
      m_turboLimiter2.reset(slewLimit2);
    }

    // $TODOLATER - This should be in init or update DashBoard?
    SmartDashboard.putNumber("Left Motor", m_leftMotors.get());
    SmartDashboard.putNumber("Right Motor", m_rightMotors.get());
  }

  public void arcadeDrive(double xspeed, double zrotation, boolean squareInputs) {
    m_drive.arcadeDrive(xspeed, zrotation, squareInputs);
  }

  public void tankDrive(double leftSpeed, double rightSpeed, boolean squareInputs) {
    m_drive.tankDrive(leftSpeed, rightSpeed, squareInputs);
  }

  public void calibrate() {
  }

  public double getGyroAngle() {
    return m_gyro.getRoll();
  }

  public double getGyroYaw() {
    return m_gyro.getYaw();
  }

  public double getGyroRate() {
    return Math.abs(m_prevRate - m_gyro.getRoll());
  }

  public void setRate() {
    m_prevRate = m_gyro.getRoll();
  }
}
