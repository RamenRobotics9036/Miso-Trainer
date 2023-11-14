// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import frc.robot.commands.Auto;
import frc.robot.commands.RetractArmCommand;

/**
 * The JVM is configured to automatically run this class, and to call the functions corresponding to
 * each mode, as described in the TimedRobot documentation. If you change the name of this class or
 * the package after creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
  private Command m_autonomousCommand;
  private RobotContainer m_robotContainer;
  private VerifyJoysticks m_verifyJoysticks;
  private SendableChooser<String> m_chooser = null;
  private LedLights m_ledLights;

  /**
   * This function is run when the robot is first started up and should be used for any
   * initialization code.
   */
  @Override
  public void robotInit() {
    m_robotContainer = new RobotContainer();
    m_robotContainer.initDashboard();
    m_chooser = Auto.addAutoModeChooser();
    m_ledLights = new LedLights();

    m_verifyJoysticks = new VerifyJoysticks(VerifyJoysticks.getDefaultJoystickConfigs(),
        new DriverStationFunctions(), 1);

    // $TODO - This should be in init or update DashBoard?
    SmartDashboard.putBoolean("Get Cube", true);
  }

  /**
   * This function is called every 20 ms, no matter the mode. Use this for items like diagnostics
   * that you want ran during disabled, autonomous, teleoperated and test.
   * <p>
   * This runs after the mode specific periodic functions, but before LiveWindow and SmartDashboard
   * integrated updating.
   * </p>
   */
  @Override
  public void robotPeriodic() {
    CommandScheduler.getInstance().run();

    m_verifyJoysticks.verifyJoysticksPeriodically();
    m_ledLights.updateLeds();
  }

  @Override
  public void disabledInit() {
    m_ledLights.resetLeds();

    CommandScheduler.getInstance().cancelAll();
    m_robotContainer.m_driveSystem.calibrate();
  }

  @Override
  public void disabledPeriodic() {
  }

  /**
   * This autonomous runs the autonomous command selected by your {@link RobotContainer} class.
   */
  @Override
  public void autonomousInit() {
    m_ledLights.resetLeds();

    CommandScheduler.getInstance().cancelAll();
    m_autonomousCommand = m_robotContainer.getAutonomousCommand(m_chooser);
    m_autonomousCommand.schedule();
  }

  @Override
  public void autonomousPeriodic() {
  }

  @Override
  public void teleopInit() {
    m_ledLights.resetLeds();

    CommandScheduler.getInstance().cancelAll();
    m_robotContainer.configureBindings();
    m_robotContainer.updateDashBoard();

    new RetractArmCommand(m_robotContainer.m_armSystem).schedule();

    // $TODO - This should be in init or update DashBoard?
    SmartDashboard.putNumber("Winch Encoder",
        m_robotContainer.m_armSystem.getWinchAbsoluteEncoder());
  }

  /**
   * This function is called periodically during operator control.
   */
  @Override
  public void teleopPeriodic() {

    if (m_robotContainer.m_controller2.getLeftTriggerAxis() > 0.05) {
      // $TODO - This should be in init or update DashBoard?
      SmartDashboard.putBoolean("Get Cube", true);

      m_ledLights.setLedsYellow();
    }
    else if (m_robotContainer.m_controller2.getRightTriggerAxis() > 0.05) {
      // $TODO - This should be in init or update DashBoard?
      SmartDashboard.putBoolean("Get Cube", false);

      m_ledLights.setLedsMagenta();
    }
  }

  @Override
  public void testInit() {
    // Cancels all running commands at the start of test mode.
    CommandScheduler.getInstance().cancelAll();
  }

  @Override
  public void testPeriodic() {
  }

  /**
   * This function is called once when the robot is first started up.
   */
  @Override
  public void simulationInit() {
    CommandScheduler.getInstance().cancelAll();
  }

  @Override
  public void simulationPeriodic() {
  }
}
