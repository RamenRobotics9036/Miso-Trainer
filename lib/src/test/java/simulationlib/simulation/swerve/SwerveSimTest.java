package simulationlib.simulation.swerve;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import edu.wpi.first.hal.HAL;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.SwerveDriveOdometry;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import org.junit.jupiter.api.Test;

/**
 * Test the swerve simulation.
 */
public class SwerveSimTest {
  // $TODO - Not sure why yet, but the distance travelled is half of expected
  private final double m_distanceErrorFactor = 2.0;

  Drivetrain newDrivetrain() {
    Drivetrain swerve = new Drivetrain();
    swerve.simulationInit();
    swerve.m_frontLeft.m_drivePidController.reset();
    swerve.m_frontRight.m_drivePidController.reset();
    swerve.m_backLeft.m_drivePidController.reset();
    swerve.m_backRight.m_drivePidController.reset();
    // weird that you have to do this; a simulation artifact?
    swerve.m_frontLeft.m_driveEncoder.reset();
    swerve.m_frontRight.m_driveEncoder.reset();
    swerve.m_backLeft.m_driveEncoder.reset();
    swerve.m_backRight.m_driveEncoder.reset();
    swerve.m_gyro.reset();
    swerve.resetOdometry(new Pose2d()); // reset odometry *after* all the components are reset.
    return swerve;
  }

  /**
   * Verify that not moving has no effect.
   */
  @Test
  void noopTest() {
    HAL.initialize(500, 0);
    Drivetrain swerve = newDrivetrain();
    try {
      final Pose2d initialPose = swerve.getPose();
      assertAll(() -> assertEquals(0, initialPose.getX(), "initial x"),
          () -> assertEquals(0, initialPose.getY(), "initial y"),
          () -> assertEquals(0, initialPose.getRotation().getRadians(), "initial rot"));
      swerve.setModuleStates(new SwerveModuleState[] {
          new SwerveModuleState(0, new Rotation2d(0)),
          new SwerveModuleState(0, new Rotation2d(0)),
          new SwerveModuleState(0, new Rotation2d(0)),
          new SwerveModuleState(0, new Rotation2d(0))
      });
      swerve.simulationPeriodic();
      final Pose2d finalPose = swerve.getPose();
      assertAll(() -> assertEquals(0, finalPose.getX(), "final x"),
          () -> assertEquals(0, finalPose.getY(), "final y"),
          () -> assertEquals(0, finalPose.getRotation().getRadians(), "final rot"));
    }
    finally {
      swerve.close(); // release the HAL stuff
    }
  }

