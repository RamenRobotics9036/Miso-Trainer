package frc.robot.commands;

import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import frc.robot.Constants;
import frc.robot.subsystems.ArmSystem;
import frc.robot.subsystems.GrabberSystem;
import frc.robot.subsystems.TankDriveSystem;

/**
 * This class contains the logic for autonomous.
 */
public class Auto {
  public static final String kAutoModeKey = "Auto Mode"; // values shown in shuffleboard
  public static final String kDropAndDriveMode = "Score and Move";
  public static final String kAutoBalanceMode = "Auto Balance";
  public static final String kOnlyScore = "Only Score";
  public static final String kScoreLow = "Score Low";

  public static final String kAutoTestSlow = "DO NOT USE (Auto Test Slow Auto Balance)";
  public static final String kTestDriveOnly = "DO NOT USE (Simple test of drive only)";

  public static final String kDefaultAutoModeValue = kOnlyScore;

  private Auto() {
    throw new Error("Auto is a utility class and should not be constructed. "
        + "One should utilize this class via static methods.");
  }

  /**
   * Creates a SendableChooser for the auto mode and adds it to the smartdashboard.
   */
  public static SendableChooser<String> addAutoModeChooser() {
    // Force the smartdashboard to update by first deleting all options in chooser
    SendableChooser<String> emptyChooser = new SendableChooser<String>();
    SmartDashboard.putData(Auto.kAutoModeKey, emptyChooser);

    SendableChooser<String> resultChooser = new SendableChooser<String>();
    resultChooser.addOption(Auto.kDropAndDriveMode, Auto.kDropAndDriveMode); // adding options
    resultChooser.addOption(Auto.kAutoBalanceMode, Auto.kAutoBalanceMode);
    resultChooser.addOption(Auto.kOnlyScore, Auto.kOnlyScore);
    resultChooser.addOption(Auto.kScoreLow, Auto.kScoreLow);
    resultChooser.addOption(Auto.kAutoTestSlow, Auto.kAutoTestSlow);
    resultChooser.addOption(Auto.kTestDriveOnly, Auto.kTestDriveOnly);

    resultChooser.setDefaultOption(Auto.kDefaultAutoModeValue, Auto.kDefaultAutoModeValue);
    SmartDashboard.putData(Auto.kAutoModeKey, resultChooser);

    return resultChooser;
  }

  /**
   * Returns the command to run for autonomous.
   */
  public static Command getAutoCommand(SendableChooser<String> chooser,
      TankDriveSystem driveSystem,
      ArmSystem armSystem,
      GrabberSystem grabSystem) {

    if (chooser == null) {
      throw new IllegalArgumentException("Chooser cannot be null");
    }
    if (driveSystem == null) {
      throw new IllegalArgumentException("Drive system cannot be null");
    }
    if (armSystem == null) {
      throw new IllegalArgumentException("Arm system cannot be null");
    }
    if (grabSystem == null) {
      throw new IllegalArgumentException("Grab system cannot be null");
    }

    String autoMode = chooser.getSelected();
    System.out.println("Auto mode selected: " + autoMode);

    // Switch statement to use specific autoroutines based on sendable dropdown
    switch (autoMode) {
      case kDropAndDriveMode:
        return Commands.sequence(new SetWinchToAngle(armSystem, 0.75, 0.9),
            new SetExtenderToLength(armSystem, -100, 0.9),
            new WaitCommand(0.5),
            new GrabberOpenCommand(grabSystem),
            new WaitCommand(0.5),
            new DriveCommand(driveSystem, 15 * 12, Constants.OperatorConstants.kGearBoxRatioDrive,
                0.5, Constants.OperatorConstants.kWheelCircumferenceInchesDrive));

      case kAutoBalanceMode:
        return Commands.sequence(new SetWinchToAngle(armSystem, 0.75, 1),
            new WaitCommand(0.25),
            new DriveCommand(driveSystem, 8 * 12, Constants.OperatorConstants.kGearBoxRatioDrive,
                -0.4, Constants.OperatorConstants.kWheelCircumferenceInchesDrive),
            new AutoBalanceCommand(driveSystem, 0.25));

      case kOnlyScore:
        return Commands.sequence(new SetWinchToAngle(armSystem, 0.75, 0.9),
            new SetExtenderToLength(armSystem, -100, 0.9),
            new WaitCommand(0.5),
            new GrabberOpenCommand(grabSystem));

      case kAutoTestSlow:
        return Commands.sequence(new SetWinchToAngle(armSystem, 0.75, 1),
            new WaitCommand(0.25),
            new WaitCommand(0.25),
            new DriveUntilTiltCommand(driveSystem, -0.4),
            new DriveCommand(driveSystem, 1.5 * 12, Constants.OperatorConstants.kGearBoxRatioDrive,
                -0.4, Constants.OperatorConstants.kWheelCircumferenceInchesDrive),
            new AutoBalanceCommand(driveSystem, 0.25));

      case kScoreLow:
        return Commands.sequence(new SetWinchToAngle(armSystem, 0.75, 1),
            new WaitCommand(0.5),
            new DriveCommand(driveSystem, 15 * 12, Constants.OperatorConstants.kGearBoxRatioDrive,
                0.5, Constants.OperatorConstants.kWheelCircumferenceInchesDrive));

      case kTestDriveOnly:
        return Commands.sequence(new WaitCommand(0.25),
            new DriveCommand(driveSystem, 5 * 12, Constants.OperatorConstants.kGearBoxRatioDrive,
                0.4, Constants.OperatorConstants.kWheelCircumferenceInchesDrive),
            new TurnDegrees(driveSystem, 0.6, 90),
            new TurnDegrees(driveSystem, 0.6, -90),
            new DriveCommand(driveSystem, 5 * 12, Constants.OperatorConstants.kGearBoxRatioDrive,
                0.4, Constants.OperatorConstants.kWheelCircumferenceInchesDrive));

      default:
        System.out.println("UNEXPECTED AUTO MODE - auto mode will do nothing");
        return new InstantCommand();
    }
  }
}
