package frc.robot.simulation.simplearm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import edu.wpi.first.hal.HAL;
import edu.wpi.first.math.Pair;
import edu.wpi.first.wpilibj.DutyCycleEncoder;
import edu.wpi.first.wpilibj.simulation.DutyCycleEncoderSim;
import frc.robot.Constants;
import frc.robot.helpers.DutyCycleEncoderSim2;
import frc.robot.helpers.UnitConversions;
import frc.robot.simulation.armangle.ArmAngleParams;
import frc.robot.simulation.armangle.ArmAngleSimInput;
import frc.robot.simulation.armangle.ArmAngleSimModel;
import frc.robot.simulation.armangle.ArmAngleState;
import frc.robot.simulation.armangle.CalcArmAngleHelper;
import frc.robot.simulation.framework.SimManager;
import frc.robot.simulation.framework.inputoutputs.CopySimOutput;
import frc.robot.simulation.framework.inputoutputs.LambdaSimInput;
import frc.robot.simulation.framework.inputoutputs.LambdaSimOutput;
import frc.robot.simulation.simplearm.ramenarmlogic.RamenArmSimLogic;
import frc.robot.simulation.winch.WinchSimModel;
import frc.robot.simulation.winch.WinchSimModel.WindingOrientation;
import frc.robot.simulation.winch.WinchState;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests the ArmSimulation class.
 */
public class ArmSimModelTest {
  private final ArmSimParams m_defaultArmParams;
  private final double m_defaultHeightFromWinchToPivotPoint;
  private final double m_defaultArmLengthFromEdgeToPivot;
  private final double m_defaultArmLengthFromEdgeToPivotMin;
  private final double m_defaultGrabberBreaksRotations = 0.80;
  private CalcArmAngleHelper m_calcArmAngleHelper;
  private final double m_winchSpoolDiameterMeters = 0.01; // (1 centimeter)
  private final double m_winchTotalStringLenMeters = 5;
  private final double m_winchInitialLenSpooled = 4;
  private final WindingOrientation m_winchInitialStringOrientation = WindingOrientation.BackOfRobot;
  private final boolean m_winchinvertMotor = false;

  // $TODO - Delete m_winchSimulation
  private WinchSimModel m_winchSimulation;
  private SimManager<Double, WinchState> m_winchSimManager; // $TODO - This is new, but not used yet
  private ArmAngleState m_armAngleState; // $TODO - This should go away?
  Supplier<Double> m_armAngleSupplier;
  private DutyCycleEncoder m_winchAbsoluteEncoder = null;
  private DutyCycleEncoderSim m_winchAbsoluteEncoderSim = null;

  /**
   * Constructor.
   */
  public ArmSimModelTest() {
    m_defaultHeightFromWinchToPivotPoint = 1;
    m_defaultArmLengthFromEdgeToPivot = 0.5;
    m_defaultArmLengthFromEdgeToPivotMin = 0.1;

    m_defaultArmParams = new ArmSimParams(UnitConversions.rotationToSignedDegrees(0.25),
        UnitConversions.rotationToSignedDegrees(0.75), // bottomRotationsBreak
        UnitConversions.rotationToUnsignedDegrees(0)); // encoderRotationsOffset

    m_calcArmAngleHelper = new CalcArmAngleHelper(m_defaultHeightFromWinchToPivotPoint,
        m_defaultArmLengthFromEdgeToPivot);

    // Create a Producer that gets the value of m_armAngleDegrees
    m_armAngleSupplier = () -> {
      return m_armAngleState.getAngleSignedDegrees();
    };
  }

  // Internal return type used to return multiple SimManagers.
  class SimManagersType {
    @SuppressWarnings("MemberNameCheck")
    public SimManager<Double, Double> armSimManager;
    @SuppressWarnings("MemberNameCheck")
    public SimManager<Double, ArmAngleState> angleSimManager;
    @SuppressWarnings("MemberNameCheck")
    public SimManager<Double, WinchState> winchSimManager;

