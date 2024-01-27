// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package simulationlib.simulation.swerve;

import static simulationlib.simulation.swerve.SwerveSimConstants.Swerve.Module.kDriveRevToMeters;
import static simulationlib.simulation.swerve.SwerveSimConstants.Swerve.Module.kDriveRpmToMetersPerSecond;
import static simulationlib.simulation.swerve.SwerveSimConstants.Swerve.Module.kTurnRotationsToDegrees;
import static simulationlib.simulation.swerve.SwerveSimConstants.Swerve.Module.kaDriveVoltSecondsSquaredPerMeter;
import static simulationlib.simulation.swerve.SwerveSimConstants.Swerve.Module.ksDriveVoltSecondsPerMeter;
import static simulationlib.simulation.swerve.SwerveSimConstants.Swerve.Module.kvDriveVoltSecondsSquaredPerMeter;
import static simulationlib.simulation.swerve.SwerveSimConstants.Swerve.kMaxSpeedMetersPerSecond;

import com.ctre.phoenix.sensors.CANCoder;
import com.revrobotics.CANSparkMax;
import com.revrobotics.REVPhysicsSim;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.SparkMaxPIDController;
import edu.wpi.first.math.controller.SimpleMotorFeedforward;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.math.system.plant.DCMotor;
import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import simulationlib.simulation.swerve.utils.CtreUtils;
import simulationlib.simulation.swerve.utils.RevUtils;

/**
 * Swerve module implementation.
 */
public class SwerveModule extends SubsystemBase {
  private final int m_posSlot = 0;
  private final int m_velSlot = 1;
  private final int m_simSlot = 2;

  int m_moduleNumber;
  CANSparkMax m_turnMotor;
  CANSparkMax m_driveMotor;
  private final SparkMaxPIDController m_driveController;
  private SparkMaxPIDController m_turnController;
  public final RelativeEncoder m_driveEncoder;
  private final RelativeEncoder m_turnEncoder;
  CANCoder m_angleEncoder;
  double m_angleOffset;
  double m_currentAngle;
  double m_lastAngle;

  private double m_simDriveEncoderPosition;
  private double m_simDriveEncoderVelocity;
  Pose2d m_pose;

  SimpleMotorFeedforward m_feedforward = new SimpleMotorFeedforward(ksDriveVoltSecondsPerMeter,
      kaDriveVoltSecondsSquaredPerMeter, kvDriveVoltSecondsSquaredPerMeter);

  /**
   * Constructor.
   */
  public SwerveModule(int moduleNumber,
      CANSparkMax turnMotor,
      CANSparkMax driveMotor,
      CANCoder angleEncoder,
      double angleOffset) {
    m_moduleNumber = moduleNumber;
    m_turnMotor = turnMotor;
    m_driveMotor = driveMotor;
    m_angleEncoder = angleEncoder;
    m_angleOffset = angleOffset;

    m_driveMotor.restoreFactoryDefaults();
    RevUtils.setDriveMotorConfig(m_driveMotor);
    m_driveMotor.setIdleMode(CANSparkMax.IdleMode.kBrake);

    m_turnMotor.restoreFactoryDefaults();
    RevUtils.setTurnMotorConfig(m_turnMotor);
    m_turnMotor.setIdleMode(CANSparkMax.IdleMode.kBrake);

    m_angleEncoder.configFactoryDefault();
    m_angleEncoder.configAllSettings(CtreUtils.generateCanCoderConfig());

    m_driveEncoder = m_driveMotor.getEncoder();
    m_driveEncoder.setPositionConversionFactor(kDriveRevToMeters);
    m_driveEncoder.setVelocityConversionFactor(kDriveRpmToMetersPerSecond);

    m_turnEncoder = m_turnMotor.getEncoder();
    m_turnEncoder.setPositionConversionFactor(kTurnRotationsToDegrees);
    m_turnEncoder.setVelocityConversionFactor(kTurnRotationsToDegrees / 60);

    m_driveController = m_driveMotor.getPIDController();
    m_turnController = m_turnMotor.getPIDController();

    if (RobotBase.isSimulation()) {
      REVPhysicsSim.getInstance().addSparkMax(m_driveMotor, DCMotor.getNEO(1));
      REVPhysicsSim.getInstance().addSparkMax(m_turnMotor, DCMotor.getNEO(1));
      m_driveController.setP(1, m_simSlot);
    }

    resetAngleToAbsolute();
  }

  public int getModuleNumber() {
    return m_moduleNumber;
  }

  public void resetAngleToAbsolute() {
    double angle = m_angleEncoder.getAbsolutePosition() - m_angleOffset;
    m_turnEncoder.setPosition(angle);
  }

  /**
   * Get the current angle of the module.
   */
  public double getHeadingDegrees() {
    if (RobotBase.isReal()) {
      return m_turnEncoder.getPosition();
    }
    else {
      return m_currentAngle;
    }
  }

  public Rotation2d getHeadingRotation2d() {
    return Rotation2d.fromDegrees(getHeadingDegrees());
  }

  /**
   * Total distance driven by the module.
   */
  public double getDriveMeters() {
    if (RobotBase.isReal()) {
      return m_driveEncoder.getPosition();
    }
    else {
      return m_simDriveEncoderPosition;
    }
  }

  /**
   * Current speed of the module.
   */
  public double getDriveMetersPerSecond() {
    if (RobotBase.isReal()) {
      return m_driveEncoder.getVelocity();
    }
    else {
      return m_simDriveEncoderVelocity;
    }
  }

  /**
   * Set the desired state of the module (and then Pid loop will do its thing).
   */
  public void setDesiredState(SwerveModuleState desiredState, boolean isOpenLoop) {
    desiredState = RevUtils.optimize(desiredState, getHeadingRotation2d());

    if (isOpenLoop) {
      double percentOutput = desiredState.speedMetersPerSecond / kMaxSpeedMetersPerSecond;
      m_driveMotor.set(percentOutput);
    }
    else {
      int drivePidSlot = RobotBase.isReal() ? m_velSlot : m_simSlot;
      m_driveController.setReference(desiredState.speedMetersPerSecond,
          CANSparkMax.ControlType.kVelocity,
          drivePidSlot);
    }

    // getDegrees() prevents rotating module if speed is less than 1%.
    // Prevents Jittering.
    double angle = (Math
        .abs(desiredState.speedMetersPerSecond) <= (kMaxSpeedMetersPerSecond * 0.01)) ? m_lastAngle
            : desiredState.angle.getDegrees();

    m_turnController.setReference(angle, CANSparkMax.ControlType.kPosition, m_posSlot);

    if (RobotBase.isSimulation()) {
      simUpdateDrivePosition(desiredState);
      // simTurnPosition(angle);
      m_currentAngle = angle;

    }
  }

  private void simUpdateDrivePosition(SwerveModuleState state) {
    m_simDriveEncoderVelocity = state.speedMetersPerSecond;
    double distancePer20Ms = m_simDriveEncoderVelocity / 50.0;

    m_simDriveEncoderPosition += distancePer20Ms;
  }

  public SwerveModuleState getState() {
    return new SwerveModuleState(getDriveMetersPerSecond(), getHeadingRotation2d());
  }

  public SwerveModulePosition getPosition() {
    return new SwerveModulePosition(getDriveMeters(), getHeadingRotation2d());
  }

  public void setModulePose(Pose2d pose) {
    m_pose = pose;
  }

  public Pose2d getModulePose() {
    return m_pose;
  }

  @Override
  public void periodic() {
  }

  @Override
  public void simulationPeriodic() {
    REVPhysicsSim.getInstance().run();
  }
}
