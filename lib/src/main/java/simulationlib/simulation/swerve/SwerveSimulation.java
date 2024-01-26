// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.
// Swerve simulation implementation from: https://github.com/truher/swerve-sim

package simulationlib.simulation.swerve;

import edu.wpi.first.math.estimator.SwerveDrivePoseEstimator;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.networktables.DoubleArrayPublisher;
import edu.wpi.first.networktables.DoublePublisher;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.networktables.StringPublisher;
import edu.wpi.first.wpilibj.AnalogGyro;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.simulation.AnalogGyroSim;
import edu.wpi.first.wpilibj.simulation.CallbackStore;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import java.util.ArrayList;
import java.util.List;

/** Represents a swerve drive style drivetrain. */
public class SwerveSimulation extends SubsystemBase {
  // $TODO - Is it possible that this is why the test is off by factor of two? 3 vs. 6
  // 3 m/s
  public static final double kMaxSpeed = 6.0;
  // pi rad/s
  public static final double kMaxAngularSpeed = 6 * Math.PI;

  private final Translation2d m_frontLeftLocation = new Translation2d(0.381, 0.381);
  private final Translation2d m_frontRightLocation = new Translation2d(0.381, -0.381);
  private final Translation2d m_backLeftLocation = new Translation2d(-0.381, 0.381);
  private final Translation2d m_backRightLocation = new Translation2d(-0.381, -0.381);

  // package visibility for testing
  final SwerveModule m_frontLeft = new SwerveModule("FrontLeft", 1, 2, 0, 1, 2, 3);
  final SwerveModule m_frontRight = new SwerveModule("FrontRight", 3, 4, 4, 5, 6, 7);
  final SwerveModule m_backLeft = new SwerveModule("BackLeft", 5, 6, 8, 9, 10, 11);
  final SwerveModule m_backRight = new SwerveModule("BackRight", 7, 8, 12, 13, 14, 15);

  final AnalogGyro m_gyro = new AnalogGyro(0);
  // note gyro is NED, robot is NWU, see inversion below.
  final AnalogGyroSim m_gyroSim = new AnalogGyroSim(m_gyro);

  final SwerveDriveKinematics m_kinematics = new SwerveDriveKinematics(m_frontLeftLocation,
      m_frontRightLocation, m_backLeftLocation, m_backRightLocation);

  // Pose2d robotPose = new Pose2d();
  private double m_prevTimeSeconds = Timer.getFPGATimestamp();
  private final double m_nominalDtS = 0.02; // Seconds

  /*
   * Here we use SwerveDrivePoseEstimator so that we can fuse odometry readings.
   * The numbers used below are robot specific, and should be tuned.
   */
  private final SwerveDrivePoseEstimator m_poseEstimator = new SwerveDrivePoseEstimator(
      m_kinematics, m_gyro.getRotation2d(), // NWU
      new SwerveModulePosition[] {
          m_frontLeft.getPosition(),
          m_frontRight.getPosition(),
          m_backLeft.getPosition(),
          m_backRight.getPosition()
      }, new Pose2d());

  // $TODO - This networking stuff should go away.
  NetworkTableInstance m_inst = NetworkTableInstance.getDefault();

  DoublePublisher m_xspeedPubMperS = m_inst.getTable("desired").getDoubleTopic("xspeed m_s")
      .publish();
  DoublePublisher m_yspeedPubMperS = m_inst.getTable("desired").getDoubleTopic("yspeed m_s")
      .publish();
  DoublePublisher m_thetaSpeedPubRadPerS = m_inst.getTable("desired")
      .getDoubleTopic("thetaspeed rad_s").publish();

  DoublePublisher m_actualXspeedPubMperS = m_inst.getTable("actual").getDoubleTopic("xspeed m_s")
      .publish();
  DoublePublisher m_actualYspeedPubMperS = m_inst.getTable("actual").getDoubleTopic("yspeed m_s")
      .publish();
  DoublePublisher m_actualThetaSpeedPubRadPerS = m_inst.getTable("actual")
      .getDoubleTopic("thetaspeed rad_s").publish();

  DoubleArrayPublisher m_fieldPub;
  StringPublisher m_fieldTypePub;

  List<CallbackStore> m_cbs = new ArrayList<CallbackStore>();

  ChassisSpeeds m_speeds;

  /**
   * Constructor.
   */
  public SwerveSimulation() {
    m_gyro.reset();
    m_inst.startClient4("blarg");
    NetworkTable fieldTable = m_inst.getTable("field");
    m_fieldPub = fieldTable.getDoubleArrayTopic("robotPose").publish();
    m_fieldTypePub = fieldTable.getStringTopic(".type").publish();
    m_fieldTypePub.set("Field2d");
  }

  public Pose2d getPose() {
    return m_poseEstimator.getEstimatedPosition();
  }

  /**
   * Method to reset the robot's odometry.
   */
  public void resetOdometry(Pose2d pose) {
    m_poseEstimator.resetPosition(m_gyro.getRotation2d(), // NWU
        new SwerveModulePosition[] {
            m_frontLeft.getPosition(),
            m_frontRight.getPosition(),
            m_backLeft.getPosition(),
            m_backRight.getPosition()
        },
        pose);
  }