    public SimManagersType(SimManager<Double, Double> armSimManager,
        SimManager<Double, ArmAngleState> angleSimManager,
        SimManager<Double, WinchState> winchSimManager) {
      this.armSimManager = armSimManager;
      this.angleSimManager = angleSimManager;
      this.winchSimManager = winchSimManager;
    }
  }

  /**
   * Runs before every test.
   */
  @BeforeEach
  public void setUp() {
    assert HAL.initialize(500, 0); // initialize the HAL, crash if failed

    // $TODO - DONT stash this. Fix ASAP
    m_winchSimulation = new WinchSimModel(m_winchSpoolDiameterMeters, m_winchTotalStringLenMeters,
        m_winchInitialLenSpooled, m_winchInitialStringOrientation, m_winchinvertMotor);

    m_armAngleState = new ArmAngleState();

    m_winchAbsoluteEncoder = new DutyCycleEncoder(
        Constants.OperatorConstants.kAbsoluteEncoderWinchChannel);
    m_winchAbsoluteEncoderSim = new DutyCycleEncoderSim2(m_winchAbsoluteEncoder);
  }

  @SuppressWarnings("PMD.SignatureDeclareThrowsException")
  @AfterEach
  void shutdown() throws Exception {
    if (m_winchAbsoluteEncoder != null) {
      m_winchAbsoluteEncoder.close();
      m_winchAbsoluteEncoder = null;
    }
  }

  // $LATER - This is temporary. Once we place the string angle simulation plus the arm
  // simulation into a single simulation class, we can get rid of this.
  private boolean getIsStringOrArmBroken(SimManager<Double, ArmAngleState> armAngleManager,
      SimManager<Double, Double> armSimManager) {

    return armAngleManager.isBroken() || armSimManager.isBroken();
  }

  // $LATER - This is temporary until we place the string angle simulation plus the arm
  // simulation into a single simulation class.
  private void simulatePeriodicStringAndArm(SimManager<Double, ArmAngleState> angleSimManager,
      SimManager<Double, Double> armSimManager) {

    angleSimManager.simulationPeriodic();
    armSimManager.simulationPeriodic();
  }

  // $TODO - This is the old version
  @SuppressWarnings("LineLengthCheck")
  private Pair<SimManager<Double, Double>, SimManager<Double, ArmAngleState>> createDefaultArmHelper_old(
      WinchSimModel winchSimulation,
      ArmAngleState armAngleState,
      boolean initialIsGrabberOpen,
      boolean expectArmBroken) {

    // Create a DoubleSupplier that gets the value getStringUnspooledLen()
    Supplier<Double> stringUnspooledLenSupplier = () -> {
      return winchSimulation.getStringUnspooledLen();
    };

    ArmAngleParams armAngleParams = new ArmAngleParams(m_defaultHeightFromWinchToPivotPoint,
        m_defaultArmLengthFromEdgeToPivot, m_defaultArmLengthFromEdgeToPivotMin);

    SimManager<Double, ArmAngleState> angleSimManager = new SimManager<Double, ArmAngleState>(
        new ArmAngleSimModel(armAngleParams), true);
    angleSimManager.setInputHandler(new ArmAngleSimInput(stringUnspooledLenSupplier));
    angleSimManager.setOutputHandler(new CopySimOutput<ArmAngleState>(armAngleState));

    double offsetRotations = 0;
    double grabberLimitRotations = m_defaultGrabberBreaksRotations + offsetRotations;

    Pair<SimManager<Double, Double>, RamenArmSimLogic> createResult = RamenArmSimLogic
        .createRamenArmSimulation(m_armAngleSupplier,
            m_winchAbsoluteEncoderSim,
            m_defaultArmParams,
            UnitConversions.rotationToSignedDegrees(grabberLimitRotations - offsetRotations),
            true);

    SimManager<Double, Double> armSimManager = createResult.getFirst();
    RamenArmSimLogic ramenArmSimLogic = createResult.getSecond();

    // Set grabber
    BooleanSupplier isGrabberOpen = () -> initialIsGrabberOpen;
    ramenArmSimLogic.setGrabberOpenSupplier(isGrabberOpen);

    assertTrue(armSimManager != null);
    assertTrue(!winchSimulation.isModelBroken()); // $TODO - Shouldnt be stashing winchSimulation!
    assertTrue(getIsStringOrArmBroken(angleSimManager, armSimManager) == expectArmBroken);

    return new Pair<SimManager<Double, Double>, SimManager<Double, ArmAngleState>>(armSimManager,
        angleSimManager);
  }

