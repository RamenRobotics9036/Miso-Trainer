package frc.robot.subsystems;

import edu.wpi.first.math.Pair;
import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.RobotState;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.simulation.DIOSim;
import edu.wpi.first.wpilibj.simulation.DutyCycleEncoderSim;
import frc.robot.Constants;
import frc.robot.helpers.DutyCycleEncoderSim2;
import frc.robot.helpers.RelativeEncoderSim;
import frc.robot.helpers.UnitConversions;

import java.util.function.BooleanSupplier;
import java.util.function.Supplier;
import simulationlib.shuffle.MultiType;
import simulationlib.shuffle.PrefixedConcurrentMap;
import simulationlib.shuffle.PrefixedConcurrentMap.Client;
import simulationlib.simulation.armangle.ArmAngleSimModel;
import simulationlib.simulation.armangle.ArmAngleState;
import simulationlib.simulation.armangle.PivotMechanism;
import simulationlib.simulation.extender.ExtenderDashboardPlugin;
import simulationlib.simulation.extender.ExtenderParams;
import simulationlib.simulation.extender.ExtenderSimModel;
import simulationlib.simulation.extender.ExtenderState;
import simulationlib.simulation.framework.SimManager;
import simulationlib.simulation.framework.inputoutputs.CopySimOutput;
import simulationlib.simulation.framework.inputoutputs.LambdaSimInput;
import simulationlib.simulation.framework.inputoutputs.MotorSparkMaxSimInput;
import simulationlib.simulation.framework.inputoutputs.RelEncoderSimInput;
import simulationlib.simulation.framework.inputoutputs.RelEncoderSimOutput;
import simulationlib.simulation.motor.MotorDashboardPlugin;
import simulationlib.simulation.motor.MotorSimModel;
import simulationlib.simulation.simplearm.ArmSimParams;
import simulationlib.simulation.simplearm.ramenarmlogic.RamenArmSimLogic;
import simulationlib.simulation.winch.WinchCable;
import simulationlib.simulation.winch.WinchDashboardPlugin;
import simulationlib.simulation.winch.WinchParams;
import simulationlib.simulation.winch.WinchSimModel;
import simulationlib.simulation.winch.WinchState;
import simulationlib.simulation.winch.WinchSimModel.WindingOrientation;
import simulationlib.shuffle.SendableArmPosition;

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

  private WinchState m_winchState;
  private ArmAngleState m_armAngleState;
  private ExtenderState m_extenderState;

  private RelativeEncoderSim m_extenderEncoderSim;
  private SimManager<Double, Double> m_extenderMotorSimManager;
  private SimManager<Double, ExtenderState> m_extenderSimManager;

  private DIOSim m_sensorSim;

  private SimManager<Double, Double> m_armSimManager;
  private RamenArmSimLogic m_ramenArmSimLogic;

  /**
   * Creates an instance of the ArmSystem or ArmSystemSim class.
   */
  public static ArmSystem createArmSystemInstance(XboxController controller) {
    ArmSystem result;

    if (RobotBase.isSimulation()) {
      result = new ArmSystemSim(controller);

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

    Client<Supplier<MultiType>> shuffleClient = PrefixedConcurrentMap
        .createShuffleboardClientForSubsystem("ArmSystem");
    createWinchSimParts(shuffleClient);
    createExtenderSimParts(shuffleClient);
    createArmAngleSimParts(shuffleClient);

    m_sensorSim = new DIOSim(m_sensor);

    // $LATER - Eventually, move this into ShuffleboardManager class
    shuffleClient.getSubdirectoryClient("Extender").addItem("Sensor",
        () -> MultiType.of(!m_sensorSim.getValue()));

    // Create simulated absolute encoder
    m_winchAbsoluteEncoderSim = new DutyCycleEncoderSim2(m_winchAbsoluteEncoder);

    // Create a DoubleSupplier that gets the angle
    Supplier<Double> armAngleSupplier = () -> {
      return m_armAngleState.getAngleSignedDegrees();
    };

    ArmSimParams armParams = new ArmSimParams(
        UnitConversions.rotationToSignedDegrees(Constants.OperatorConstants.kWinchEncoderUpperLimit
            - Constants.SimConstants.karmEncoderRotationsOffset
            + Constants.SimConstants.kdeltaRotationsBeforeBroken),
        UnitConversions.rotationToSignedDegrees(Constants.OperatorConstants.kWinchEncoderLowerLimit
            - Constants.SimConstants.karmEncoderRotationsOffset
            - Constants.SimConstants.kdeltaRotationsBeforeBroken),
        Constants.SimConstants.karmEncoderRotationsOffset);

    Pair<SimManager<Double, Double>, RamenArmSimLogic> createResult = RamenArmSimLogic
        .createRamenArmSimulation(shuffleClient,
            armAngleSupplier,
            m_winchAbsoluteEncoderSim,
            armParams,
            UnitConversions
                .rotationToSignedDegrees(Constants.SimConstants.kgrabberBreaksIfOpenBelowThisLimit
                    - Constants.SimConstants.karmEncoderRotationsOffset),
            false);

    m_armSimManager = createResult.getFirst();
    m_ramenArmSimLogic = createResult.getSecond();
  }

  private void createArmAngleSimParts(Client<Supplier<MultiType>> shuffleClient) {
    // Create a DoubleSupplier that gets the value m_winchState.getStringUnspooledLen()
    Supplier<Double> stringUnspooledLenSupplier = () -> {
      return m_winchState.getStringUnspooledLen();
    };

    m_armAngleState = new ArmAngleState();

    PivotMechanism pivotMechanism = new PivotMechanism(
        Constants.SimConstants.karmHeightFromWinchToPivotPoint,
        Constants.SimConstants.karmLengthFromEdgeToPivot);

    m_angleSimManager = new SimManager<Double, ArmAngleState>(new ArmAngleSimModel(pivotMechanism),
        null, null, false);
    m_angleSimManager.setInputHandler(new LambdaSimInput<Double>(stringUnspooledLenSupplier));
    m_angleSimManager.setOutputHandler(new CopySimOutput<ArmAngleState>(m_armAngleState));
  }

  private void createWinchSimParts(Client<Supplier<MultiType>> shuffleClient) {
    // Create winch simulated encoder
    m_winchEncoderSim = new RelativeEncoderSim(m_winchEncoder);

    m_winchState = new WinchState();

    // Create the motor simulation for the winch motor
    m_winchMotorSimManager = new SimManager<Double, Double>(
        new MotorSimModel(Constants.SimConstants.kwinchSimGearRatio),
        shuffleClient.getSubdirectoryClient("WinchMotor"), new MotorDashboardPlugin(), false);
    m_winchMotorSimManager.setInputHandler(new MotorSparkMaxSimInput(m_armWinch));
    m_winchMotorSimManager.setOutputHandler(new RelEncoderSimOutput(m_winchEncoderSim));

    // Create the winch simulation
    WinchParams winchParams = new WinchParams(0.0254, new WinchCable(
        Constants.SimConstants.kTotalStringLenMeters,
        Constants.SimConstants.kTotalStringLenMeters - Constants.SimConstants.kCurrentLenSpooled,
        WindingOrientation.BackOfRobot), true);

    m_winchSimManager = new SimManager<Double, WinchState>(new WinchSimModel(winchParams),
        shuffleClient.getSubdirectoryClient("Winch"), new WinchDashboardPlugin(), false);
    m_winchSimManager.setInputHandler(new RelEncoderSimInput(m_winchEncoderSim));
    m_winchSimManager.setOutputHandler(new CopySimOutput<WinchState>(m_winchState));
  }

  private void createExtenderSimParts(Client<Supplier<MultiType>> shuffleClient) {
    // Create extender simulated encoder
    m_extenderEncoderSim = new RelativeEncoderSim(m_extenderEncoder);

    // Create the motor simulation for the extender motor
    m_extenderMotorSimManager = new SimManager<Double, Double>(
        new MotorSimModel(Constants.SimConstants.kextenderSimGearRatio),
        shuffleClient.getSubdirectoryClient("ExtenderMotor"), new MotorDashboardPlugin(), false);
    m_extenderMotorSimManager.setInputHandler(new MotorSparkMaxSimInput(m_armExtender));
    m_extenderMotorSimManager.setOutputHandler(new RelEncoderSimOutput(m_extenderEncoderSim));

    // Create the extender simulation
    m_extenderState = new ExtenderState();

    ExtenderParams extenderParams = new ExtenderParams(
        Constants.SimConstants.kcylinderDiameterMeters,
        Constants.SimConstants.kTotalExtenderLenMeters, Constants.SimConstants.kInitialExtendedLen,
        true);

    m_extenderSimManager = new SimManager<Double, ExtenderState>(
        new ExtenderSimModel(m_extenderEncoderSim.getPosition(), extenderParams),
        shuffleClient.getSubdirectoryClient("Extender"), new ExtenderDashboardPlugin(), false);

    m_extenderSimManager.setInputHandler(new RelEncoderSimInput(m_extenderEncoderSim));
    m_extenderSimManager.setOutputHandler(new CopySimOutput<ExtenderState>(m_extenderState));
  }

  // $LATER Get rid of isRobotEnabled
  private boolean isRobotEnabled() {
    return RobotState.isEnabled();
  }

  public void setGrabberOpenSupplier(BooleanSupplier grabberOpenSupplier) {
    m_ramenArmSimLogic.setGrabberOpenSupplier(grabberOpenSupplier);
  }

  // $LATER - This is temporary until we combine string and arm simulation
  private void simulatePeriodicStringAndArm(SimManager<Double, ArmAngleState> angleSimulation,
      SimManager<Double, Double> armSimManager) {

    angleSimulation.simulationPeriodic();
    armSimManager.simulationPeriodic();
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
      m_extenderSimManager.simulationPeriodic();

      simulatePeriodicStringAndArm(m_angleSimManager, m_armSimManager);

      boolean isExtenderSensorOn = m_extenderState
          .getExtendedLen() <= Constants.SimConstants.kextenderFullyRetractedLen;
      m_sensorSim.setValue(!isExtenderSensorOn);
    }
  }

  private double getArmPercentRaised() {
    double lowerLimit = Constants.OperatorConstants.kWinchEncoderLowerLimit;
    double upperLimit = Constants.OperatorConstants.kWinchEncoderUpperLimit;
    double currentPosition = m_winchAbsoluteEncoder.getAbsolutePosition();

    return (currentPosition - lowerLimit) / (upperLimit - lowerLimit);
  }

  private void addShuffleboardWidgets() {
    // Add Robot Arm widget
    // $LATER Don't hardcode name of the widget and location
    Shuffleboard.getTab("Simulation").add("Happy",
        new SendableArmPosition(() -> getArmPercentRaised(),
            () -> m_extenderState.getExtendedPercent(), () -> m_ramenArmSimLogic.getGrabberOpen()))
        .withWidget(simulationlib.Constants.kAnimatedArmWidget).withPosition(7, 0).withSize(3, 3);
  }

  @Override
  public void initDashBoard() {
    super.initDashBoard();

    addShuffleboardWidgets();
  }
}
