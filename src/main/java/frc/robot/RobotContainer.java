// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.robot.commands.Auto;
import frc.robot.commands.RetractArmCommand;
import frc.robot.commands.SetSoftLimitCommand;
import frc.robot.commands.SetWinchToAngle;
import frc.robot.subsystems.ArmSystem;
import frc.robot.subsystems.ArmSystemSim;
import frc.robot.subsystems.GrabberSystem;
import frc.robot.subsystems.GrabberSystemSim;
import frc.robot.subsystems.TankDriveSystem;
import frc.robot.subsystems.TankDriveSystemSim;
import java.util.function.BooleanSupplier;

/**
 * This class is where the bulk of the robot should be declared. Since Command-based is a
 * "declarative" paradigm, very little robot logic should actually be handled in the {@link Robot}
 * periodic methods (other than the scheduler calls). Instead, the structure of the robot (including
 * subsystems, commands, and trigger mappings) should be declared here.
 */
public class RobotContainer {
  public final XboxController m_controller1 = new XboxController(
      Constants.OperatorConstants.kDriverControllerPort1);
  public final XboxController m_controller2 = new XboxController(
      Constants.OperatorConstants.kDriverControllerPort2);

  // $TODO - Stop passing around a ton of constants
  public final TankDriveSystem m_driveSystem = TankDriveSystemSim.createTankDriveSystemInstance(
      Constants.OperatorConstants.kLeftMotorBackChannel,
      Constants.OperatorConstants.kLeftMotorForwardChannel,
      Constants.OperatorConstants.kRightMotorBackChannel,
      Constants.OperatorConstants.kRightMotorForwardChannel,
      m_controller1,
      Constants.OperatorConstants.kSquareInputsDrive,
      Constants.OperatorConstants.kMaxOutputDrive,
      Constants.OperatorConstants.kDeadband,
      Constants.OperatorConstants.kGearBoxRatioDrive,
      Constants.OperatorConstants.kWheelDiameterMetersDrive,
      Constants.OperatorConstants.kSlewLimit,
      Constants.OperatorConstants.kTurboSlew);

  public final ArmSystem m_armSystem = ArmSystemSim.createArmSystemInstance(m_controller2,
      Constants.OperatorConstants.kSquareInputsArm,
      Constants.OperatorConstants.kMaxOutputWinch);

  public final GrabberSystem m_grabSystem = GrabberSystemSim.createGrabberSystemInstance(
      Constants.OperatorConstants.kGrabberForwardChannel,
      Constants.OperatorConstants.kGrabberBackwardChannel,
      m_controller2);

  /**
   * The container for the robot. Contains subsystems, OI devices, and commands.
   * 
   * <p>
   * Configure default command for Drive Subsystem. We want there to ALWAYS be a motor signal
   * sent, even in autonomous mode. If we didn't send a motor signal EVERY 20ms,
   * then Slew would prevent the robot from ever stopping fully in some cases.
   * Also, the motor watchdog would trip.
   * </p>
   */
  public RobotContainer() {
    m_driveSystem.setDefaultCommand(m_driveSystem.getDefaultDriveCommand());
    m_armSystem.setDefaultCommand(m_armSystem.getDefaultArmCommand());

    // Stitch together BooleanSupplier from GrabberSystemSim with ArmSystemSim
    if (m_grabSystem instanceof GrabberSystemSim && m_armSystem instanceof ArmSystemSim) {

      BooleanSupplier supplier = ((GrabberSystemSim) m_grabSystem).getGrabberOpenSupplier();
      ((ArmSystemSim) m_armSystem).setGrabberOpenSupplier(supplier);
    }
  }

  /**
   * Use this method to define your trigger->command mappings.
   */
  public void configureBindings() {
    new Trigger(m_controller2::getAButtonReleased).onTrue(
        new SetWinchToAngle(m_armSystem, Constants.OperatorConstants.kWinchMiddleNodeCone, 1));

    new Trigger(m_controller2::getXButtonReleased).onTrue(
        new SetWinchToAngle(m_armSystem, Constants.OperatorConstants.kWinchMiddleNodeCube, 1));

    new Trigger(m_controller2::getBButtonReleased)
        .onTrue(new RetractArmCommand(m_armSystem).andThen(
            new SetWinchToAngle(m_armSystem, Constants.OperatorConstants.kWinchGroundAngle, 1)));

    new Trigger(m_controller2::getYButtonReleased)
        .onTrue(new RetractArmCommand(m_armSystem).andThen(new SetSoftLimitCommand(m_armSystem)));
  }

  public Command getAutonomousCommand() {
    System.out.println("Auto command scheduled container");
    return Auto.getAutoCommand(m_driveSystem, m_armSystem, m_grabSystem);
  }

  /**
   * This is the single point in code that sets up Shuffleboard. It calls
   * each subsystem in-turn to setup up Shuffleboard for that system.
   */
  public void initDashboard() {
    m_driveSystem.initDashBoard();
    m_armSystem.initDashBoard();
    m_grabSystem.initDashBoard();
  }

  /**
   * This is the single point in code that updates Shuffleboard.
   */
  public void updateDashBoard() {
    m_driveSystem.updateDashBoard();
    m_armSystem.updateDashBoard();
    m_grabSystem.updateDashBoard();
  }
}