  private SimManagersType createDefaultArmHelper_new(Supplier<Double> winchInputSupplier,
      ArmAngleState armAngleState,
      double winchInitialLenSpooled,
      boolean initialIsGrabberOpen,
      boolean expectArmBroken) {

    // Local variable used to hold the winch output. It exists outside of this
    // function since the lambdas capture it.
    WinchState winchState = new WinchState(m_winchTotalStringLenMeters);

    WinchSimModel winchSimModel = new WinchSimModel(m_winchSpoolDiameterMeters,
        m_winchTotalStringLenMeters, winchInitialLenSpooled, m_winchInitialStringOrientation,
        m_winchinvertMotor);

    SimManager<Double, WinchState> winchSimManager = new SimManager<Double, WinchState>(
        winchSimModel, true);
    winchSimManager.setInputHandler(new LambdaSimInput<Double>(winchInputSupplier));
    winchSimManager.setOutputHandler(new CopySimOutput<WinchState>(winchState));

    // Create a DoubleSupplier that gets the value getStringUnspooledLen()
    Supplier<Double> stringUnspooledLenSupplier = () -> {
      return winchState.getStringUnspooledLen();
    };

    ArmAngleParams armAngleParams = new ArmAngleParams(m_defaultHeightFromWinchToPivotPoint,
        m_defaultArmLengthFromEdgeToPivot, m_defaultArmLengthFromEdgeToPivotMin);

    SimManager<Double, ArmAngleState> angleSimManager = new SimManager<Double, ArmAngleState>(
        new ArmAngleSimModel(armAngleParams), true);
    angleSimManager.setInputHandler(new ArmAngleSimInput(stringUnspooledLenSupplier));
    angleSimManager.setOutputHandler(new CopySimOutput<ArmAngleState>(armAngleState));

    double offsetRotations = 0;
    double grabberLimitRotations = m_defaultGrabberBreaksRotations + offsetRotations;

    Supplier<Double> desiredArmAngleSupplier = () -> {
      return armAngleState.getAngleSignedDegrees();
    };

    Pair<SimManager<Double, Double>, RamenArmSimLogic> createResult = RamenArmSimLogic
        .createRamenArmSimulation(desiredArmAngleSupplier,
            m_winchAbsoluteEncoderSim,
            m_defaultArmParams,
            UnitConversions.rotationToSignedDegrees(grabberLimitRotations - offsetRotations),
            true);

    SimManager<Double, Double> armSimManager = createResult.getFirst();
    RamenArmSimLogic ramenArmSimLogic = createResult.getSecond();

    // Set grabber
    BooleanSupplier isGrabberOpen = () -> initialIsGrabberOpen;
    ramenArmSimLogic.setGrabberOpenSupplier(isGrabberOpen);

    assertTrue(armSimManager != null);
    assertTrue(!winchSimManager.isBroken());
    assertTrue(getIsStringOrArmBroken(angleSimManager, armSimManager) == expectArmBroken);

    return new SimManagersType(armSimManager, angleSimManager, winchSimManager);
  }

  // $TODO - This method should be deleted
  private WinchSimModel createWinchSimulation(double winchInitialLenSpooled) {
    WinchSimModel winchSimulation = new WinchSimModel(m_winchSpoolDiameterMeters,
        m_winchTotalStringLenMeters, winchInitialLenSpooled, m_winchInitialStringOrientation,
        m_winchinvertMotor);

    assertTrue(winchSimulation != null);
    assertTrue(!winchSimulation.isModelBroken()); // $TODO - Shouldnt be stashing winchSimulation!

    return winchSimulation;
  }

