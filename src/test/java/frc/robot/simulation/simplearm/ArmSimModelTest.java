package frc.robot.simulation.simplearm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import edu.wpi.first.hal.HAL;
import edu.wpi.first.math.Pair;
import edu.wpi.first.wpilibj.DutyCycleEncoder;
import edu.wpi.first.wpilibj.simulation.DutyCycleEncoderSim;
import frc.robot.Constants;
import frc.robot.helpers.UnitConversions;
import simulationlib.simulation.armangle.ArmAngleSimModel;
import simulationlib.simulation.armangle.ArmAngleState;
import simulationlib.simulation.armangle.PivotMechanism;
import simulationlib.simulation.framework.SimManager;
import simulationlib.simulation.framework.customwrappers.DutyCycleEncoderSim2;
import simulationlib.simulation.framework.inputoutputs.CopySimOutput;
import simulationlib.simulation.framework.inputoutputs.LambdaSimInput;
import simulationlib.simulation.simplearm.ArmSimParams;
import simulationlib.simulation.simplearm.ArmSimParamsBuilder;
import simulationlib.simulation.simplearm.ramenarmlogic.RamenArmSimLogic;
import simulationlib.simulation.winch.WinchCable;
import simulationlib.simulation.winch.WinchParams;
import simulationlib.simulation.winch.WinchSimModel;
import simulationlib.simulation.winch.WinchState;
import simulationlib.simulation.winch.WinchSimModel.WindingOrientation;

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
  private final double m_defaultGrabberBreaksRotations = 0.80;
  private PivotMechanism m_pivotMechanism;
  private final double m_winchSpoolDiameterMeters = 0.01; // (1 centimeter)
  private final double m_winchTotalStringLenMeters = 5;
  private final double m_winchInitialLenSpooled = 4;
  private final WindingOrientation m_winchInitialStringOrientation = WindingOrientation.BackOfRobot;
  private final boolean m_winchinvertMotor = false;

  private DutyCycleEncoder m_winchAbsoluteEncoder = null;
  private DutyCycleEncoderSim m_winchAbsoluteEncoderSim = null;

  /**
   * Constructor.
   */
  public ArmSimModelTest() {
    m_defaultHeightFromWinchToPivotPoint = 1;
    m_defaultArmLengthFromEdgeToPivot = 0.5;

    m_defaultArmParams = new ArmSimParams(UnitConversions.rotationToSignedDegrees(0.25),
        UnitConversions.rotationToSignedDegrees(0.75), // bottomRotationsBreak
        UnitConversions.rotationToUnsignedDegrees(0)); // encoderRotationsOffset

    m_pivotMechanism = new PivotMechanism(m_defaultHeightFromWinchToPivotPoint,
        m_defaultArmLengthFromEdgeToPivot);
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

  private SimManagersType createDefaultArmHelper(Supplier<Double> winchInputSupplier,
      ArmAngleState armAngleState,
      double winchInitialLenSpooled,
      boolean initialIsGrabberOpen,
      boolean expectArmBroken) {

    // Local variable used to hold the winch output. It exists outside of this
    // function since the lambdas capture it.
    WinchState winchState = new WinchState();

    WinchParams winchParams = new WinchParams(m_winchSpoolDiameterMeters,
        new WinchCable(m_winchTotalStringLenMeters,
            m_winchTotalStringLenMeters - winchInitialLenSpooled, m_winchInitialStringOrientation),
        m_winchinvertMotor);

    SimManager<Double, WinchState> winchSimManager = new SimManager<Double, WinchState>(
        new WinchSimModel(winchParams), null, null, true);
    winchSimManager.setInputHandler(new LambdaSimInput<Double>(winchInputSupplier));
    winchSimManager.setOutputHandler(new CopySimOutput<WinchState>(winchState));

    // Create a DoubleSupplier that gets the value getStringUnspooledLen()
    Supplier<Double> stringUnspooledLenSupplier = () -> {
      return winchState.getStringUnspooledLen();
    };

    PivotMechanism pivotMechanism = new PivotMechanism(m_defaultHeightFromWinchToPivotPoint,
        m_defaultArmLengthFromEdgeToPivot);

    SimManager<Double, ArmAngleState> angleSimManager = new SimManager<Double, ArmAngleState>(
        new ArmAngleSimModel(pivotMechanism), null, null, true);
    angleSimManager.setInputHandler(new LambdaSimInput<Double>(stringUnspooledLenSupplier));
    angleSimManager.setOutputHandler(new CopySimOutput<ArmAngleState>(armAngleState));

    double offsetRotations = 0;
    double grabberLimitRotations = m_defaultGrabberBreaksRotations + offsetRotations;

    Supplier<Double> desiredArmAngleSupplier = () -> {
      return armAngleState.getAngleSignedDegrees();
    };

    Pair<SimManager<Double, Double>, RamenArmSimLogic> createResult = RamenArmSimLogic
        .createRamenArmSimulation(null,
            desiredArmAngleSupplier,
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

  @Test
  public void createArmSimulationShouldSucceed() {
    ArmAngleState tempArmAngleState = new ArmAngleState();
    Supplier<Double> staticWinchInputSupplier = () -> {
      return 0.0;
    };

    SimManagersType simManagers = createDefaultArmHelper(staticWinchInputSupplier,
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
              null,
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

    SimManagersType simManagers = createDefaultArmHelper(staticWinchInputSupplier,
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
    ArmAngleState tempArmAngleState = new ArmAngleState();
    Supplier<Double> staticWinchInputSupplier = () -> {
      return 0.0;
    };

    SimManagersType simManagers = createDefaultArmHelper(staticWinchInputSupplier,
        tempArmAngleState,
        winchInitialLenSpooled,
        true,
        false);

    SimManager<Double, Double> tempArmSimManager = simManagers.armSimManager;
    SimManager<Double, ArmAngleState> tempAngleSimManager = simManagers.angleSimManager;
    SimManager<Double, WinchState> tempWinchSimManager = simManagers.winchSimManager;

    // Now that grabber is set open, need to simulate one cycle
    simulatePeriodicStringAndArm(tempAngleSimManager, tempArmSimManager);

    assertTrue(tempArmSimManager != null);
    assertTrue(!tempWinchSimManager.isBroken());
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
    ArmAngleState tempArmAngleState = new ArmAngleState();
    double[] currentWinchRotations = {
        0
    };
    Supplier<Double> staticWinchInputSupplier = () -> {
      return currentWinchRotations[0];
    };
    double initialPosSignedDegrees = UnitConversions
        .rotationToSignedDegrees(m_defaultGrabberBreaksRotations) + initialDegreesAboveBreakPoint;
    double winchInitialLenSpooled = m_winchTotalStringLenMeters
        - m_pivotMechanism.calcAndValidateStringLengthForSignedDegrees(initialPosSignedDegrees);

    SimManagersType simManagers = createDefaultArmHelper(staticWinchInputSupplier,
        tempArmAngleState,
        winchInitialLenSpooled,
        initialIsGrabberOpen,
        false);

    SimManager<Double, Double> tempArmSimManager = simManagers.armSimManager;
    SimManager<Double, ArmAngleState> tempAngleSimManager = simManagers.angleSimManager;
    SimManager<Double, WinchState> tempWinchSimManager = simManagers.winchSimManager;

    // Initialize the number of rotations
    currentWinchRotations[0] = 0;
    tempWinchSimManager.simulationPeriodic();

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
        - m_pivotMechanism.calcAndValidateStringLengthForSignedDegrees(targetPosSignedDegrees);

    // Now calculate how much to turn the winch motor to get it to the target position
    double spoolCircumferenceMeters = Math.PI * m_winchSpoolDiameterMeters;
    double deltaWinchRotations = (winchInitialLenSpooled - winchTargetLenSpooled)
        / spoolCircumferenceMeters;

    // Simulate one cycle for winch, so that it updates
    currentWinchRotations[0] = deltaWinchRotations;
    tempWinchSimManager.simulationPeriodic();

    simulatePeriodicStringAndArm(tempAngleSimManager, tempArmSimManager);

    assertTrue(tempWinchSimManager.isBroken() == expectedWinchIsBroken);
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
    ArmAngleState tempArmAngleState = new ArmAngleState();
    Supplier<Double> staticWinchInputSupplier = () -> {
      return 0.0;
    };

    SimManagersType simManagers = createDefaultArmHelper(staticWinchInputSupplier,
        tempArmAngleState,
        winchInitialLenSpooled,
        false,
        expectArmBroken);

    SimManager<Double, Double> tempArmSimManager = simManagers.armSimManager;
    SimManager<Double, WinchState> tempWinchSimManager = simManagers.winchSimManager;

    assertTrue(tempArmSimManager != null);
    assertTrue(!tempWinchSimManager.isBroken());

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
    Supplier<Double> staticWinchInputSupplier = () -> {
      return 0.0;
    };

    // Local variable used to hold the winch output. It exists outside of this
    // function since the lambdas capture it.
    WinchState winchState = new WinchState();

    WinchParams winchParams = new WinchParams(m_winchSpoolDiameterMeters,
        new WinchCable(m_winchTotalStringLenMeters,
            m_winchTotalStringLenMeters - winchInitialLenSpooled, m_winchInitialStringOrientation),
        m_winchinvertMotor);

    SimManager<Double, WinchState> winchSimManager = new SimManager<Double, WinchState>(
        new WinchSimModel(winchParams), null, null, true);
    winchSimManager.setInputHandler(new LambdaSimInput<Double>(staticWinchInputSupplier));
    winchSimManager.setOutputHandler(new CopySimOutput<WinchState>(winchState));

    // Create a DoubleSupplier that gets the value getStringUnspooledLen()
    Supplier<Double> stringUnspooledLenSupplier = () -> {
      return winchState.getStringUnspooledLen();
    };

    PivotMechanism pivotMechanism = new PivotMechanism(m_defaultHeightFromWinchToPivotPoint,
        m_defaultArmLengthFromEdgeToPivot);

    ArmAngleState tempArmAngleState = new ArmAngleState();

    SimManager<Double, ArmAngleState> angleSimManager = new SimManager<Double, ArmAngleState>(
        new ArmAngleSimModel(pivotMechanism), null, null, true);
    angleSimManager.setInputHandler(new LambdaSimInput<Double>(stringUnspooledLenSupplier));
    angleSimManager.setOutputHandler(new CopySimOutput<ArmAngleState>(tempArmAngleState));

    double offsetRotations = 0.25;
    double grabberLimitRotations = m_defaultGrabberBreaksRotations + offsetRotations;

    ArmSimParamsBuilder tempArmParamsBuilder = new ArmSimParamsBuilder(m_defaultArmParams);

    tempArmParamsBuilder.setTopSignedDegreesBreak(m_defaultArmParams.topSignedDegreesBreak)
        .setBottomSignedDegreesBreak(m_defaultArmParams.bottomSignedDegreesBreak)
        .setEncoderRotationsOffset(offsetRotations);

    Supplier<Double> desiredArmAngleSupplier = () -> {
      return tempArmAngleState.getAngleSignedDegrees();
    };

    Pair<SimManager<Double, Double>, RamenArmSimLogic> createResult = RamenArmSimLogic
        .createRamenArmSimulation(null,
            desiredArmAngleSupplier,
            m_winchAbsoluteEncoderSim,
            tempArmParamsBuilder.build(),
            UnitConversions.rotationToSignedDegrees(grabberLimitRotations - offsetRotations),
            true);

    SimManager<Double, Double> tempArmSimManager = createResult.getFirst();

    assertTrue(tempArmSimManager != null);
    assertTrue(!winchSimManager.isBroken());
    assertTrue(!getIsStringOrArmBroken(angleSimManager, tempArmSimManager));

    double expectedDegrees = 45 + 90;
    assertEquals(expectedDegrees,
        UnitConversions.rotationToUnsignedDegrees(m_winchAbsoluteEncoder.get()),
        UnitConversions.kAngleTolerance);
  }
}
