// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.simulation.drive;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.controller.SimpleMotorFeedforward;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Transform2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.DifferentialDriveKinematics;
import edu.wpi.first.math.kinematics.DifferentialDriveOdometry;
import edu.wpi.first.math.kinematics.DifferentialDriveWheelSpeeds;
import edu.wpi.first.math.numbers.N2;
import edu.wpi.first.math.system.LinearSystem;
import edu.wpi.first.math.system.plant.DCMotor;
import edu.wpi.first.math.system.plant.LinearSystemId;
import edu.wpi.first.wpilibj.AnalogGyro;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.RobotController;
import edu.wpi.first.wpilibj.motorcontrol.MotorControllerGroup;
import edu.wpi.first.wpilibj.motorcontrol.PWMSparkMax;
import edu.wpi.first.wpilibj.simulation.AnalogGyroSim;
import edu.wpi.first.wpilibj.simulation.DifferentialDrivetrainSim;
import edu.wpi.first.wpilibj.simulation.EncoderSim;
import frc.robot.helpers.RelEncoderWrapper;

/**
 * Simulates a real world drivetrain. E.g. the position of the robot is even shown
 * on the field.
 */
public class DriveSimModel {
  private static final double kTrackWidth = 0.381 * 2;
  private final double m_wheelRadius;
  private static final int kEncoderResolution = -4096;

  private final PWMSparkMax m_leftLeader = new PWMSparkMax(1);
  private final PWMSparkMax m_leftFollower = new PWMSparkMax(2);
  private final PWMSparkMax m_rightLeader = new PWMSparkMax(3);
  private final PWMSparkMax m_rightFollower = new PWMSparkMax(4);

  private final MotorControllerGroup m_leftGroup = new MotorControllerGroup(m_leftLeader,
      m_leftFollower);
  private final MotorControllerGroup m_rightGroup = new MotorControllerGroup(m_rightLeader,
      m_rightFollower);

  private final Encoder m_leftEncoder = new Encoder(0, 1);
  private final Encoder m_rightEncoder = new Encoder(2, 3);

  private final PIDController m_leftPidController = new PIDController(8.5, 0, 0);
  private final PIDController m_rightPidController = new PIDController(8.5, 0, 0);

  private final AnalogGyro m_gyro = new AnalogGyro(0);

  private final DifferentialDriveKinematics m_kinematics = new DifferentialDriveKinematics(
      kTrackWidth);
  private final DifferentialDriveOdometry m_odometry = new DifferentialDriveOdometry(
      m_gyro.getRotation2d(), m_leftEncoder.getDistance(), m_rightEncoder.getDistance());

  private final Pose2d m_initialPose;

  // Gains are for example purposes only - must be determined for your own
  // robot!
  private final SimpleMotorFeedforward m_feedforward = new SimpleMotorFeedforward(1, 3);

  // Simulation classes help us simulate our robot
  private final AnalogGyroSim m_gyroSim = new AnalogGyroSim(m_gyro);

  private final EncoderSim m_leftEncoderSim = new EncoderSim(m_leftEncoder);
  private final EncoderSim m_rightEncoderSim = new EncoderSim(m_rightEncoder);
  private final RelEncoderWrapper m_leftEncoderSimWrapper;
  private final RelEncoderWrapper m_rightEncoderSimWrapper;

  private final LinearSystem<N2, N2, N2> m_drivetrainSystem = LinearSystemId
      .identifyDrivetrainSystem(1.98, 0.2, 1.5, 0.3);
  private final DifferentialDrivetrainSim m_drivetrainSimulator;

  // Resets both the absolute-encoders AND the relative-encoders
  private void resetAllEncoders() {
    m_leftEncoder.reset();
    m_rightEncoder.reset();
    resetRelativeEncoders();
  }

  private void resetRelativeEncoders() {
    m_leftEncoderSimWrapper.reset();
    m_rightEncoderSimWrapper.reset();
  }

  /** Subsystem constructor. */
  public DriveSimModel(Pose2d initialPose, double wheelRadiusMeters) {
    m_wheelRadius = wheelRadiusMeters;

    m_drivetrainSimulator = new DifferentialDrivetrainSim(m_drivetrainSystem, DCMotor.getCIM(2), 8,
        kTrackWidth, m_wheelRadius, null);

    // We need to invert one side of the drivetrain so that positive voltages
    // result in both sides moving forward. Depending on how your robot's
    // gearbox is constructed, you might have to invert the left side instead.
    m_rightGroup.setInverted(true);

    // Set the distance per pulse for the drive encoders. We can simply use the
    // distance traveled for one rotation of the wheel divided by the encoder
    // resolution.
    m_leftEncoder.setDistancePerPulse(2 * Math.PI * m_wheelRadius / kEncoderResolution);
    m_rightEncoder.setDistancePerPulse(2 * Math.PI * m_wheelRadius / kEncoderResolution);

    m_leftEncoderSimWrapper = new RelEncoderWrapper(m_leftEncoderSim);
    m_rightEncoderSimWrapper = new RelEncoderWrapper(m_rightEncoderSim);

    resetAllEncoders();
    m_initialPose = initialPose;

    m_rightGroup.setInverted(true);
  }