  @Test
  public void createArmSimulationShouldSucceed() {
    ArmAngleState tempArmAngleState = new ArmAngleState();
    Supplier<Double> staticWinchInputSupplier = () -> {
      return 0.0;
    };

    SimManagersType simManagers = createDefaultArmHelper_new(staticWinchInputSupplier,
        tempArmAngleState,
        m_winchInitialLenSpooled,
        false,
        false);

    SimManager<Double, Double> tempArmSimManager = simManagers.armSimManager;
    SimManager<Double, ArmAngleState> tempAngleSimManager = simManagers.angleSimManager;
    SimManager<Double, WinchState> tempWinchSimManager = simManagers.winchSimManager;

    assertTrue(tempArmSimManager != null);
    assertTrue(!getIsStringOrArmBroken(tempAngleSimManager, tempArmSimManager)
        && !tempWinchSimManager.isBroken());
  }

  @Test
  public void nullWinchSimShouldThrowException() {
    assertThrows(IllegalArgumentException.class, () -> {

      double offsetRotations = 0;
      double grabberLimitRotations = m_defaultGrabberBreaksRotations + offsetRotations;

      Pair<SimManager<Double, Double>, RamenArmSimLogic> createResult = RamenArmSimLogic
          .createRamenArmSimulation(null,
              m_winchAbsoluteEncoderSim,
              m_defaultArmParams,
              UnitConversions.rotationToSignedDegrees(grabberLimitRotations - offsetRotations),
              true);

      SimManager<Double, Double> tempArmSimManager = createResult.getFirst();
      assertTrue(tempArmSimManager != null);
    });
  }

  @Test
  public void armUpWithGrabberInitiallyOpenShouldSucceed() {
    double lengthStringExtended = m_defaultHeightFromWinchToPivotPoint - 0.5;
    double winchInitialLenSpooled = m_winchTotalStringLenMeters - lengthStringExtended;
    ArmAngleState tempArmAngleState = new ArmAngleState();
    Supplier<Double> staticWinchInputSupplier = () -> {
      return 0.0;
    };

    SimManagersType simManagers = createDefaultArmHelper_new(staticWinchInputSupplier,
        tempArmAngleState,
        winchInitialLenSpooled,
        true,
        false);

    SimManager<Double, Double> tempArmSimManager = simManagers.armSimManager;
    SimManager<Double, ArmAngleState> tempAngleSimManager = simManagers.angleSimManager;
    SimManager<Double, WinchState> tempWinchSimManager = simManagers.winchSimManager;

    assertTrue(tempArmSimManager != null);
    assertTrue(!tempWinchSimManager.isBroken());
    assertTrue(!getIsStringOrArmBroken(tempAngleSimManager, tempArmSimManager));
    assertEquals(90,
        UnitConversions.rotationToSignedDegrees(m_winchAbsoluteEncoder.get()),
        UnitConversions.kAngleTolerance);
  }

  @Test
  public void armDownWithGrabberInitiallyOpenShouldFail() {
    double lengthStringExtended = m_defaultHeightFromWinchToPivotPoint + 0.5;
    double winchInitialLenSpooled = m_winchTotalStringLenMeters - lengthStringExtended;

    WinchSimModel tempwinchSimulation = createWinchSimulation(winchInitialLenSpooled);
    Pair<SimManager<Double, Double>, SimManager<Double, ArmAngleState>> resultPair;
    resultPair = createDefaultArmHelper_old(tempwinchSimulation, m_armAngleState, true, false);
    SimManager<Double, Double> tempArmSimManager = resultPair.getFirst();
    SimManager<Double, ArmAngleState> tempAngleSimManager = resultPair.getSecond();

    // Now that grabber is set open, need to simulate one cycle
    simulatePeriodicStringAndArm(tempAngleSimManager, tempArmSimManager);

    assertTrue(getIsStringOrArmBroken(tempAngleSimManager, tempArmSimManager));
    assertEquals(-90,
        UnitConversions.rotationToSignedDegrees(m_winchAbsoluteEncoder.get()),
        UnitConversions.kAngleTolerance);
  }

