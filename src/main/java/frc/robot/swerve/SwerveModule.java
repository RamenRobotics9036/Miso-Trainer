// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.
// Swerve simulation implementation from: https://github.com/truher/swerve-sim

package frc.robot.swerve;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.controller.ProfiledPIDController;
import edu.wpi.first.math.controller.SimpleMotorFeedforward;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.math.trajectory.TrapezoidProfile;
import edu.wpi.first.networktables.DoublePublisher;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.motorcontrol.PWMMotorController;
import edu.wpi.first.wpilibj.motorcontrol.PWMSparkMax;
import edu.wpi.first.wpilibj.simulation.CallbackStore;
import edu.wpi.first.wpilibj.simulation.EncoderSim;
import edu.wpi.first.wpilibj.simulation.PWMSim;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a swerve module.
 */
public class SwerveModule {
  public static final double TURN_KV = 0.05;
  public static final double DRIVE_KV = 0.15;
  public static final double TURN_KS = 0.001;
  public static final double DRIVE_KS = 0.001;
  private static final double kWheelRadius = 0.0508;
  private static final int kEncoderResolution = 4096;

  private static final double kModuleMaxAngularVelocity = 10 * Math.PI; // rad/s, fast
  private static final double kModuleMaxAngularAcceleration = 50 * Math.PI; // rad/s^2

  private final PWMMotorController m_driveMotor;
  private final PWMSim m_drivePwmSim;
  // these are out here so i can observe them for testing.
  double m_driveOutput;
  double m_driveFeedforwardVal;

  private final PWMMotorController m_turningMotor;
  private final PWMSim m_turnPwmSim;

  final Encoder m_driveEncoder;
  private final EncoderSim m_driveEncoderSim;
  private final Encoder m_turningEncoder; // NWU
  final EncoderSim m_turnEncoderSim;

  final PIDController m_drivePidController = new PIDController(0.1, 0, 0);

  private final ProfiledPIDController m_turningPidController = new ProfiledPIDController(0.2, 0, 0,
      new TrapezoidProfile.Constraints(kModuleMaxAngularVelocity, kModuleMaxAngularAcceleration));

  // Gains are for example purposes only - must be determined for your own robot!
  private final SimpleMotorFeedforward m_driveFeedforward = new SimpleMotorFeedforward(DRIVE_KS,
      DRIVE_KV);
  private final SimpleMotorFeedforward m_turnFeedforward = new SimpleMotorFeedforward(TURN_KS,
      TURN_KV);

  // ######## network tables ########
  private final NetworkTableInstance m_inst = NetworkTableInstance.getDefault();
  // private final String m_name;
  private final NetworkTable m_table;
  // distance, m
  private final DoublePublisher m_driveEncoderPubM;
  // distance, rad
  private final DoublePublisher m_turnEncoderPubRad;
  // drive rate only, m/s; turn rate is ignored
  private final DoublePublisher m_driveEncoderRatePubMperS;
  // motor output, [-1,1]
  private final DoublePublisher m_drivePwmPub11;
  private final DoublePublisher m_turnPwmPub11;
  // desired velocity from "inverse feed forward", m/s
  private final DoublePublisher m_drivevPubMperS;
  // desired velocity from "inverse feed forward", rad/s
  private final DoublePublisher m_turnvPubRadPerS;
  // desired velocity from input.
  private final DoublePublisher m_drivevInPubMperS;
  // desired position from input.
  private final DoublePublisher m_turnPinPubRad;

  List<CallbackStore> m_cbs = new ArrayList<CallbackStore>();
  private double m_prevTimeSeconds = Timer.getFPGATimestamp();
  private final double m_nominalDtS = 0.02; // Seconds