  /** Sets speeds to the drivetrain motors. */
  private void setSpeeds(DifferentialDriveWheelSpeeds speeds) {
    var leftFeedforward = m_feedforward.calculate(speeds.leftMetersPerSecond);
    var rightFeedforward = m_feedforward.calculate(speeds.rightMetersPerSecond);
    double leftOutput = m_leftPidController.calculate(m_leftEncoder.getRate(),
        speeds.leftMetersPerSecond);
    double rightOutput = m_rightPidController.calculate(m_rightEncoder.getRate(),
        speeds.rightMetersPerSecond);

    m_leftGroup.setVoltage(leftOutput + leftFeedforward);
    m_rightGroup.setVoltage(rightOutput + rightFeedforward);
  }

  /**
   * Controls the robot using arcade drive.
   *
   * @param xspeed the speed for the x axis
   * @param rot    the rotation
   */
  private void arcadeDrive(ArcadeInputParams arcadeParams) {
    // System.out.println("ARCADE: xSpeed = " + xSpeed);

    // $LATER - If the robot is stopped too quickly, or direction is changed instantly,
    // this robot simulation doesnt handle it well. Consider adding slew within this
    // simulation to avoid that.
    double xspeed = MathUtil.clamp(arcadeParams.xspeed, -1.0, 1.0);
    double rot = MathUtil.clamp(arcadeParams.zrotation, -1.0, 1.0);

    // $LATER - Slew rate limiters to make joystick inputs more gentle; 1/3 sec from 0 to 1.
    // private final SlewRateLimiter m_speedLimiter = new SlewRateLimiter(3);
    // private final SlewRateLimiter m_rotLimiter = new SlewRateLimiter(3);

    if (arcadeParams.squareInputs) {
      xspeed = Math.copySign(xspeed * xspeed, xspeed);
      rot = Math.copySign(rot * rot, rot);
    }

    setSpeeds(m_kinematics.toWheelSpeeds(new ChassisSpeeds(xspeed, 0, rot)));
  }

  /** Update robot odometry. */
  private void updateOdometry() {
    m_odometry
        .update(m_gyro.getRotation2d(), m_leftEncoder.getDistance(), m_rightEncoder.getDistance());
  }

  private double getHeading() {
    return m_gyroSim.getAngle();
  }

  private Pose2d getPhysicalWorldPose() {
    // Move initial position by x,y (translation)
    Pose2d translatedPos = m_initialPose
        .plus(new Transform2d(m_odometry.getPoseMeters().getTranslation(), new Rotation2d(0)));

    // Add rotation back
    Rotation2d newRotation = m_odometry.getPoseMeters().getRotation()
        .rotateBy(m_initialPose.getRotation());

    return new Pose2d(translatedPos.getTranslation(), newRotation);
  }

  /** Update our simulation. This should be run every robot loop in simulation. */
  public DriveState simulationPeriodicForDrive(DriveInputState input) {
    double leftVoltagePercent = m_leftGroup.get();
    double rightVoltagePercent = m_rightGroup.get();

    if (input.resetRelativeEncoders) {
      resetRelativeEncoders();
    }
    arcadeDrive(input.arcadeParams);

    // To update our simulation, we set motor voltage inputs, update the
    // simulation, and write the simulated positions and velocities to our
    // simulated encoder and gyro. We negate the right side so that positive
    // voltages make the right side move forward.
    m_drivetrainSimulator.setInputs(leftVoltagePercent * RobotController.getInputVoltage(),
        rightVoltagePercent * RobotController.getInputVoltage());
    m_drivetrainSimulator.update(0.02);

    m_leftEncoderSim.setDistance(m_drivetrainSimulator.getLeftPositionMeters());
    m_leftEncoderSim.setRate(m_drivetrainSimulator.getLeftVelocityMetersPerSecond());
    m_rightEncoderSim.setDistance(m_drivetrainSimulator.getRightPositionMeters());
    m_rightEncoderSim.setRate(m_drivetrainSimulator.getRightVelocityMetersPerSecond());
    m_gyroSim.setAngle(-m_drivetrainSimulator.getHeading().getDegrees());
    updateOdometry();

    DriveState driveState = new DriveState();
    driveState.setRelativePose(m_odometry.getPoseMeters());
    driveState.setPhysicalWorldPose(getPhysicalWorldPose());
    driveState.setGyroHeadingDegrees(getHeading());
    driveState.setLeftRelativeEncoderDistance(m_leftEncoderSimWrapper.getDistance());
    driveState.setRightRelativeEncoderDistance(m_rightEncoderSimWrapper.getDistance());
    return driveState;
  }
}