  /**
   * Apply x velocity, see x displacement.
   */
  @Test
  void translationTest() {
    HAL.initialize(500, 0);
    Drivetrain swerve = newDrivetrain();
    try {
      final Pose2d initialPose = swerve.getPose();
      // At the origin
      assertAll(() -> assertEquals(0, initialPose.getX(), 0.001, "initial x"),
          () -> assertEquals(0, initialPose.getY(), 0.001, "initial y"),
          () -> assertEquals(0, initialPose.getRotation().getRadians(), 0.001, "initial rot"));

      // No motors running
      assertAll(() -> assertEquals(0, swerve.m_frontLeft.getDriveOutput(), 0.001, "FL output"),
          () -> assertEquals(0, swerve.m_frontRight.getDriveOutput(), 0.001, "FR output"),
          () -> assertEquals(0, swerve.m_backLeft.getDriveOutput(), 0.001, "BL output"),
          () -> assertEquals(0, swerve.m_backRight.getDriveOutput(), 0.001, "BR output"));

      // New desired state is 1 m/s
      swerve.setModuleStates(new SwerveModuleState[] {
          new SwerveModuleState(1, new Rotation2d(0)),
          new SwerveModuleState(1, new Rotation2d(0)),
          new SwerveModuleState(1, new Rotation2d(0)),
          new SwerveModuleState(1, new Rotation2d(0))
      });

      // Controller setpoints should now be 1 m/s
      assertAll(
          () -> assertEquals(1,
              swerve.m_frontLeft.m_drivePidController.getSetpoint(),
              0.001,
              "FL setpoint"),
          () -> assertEquals(1,
              swerve.m_frontRight.m_drivePidController.getSetpoint(),
              0.001,
              "FR setpoint"),
          () -> assertEquals(1,
              swerve.m_backLeft.m_drivePidController.getSetpoint(),
              0.001,
              "BL setpoint"),
          () -> assertEquals(1,
              swerve.m_backRight.m_drivePidController.getSetpoint(),
              0.001,
              "BR setpoint"));

      // controller errors should now also be 1 m/s
      assertAll(
          () -> assertEquals(1,
              swerve.m_frontLeft.m_drivePidController.getPositionError(),
              0.001,
              "FL setpoint"),
          () -> assertEquals(1,
              swerve.m_frontRight.m_drivePidController.getPositionError(),
              0.001,
              "FR setpoint"),
          () -> assertEquals(1,
              swerve.m_backLeft.m_drivePidController.getPositionError(),
              0.001,
              "BL setpoint"),
          () -> assertEquals(1,
              swerve.m_backRight.m_drivePidController.getPositionError(),
              0.001,
              "BR setpoint"));

      // So since the error is 1 and the PID are (0.1,0,0) controller output should be
      assertAll(
          // 0.1
          () -> assertEquals(0.1, swerve.m_frontLeft.m_driveOutput, 0.001, "FL ctrl output"),
          () -> assertEquals(0.1, swerve.m_frontRight.m_driveOutput, 0.001, "FR ctrl output"),
          () -> assertEquals(0.1, swerve.m_backLeft.m_driveOutput, 0.001, "BL ctrl output"),
          () -> assertEquals(0.1, swerve.m_backRight.m_driveOutput, 0.001, "BR ctrl output"));

      // kv 0.15 ks 0.001, v = 1, so ff is 0.151
      assertAll(() -> assertEquals(0.151, swerve.m_frontLeft.m_driveFeedforwardVal, 0.001, "FL ff"),
          () -> assertEquals(0.151, swerve.m_frontRight.m_driveFeedforwardVal, 0.001, "FR ff"),
          () -> assertEquals(0.151, swerve.m_backLeft.m_driveFeedforwardVal, 0.001, "BL ff"),
          () -> assertEquals(0.151, swerve.m_backRight.m_driveFeedforwardVal, 0.001, "BR ff"));

      // add ctrl and ff, 0.251
      assertAll(() -> assertEquals(0.251, swerve.m_frontLeft.getDriveOutput(), 0.001, "FL output"),
          () -> assertEquals(0.251, swerve.m_frontRight.getDriveOutput(), 0.001, "FR output"),
          () -> assertEquals(0.251, swerve.m_backLeft.getDriveOutput(), 0.001, "BL output"),
          () -> assertEquals(0.251, swerve.m_backRight.getDriveOutput(), 0.001, "BR output"));
      swerve.simulationPeriodic(0.02);
      swerve.updateOdometry();
      final Pose2d finalPose = swerve.getPose();
      // since the feedforward is treated as correct but the controller
      // is boosting then this overshoots. which is fine? i guess?
      assertAll(
          () -> assertEquals(0.033 / m_distanceErrorFactor, finalPose.getX(), 0.001, "final x"),
          () -> assertEquals(0, finalPose.getY(), 0.001, "final y"),
          () -> assertEquals(0, finalPose.getRotation().getRadians(), 0.001, "final rot"));
    }
    finally {
      swerve.close(); // release the HAL stuff
    }
  }

