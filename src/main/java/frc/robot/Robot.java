package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import frc.robot.commands.Auto;
import frc.robot.commands.RetractArmCommand;
import frc.robot.helpers.DriverStationFunctions;
import frc.robot.helpers.VerifyJoysticks;

/**
 * Main Robot class.
 */
public class Robot extends TimedRobot {
  private Command m_autonomousCommand;
  private RobotContainer m_robotContainer;
  private VerifyJoysticks m_verifyJoysticks;
  private SendableChooser<String> m_chooser = null;
  private LedLights m_ledLights;

  @Override
  public void robotInit() {
    m_robotContainer = new RobotContainer();
    m_robotContainer.initDashboard();
    m_chooser = Auto.addAutoModeChooser();
    m_ledLights = new LedLights();

    m_verifyJoysticks = new VerifyJoysticks(VerifyJoysticks.getDefaultJoystickConfigs(),
        new DriverStationFunctions(), 1);

    // $TODOLATER - This should be in init or update DashBoard?
    SmartDashboard.putBoolean("Get Cube", true);
  }

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

    // $TODOLATER - This should be in init or update DashBoard?
    SmartDashboard.putNumber("Winch Encoder",
        m_robotContainer.m_armSystem.getWinchAbsoluteEncoder());
  }

  @Override
  public void teleopPeriodic() {

    if (m_robotContainer.m_controller2.getLeftTriggerAxis() > 0.05) {
      // $TODOLATER - This should be in init or update DashBoard?
      SmartDashboard.putBoolean("Get Cube", true);

      m_ledLights.setLedsYellow();
    }
    else if (m_robotContainer.m_controller2.getRightTriggerAxis() > 0.05) {
      // $TODOLATER - This should be in init or update DashBoard?
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

  @Override
  public void simulationInit() {
    CommandScheduler.getInstance().cancelAll();
  }

  @Override
  public void simulationPeriodic() {
  }
}