  private void moveArmWithGrabberOpenHelper(double initialDegreesAboveBreakPoint,
      double targetDegreesAboveBreakPoint,
      double expectedFinalDegreesAboveBreakPoint,
      boolean expectedArmInitiallyBroken,
      boolean expectedWinchIsBroken,
      boolean expectedIsArmBroken) {

    boolean initialIsGrabberOpen = true;

    double initialPosSignedDegrees = UnitConversions
        .rotationToSignedDegrees(m_defaultGrabberBreaksRotations) + initialDegreesAboveBreakPoint;
    double winchInitialLenSpooled = m_winchTotalStringLenMeters
        - m_calcArmAngleHelper.calcAndValidateStringLengthForSignedDegrees(initialPosSignedDegrees);

    WinchSimModel tempwinchSimulation = createWinchSimulation(winchInitialLenSpooled);
    Pair<SimManager<Double, Double>, SimManager<Double, ArmAngleState>> resultPair;
    resultPair = createDefaultArmHelper_old(tempwinchSimulation,
        m_armAngleState,
        initialIsGrabberOpen,
        false);
    SimManager<Double, Double> tempArmSimManager = resultPair.getFirst();
    SimManager<Double, ArmAngleState> tempAngleSimManager = resultPair.getSecond();

    // Initialize the number of rotations
    double currentWinchRotations = 0;
    tempwinchSimulation.updateSimulation(currentWinchRotations);

    // Now that grabber is set open, need to simulate one cycle
    simulatePeriodicStringAndArm(tempAngleSimManager, tempArmSimManager);

    assertTrue(getIsStringOrArmBroken(tempAngleSimManager,
        tempArmSimManager) == expectedArmInitiallyBroken);
    double expect = initialPosSignedDegrees;
    double actual = UnitConversions.rotationToSignedDegrees(m_winchAbsoluteEncoder.get());
    assertEquals(expect, actual, UnitConversions.kAngleTolerance);

    double targetPosSignedDegrees = UnitConversions
        .rotationToSignedDegrees(m_defaultGrabberBreaksRotations) + targetDegreesAboveBreakPoint;
    double winchTargetLenSpooled = m_winchTotalStringLenMeters
        - m_calcArmAngleHelper.calcAndValidateStringLengthForSignedDegrees(targetPosSignedDegrees);

    // Now calculate how much to turn the winch motor to get it to the target position
    double spoolCircumferenceMeters = Math.PI * m_winchSpoolDiameterMeters;
    double deltaWinchRotations = (winchInitialLenSpooled - winchTargetLenSpooled)
        / spoolCircumferenceMeters;

    // Simulate one cycle for winch, so that it updates
    tempwinchSimulation.updateSimulation(deltaWinchRotations);
    simulatePeriodicStringAndArm(tempAngleSimManager, tempArmSimManager);

    // $TODO - Shouldnt be stashing winchSimulation!
    assertTrue(tempwinchSimulation.isModelBroken() == expectedWinchIsBroken);
    assertTrue(
        getIsStringOrArmBroken(tempAngleSimManager, tempArmSimManager) == expectedIsArmBroken);

    expect = UnitConversions.rotationToSignedDegrees(m_defaultGrabberBreaksRotations)
        + expectedFinalDegreesAboveBreakPoint;
    actual = UnitConversions.rotationToSignedDegrees(m_winchAbsoluteEncoder.get());
    assertEquals(expect, actual, UnitConversions.kAngleTolerance);
  }

  @Test
  public void movingArmDownwardPastBreakLimitWithGrabberOpenShouldNotMoveArm() {
    // We expect that the arm gets stuck at the break limit, instead of going all the way to the
    // target degrees
    double initialDegreesAboveBreakPoint = 4;
    double targetDegreesAboveBreakPoint = -4;
    double expectedFinalDegreesAboveBreakPoint = 0;
    boolean expectedArmInitiallyBroken = false;
    boolean expectedWinchIsBroken = false;
    boolean expectedIsArmBroken = false;

    moveArmWithGrabberOpenHelper(initialDegreesAboveBreakPoint,
        targetDegreesAboveBreakPoint,
        expectedFinalDegreesAboveBreakPoint,
        expectedArmInitiallyBroken,
        expectedWinchIsBroken,
        expectedIsArmBroken);
  }