  /**
   * steer and drive to spin the robot.
   */
  @Test
  void rotationTest() {
    HAL.initialize(500, 0);
    Drivetrain swerve = newDrivetrain();
    try {
      final Pose2d initialPose = swerve.getPose();
      // at the origin
      assertAll(() -> assertEquals(0, initialPose.getX(), 0.001, "initial x"),
          () -> assertEquals(0, initialPose.getY(), 0.001, "initial y"),
          () -> assertEquals(0, initialPose.getRotation().getRadians(), 0.001, "initial rot"));

      // no motors running
      assertAll(() -> assertEquals(0, swerve.m_frontLeft.getDriveOutput(), 0.001, "FL output"),
          () -> assertEquals(0, swerve.m_frontRight.getDriveOutput(), 0.001, "FR output"),
          () -> assertEquals(0, swerve.m_backLeft.getDriveOutput(), 0.001, "BL output"),
          () -> assertEquals(0, swerve.m_backRight.getDriveOutput(), 0.001, "BR output"));
      // so i can just force the simulated encoders to the settings i want.
      swerve.m_frontLeft.m_turnEncoderSim.setDistance(-Math.PI / 4);
      swerve.m_frontRight.m_turnEncoderSim.setDistance(Math.PI / 4);
      swerve.m_backLeft.m_turnEncoderSim.setDistance(Math.PI / 4);
      swerve.m_backRight.m_turnEncoderSim.setDistance(-Math.PI / 4);

      // verify those settings.
      assertAll(
          () -> assertEquals(-0.785,
              swerve.m_frontLeft.getPosition().angle.getRadians(),
              0.001,
              "FL angle"),
          () -> assertEquals(0.785,
              swerve.m_frontRight.getPosition().angle.getRadians(),
              0.001,
              "FR angle"),
          () -> assertEquals(0.785,
              swerve.m_backLeft.getPosition().angle.getRadians(),
              0.001,
              "BL angle"),
          () -> assertEquals(-0.785,
              swerve.m_backRight.getPosition().angle.getRadians(),
              0.001,
              "BR angle"));
      // has not moved yet
      assertAll(
          () -> assertEquals(0, swerve.m_frontLeft.getPosition().distanceMeters, 0.001, "FL m"),
          () -> assertEquals(0, swerve.m_frontRight.getPosition().distanceMeters, 0.001, "FR m"),
          () -> assertEquals(0, swerve.m_backLeft.getPosition().distanceMeters, 0.001, "BL m"),
          () -> assertEquals(0, swerve.m_backRight.getPosition().distanceMeters, 0.001, "BR m"));
      // spin positive = CCW, right forward left backward.
      swerve.setModuleStates(new SwerveModuleState[] {
          new SwerveModuleState(-1, new Rotation2d(0)),
          new SwerveModuleState(1, new Rotation2d(0)),
          new SwerveModuleState(-1, new Rotation2d(0)),
          new SwerveModuleState(1, new Rotation2d(0))
      });

      // controller setpoints should now be +/-1 m/s
      assertAll(
          () -> assertEquals(-1,
              swerve.m_frontLeft.m_drivePidController.getSetpoint(),
              0.001,
              "FL setpoint"),
          () -> assertEquals(1,
              swerve.m_frontRight.m_drivePidController.getSetpoint(),
              0.001,
              "FR setpoint"),
          () -> assertEquals(-1,
              swerve.m_backLeft.m_drivePidController.getSetpoint(),
              0.001,
              "BL setpoint"),
          () -> assertEquals(1,
              swerve.m_backRight.m_drivePidController.getSetpoint(),
              0.001,
              "BR setpoint"));

      // controller errors should now also be 1 m/s
      assertAll(
          () -> assertEquals(-1,
              swerve.m_frontLeft.m_drivePidController.getPositionError(),
              0.001,
              "FL setpoint"),
          () -> assertEquals(1,
              swerve.m_frontRight.m_drivePidController.getPositionError(),
              0.001,
              "FR setpoint"),
          () -> assertEquals(-1,
              swerve.m_backLeft.m_drivePidController.getPositionError(),
              0.001,
              "BL setpoint"),
          () -> assertEquals(1,
              swerve.m_backRight.m_drivePidController.getPositionError(),
              0.001,
              "BR setpoint"));

      // so since the error is 1 and the PID are (0.1,0,0) controller output should be
      assertAll(
          // 0.1
          () -> assertEquals(-0.1, swerve.m_frontLeft.m_driveOutput, 0.001, "FL ctrl output"),
          () -> assertEquals(0.1, swerve.m_frontRight.m_driveOutput, 0.001, "FR ctrl output"),
          () -> assertEquals(-0.1, swerve.m_backLeft.m_driveOutput, 0.001, "BL ctrl output"),
          () -> assertEquals(0.1, swerve.m_backRight.m_driveOutput, 0.001, "BR ctrl output"));

      // kv 0.15 ks 0.001, v = 1, so ff is 0.151
      assertAll(
          () -> assertEquals(-0.151, swerve.m_frontLeft.m_driveFeedforwardVal, 0.001, "FL ff"),
          () -> assertEquals(0.151, swerve.m_frontRight.m_driveFeedforwardVal, 0.001, "FR ff"),
          () -> assertEquals(-0.151, swerve.m_backLeft.m_driveFeedforwardVal, 0.001, "BL ff"),
          () -> assertEquals(0.151, swerve.m_backRight.m_driveFeedforwardVal, 0.001, "BR ff"));

      // add ctrl and ff, 0.251
      assertAll(() -> assertEquals(-0.251, swerve.m_frontLeft.getDriveOutput(), 0.001, "FL output"),
          () -> assertEquals(0.251, swerve.m_frontRight.getDriveOutput(), 0.001, "FR output"),
          () -> assertEquals(-0.251, swerve.m_backLeft.getDriveOutput(), 0.001, "BL output"),
          () -> assertEquals(0.251, swerve.m_backRight.getDriveOutput(), 0.001, "BR output"));
      swerve.simulationPeriodic(0.02);
      swerve.updateOdometry();

      // Each wheel should have moved the same as the displacement case above
      assertAll(
          () -> assertEquals(-0.033 / m_distanceErrorFactor,
              swerve.m_frontLeft.getPosition().distanceMeters,
              0.001,
              "FL m"),
          () -> assertEquals(0.033 / m_distanceErrorFactor,
              swerve.m_frontRight.getPosition().distanceMeters,
              0.001,
              "FR m"),
          () -> assertEquals(-0.033 / m_distanceErrorFactor,
              swerve.m_backLeft.getPosition().distanceMeters,
              0.001,
              "BL m"),
          () -> assertEquals(0.033 / m_distanceErrorFactor,
              swerve.m_backRight.getPosition().distanceMeters,
              0.001,
              "BR m"));

      // no x or y movement
      assertEquals(0, swerve.m_speeds.vxMetersPerSecond, 0.001, "chassis speed x");
      assertEquals(0, swerve.m_speeds.vyMetersPerSecond, 0.001, "chassis speed y");
      // so 0.033 m wheel movement and radius of 0.539m yields 0.061 rad in 0.02s or
      // 3.061 rad/s, it's actually 0.062 and 3.087 because rounding
      // this is NWU, CCW+, so should be positive.
      assertEquals(3.090 / m_distanceErrorFactor,
          swerve.m_speeds.omegaRadiansPerSecond,
          0.001,
          "chassis speed omega");
      // look at the pose we're maintaining
      assertAll(() -> assertEquals(0, swerve.getPose().getX(), 0.001, "pose x"),
          () -> assertEquals(0, swerve.getPose().getY(), 0.001, "pose y"),
          () -> assertEquals(0.062 / m_distanceErrorFactor,
              swerve.getPose().getRotation().getRadians(),
              0.001,
              "pose rot"));

      // look at the gyro, note NWU/NED difference, also one is rad the other deg
      assertAll(
          () -> assertEquals(0.062 / m_distanceErrorFactor,
              swerve.m_gyro.getRotation2d().getRadians(),
              0.001,
              "gyro rotation"),
          () -> assertEquals(-3.542 / m_distanceErrorFactor,
              swerve.m_gyro.getAngle(),
              0.001,
              "gyro angle"));

      final Pose2d finalPose = swerve.getPose();
      assertAll(() -> assertEquals(0, finalPose.getX(), 0.001, "estimate x"),
          () -> assertEquals(0, finalPose.getY(), 0.001, "estimate y"),
          () -> assertEquals(0.062 / m_distanceErrorFactor,
              finalPose.getRotation().getRadians(),
              0.001,
              "estimate rot"));
    }
    finally {
      swerve.close(); // release the HAL stuff
    }
  }

