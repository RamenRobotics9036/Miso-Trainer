// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package simulationlib.simulation.swerve;

import static simulationlib.simulation.swerve.SwerveSimConstants.Swerve.kMaxRotationRadiansPerSecond;
import static simulationlib.simulation.swerve.SwerveSimConstants.Swerve.kMaxSpeedMetersPerSecond;
import static simulationlib.simulation.swerve.SwerveSimConstants.Swerve.kModuleTranslations;
import static simulationlib.simulation.swerve.SwerveSimConstants.Swerve.kSwerveKinematics;

import com.ctre.phoenix.sensors.CANCoder;
import com.ctre.phoenix.sensors.Pigeon2;
import com.ctre.phoenix.unmanaged.Unmanaged;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.kinematics.SwerveDriveOdometry;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.math.util.Units;
import java.util.HashMap;
import java.util.Map;
import simulationlib.simulation.swerve.SwerveSimConstants.Can;
import simulationlib.simulation.swerve.SwerveSimConstants.Swerve;
import simulationlib.simulation.swerve.SwerveSimConstants.Swerve.ModulePosition;

/**
 * Swerve drive implementation.
 */
// $TODO - This shouldnt be a subsystem
public class SwerveDrive {

  private final HashMap<ModulePosition, SwerveModule> m_swerveModules = new HashMap<>(
      Map.of(ModulePosition.FRONT_LEFT,
          new SwerveModule(0,
              new CANSparkMax(Can.frontLeftTurnMotor, CANSparkMaxLowLevel.MotorType.kBrushless),
              new CANSparkMax(Can.frontLeftDriveMotor, CANSparkMaxLowLevel.MotorType.kBrushless),
              new CANCoder(Can.frontLeftCanCoder), 0),
          ModulePosition.FRONT_RIGHT,
          new SwerveModule(1,
              new CANSparkMax(Can.frontRightTurnMotor, CANSparkMaxLowLevel.MotorType.kBrushless),
              new CANSparkMax(Can.frontRightDriveMotor, CANSparkMaxLowLevel.MotorType.kBrushless),
              new CANCoder(Can.frontRightCanCoder), 0),
          ModulePosition.BACK_LEFT,
          new SwerveModule(2,
              new CANSparkMax(Can.backLeftTurnMotor, CANSparkMaxLowLevel.MotorType.kBrushless),
              new CANSparkMax(Can.backLeftDriveMotor, CANSparkMaxLowLevel.MotorType.kBrushless),
              new CANCoder(Can.backLeftCanCoder), 0),
          ModulePosition.BACK_RIGHT,
          new SwerveModule(3,
              new CANSparkMax(Can.backRightTurnMotor, CANSparkMaxLowLevel.MotorType.kBrushless),
              new CANSparkMax(Can.backRightDriveMotor, CANSparkMaxLowLevel.MotorType.kBrushless),
              new CANCoder(Can.backRightCanCoder), 0)));

  private Pigeon2 m_pigeon = new Pigeon2(Can.pigeon);

  private SwerveDriveOdometry m_odometry = new SwerveDriveOdometry(Swerve.kSwerveKinematics,
      getHeadingRotation2d(), getModulePositions(), new Pose2d());

  private double m_simYaw;

  public SwerveDrive() {
    m_pigeon.setYaw(0);
  }

  /**
   * Main method to call to tell the swerve drive to move (e.g. when
   * joystick moved).
   */
  public void drive(double throttle,
      double strafe,
      double rotation,
      boolean isFieldRelative,
      boolean isOpenLoop) {
    throttle *= kMaxSpeedMetersPerSecond;
    strafe *= kMaxSpeedMetersPerSecond;
    rotation *= kMaxRotationRadiansPerSecond;

    ChassisSpeeds chassisSpeeds = isFieldRelative
        ? ChassisSpeeds.fromFieldRelativeSpeeds(throttle, strafe, rotation, getHeadingRotation2d())
        : new ChassisSpeeds(throttle, strafe, rotation);

    SwerveModuleState[] moduleStates = kSwerveKinematics.toSwerveModuleStates(chassisSpeeds);

    SwerveDriveKinematics.desaturateWheelSpeeds(moduleStates, kMaxSpeedMetersPerSecond);

    for (SwerveModule module : m_swerveModules.values()) {
      module.setDesiredState(moduleStates[module.getModuleNumber()], isOpenLoop);
    }
  }

  /**
   * Set the swerve drive to a specific state.
   */
  public void setSwerveModuleStates(SwerveModuleState[] states, boolean isOpenLoop) {
    SwerveDriveKinematics.desaturateWheelSpeeds(states, kMaxSpeedMetersPerSecond);

    for (SwerveModule module : m_swerveModules.values()) {
      module.setDesiredState(states[module.getModuleNumber()], isOpenLoop);
    }
  }

  public double getHeadingDegrees() {
    return Math.IEEEremainder(m_pigeon.getYaw(), 360);
  }

  public Rotation2d getHeadingRotation2d() {
    return Rotation2d.fromDegrees(getHeadingDegrees());
  }

  public Pose2d getPoseMeters() {
    return m_odometry.getPoseMeters();
  }

  public SwerveModule getSwerveModule(int moduleNumber) {
    return m_swerveModules.get(ModulePosition.values()[moduleNumber]);
  }

  /**
   * Get the current state of the swerve modules.
   */
  public SwerveModuleState[] getModuleStates() {
    return new SwerveModuleState[] {
        m_swerveModules.get(ModulePosition.FRONT_LEFT).getState(),
        m_swerveModules.get(ModulePosition.FRONT_RIGHT).getState(),
        m_swerveModules.get(ModulePosition.BACK_LEFT).getState(),
        m_swerveModules.get(ModulePosition.BACK_RIGHT).getState()
    };
  }

  /**
   * Get the current positions of the swerve modules.
   */
  public SwerveModulePosition[] getModulePositions() {
    return new SwerveModulePosition[] {
        m_swerveModules.get(ModulePosition.FRONT_LEFT).getPosition(),
        m_swerveModules.get(ModulePosition.FRONT_RIGHT).getPosition(),
        m_swerveModules.get(ModulePosition.BACK_LEFT).getPosition(),
        m_swerveModules.get(ModulePosition.BACK_RIGHT).getPosition()
    };
  }

  /**
   * Update the odometry of the swerve drive.
   */
  public void updateOdometry() {
    m_odometry.update(getHeadingRotation2d(), getModulePositions());

    for (SwerveModule module : m_swerveModules.values()) {
      var modulePositionFromChassis = kModuleTranslations[module.getModuleNumber()]
          .rotateBy(getHeadingRotation2d()).plus(getPoseMeters().getTranslation());
      module.setModulePose(new Pose2d(modulePositionFromChassis,
          module.getHeadingRotation2d().plus(getHeadingRotation2d())));
    }
  }

  private void updateSmartDashboard() {
  }

  public void periodic() {
    updateOdometry();
    updateSmartDashboard();
  }

  public void simulationPeriodic() {
    ChassisSpeeds chassisSpeed = kSwerveKinematics.toChassisSpeeds(getModuleStates());
    m_simYaw += chassisSpeed.omegaRadiansPerSecond * 0.02;

    Unmanaged.feedEnable(20);
    m_pigeon.getSimCollection().setRawHeading(-Units.radiansToDegrees(m_simYaw));
  }
}