  @Test
  public void movingArmWithGrabberOpenShouldSucceedIfArmIsTowardsTop() {
    double initialDegreesAboveBreakPoint = 4;
    double targetDegreesAboveBreakPoint = 8;
    double expectedFinalDegreesAboveBreakPoint = 8;
    boolean expectedArmInitiallyBroken = false;
    boolean expectedWinchIsBroken = false;
    boolean expectedIsArmBroken = false;

    moveArmWithGrabberOpenHelper(initialDegreesAboveBreakPoint,
        targetDegreesAboveBreakPoint,
        expectedFinalDegreesAboveBreakPoint,
        expectedArmInitiallyBroken,
        expectedWinchIsBroken,
        expectedIsArmBroken);
  }

  @Test
  public void movingArmUpwardFromBreakLimitWithGrabberOpenShouldSucceed() {
    double initialDegreesAboveBreakPoint = 0;
    double targetDegreesAboveBreakPoint = 8;
    double expectedFinalDegreesAboveBreakPoint = 8;
    boolean expectedArmInitiallyBroken = false;
    boolean expectedWinchIsBroken = false;
    boolean expectedIsArmBroken = false;

    moveArmWithGrabberOpenHelper(initialDegreesAboveBreakPoint,
        targetDegreesAboveBreakPoint,
        expectedFinalDegreesAboveBreakPoint,
        expectedArmInitiallyBroken,
        expectedWinchIsBroken,
        expectedIsArmBroken);
  }

  @Test
  public void movingAlreadyBrokenArmShouldNotMoveArm() {
    double initialDegreesAboveBreakPoint = -4;
    double targetDegreesAboveBreakPoint = 8;
    double expectedFinalDegreesAboveBreakPoint = -4;
    boolean expectedArmInitiallyBroken = true;
    boolean expectedWinchIsBroken = false;
    boolean expectedIsArmBroken = true;

    moveArmWithGrabberOpenHelper(initialDegreesAboveBreakPoint,
        targetDegreesAboveBreakPoint,
        expectedFinalDegreesAboveBreakPoint,
        expectedArmInitiallyBroken,
        expectedWinchIsBroken,
        expectedIsArmBroken);
  }

  private void createWithDegreeArmHelper(double backArmAbovePivot,
      double expectedDegrees,
      boolean expectArmBroken) {
    double lengthStringExtended = m_defaultHeightFromWinchToPivotPoint + backArmAbovePivot;
    double winchInitialLenSpooled = m_winchTotalStringLenMeters - lengthStringExtended;

    WinchSimModel tempwinchSimulation = createWinchSimulation(winchInitialLenSpooled);
    Pair<SimManager<Double, Double>, SimManager<Double, ArmAngleState>> resultPair;
    resultPair = createDefaultArmHelper_old(tempwinchSimulation,
        m_armAngleState,
        false,
        expectArmBroken);
    SimManager<Double, Double> tempArmSimManager = resultPair.getFirst();

    assertTrue(tempArmSimManager != null);
    // $TODO - Shouldnt be stashing winchSimulation!
    assertTrue(!tempwinchSimulation.isModelBroken());

    if (!expectArmBroken) {
      assertEquals(expectedDegrees,
          m_winchAbsoluteEncoder.get() * 360,
          UnitConversions.kAngleTolerance);
    }
  }

  @Test
  public void createWithLevelArmShouldSucceed() {
    createWithDegreeArmHelper(0, 0, false);
  }

  @Test
  public void createWith45DegreeArmShouldSucceed() {
    createWithDegreeArmHelper(-0.35355, 45, false);
  }

  @Test
  public void createWith30DegreeArmShouldSucceed() {
    createWithDegreeArmHelper(-0.25, 30, false);
  }

  @Test
  public void createWith90DegreeArmShouldSucceed() {
    createWithDegreeArmHelper(-0.5, 90, false);
  }

