package frc.robot.simulation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import edu.wpi.first.hal.HAL;
import edu.wpi.first.wpilibj.DutyCycleEncoder;
import edu.wpi.first.wpilibj.simulation.DutyCycleEncoderSim;
import frc.robot.Constants;
import frc.robot.helpers.CalcArmAngleHelper;
import frc.robot.helpers.UnitConversions;
import frc.robot.simulation.winch.WinchSimModel;
import frc.robot.simulation.winch.WinchSimModel.WindingOrientation;
import frc.robot.subsystems.DutyCycleEncoderSim2;
import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests the ArmSimulation class.
 */
public class ArmSimulationTest {
  private final ArmSimulationParams m_defaultArmParams;
  private CalcArmAngleHelper m_calcArmAngleHelper;
  private final double m_winchSpoolDiameterMeters = 0.01; // (1 centimeter)
  private final double m_winchTotalStringLenMeters = 5;
  private final double m_winchInitialLenSpooled = 4;
  private final WindingOrientation m_winchInitialStringOrientation = WindingOrientation.BackOfRobot;
  private final boolean m_winchinvertMotor = false;

  private WinchSimModel m_winchSimulation;
  private DutyCycleEncoder m_winchAbsoluteEncoder = null;
  private DutyCycleEncoderSim m_winchAbsoluteEncoderSim = null;

  /**
   * Constructor.
   */
  public ArmSimulationTest() {
    m_defaultArmParams = new ArmSimulationParams(0.25, // topRotationsLimit
        0.75, // bottomRotationsLimit
        0, // deltaRotationsBeforeBroken
        0.80, // grabberBreaksIfOpenBelowThisLimit
        1, // heightFromWinchToPivotPoint
        0.5, // armLengthFromEdgeToPivot
        0.1, // armLengthFromEdgeToPivotMin
        0); // encoderRotationsOffset

    m_calcArmAngleHelper = new CalcArmAngleHelper(m_defaultArmParams.m_heightFromWinchToPivotPoint,
        m_defaultArmParams.m_armLengthFromEdgeToPivot);
  }

