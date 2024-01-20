package frc.robot.subsystems;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMax.SoftLimitDirection;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.revrobotics.RelativeEncoder;
import edu.wpi.first.math.MathUtil;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DutyCycleEncoder;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

/**
 * The class is responsible for managing the arm subsystem
 * which includes a winch and an extender.
 */
public class ArmSystem extends SubsystemBase {
  private XboxController m_controller;
  private double m_maxOutputWinch;

  // Devices
  protected CANSparkMax m_armWinch;
  protected CANSparkMax m_armExtender;
  protected DigitalInput m_sensor;
  protected DutyCycleEncoder m_winchAbsoluteEncoder;
  protected RelativeEncoder m_winchEncoder;
  protected RelativeEncoder m_extenderEncoder;

  /**
   * Constructor.
   */
  public ArmSystem(XboxController controller) {
    m_controller = controller;
    m_maxOutputWinch = Constants.OperatorConstants.kMaxOutputWinch;

    m_sensor = new DigitalInput(Constants.OperatorConstants.kHallEffectExtenderChannel);
    m_armWinch = new CANSparkMax(Constants.OperatorConstants.kArmWinchChannel,
        MotorType.kBrushless);

    // $LATER - For simulation, test that smart limits actually work when values
    // are set on SmartMax
    m_armWinch.setSmartCurrentLimit(20);
    m_armExtender = new CANSparkMax(Constants.OperatorConstants.kArmExtenderChannel,
        MotorType.kBrushless);
    m_winchEncoder = m_armWinch.getEncoder();
    m_extenderEncoder = m_armExtender.getEncoder();
    m_winchAbsoluteEncoder = new DutyCycleEncoder(
        Constants.OperatorConstants.kAbsoluteEncoderWinchChannel);

    m_armExtender.setSmartCurrentLimit(20);
    m_armExtender.setInverted(false);
    setSoftLimit();
  }

  /**
   * Initialize the values to display on the dashboard.
   */
  public void initDashBoard() {
    SmartDashboard.putNumber("Winch Encoder", m_winchEncoder.getPosition());
    SmartDashboard.putNumber("Extender Encoder", m_extenderEncoder.getPosition());
    SmartDashboard.putNumber("Winch Max Output", m_maxOutputWinch);
  }

  /**
   * Update the values displayed on the dashboard.
   */
  public void updateDashBoard() {
    SmartDashboard.putNumber("Winch Encoder", m_winchEncoder.getPosition());
    SmartDashboard.putNumber("Extender Encoder", m_extenderEncoder.getPosition());
    m_maxOutputWinch = SmartDashboard.getNumber("Winch Max Output", m_maxOutputWinch);
  }

  public double getWinchAbsoluteEncoder() {
    return m_winchAbsoluteEncoder.getAbsolutePosition();
  }

  /**
   * Display sensor information on smart dashboard.
   */
  public void putSensorOutputs() {
    SmartDashboard.putNumber("Winch Absolute Encoder Position",
        m_winchAbsoluteEncoder.getAbsolutePosition());
    SmartDashboard.putBoolean("Hall Effect Sensor Output", getDigitalSensor());
  }

  /**
   * Returns the default command for the arm subsystem. Note that default
   * commands are always run when the arm is idle (no other commands running).
   */
  public CommandBase getDefaultArmCommand() {
    CommandBase defaultCommand = run(() -> {
      // System.out.println("ARMCOMMAND");
      processJoystickInputForArm();
    });

    defaultCommand.setName("Default");
    return defaultCommand;
  }

  @Override
  public void periodic() {
    Double winchAbsoluteEncoder = Double.valueOf(getWinchAbsoluteEncoder());

    SmartDashboard.putBoolean("Winch Absolute Encoder", !(winchAbsoluteEncoder == 0.0));
  }

  public void resetEncoders() {
    m_winchEncoder.setPosition(0);
    m_extenderEncoder.setPosition(0);
  }

  public void resetWinchEncoder() {
    m_winchEncoder.setPosition(0);
  }

  public void resetExtenderEncoder() {
    m_extenderEncoder.setPosition(0);
  }

  public Double getWinchEncoder() {
    return m_winchEncoder.getPosition();
  }

  public Double getExtenderEncoder() {
    return m_extenderEncoder.getPosition();
  }

  /**
   * Set the speed of the winch motor.
   */
  public void setWinchSpeed(double speed) {
    if (speed > 1 || speed < -1) {
      System.out.println("**** setWinchSpeed() called with invalid speed: " + speed);
    }

    m_armWinch.set(speed);
  }

  /**
   * Set the speed of the extender motor.
   */
  public void setExtenderSpeed(double speed) {
    if (speed > 1 || speed < -1) {
      System.out.println("**** setExtenderSpeed() called with invalid speed: " + speed);
    }

    m_armExtender.set(speed);
  }

  public double getLeftAxis() {
    return m_controller.getLeftY();
  }

  public boolean getDigitalSensor() {
    return m_sensor.get();
  }

  /**
   * Set the soft limits for the arm extender.
   */
  public void setSoftLimit() {
    // $LATER - For simulation, test that smart limits actually work when I set a value on
    // SparkMax
    m_armExtender.enableSoftLimit(SoftLimitDirection.kForward, false);
    m_armExtender.enableSoftLimit(SoftLimitDirection.kReverse, false);
    resetExtenderEncoder();
  }

  private void processJoystickInputForArm() {
    double winchOutput = MathUtil.applyDeadband(-m_controller.getLeftY(),
        Constants.OperatorConstants.kDeadband);
    double extenderOutput = MathUtil.applyDeadband(m_controller.getRightY(),
        Constants.OperatorConstants.kDeadband);

    winchOutput = winchOutput * Math.abs(winchOutput);
    extenderOutput = extenderOutput * Math.abs(extenderOutput);

    double winchUpperLimit = Constants.OperatorConstants.kWinchEncoderUpperLimit;
    double winchLowerLimit = Constants.OperatorConstants.kWinchEncoderLowerLimit;

    if (getWinchAbsoluteEncoder() != 0.0) {
      if (getWinchAbsoluteEncoder() >= winchUpperLimit && winchOutput > 0) {
        m_armWinch.set(0);
      }
      else if (getWinchAbsoluteEncoder() <= winchLowerLimit && winchOutput < 0) {
        m_armWinch.set(0);
      }
      else {
        setWinchSpeed(winchOutput * m_maxOutputWinch);
      }
    }
    else {
      setWinchSpeed(winchOutput * m_maxOutputWinch);
    }

    // $LATER - For simulation, test that smart limits actually work when I set a value on
    // SparkMax
    if (getExtenderEncoder() <= Constants.OperatorConstants.kExtenderSoftLimitTurns
        && extenderOutput < 0) {
      m_armExtender.set(0);
    }
    else if (getExtenderEncoder() > 0 && extenderOutput > 0) {
      m_armExtender.set(0);
    }
    else {
      setExtenderSpeed(extenderOutput);
    }
  }
}