  @Test
  public void createWithNegative90DegreeArmShouldSucceed() {
    createWithDegreeArmHelper(0.5, 360 - 90, false);
  }

  @Test
  public void createWith91DegreeArmShouldFail() {
    double amountBeyondLimit = 0.0001;

    createWithDegreeArmHelper(-0.5 - amountBeyondLimit, 90, true);
  }

  @Test
  public void createWithNegative91DegreeArmShouldNotBreakArm() {
    double amountBeyondLimit = 0.0001;

    createWithDegreeArmHelper(0.5 + amountBeyondLimit, 360 - 90, false);
  }

  @Test
  public void createWith98DegreeArmShouldFail() {
    double amountBeyondLimit = 0.1;

    createWithDegreeArmHelper(-0.5 - amountBeyondLimit, 90, true);
  }

  @Test
  public void createWithNegative98DegreeArmShouldNotBreakArm() {
    double amountBeyondLimit = 0.1;

    createWithDegreeArmHelper(0.5 + amountBeyondLimit, 360 - 90, false);
  }

  @Test
  public void createWithNegative45DegreeArmShouldSucceed() {
    createWithDegreeArmHelper(0.35355, 360 - 45, false);
  }

  // Sometimes, the absolute encoder is offset, and 0 isn't level
  @Test
  public void createWithOffsetShouldSucceed() {
    double lengthStringExtended = m_defaultHeightFromWinchToPivotPoint - 0.35355;
    double winchInitialLenSpooled = m_winchTotalStringLenMeters - lengthStringExtended;

    WinchSimModel tempwinchSimulation = createWinchSimulation(winchInitialLenSpooled);

    // Create a DoubleSupplier that gets the value getStringUnspooledLen()
    Supplier<Double> stringUnspooledLenSupplier = () -> {
      return tempwinchSimulation.getStringUnspooledLen();
    };

    ArmAngleParams armAngleParams = new ArmAngleParams(m_defaultHeightFromWinchToPivotPoint,
        m_defaultArmLengthFromEdgeToPivot, m_defaultArmLengthFromEdgeToPivotMin);

    SimManager<Double, ArmAngleState> angleSimManager = new SimManager<Double, ArmAngleState>(
        new ArmAngleSimModel(armAngleParams), true);
    angleSimManager.setInputHandler(new ArmAngleSimInput(stringUnspooledLenSupplier));
    angleSimManager.setOutputHandler(new CopySimOutput<ArmAngleState>(m_armAngleState));

    double offsetRotations = 0.25;
    double grabberLimitRotations = m_defaultGrabberBreaksRotations + offsetRotations;

    ArmSimParamsBuilder tempArmParamsBuilder = new ArmSimParamsBuilder(m_defaultArmParams);

    tempArmParamsBuilder.setTopSignedDegreesBreak(m_defaultArmParams.topSignedDegreesBreak)
        .setBottomSignedDegreesBreak(m_defaultArmParams.bottomSignedDegreesBreak)
        .setEncoderRotationsOffset(offsetRotations);

    Pair<SimManager<Double, Double>, RamenArmSimLogic> createResult = RamenArmSimLogic
        .createRamenArmSimulation(m_armAngleSupplier,
            m_winchAbsoluteEncoderSim,
            tempArmParamsBuilder.build(),
            UnitConversions.rotationToSignedDegrees(grabberLimitRotations - offsetRotations),
            true);

    SimManager<Double, Double> tempArmSimManager = createResult.getFirst();

    assertTrue(tempArmSimManager != null);
    // $TODO - Shouldnt be stashing winchSimulation!
    assertTrue(!tempwinchSimulation.isModelBroken());
    assertTrue(!getIsStringOrArmBroken(angleSimManager, tempArmSimManager));

    double expectedDegrees = 45 + 90;
    assertEquals(expectedDegrees,
        UnitConversions.rotationToUnsignedDegrees(m_winchAbsoluteEncoder.get()),
        UnitConversions.kAngleTolerance);
  }
}