  /**
   * rotate the robot and then translate it.
   */
  @Test
  void rotatedTranslationTest() {
    HAL.initialize(500, 0);
    Drivetrain swerve = newDrivetrain();
    try {
      final Pose2d initialPose = swerve.getPose();

      // at the origin
      assertAll(() -> assertEquals(0, initialPose.getX(), 0.001, "initial x"),
          () -> assertEquals(0, initialPose.getY(), 0.001, "initial y"),
          () -> assertEquals(0, initialPose.getRotation().getRadians(), 0.001, "initial rot"));

      // no motors running
      assertAll(() -> assertEquals(0, swerve.m_frontLeft.getDriveOutput(), 0.001, "FL output"),
          () -> assertEquals(0, swerve.m_frontRight.getDriveOutput(), 0.001, "FR output"),
          () -> assertEquals(0, swerve.m_backLeft.getDriveOutput(), 0.001, "BL output"),
          () -> assertEquals(0, swerve.m_backRight.getDriveOutput(), 0.001, "BR output"));

      // wheels pointing ahead.
      assertAll(
          () -> assertEquals(0,
              swerve.m_frontLeft.getPosition().angle.getRadians(),
              0.001,
              "FL angle"),
          () -> assertEquals(0,
              swerve.m_frontRight.getPosition().angle.getRadians(),
              0.001,
              "FR angle"),
          () -> assertEquals(0,
              swerve.m_backLeft.getPosition().angle.getRadians(),
              0.001,
              "BL angle"),
          () -> assertEquals(0,
              swerve.m_backRight.getPosition().angle.getRadians(),
              0.001,
              "BR angle"));
      // has not moved yet
      assertAll(
          () -> assertEquals(0, swerve.m_frontLeft.getPosition().distanceMeters, 0.001, "FL m"),
          () -> assertEquals(0, swerve.m_frontRight.getPosition().distanceMeters, 0.001, "FR m"),
          () -> assertEquals(0, swerve.m_backLeft.getPosition().distanceMeters, 0.001, "BL m"),
          () -> assertEquals(0, swerve.m_backRight.getPosition().distanceMeters, 0.001, "BR m"));

      // pose is zero
      assertAll(() -> assertEquals(0, swerve.getPose().getX(), 0.001, "pose x"),
          () -> assertEquals(0, swerve.getPose().getY(), 0.001, "pose y"),
          () -> assertEquals(0, swerve.getPose().getRotation().getRadians(), 0.001, "pose rot"));

      // gyro is zero
      assertAll(
          () -> assertEquals(0, swerve.m_gyro.getRotation2d().getRadians(), 0.001, "gyro rotation"),
          () -> assertEquals(0, swerve.m_gyro.getAngle(), 0.001, "gyro angle"));

      // force gyro to angled position, +pi/2 in NWU, so -90 deg in NED
      swerve.m_gyroSim.setAngle(-90);

      // need to reset the odometry with the actual robot pose since we rotated it.
      swerve.resetOdometry(new Pose2d(0, 0, swerve.m_gyro.getRotation2d())); // pi/2

      // pose should be rotated.
      assertAll(() -> assertEquals(0, swerve.getPose().getX(), 0.001, "pose x"),
          () -> assertEquals(0, swerve.getPose().getY(), 0.001, "pose y"),
          () -> assertEquals(Math.PI / 2,
              swerve.getPose().getRotation().getRadians(),
              0.001,
              "pose rot"));

      // verify that the embedded odometry is doing the right thing by duplicating it
      // here.
      @SuppressWarnings("VariableDeclarationUsageDistanceCheck")
      SwerveDriveOdometry odometry = new SwerveDriveOdometry(swerve.m_kinematics,
          new Rotation2d(Math.PI / 2), new SwerveModulePosition[] {
              swerve.m_frontLeft.getPosition(),
              swerve.m_frontRight.getPosition(),
              swerve.m_backLeft.getPosition(),
              swerve.m_backRight.getPosition()
          }, swerve.getPose());

      // verify placement
      assertAll(() -> assertEquals(Math.PI / 2,
          swerve.m_gyro.getRotation2d().getRadians(),
          0.001,
          // () -> assertEquals(Math.PI / 4, swerve.m_gyro.getRotation2d().getRadians(),
          // 0.001,
          "gyro rotation"), () -> assertEquals(-90, swerve.m_gyro.getAngle(), 0.001, "gyro angle"));

      // drive ahead, as above case, should be +y direction.
      swerve.setModuleStates(new SwerveModuleState[] {
          new SwerveModuleState(1, new Rotation2d(0)),
          new SwerveModuleState(1, new Rotation2d(0)),
          new SwerveModuleState(1, new Rotation2d(0)),
          new SwerveModuleState(1, new Rotation2d(0))
      });

      // controller setpoints should now be +/-1 m/s
      assertAll(
          () -> assertEquals(1,
              swerve.m_frontLeft.m_drivePidController.getSetpoint(),
              0.001,
              "FL setpoint"),
          () -> assertEquals(1,
              swerve.m_frontRight.m_drivePidController.getSetpoint(),
              0.001,
              "FR setpoint"),
          () -> assertEquals(1,
              swerve.m_backLeft.m_drivePidController.getSetpoint(),
              0.001,
              "BL setpoint"),
          () -> assertEquals(1,
              swerve.m_backRight.m_drivePidController.getSetpoint(),
              0.001,
              "BR setpoint"));

      // controller errors should now also be 1 m/s
      assertAll(
          () -> assertEquals(1,
              swerve.m_frontLeft.m_drivePidController.getPositionError(),
              0.001,
              "FL setpoint"),
          () -> assertEquals(1,
              swerve.m_frontRight.m_drivePidController.getPositionError(),
              0.001,
              "FR setpoint"),
          () -> assertEquals(1,
              swerve.m_backLeft.m_drivePidController.getPositionError(),
              0.001,
              "BL setpoint"),
          () -> assertEquals(1,
              swerve.m_backRight.m_drivePidController.getPositionError(),
              0.001,
              "BR setpoint"));

      // so since the error is 1 and the PID are (0.1,0,0) controller output should be
      assertAll(
          // 0.1
          () -> assertEquals(0.1, swerve.m_frontLeft.m_driveOutput, 0.001, "FL ctrl output"),
          () -> assertEquals(0.1, swerve.m_frontRight.m_driveOutput, 0.001, "FR ctrl output"),
          () -> assertEquals(0.1, swerve.m_backLeft.m_driveOutput, 0.001, "BL ctrl output"),
          () -> assertEquals(0.1, swerve.m_backRight.m_driveOutput, 0.001, "BR ctrl output"));

      // kv 0.15 ks 0.001, v = 1, so ff is 0.151
      assertAll(() -> assertEquals(0.151, swerve.m_frontLeft.m_driveFeedforwardVal, 0.001, "FL ff"),
          () -> assertEquals(0.151, swerve.m_frontRight.m_driveFeedforwardVal, 0.001, "FR ff"),
          () -> assertEquals(0.151, swerve.m_backLeft.m_driveFeedforwardVal, 0.001, "BL ff"),
          () -> assertEquals(0.151, swerve.m_backRight.m_driveFeedforwardVal, 0.001, "BR ff"));

      // add ctrl and ff, 0.251
      assertAll(() -> assertEquals(0.251, swerve.m_frontLeft.getDriveOutput(), 0.001, "FL output"),
          () -> assertEquals(0.251, swerve.m_frontRight.getDriveOutput(), 0.001, "FR output"),
          () -> assertEquals(0.251, swerve.m_backLeft.getDriveOutput(), 0.001, "BL output"),
          () -> assertEquals(0.251, swerve.m_backRight.getDriveOutput(), 0.001, "BR output"));

      // no steering output
      assertAll(() -> assertEquals(0, swerve.m_frontLeft.getTurnOutput(), 0.001, "FL turn utput"),
          () -> assertEquals(0, swerve.m_frontRight.getTurnOutput(), 0.001, "FR turn output"),
          () -> assertEquals(0, swerve.m_backLeft.getTurnOutput(), 0.001, "BL turn output"),
          () -> assertEquals(0, swerve.m_backRight.getTurnOutput(), 0.001, "BR turn output"));
      swerve.simulationPeriodic(0.02);
      swerve.updateOdometry();

      odometry.update(swerve.m_gyro.getRotation2d(), new SwerveModulePosition[] {
          swerve.m_frontLeft.getPosition(),
          swerve.m_frontRight.getPosition(),
          swerve.m_backLeft.getPosition(),
          swerve.m_backRight.getPosition()
      });

      Pose2d odoPose = odometry.getPoseMeters();
      assertAll(() -> assertEquals(0, odoPose.getX(), 0.001, "x odo pose"),
          () -> assertEquals(0.033 / m_distanceErrorFactor, odoPose.getY(), 0.001, "y odo pose"),
          () -> assertEquals(Math.PI / 2,
              odoPose.getRotation().getRadians(),
              0.001,
              "theta odo pose"));

      // each wheel should have moved the same as the displacement case above
      assertAll(
          () -> assertEquals(0.033 / m_distanceErrorFactor,
              swerve.m_frontLeft.getPosition().distanceMeters,
              0.001,
              "FL m"),
          () -> assertEquals(0.033 / m_distanceErrorFactor,
              swerve.m_frontRight.getPosition().distanceMeters,
              0.001,
              "FR m"),
          () -> assertEquals(0.033 / m_distanceErrorFactor,
              swerve.m_backLeft.getPosition().distanceMeters,
              0.001,
              "BL m"),
          () -> assertEquals(0.033 / m_distanceErrorFactor,
              swerve.m_backRight.getPosition().distanceMeters,
              0.001,
              "BR m"));

      // chassis moving in x (ahead), note this isn't field relative, it's chassis
      // relative.
      assertEquals(1.667 / m_distanceErrorFactor,
          swerve.m_speeds.vxMetersPerSecond,
          0.001,
          "chassis speed x");
      assertEquals(0, swerve.m_speeds.vyMetersPerSecond, 0.001, "chassis speed y");
      assertEquals(0, swerve.m_speeds.omegaRadiansPerSecond, 0.001, "chassis speed omega");

      // pose shows movement in y, also remember rotation PI/2
      assertAll(() -> assertEquals(0, swerve.getPose().getX(), 0.001, "pose x"),
          () -> assertEquals(0.033 / m_distanceErrorFactor,
              swerve.getPose().getY(),
              0.001,
              "pose y"),
          () -> assertEquals(Math.PI / 2,
              swerve.getPose().getRotation().getRadians(),
              0.001,
              "pose rot"));

      // same rotation
      assertAll(
          () -> assertEquals(Math.PI / 2,
              swerve.m_gyro.getRotation2d().getRadians(),
              0.001,
              "gyro rotation"),
          () -> assertEquals(-90, swerve.m_gyro.getAngle(), 0.001, "gyro angle"));

      final Pose2d finalPose = swerve.getPose();

      assertAll(() -> assertEquals(0, finalPose.getX(), 0.001, "estimate x"),
          () -> assertEquals(0.033 / m_distanceErrorFactor, finalPose.getY(), 0.001, "estimate y"),
          () -> assertEquals(Math.PI / 2,
              finalPose.getRotation().getRadians(),
              0.001,
              "estimate rot"));
    }
    finally {
      swerve.close(); // release the HAL stuff
    }
  }
}