  /**
   * Method to drive the robot using joystick info.
   *
   * @param xspeedMperS   Speed of the robot in the x direction (forward).
   * @param yspeedMperS   Speed of the robot in the y direction (sideways).
   * @param rotRadperS    Angular rate of the robot.
   * @param fieldRelative Whether the provided x and y speeds are relative to the
   *                      field.
   */
  public void drive(double xspeedMperS,
      double yspeedMperS,
      double rotRadperS,
      boolean fieldRelative) {
    m_xspeedPubMperS.set(xspeedMperS);
    m_yspeedPubMperS.set(yspeedMperS);
    m_thetaSpeedPubRadPerS.set(rotRadperS);

    SwerveModuleState[] swerveModuleStates = m_kinematics.toSwerveModuleStates(fieldRelative
        ? ChassisSpeeds
            .fromFieldRelativeSpeeds(xspeedMperS, yspeedMperS, rotRadperS, m_gyro.getRotation2d())
        : new ChassisSpeeds(xspeedMperS, yspeedMperS, rotRadperS));

    setModuleStates(swerveModuleStates);
  }

  /**
   * Method to set desired positions for each module.
   */
  public void setModuleStates(SwerveModuleState[] swerveModuleStates) {
    m_frontLeft.publishState(swerveModuleStates[0]);
    m_frontRight.publishState(swerveModuleStates[1]);
    m_backLeft.publishState(swerveModuleStates[2]);
    m_backRight.publishState(swerveModuleStates[3]);

    SwerveDriveKinematics.desaturateWheelSpeeds(swerveModuleStates, kMaxSpeed); // 3m/s max

    m_frontLeft.setDesiredState(swerveModuleStates[0]);
    m_frontRight.setDesiredState(swerveModuleStates[1]);
    m_backLeft.setDesiredState(swerveModuleStates[2]);
    m_backRight.setDesiredState(swerveModuleStates[3]);
  }

  /**
   * Drive one module directly.
   */
  public void test(double drive, double turn) {
    m_frontLeft.setDesiredState(new SwerveModuleState(drive, new Rotation2d(turn) // NWU
    ));
  }

  /** Updates the field relative position of the robot. */
  public void updateOdometry() {
    m_poseEstimator.update(m_gyro.getRotation2d(), // NWU
        new SwerveModulePosition[] {
            m_frontLeft.getPosition(),
            m_frontRight.getPosition(),
            m_backLeft.getPosition(),
            m_backRight.getPosition()
        });

    Pose2d newEstimate = m_poseEstimator.getEstimatedPosition();
    m_fieldPub.set(new double[] {
        newEstimate.getX(), newEstimate.getY(), newEstimate.getRotation().getDegrees()
    });

    // Also apply vision measurements. We use 0.3 seconds in the past as an example
    // -- on a real robot, this must be calculated based either on latency or
    // timestamps.

    // m_poseEstimator.addVisionMeasurement(
    // ExampleGlobalMeasurementSensor.getEstimatedGlobalPose(
    // m_poseEstimator.getEstimatedPosition()),
    // Timer.getFPGATimestamp() - 0.3);

  }

  /**
   * Initialization.
   */
  public void simulationInit() {
    m_frontLeft.simulationInit();
    m_frontRight.simulationInit();
    m_backLeft.simulationInit();
    m_backRight.simulationInit();
  }

  /**
   * Simulation periodic.
   */
  public void simulationPeriodic() {
    double currentTimeSeconds = Timer.getFPGATimestamp();
    double dtS = m_prevTimeSeconds >= 0 ? currentTimeSeconds - m_prevTimeSeconds : m_nominalDtS;
    m_prevTimeSeconds = currentTimeSeconds;
    simulationPeriodic(dtS);
  }

  /**
   * Simulation periodic.
   */
  public void simulationPeriodic(final double dtS) {
    m_frontLeft.simulationPeriodic(dtS);
    m_frontRight.simulationPeriodic(dtS);
    m_backLeft.simulationPeriodic(dtS);
    m_backRight.simulationPeriodic(dtS);

    // in simulation these should be the values we just set
    // in SwerveModule.simulationPeriodic(), so we don't need
    // to adjust them *again*, just use them to update the gyro.
    SwerveModuleState[] states = new SwerveModuleState[] {
        m_frontLeft.getState(),
        m_frontRight.getState(),
        m_backLeft.getState(),
        m_backRight.getState()
    };

    // rotational velocity is correct here.
    m_speeds = m_kinematics.toChassisSpeeds(states);

    // finally adjust the simulator gyro.
    // the pose estimator figures out the X/Y part but it depends on the gyro.
    // since omega is the same in both coordinate schemes, just use that.
    double oldAngleDeg = m_gyroSim.getAngle();
    double dthetaDeg = -1.0 * new Rotation2d(m_speeds.omegaRadiansPerSecond * dtS).getDegrees();
    double newAngleDeg = oldAngleDeg + dthetaDeg;
    // note that the "angle" in a gyro is NED, but everything else (e.g robot pose)
    // is NWU, so invert here.
    m_gyroSim.setAngle(newAngleDeg);

    m_xspeedPubMperS.set(m_speeds.vxMetersPerSecond);
    m_yspeedPubMperS.set(m_speeds.vyMetersPerSecond);
    m_thetaSpeedPubRadPerS.set(-1.0 * m_speeds.omegaRadiansPerSecond);
  }

  /**
   * Unload the swerve modules.
   */
  public void close() {
    m_frontLeft.close();
    m_frontRight.close();
    m_backLeft.close();
    m_backRight.close();
    m_gyro.close();
  }
}
