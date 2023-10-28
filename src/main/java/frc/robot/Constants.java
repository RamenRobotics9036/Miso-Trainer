// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

/**
 * The Constants class provides a convenient place for teams to hold robot-wide numerical or boolean
 * constants. This class should not be used for any other purpose. All constants should be declared
 * globally (i.e. public static). Do not put anything functional in this class.
 * <p>
 * It is advised to statically import this class (or one of its inner classes) wherever the
 * constants are needed, to reduce verbosity.
 * </p>
 */
public final class Constants {
  /**
   * Constants for the robot.
   */
  public static class OperatorConstants {
    public static final int kDriverControllerPort1 = 0;
    public static final int kDriverControllerPort2 = 1;

    public static final int kLeftMotorForwardChannel = 13;
    public static final int kLeftMotorBackChannel = 12;
    public static final int kRightMotorBackChannel = 10;
    public static final int kRightMotorForwardChannel = 11;

    public static final int kArmWinchChannel = 19;
    public static final int kArmExtenderChannel = 20;

    public static final int kGrabberForwardChannel = 0;
    public static final int kGrabberBackwardChannel = 15;

    public static boolean kSquareInputsDrive = true;

    public static double kMaxOutputDrive = 0.7;
    public static double kMaxOutputWinch = 0.8;

    public static double kDeadband = 0.14;
    public static boolean kUseArcadeDrive = false;

    public static double kGearBoxRatioDrive = 8.28;
    public static double kGearBoxRatioArm = 60;

    public static double kWheelDiameterMetersDrive = 0.1524;
    public static double kWheelDiameterInchesDrive = 6;
    public static double kWheelCircumferenceInchesDrive = kWheelDiameterInchesDrive * Math.PI;

    public static double kSlewLimit = 3;
    public static double kTurboSlew = 4;

    public static double kWheelDiameterInchWinch = 0.75;
    public static double kWheelCircumferenceInchesWinch = 0.75 * Math.PI;

    public static double kWheelDiameterInchExtender = 0.75;
    public static double kWheelCircumferenceInchesExtender = 0.75 * Math.PI;

    public static double kAutoMotorSpeed = 0.3;
    public static double kAutoMotorDistance = 1;

    public static double kAutoDriveBack = 3;
    public static double kAutoWinchRotate = 1;
    public static double kAutoExtenderRotate = 1;

    public static double kAutoDriveToGamePieces = 15 * 12; // 12 for inches to feet
    public static double kAutoDriveToChargeStation = 11 * 12; // 12 for inches to feet

    public static double kRotationDilation = 1;
    public static int kAbsoluteEncoderWinchChannel = 5;

    // public static double kWinchEncoderUpperLimit = 0.61;
    public static double kWinchEncoderUpperLimit = 0.78;
    public static double kWinchEncoderLowerLimit = 0.56;

    public static double kWinchMiddleNodeCone = 0.77;
    public static double kWinchMiddleNodeCube = 0.75;
    public static double kWinchRetractAngle = 0.56;
    public static double kWinchGroundAngle = 0.57;
    public static double kEmergencyAngle = 0.72;

    public static float kExtenderSoftLimitTurns = (float) -125; // Max is 155
    public static int kHallEffectExtenderChannel = 4;

    public static int kLEDLightsChannel = 9;
    public static int kLEDLightsLength = 40;
  }

  /**
   * Constants that we specifically use in simulation mode.
   */
  public static class SimConstants {
    // Winch
    public static double kTotalStringLenMeters = 1;
    public static double kCurrentLenSpooled = 0.25;
    public static double kwinchSimGearRatio = 20.0; // 20:1

    // Extender
    public static double kTotalExtenderLenMeters = 0.75;
    public static double kInitialExtendedLen = 0.2;
    public static double kextenderSimGearRatio = 2.0; // 2:1
    public static double kcylinderDiameterMeters = 0.00155;
    public static double kextenderFullyRetractedLen = 0.05;

    // Arm
    public static double karmLengthFromEdgeToPivot = 0.25;
    public static double karmHeightFromWinchToPivotPoint = 0.75;

    public static double klengthFromWinchToPivotPoint_Min = 0.1;
    public static double klengthFromPivotPointToArmBackEnd_Min = 0.1;
    public static double karmEncoderRotationsOffset = 0.56;

    public static double kdeltaRotationsBeforeBroken = .01;
    public static double kgrabberBreaksIfOpenBelowThisLimit = 0.60;

    // Grabber
    public static boolean kgrabberInitiallyOpened = false;

    // Arm widget for shuffleboard to load
    public static final String kAnimatedArmWidget = "AnimatedArm";
  }
}