  /**
   * Runs before every test.
   */
  @BeforeEach
  public void setUp() {
    assert HAL.initialize(500, 0); // initialize the HAL, crash if failed

    m_winchSimulation = new WinchSimModel(m_winchSpoolDiameterMeters, m_winchTotalStringLenMeters,
        m_winchInitialLenSpooled, m_winchInitialStringOrientation, m_winchinvertMotor);

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

  private ArmSimulation createDefaultArmHelper(WinchSimModel winchSimulation,
      boolean initialIsGrabberOpen,
      boolean expectArmBroken) {

    // Create a DoubleSupplier that gets the value getStringUnspooledLen()
    DoubleSupplier stringUnspooledLenSupplier = () -> {
      return winchSimulation.getStringUnspooledLen();
    };

    ArmSimulation armSimulation = new ArmSimulation(stringUnspooledLenSupplier,
        m_winchAbsoluteEncoderSim, m_defaultArmParams);

    // Set grabber
    BooleanSupplier isGrabberOpen = () -> initialIsGrabberOpen;
    armSimulation.setGrabberOpenSupplier(isGrabberOpen);

    assertTrue(armSimulation != null);
    assertTrue(!winchSimulation.getIsBroken());
    assertTrue(armSimulation.getIsBroken() == expectArmBroken);

    return armSimulation;
  }

  private ArmSimulation createDefaultArm() {
    return createDefaultArmHelper(m_winchSimulation, false, false);
  }

  private WinchSimModel createWinchSimulation(double winchInitialLenSpooled) {
    WinchSimModel winchSimulation = new WinchSimModel(m_winchSpoolDiameterMeters,
        m_winchTotalStringLenMeters, winchInitialLenSpooled, m_winchInitialStringOrientation,
        m_winchinvertMotor);

    assertTrue(winchSimulation != null);
    assertTrue(!winchSimulation.getIsBroken());

    return winchSimulation;
  }

  @Test
  public void createArmSimulationShouldSucceed() {
    ArmSimulation tempArmSimulation = createDefaultArm();

    assertTrue(tempArmSimulation != null);
    assertTrue(!tempArmSimulation.getIsBroken() && !m_winchSimulation.getIsBroken());
  }

  @Test
  public void nullWinchSimShouldThrowException() {
    assertThrows(IllegalArgumentException.class, () -> {
      ArmSimulation tempArmSimulation = new ArmSimulation(null, m_winchAbsoluteEncoderSim,
          m_defaultArmParams);
      assertTrue(tempArmSimulation != null);
    });
  }

  @Test
  public void armUpWithGrabberInitiallyOpenShouldSucceed() {
    double lengthStringExtended = m_defaultArmParams.m_heightFromWinchToPivotPoint - 0.5;
    double winchInitialLenSpooled = m_winchTotalStringLenMeters - lengthStringExtended;

    WinchSimModel tempwinchSimulation = createWinchSimulation(winchInitialLenSpooled);
    ArmSimulation tempArmSimulation = createDefaultArmHelper(tempwinchSimulation, true, false);

    assertTrue(tempArmSimulation != null);
    assertTrue(!tempwinchSimulation.getIsBroken());
    assertTrue(!tempArmSimulation.getIsBroken());
    assertEquals(UnitConversions.rotationToSignedDegrees(m_winchAbsoluteEncoder.get()),
        90,
        UnitConversions.kAngleTolerance);
  }

  @Test
  public void armDownWithGrabberInitiallyOpenShouldFail() {
    double lengthStringExtended = m_defaultArmParams.m_heightFromWinchToPivotPoint + 0.5;
    double winchInitialLenSpooled = m_winchTotalStringLenMeters - lengthStringExtended;

    WinchSimModel tempwinchSimulation = createWinchSimulation(winchInitialLenSpooled);
    ArmSimulation tempArmSimulation = createDefaultArmHelper(tempwinchSimulation, true, false);

    // Now that grabber is set open, need to simulate one cycle
    tempArmSimulation.simulationPeriodic();

    assertTrue(tempArmSimulation.getIsBroken());
    assertEquals(UnitConversions.rotationToSignedDegrees(m_winchAbsoluteEncoder.get()),
        -90,
        UnitConversions.kAngleTolerance);
  }

  private void moveArmWithGrabberOpenHelper(double initialDegreesAboveBreakPoint,
      double targetDegreesAboveBreakPoint,
      double expectedFinalDegreesAboveBreakPoint,
      boolean expectedArmInitiallyBroken,
      boolean expectedWinchIsBroken,
      boolean expectedIsArmBroken) {

    boolean initialIsGrabberOpen = true;

    double breakLimitSignedDegrees = UnitConversions
        .rotationToSignedDegrees(m_defaultArmParams.m_grabberBreaksIfOpenBelowThisLimit);

    double initialPosSignedDegrees = breakLimitSignedDegrees + initialDegreesAboveBreakPoint;
    double winchInitialLenSpooled = m_winchTotalStringLenMeters
        - m_calcArmAngleHelper.calcAndValidateStringLengthForSignedDegrees(initialPosSignedDegrees);

    WinchSimModel tempwinchSimulation = createWinchSimulation(winchInitialLenSpooled);
    ArmSimulation tempArmSimulation = createDefaultArmHelper(tempwinchSimulation,
        initialIsGrabberOpen,
        false);

    // Initialize the number of rotations
    tempwinchSimulation.updateNewLenSpooled(0);

    // Now that grabber is set open, need to simulate one cycle
    tempArmSimulation.simulationPeriodic();

    assertTrue(tempArmSimulation.getIsBroken() == expectedArmInitiallyBroken);
    double expect = initialPosSignedDegrees;
    double actual = UnitConversions.rotationToSignedDegrees(m_winchAbsoluteEncoder.get());
    assertEquals(expect, actual, UnitConversions.kAngleTolerance);

    double targetPosSignedDegrees = breakLimitSignedDegrees + targetDegreesAboveBreakPoint;
    double winchTargetLenSpooled = m_winchTotalStringLenMeters
        - m_calcArmAngleHelper.calcAndValidateStringLengthForSignedDegrees(targetPosSignedDegrees);

    // Now calculate how much to turn the winch motor to get it to the target position
    double spoolCircumferenceMeters = Math.PI * m_winchSpoolDiameterMeters;
    double deltaWinchRotations = (winchInitialLenSpooled - winchTargetLenSpooled)
        / spoolCircumferenceMeters;

    // Simulate one cycle for winch, so that it updates
    tempwinchSimulation.updateNewLenSpooled(deltaWinchRotations);
    tempArmSimulation.simulationPeriodic();

    assertTrue(tempwinchSimulation.getIsBroken() == expectedWinchIsBroken);
    assertTrue(tempArmSimulation.getIsBroken() == expectedIsArmBroken);

    expect = breakLimitSignedDegrees + expectedFinalDegreesAboveBreakPoint;
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
    double lengthStringExtended = m_defaultArmParams.m_heightFromWinchToPivotPoint
        + backArmAbovePivot;
    double winchInitialLenSpooled = m_winchTotalStringLenMeters - lengthStringExtended;

    WinchSimModel tempwinchSimulation = createWinchSimulation(winchInitialLenSpooled);
    ArmSimulation tempArmSimulation = createDefaultArmHelper(tempwinchSimulation,
        false,
        expectArmBroken);

    assertTrue(tempArmSimulation != null);
    assertTrue(!tempwinchSimulation.getIsBroken());

    if (!expectArmBroken) {
      assertEquals(m_winchAbsoluteEncoder.get() * 360,
          expectedDegrees,
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
    double lengthStringExtended = m_defaultArmParams.m_heightFromWinchToPivotPoint - 0.35355;
    double winchInitialLenSpooled = m_winchTotalStringLenMeters - lengthStringExtended;
    double offsetRotations = 0.25;

    WinchSimModel tempwinchSimulation = createWinchSimulation(winchInitialLenSpooled);

    // Create a DoubleSupplier that gets the value getStringUnspooledLen()
    DoubleSupplier stringUnspooledLenSupplier = () -> {
      return tempwinchSimulation.getStringUnspooledLen();
    };

    ArmSimulationParamsBuilder tempArmParamsBuilder = new ArmSimulationParamsBuilder(
        m_defaultArmParams);

    tempArmParamsBuilder
        .setTopRotationsLimit(m_defaultArmParams.m_topRotationsLimit + offsetRotations)
        .setBottomRotationsLimit(m_defaultArmParams.m_bottomRotationsLimit + offsetRotations)
        .setGrabberBreaksIfOpenBelowThisLimit(
            m_defaultArmParams.m_grabberBreaksIfOpenBelowThisLimit + offsetRotations)
        .setEncoderRotationsOffset(offsetRotations);

    ArmSimulation tempArmSimulation = new ArmSimulation(stringUnspooledLenSupplier,
        m_winchAbsoluteEncoderSim, tempArmParamsBuilder.build());

    assertTrue(tempArmSimulation != null);
    assertTrue(!tempwinchSimulation.getIsBroken());
    assertTrue(!tempArmSimulation.getIsBroken());

    double expectedDegrees = 45 + 90;
    assertEquals(UnitConversions.rotationToUnsignedDegrees(m_winchAbsoluteEncoder.get()),
        expectedDegrees,
        UnitConversions.kAngleTolerance);
  }
}
