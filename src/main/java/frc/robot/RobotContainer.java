// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.robot.commands.Auto;
import frc.robot.commands.RetractArmCommand;
import frc.robot.commands.SetSoftLimitCommand;
import frc.robot.commands.SetWinchToAngle;
import frc.robot.helpers.DefaultLayout;
import frc.robot.subsystems.ArmSystem;
import frc.robot.subsystems.ArmSystemSim;
import frc.robot.subsystems.GrabberSystem;
import frc.robot.subsystems.GrabberSystemSim;
import frc.robot.subsystems.TankDriveSystem;
import frc.robot.subsystems.TankDriveSystemSim;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;
import simulationlib.shuffle.MultiType;
import simulationlib.shuffle.PrefixedConcurrentMap;
import simulationlib.shuffle.ShuffleboardHelpers;
import simulationlib.shuffle.SupplierMapFactory;

/**
 * This class is where the bulk of the robot should be declared. Since Command-based is a
 * "declarative" paradigm, very little robot logic should actually be handled in the {@link Robot}
 * periodic methods (other than the scheduler calls). Instead, the structure of the robot (including
 * subsystems, commands, and trigger mappings) should be declared here.
 */
public class RobotContainer {
  public final XboxController m_controller1;
  public final XboxController m_controller2;

  public final TankDriveSystem m_driveSystem;
  public final ArmSystem m_armSystem;
  public final GrabberSystem m_grabSystem;

  private final PopulateShuffleboard m_shuffleboardManager;

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
    // Create controllers
    m_controller1 = new XboxController(Constants.OperatorConstants.kDriverControllerPort1);
    m_controller2 = new XboxController(Constants.OperatorConstants.kDriverControllerPort2);

    // Crreate all subsystems
    m_driveSystem = TankDriveSystemSim.createTankDriveSystemInstance(m_controller1);
    m_armSystem = ArmSystemSim.createArmSystemInstance(m_controller2);
    m_grabSystem = GrabberSystemSim.createGrabberSystemInstance(m_controller2);

    setDefaultCommands();

    setupGrabberNotificationsToArm();

    // Now that all subsystems are created, print out the list of properties
    // available for display in Shuffleboard.
    printAvailableDashboardProperties();

    m_shuffleboardManager = new PopulateShuffleboard(
        new ShuffleboardHelpers(SupplierMapFactory.getGlobalInstance()), new DefaultLayout(),
        Shuffleboard.getTab("Simulation"));
  }

  private void printAvailableDashboardProperties() {
    PrefixedConcurrentMap<Supplier<MultiType>> globalMap = SupplierMapFactory.getGlobalInstance();

    globalMap.prettyPrint();
  }

  /**
   * Binds a button on the joystick to a command to run.
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

  /**
   * When autonomous mode is started, this is the one command we run. It sequences
   * together lots of smaller commands.
   */
  public Command getAutonomousCommand(SendableChooser<String> chooser) {
    if (chooser == null) {
      throw new IllegalArgumentException("Chooser cannot be null");
    }

    // System.out.println("Auto command scheduled container");
    return Auto.getAutoCommand(chooser, m_driveSystem, m_armSystem, m_grabSystem);
  }

  /**
   * This is the single point in code that sets up Shuffleboard. It calls
   * each subsystem in-turn to setup up Shuffleboard for that system.
   */
  public void initDashboard() {
    m_driveSystem.initDashBoard();
    m_armSystem.initDashBoard();
    m_grabSystem.initDashBoard();

    m_shuffleboardManager.addShuffleboardWidgets();
    m_shuffleboardManager.addMacros(m_armSystem);
  }

  /**
   * This is the single point in code that updates Shuffleboard.
   */
  public void updateDashOnRobotPeriodic() {
    m_driveSystem.updateDashOnRobotPeriodic();
    m_armSystem.updateDashOnRobotPeriodic();
    m_grabSystem.updateDashOnRobotPeriodic();

    m_shuffleboardManager.updateDashOnRobotPeriodic();
  }

  private void setDefaultCommands() {
    m_driveSystem.setDefaultCommand(m_driveSystem.getDefaultDriveCommand());
    m_armSystem.setDefaultCommand(m_armSystem.getDefaultArmCommand());
  }

  // Stitch together BooleanSupplier from GrabberSystemSim with ArmSystemSim
  private void setupGrabberNotificationsToArm() {
    if (m_grabSystem instanceof GrabberSystemSim && m_armSystem instanceof ArmSystemSim) {

      BooleanSupplier supplier = ((GrabberSystemSim) m_grabSystem).getGrabberOpenSupplier();
      ((ArmSystemSim) m_armSystem).setGrabberOpenSupplier(supplier);
    }
  }
}