  /**
   * Constructs a SwerveModule with a drive motor, turning motor, drive encoder
   * and turning encoder.
   *
   * @param driveMotorChannel      PWM output for the drive motor.
   * @param turningMotorChannel    PWM output for the turning motor.
   * @param driveEncoderChannelA   DIO input for the drive encoder channel A
   * @param driveEncoderChannelB   DIO input for the drive encoder channel B
   * @param turningEncoderChannelA DIO input for the turning encoder channel A
   * @param turningEncoderChannelB DIO input for the turning encoder channel B
   */
  public SwerveModule(String name,
      int driveMotorChannel,
      int turningMotorChannel,
      int driveEncoderChannelA,
      int driveEncoderChannelB,
      int turningEncoderChannelA,
      int turningEncoderChannelB) {
    // m_name = name;
    m_table = m_inst.getTable(name);
    m_driveEncoderPubM = m_table.getDoubleTopic("driveEncoderDistanceM").publish();
    m_turnEncoderPubRad = m_table.getDoubleTopic("turnEncoderDistanceRad").publish();
    m_driveEncoderRatePubMperS = m_table.getDoubleTopic("driveEncoderRateM_s").publish();
    m_drivePwmPub11 = m_table.getDoubleTopic("drivePWMOutput1_1").publish();
    m_turnPwmPub11 = m_table.getDoubleTopic("turnPWMOutput1_1").publish();
    m_drivevPubMperS = m_table.getDoubleTopic("driveDesiredSpeedM_s").publish();
    m_turnvPubRadPerS = m_table.getDoubleTopic("turnDesiredSpeedRad_s").publish();
    m_drivevInPubMperS = m_table.getDoubleTopic("driveInputSpeedM_s").publish();
    m_turnPinPubRad = m_table.getDoubleTopic("turnInputRad").publish();

    m_driveMotor = new PWMSparkMax(driveMotorChannel);
    m_drivePwmSim = new PWMSim(m_driveMotor);

    m_turningMotor = new PWMSparkMax(turningMotorChannel);
    m_turnPwmSim = new PWMSim(m_turningMotor);

    m_driveEncoder = new Encoder(driveEncoderChannelA, driveEncoderChannelB);
    m_driveEncoderSim = new EncoderSim(m_driveEncoder);

    m_turningEncoder = new Encoder(turningEncoderChannelA, turningEncoderChannelB);
    m_turnEncoderSim = new EncoderSim(m_turningEncoder);

    pubSim(m_drivePwmSim, m_drivePwmPub11);
    pubSim(m_turnPwmSim, m_turnPwmPub11);

    // Set the distance per pulse for the drive encoder. We can simply use the
    // distance traveled for one rotation of the wheel divided by the encoder
    // resolution.
    m_driveEncoder.setDistancePerPulse(2 * Math.PI * kWheelRadius / kEncoderResolution);

    // Set the distance (in this case, angle) in radians per pulse for the turning
    // encoder.
    // This is the the angle through an entire rotation (2 * pi) divided by the
    // encoder resolution.
    m_turningEncoder.setDistancePerPulse(2 * Math.PI / kEncoderResolution);

    // Limit the PID Controller's input range between -pi and pi and set the input
    // to be continuous.
    m_turningPidController.enableContinuousInput(-Math.PI, Math.PI);
  }

  public void pubSim(PWMSim sim, DoublePublisher pub) {
    m_cbs.add(sim.registerSpeedCallback((name, value) -> pub.set(value.getDouble()), true));
  }

  /**
   * Simulate module drive/steer velocity using some heuristics.
   */
  public double simulatedVelocity(double output, double ks, double kv) {
    // Invert feedforward.
    double result = (output - ks * Math.signum(output)) / kv;
    // Add low-frequency noise.
    // $TODO - result += 0.1 * pinkNoise.nextValue();
    // Add inertia.
    return 0.5 * m_driveEncoder.getRate() + 0.5 * result;
  }

  /**
   * Simulate module drive/steer position.
   */
  public void simulationPeriodic() {
    double currentTimeSeconds = Timer.getFPGATimestamp();
    double dtS = m_prevTimeSeconds >= 0 ? currentTimeSeconds - m_prevTimeSeconds : m_nominalDtS;
    m_prevTimeSeconds = currentTimeSeconds;
    simulationPeriodic(dtS);
  }

