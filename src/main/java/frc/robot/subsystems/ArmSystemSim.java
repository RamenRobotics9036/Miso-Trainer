package frc.robot.subsystems;

import edu.wpi.first.math.Pair;
import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.RobotState;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.simulation.DIOSim;
import edu.wpi.first.wpilibj.simulation.DutyCycleEncoderSim;
import frc.robot.Constants;
import frc.robot.helpers.DutyCycleEncoderSim2;
import frc.robot.helpers.RelativeEncoderSim;
import frc.robot.helpers.UnitConversions;
import frc.robot.simulation.ExtenderSimulation;
import frc.robot.simulation.armangle.ArmAngleParams;
import frc.robot.simulation.armangle.ArmAngleSimInput;
import frc.robot.simulation.armangle.ArmAngleSimModel;
import frc.robot.simulation.armangle.ArmAngleState;
import frc.robot.simulation.framework.SimManager;
import frc.robot.simulation.framework.inputoutputs.CopySimOutput;
import frc.robot.simulation.motor.MotorSimModel;
import frc.robot.simulation.motor.MotorSimOutput;
import frc.robot.simulation.motor.MotorSparkMaxSimInput;
import frc.robot.simulation.simplearm.ArmSimModel;
import frc.robot.simulation.simplearm.ArmSimulationParams;
import frc.robot.simulation.simplearm.ramenarmlogic.RamenArmSimLogic;
import frc.robot.simulation.winch.WinchSimInput;
import frc.robot.simulation.winch.WinchSimModel;
import frc.robot.simulation.winch.WinchSimModel.WindingOrientation;
import frc.robot.simulation.winch.WinchState;
import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;
import java.util.function.Supplier;

/**
 * Subclass of ArmSystem that is used for simulation. Note that this code isn't run if
 * the robot is not running in simulation mode.
 */
public class ArmSystemSim extends ArmSystem {
  private DutyCycleEncoderSim m_winchAbsoluteEncoderSim;

  private RelativeEncoderSim m_winchEncoderSim;
  private SimManager<Double, Double> m_winchMotorSimManager;
  private SimManager<Double, WinchState> m_winchSimManager;
  private SimManager<Double, ArmAngleState> m_angleSimManager;

  protected WinchState m_winchState;
  private ArmAngleState m_armAngleState;

  private RelativeEncoderSim m_extenderEncoderSim;
  private SimManager<Double, Double> m_extenderMotorSimManager;
  protected ExtenderSimulation m_extenderSimulation;

  protected DIOSim m_sensorSim;

  protected ArmSimModel m_armSimulation;
  protected RamenArmSimLogic m_ramenArmSimLogic;

  /**
   * Creates an instance of the ArmSystem or ArmSystemSim class.
   */
  public static ArmSystem createArmSystemInstance(XboxController controller) {
    ArmSystem result;

    if (RobotBase.isSimulation()) {
      result = new ArmSystemSimWithWidgets(controller);

      // System.out.println("ARMSYSTEM: **** Simulation ****");

    }
    else {
      result = new ArmSystem(controller);

      // System.out.println("ARMSYSTEM: Physical Robot version");
    }

    return result;
  }

  /**
   * Constructor.
   */
  public ArmSystemSim(XboxController controller) {

    // FIRST, we call superclass
    super(controller);

    // This entire class should only be instantiated when we're under simulation.
    // But just in-case someone tries to instantiate it otherwise, we do an extra
    // check here.
    if (!RobotBase.isSimulation()) {
      return;
    }

    createWinchSimParts();
    createExtenderSimParts();
    createArmAngleSimParts();

    m_sensorSim = new DIOSim(m_sensor);

    // Create simulated absolute encoder
    m_winchAbsoluteEncoderSim = new DutyCycleEncoderSim2(m_winchAbsoluteEncoder);

    // Create a DoubleSupplier that gets the angle
    DoubleSupplier armAngleSupplier = () -> {
      return m_armAngleState.getAngleSignedDegrees();
    };

    ArmSimulationParams armParams = new ArmSimulationParams(
        UnitConversions.rotationToSignedDegrees(Constants.OperatorConstants.kWinchEncoderUpperLimit
            - Constants.SimConstants.karmEncoderRotationsOffset
            + Constants.SimConstants.kdeltaRotationsBeforeBroken),
        UnitConversions.rotationToSignedDegrees(Constants.OperatorConstants.kWinchEncoderLowerLimit
            - Constants.SimConstants.karmEncoderRotationsOffset
            - Constants.SimConstants.kdeltaRotationsBeforeBroken),
        Constants.SimConstants.karmEncoderRotationsOffset);

    Pair<ArmSimModel, RamenArmSimLogic> createResult = RamenArmSimLogic.createRamenArmSimulation(
        armAngleSupplier,
        m_winchAbsoluteEncoderSim,
        armParams,
        UnitConversions
            .rotationToSignedDegrees(Constants.SimConstants.kgrabberBreaksIfOpenBelowThisLimit
                - Constants.SimConstants.karmEncoderRotationsOffset));

    m_armSimulation = createResult.getFirst();
    m_ramenArmSimLogic = createResult.getSecond();
  }

