package simulationlib.simulation.swerve.utils;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.SwerveModuleState;

/**
 * REV utility functions.
 */
public final class RevUtils {
  /**
   * Set turn motor config.
   */
  public static void setTurnMotorConfig(CANSparkMax motorController) {
    motorController.getPIDController().setFF(0.0);
    motorController.getPIDController().setP(0.2);
    motorController.getPIDController().setI(0.0);
    // motorController.getPIDController().setD(12.0);

    motorController.setPeriodicFramePeriod(CANSparkMaxLowLevel.PeriodicFrame.kStatus0, 100);
    motorController.setPeriodicFramePeriod(CANSparkMaxLowLevel.PeriodicFrame.kStatus1, 20);
    motorController.setPeriodicFramePeriod(CANSparkMaxLowLevel.PeriodicFrame.kStatus2, 20);

    motorController.setSmartCurrentLimit(40, 25);
  }

  /**
   * Set drive motor config.
   */
  public static void setDriveMotorConfig(CANSparkMax motorController) {
    motorController.getPIDController().setFF(0.0);
    motorController.getPIDController().setP(0.1);
    motorController.getPIDController().setI(0.0);
    motorController.getPIDController().setD(0.0);

    motorController.setPeriodicFramePeriod(CANSparkMaxLowLevel.PeriodicFrame.kStatus0, 10);
    motorController.setPeriodicFramePeriod(CANSparkMaxLowLevel.PeriodicFrame.kStatus1, 20);
    motorController.setPeriodicFramePeriod(CANSparkMaxLowLevel.PeriodicFrame.kStatus2, 50);

    motorController.setSmartCurrentLimit(60, 35);

    motorController.setOpenLoopRampRate(0.25);
    motorController.setOpenLoopRampRate(0.1);
  }

  /**
   * Optimize swerve module state.
   */
  public static SwerveModuleState optimize(SwerveModuleState desiredState,
      Rotation2d currentAngle) {
    double targetAngle = placeInAppropriate0To360Scope(currentAngle.getDegrees(),
        desiredState.angle.getDegrees());
    double targetSpeed = desiredState.speedMetersPerSecond;
    double delta = targetAngle - currentAngle.getDegrees();
    if (Math.abs(delta) > 90) {
      targetSpeed = -targetSpeed;
      targetAngle = delta > 90 ? (targetAngle -= 180) : (targetAngle += 180);
    }
    return new SwerveModuleState(targetSpeed, Rotation2d.fromDegrees(targetAngle));
  }

  /**
   * Ensure angle is between 0 and 360.
   *
   * @param scopeReference Current Angle
   * @param newAngle       Target Angle
   * @return Closest angle within scope
   */
  private static double placeInAppropriate0To360Scope(double scopeReference, double newAngle) {
    double lowerBound;
    double upperBound;
    double lowerOffset = scopeReference % 360;
    if (lowerOffset >= 0) {
      lowerBound = scopeReference - lowerOffset;
      upperBound = scopeReference + (360 - lowerOffset);
    }
    else {
      upperBound = scopeReference - lowerOffset;
      lowerBound = scopeReference - (360 + lowerOffset);
    }
    while (newAngle < lowerBound) {
      newAngle += 360;
    }
    while (newAngle > upperBound) {
      newAngle -= 360;
    }
    if (newAngle - scopeReference > 180) {
      newAngle -= 360;
    }
    else if (newAngle - scopeReference < -180) {
      newAngle += 360;
    }
    return newAngle;
  }
}