  /**
   * Simulate module drive/steer position for a particular amount of time.
   */
  public void simulationPeriodic(double dtS) {

    // derive velocity from motor output
    double driveVmPerS = simulatedVelocity(m_drivePwmSim.getSpeed(), DRIVE_KS, DRIVE_KV);
    double turnvRadperS = simulatedVelocity(m_turnPwmSim.getSpeed(), TURN_KS, TURN_KV);

    // observe the derived velocity
    m_drivevPubMperS.set(driveVmPerS);
    m_turnvPubRadPerS.set(turnvRadperS);

    // set the encoders using the derived velocity
    m_driveEncoderSim.setRate(driveVmPerS);
    m_driveEncoderSim.setDistance(m_driveEncoderSim.getDistance() + driveVmPerS * dtS);
    m_turnEncoderSim.setDistance(m_turnEncoderSim.getDistance() + turnvRadperS * dtS);

    // observe the encoders
    m_driveEncoderPubM.set(m_driveEncoderSim.getDistance());
    m_turnEncoderPubRad.set(m_turnEncoderSim.getDistance());
    m_driveEncoderRatePubMperS.set(m_driveEncoderSim.getRate());

  }

  public void simulationInit() {
    // nothing to do
  }

  /**
   * Returns the current state of the module.
   *
   * @return The current state of the module.
   */
  public SwerveModuleState getState() {
    return new SwerveModuleState(m_driveEncoder.getRate(),
        new Rotation2d(m_turningEncoder.getDistance()));
  }

  /**
   * Returns the current position of the module.
   *
   * @return The current position of the module.
   */
  public SwerveModulePosition getPosition() {
    return new SwerveModulePosition(m_driveEncoder.getDistance(),
        new Rotation2d(m_turningEncoder.getDistance()));
  }

  // just to see it, has no effect
  public void publishState(SwerveModuleState state) {
    m_drivevInPubMperS.set(state.speedMetersPerSecond);
    m_turnPinPubRad.set(state.angle.getRadians());
  }

  /**
   * Sets the desired state for the module.
   *
   * @param desiredState Desired state with speed and angle.
   */
  public void setDesiredState(SwerveModuleState desiredState) {
    // Optimize the reference state to avoid spinning further than 90 degrees
    SwerveModuleState state = SwerveModuleState.optimize(desiredState,
        new Rotation2d(m_turningEncoder.getDistance()));
    // state.speedMetersPerSecond max is correct at 3.
    // Calculate the drive output from the drive PID controller.
    m_driveOutput = m_drivePidController.calculate(m_driveEncoder.getRate(),
        state.speedMetersPerSecond);

    m_driveFeedforwardVal = m_driveFeedforward.calculate(state.speedMetersPerSecond);

    // Calculate the turning motor output from the turning PID controller.
    final double turnOutput = m_turningPidController.calculate(m_turningEncoder.getDistance(),
        state.angle.getRadians());

    final double turnFeedforward = m_turnFeedforward
        .calculate(m_turningPidController.getSetpoint().velocity);

    // m_driveMotor.setVoltage(driveOutput + driveFeedforward);
    m_driveMotor.set(m_driveOutput + m_driveFeedforwardVal);

    // m_turningMotor.setVoltage(turnOutput + turnFeedforward);
    m_turningMotor.set(turnOutput + turnFeedforward);
  }

  public double getM_driveOutput() {
    return m_driveMotor.get();
  }

  public double getTurnOutput() {
    return m_turningMotor.get();
  }

  /** This is required to keep test cases separate. */
  public void close() {
    m_driveMotor.close();
    m_turningMotor.close();

    // m_DrivePWMSim.close();
    // m_TurnPWMSim.close();

    m_driveEncoder.close();
    m_turningEncoder.close();

    // m_DriveEncoderSim.close();
    // m_TurnEncoderSim.close();
  }
}