  private void createArmAngleSimParts() {
    // Create a DoubleSupplier that gets the value m_winchState.getStringUnspooledLen()
    Supplier<Double> stringUnspooledLenSupplier = () -> {
      return m_winchState.getStringUnspooledLen();
    };

    m_armAngleState = new ArmAngleState();

    ArmAngleParams armAngleParams = new ArmAngleParams(
        Constants.SimConstants.karmHeightFromWinchToPivotPoint,
        Constants.SimConstants.karmLengthFromEdgeToPivot,
        Constants.SimConstants.klengthFromPivotPointToArmBackEnd_Min);

    m_angleSimManager = new SimManager<Double, ArmAngleState>(new ArmAngleSimModel(armAngleParams),
        false);
    m_angleSimManager.setInputHandler(new ArmAngleSimInput(stringUnspooledLenSupplier));
    m_angleSimManager.setOutputHandler(new CopySimOutput<ArmAngleState>(m_armAngleState));
  }

  private void createWinchSimParts() {
    // Create winch simulated encoder
    m_winchEncoderSim = new RelativeEncoderSim(m_winchEncoder);

    m_winchState = new WinchState(Constants.SimConstants.kTotalStringLenMeters);

    // Create the motor simulation for the winch motor
    m_winchMotorSimManager = new SimManager<Double, Double>(
        new MotorSimModel(Constants.SimConstants.kwinchSimGearRatio), false);
    m_winchMotorSimManager.setInputHandler(new MotorSparkMaxSimInput(m_armWinch));
    m_winchMotorSimManager.setOutputHandler(new MotorSimOutput(m_winchEncoderSim));

    // Create the winch simulation
    m_winchSimManager = new SimManager<Double, WinchState>(
        new WinchSimModel(0.0254, Constants.SimConstants.kTotalStringLenMeters,
            Constants.SimConstants.kCurrentLenSpooled, WindingOrientation.BackOfRobot, true),
        false);
    m_winchSimManager.setInputHandler(new WinchSimInput(m_winchEncoderSim));
    m_winchSimManager.setOutputHandler(new CopySimOutput<WinchState>(m_winchState));
  }

  private void createExtenderSimParts() {
    // Create extender simulated encoder
    m_extenderEncoderSim = new RelativeEncoderSim(m_extenderEncoder);

    // Create the motor simulation for the extender motor
    m_extenderMotorSimManager = new SimManager<Double, Double>(
        new MotorSimModel(Constants.SimConstants.kextenderSimGearRatio), false);
    m_extenderMotorSimManager.setInputHandler(new MotorSparkMaxSimInput(m_armExtender));
    m_extenderMotorSimManager.setOutputHandler(new MotorSimOutput(m_extenderEncoderSim));

    m_extenderSimulation = new ExtenderSimulation(m_extenderEncoderSim,
        Constants.SimConstants.kcylinderDiameterMeters,
        Constants.SimConstants.kTotalExtenderLenMeters, Constants.SimConstants.kInitialExtendedLen,
        true);
  }

  // $LATER Get rid of isRobotEnabled
  private boolean isRobotEnabled() {
    return RobotState.isEnabled();
  }

  public void setGrabberOpenSupplier(BooleanSupplier grabberOpenSupplier) {
    m_ramenArmSimLogic.setGrabberOpenSupplier(grabberOpenSupplier);
  }

  // $LATER - This is temporary until we combine string and arm simulation
  protected boolean getIsStringOrArmBroken() {
    return m_armAngleState.getIsBroken() || m_armSimulation.getIsBroken();
  }

  // $LATER - This is temporary until we combine string and arm simulation
  private void simulatePeriodicStringAndArm(SimManager<Double, ArmAngleState> angleSimulation,
      ArmSimModel armSimulation) {

    angleSimulation.simulationPeriodic();
    armSimulation.simulationPeriodic();
  }

  @Override
  public void periodic() {
    super.periodic();
  }

  @Override
  public void simulationPeriodic() {
    super.simulationPeriodic();

    // When Robot is disabled, the entire simulation freezes
    if (isRobotEnabled()) {

      m_winchMotorSimManager.simulationPeriodic();
      m_extenderMotorSimManager.simulationPeriodic();
      m_winchSimManager.simulationPeriodic();

      m_extenderSimulation.simulationPeriodic();
      simulatePeriodicStringAndArm(m_angleSimManager, m_armSimulation);

      boolean isExtenderSensorOn = m_extenderSimulation
          .getExtendedLen() <= Constants.SimConstants.kextenderFullyRetractedLen;
      m_sensorSim.setValue(!isExtenderSensorOn);
    }
  }
}
