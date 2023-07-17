package frc.robot.simulation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import edu.wpi.first.hal.HAL;
import edu.wpi.first.wpilibj.DutyCycleEncoder;
import edu.wpi.first.wpilibj.simulation.DutyCycleEncoderSim;
import frc.robot.Constants;
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
  private final double m_armTopRotationsLimit = 0.25;
  private final double m_armBottomRotationsLimit = 0.75;
  private final double m_armDeltaRotationsBeforeBroken = 0;
  private final double m_grabberBreaksIfOpenBelowThisLimit = 0.80;
  private final double m_winchSpoolDiameterMeters = 0.01; // (1 centimeter)
  private final double m_winchTotalStringLenMeters = 5;
  private final double m_winchInitialLenSpooled = 4;
  private final WindingOrientation m_winchInitialStringOrientation = WindingOrientation.BackOfRobot;
  private final boolean m_winchinvertMotor = false;
  private final double m_armHeightFromWinchToPivotPoint = 1;
  private final double m_armLengthFromEdgeToPivot = 0.5; // The pivot is halfway down the arm
  private final double m_armLengthFromEdgeToPivotMin = 0.1;
  private final double m_encoderPositionOffsetRotations = 0;

  private WinchSimModel m_winchSimulation;
  private DutyCycleEncoder m_winchAbsoluteEncoder = null;
  private DutyCycleEncoderSim m_winchAbsoluteEncoderSim = null;

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

  @Test
  public void createArmSimulationShouldSucceed() {
    // Create a DoubleSupplier that gets the value getStringUnspooledLen()
    DoubleSupplier stringUnspooledLenSupplier = () -> {
      return m_winchSimulation.getStringUnspooledLen();
    };

    ArmSimulation tempArmSimulation = new ArmSimulation(stringUnspooledLenSupplier,
        m_winchAbsoluteEncoderSim, m_armTopRotationsLimit, m_armBottomRotationsLimit,
        m_armDeltaRotationsBeforeBroken, m_grabberBreaksIfOpenBelowThisLimit,
        m_armHeightFromWinchToPivotPoint, m_armLengthFromEdgeToPivot, m_armLengthFromEdgeToPivotMin,
        m_encoderPositionOffsetRotations);

    assertTrue(tempArmSimulation != null);
    assertTrue(!tempArmSimulation.getIsBroken() && !m_winchSimulation.getIsBroken());
  }

  @Test
  public void nullWinchSimShouldThrowException() {
    assertThrows(IllegalArgumentException.class, () -> {
      ArmSimulation tempArmSimulation = new ArmSimulation(null, m_winchAbsoluteEncoderSim,
          m_armTopRotationsLimit, m_armBottomRotationsLimit, m_armDeltaRotationsBeforeBroken,
          m_grabberBreaksIfOpenBelowThisLimit, m_armHeightFromWinchToPivotPoint,
          m_armLengthFromEdgeToPivot, m_armLengthFromEdgeToPivotMin,
          m_encoderPositionOffsetRotations);
      assertTrue(tempArmSimulation != null);
    });
  }

  @Test
  public void armUpWithGrabberInitiallyOpenShouldSucceed() {
    double lengthStringExtended = m_armHeightFromWinchToPivotPoint - 0.5;
    double winchInitialLenSpooled = m_winchTotalStringLenMeters - lengthStringExtended;

    WinchSimModel tempwinchSimulation = new WinchSimModel(m_winchSpoolDiameterMeters,
        m_winchTotalStringLenMeters, winchInitialLenSpooled, m_winchInitialStringOrientation,
        m_winchinvertMotor);

    // Create a DoubleSupplier that gets the value getStringUnspooledLen()
    DoubleSupplier stringUnspooledLenSupplier = () -> {
      return tempwinchSimulation.getStringUnspooledLen();
    };

    ArmSimulation tempArmSimulation = new ArmSimulation(stringUnspooledLenSupplier,
        m_winchAbsoluteEncoderSim, m_armTopRotationsLimit, m_armBottomRotationsLimit,
        m_armDeltaRotationsBeforeBroken, m_grabberBreaksIfOpenBelowThisLimit,
        m_armHeightFromWinchToPivotPoint, m_armLengthFromEdgeToPivot, m_armLengthFromEdgeToPivotMin,
        m_encoderPositionOffsetRotations);

    // Set grabber open
    BooleanSupplier isGrabberOpen = () -> true;
    tempArmSimulation.setGrabberOpenSupplier(isGrabberOpen);

    assertTrue(tempArmSimulation != null);
    assertTrue(!tempwinchSimulation.getIsBroken());

    assertTrue(!tempArmSimulation.getIsBroken());
    assertEquals(m_winchAbsoluteEncoder.get() * 360, 90, UnitConversions.kAngleTolerance);
  }

  @Test
  public void armDownWithGrabberInitiallyOpenShouldFail() {
    double lengthStringExtended = m_armHeightFromWinchToPivotPoint + 0.5;
    double winchInitialLenSpooled = m_winchTotalStringLenMeters - lengthStringExtended;

    WinchSimModel tempwinchSimulation = new WinchSimModel(m_winchSpoolDiameterMeters,
        m_winchTotalStringLenMeters, winchInitialLenSpooled, m_winchInitialStringOrientation,
        m_winchinvertMotor);

    // Create a DoubleSupplier that gets the value getStringUnspooledLen()
    DoubleSupplier stringUnspooledLenSupplier = () -> {
      return tempwinchSimulation.getStringUnspooledLen();
    };

    ArmSimulation tempArmSimulation = new ArmSimulation(stringUnspooledLenSupplier,
        m_winchAbsoluteEncoderSim, m_armTopRotationsLimit, m_armBottomRotationsLimit,
        m_armDeltaRotationsBeforeBroken, m_grabberBreaksIfOpenBelowThisLimit,
        m_armHeightFromWinchToPivotPoint, m_armLengthFromEdgeToPivot, m_armLengthFromEdgeToPivotMin,
        m_encoderPositionOffsetRotations);

    // Set grabber open
    BooleanSupplier isGrabberOpen = () -> true;
    tempArmSimulation.setGrabberOpenSupplier(isGrabberOpen);

    assertTrue(tempArmSimulation != null);
    assertTrue(!tempwinchSimulation.getIsBroken());
    assertTrue(!tempArmSimulation.getIsBroken());

    // Now that grabber is set open, need to simulate one cycle
    tempArmSimulation.simulationPeriodic();

    assertTrue(tempArmSimulation.getIsBroken());
    assertEquals(m_winchAbsoluteEncoder.get() * 360, 360 - 90, UnitConversions.kAngleTolerance);
  }

  @Test
  public void movingArmDownwardPastBreakLimitWithGrabberOpenShouldNotMoveArm() {
    double breakLimitSignedDegrees = (m_grabberBreaksIfOpenBelowThisLimit * 360) - 360;
    double initialPosSignedDegrees = breakLimitSignedDegrees + 4;

    double backArmAbovePivot = -1
        * (m_armLengthFromEdgeToPivot * Math.sin(initialPosSignedDegrees * Math.PI / 180));
    double lengthStringExtended = m_armHeightFromWinchToPivotPoint + backArmAbovePivot;
    double winchInitialLenSpooled = m_winchTotalStringLenMeters - lengthStringExtended;

    WinchSimModel tempwinchSimulation = new WinchSimModel(m_winchSpoolDiameterMeters,
        m_winchTotalStringLenMeters, winchInitialLenSpooled, m_winchInitialStringOrientation,
        m_winchinvertMotor);

    // Create a DoubleSupplier that gets the value getStringUnspooledLen()
    DoubleSupplier stringUnspooledLenSupplier = () -> {
      return tempwinchSimulation.getStringUnspooledLen();
    };

    ArmSimulation tempArmSimulation = new ArmSimulation(stringUnspooledLenSupplier,
        m_winchAbsoluteEncoderSim, m_armTopRotationsLimit, m_armBottomRotationsLimit,
        m_armDeltaRotationsBeforeBroken, m_grabberBreaksIfOpenBelowThisLimit,
        m_armHeightFromWinchToPivotPoint, m_armLengthFromEdgeToPivot, m_armLengthFromEdgeToPivotMin,
        m_encoderPositionOffsetRotations);

    // Initialize the number of rotations
    tempwinchSimulation.updateNewLenSpooled(0);

    // Set grabber open
    BooleanSupplier isGrabberOpen = () -> true;
    tempArmSimulation.setGrabberOpenSupplier(isGrabberOpen);

    assertTrue(tempArmSimulation != null);
    assertTrue(!tempwinchSimulation.getIsBroken());
    assertTrue(!tempArmSimulation.getIsBroken());

    // Now that grabber is set open, need to simulate one cycle
    tempArmSimulation.simulationPeriodic();

    assertTrue(!tempArmSimulation.getIsBroken());
    double expect = initialPosSignedDegrees + 360;
    double actual = m_winchAbsoluteEncoder.get() * 360;
    assertEquals(expect, actual, UnitConversions.kAngleTolerance);

    double targetPosSignedDegrees = breakLimitSignedDegrees - 4;

    // Now calculate how much to turn the winch motor to get it to the target position
    backArmAbovePivot = -1
        * (m_armLengthFromEdgeToPivot * Math.sin(targetPosSignedDegrees * Math.PI / 180));
    lengthStringExtended = m_armHeightFromWinchToPivotPoint + backArmAbovePivot;
    double winchTargetLenSpooled = m_winchTotalStringLenMeters - lengthStringExtended;

    double spoolCircumferenceMeters = Math.PI * m_winchSpoolDiameterMeters;
    double deltaWinchRotations = (winchInitialLenSpooled - winchTargetLenSpooled)
        / spoolCircumferenceMeters;

    // Simulate one cycle for winch, so that it updates
    tempwinchSimulation.updateNewLenSpooled(deltaWinchRotations);
    tempArmSimulation.simulationPeriodic();

    assertTrue(!tempwinchSimulation.getIsBroken());
    assertTrue(!tempArmSimulation.getIsBroken());

    // We expect that the arm gets stuck at the break limit, instead of going all the way to the
    // target degrees
    expect = breakLimitSignedDegrees + 360;
    actual = m_winchAbsoluteEncoder.get() * 360;
    assertEquals(expect, actual, UnitConversions.kAngleTolerance);
  }

  /*
   * $TODO1
   * 
   * @Test
   * public void movingArmWithGrabberOpenShouldSucceedIfArmIsTowardsTop() {
   * double breakLimitSignedDegrees = (m_grabberBreaksIfOpenBelowThisLimit * 360) - 360;
   * double initialPosSignedDegrees = breakLimitSignedDegrees + 4;
   * 
   * double backArmAbovePivot = -1
   * (m_armLengthFromEdgeToPivot * Math.sin(initialPosSignedDegrees * Math.PI / 180));
   * double lengthStringExtended = m_armHeightFromWinchToPivotPoint + backArmAbovePivot;
   * double winchInitialLenSpooled = m_winchTotalStringLenMeters - lengthStringExtended;
   * 
   * WinchSimModel tempwinchSimulation = new WinchSimModel(m_winchSpoolDiameterMeters,
   * m_winchTotalStringLenMeters, winchInitialLenSpooled, m_winchInitialStringOrientation,
   * m_winchinvertMotor);
   * 
   * // Create a DoubleSupplier that gets the value getStringUnspooledLen()
   * DoubleSupplier stringUnspooledLenSupplier = () -> {
   * return tempwinchSimulation.getStringUnspooledLen();
   * };
   * 
   * ArmSimulation tempArmSimulation = new ArmSimulation(stringUnspooledLenSupplier,
   * m_winchAbsoluteEncoderSim, m_armTopRotationsLimit, m_armBottomRotationsLimit,
   * m_armDeltaRotationsBeforeBroken, m_grabberBreaksIfOpenBelowThisLimit,
   * m_armHeightFromWinchToPivotPoint, m_armLengthFromEdgeToPivot, m_armLengthFromEdgeToPivotMin,
   * m_encoderPositionOffsetRotations);
   * 
   * // Initialize the number of rotations
   * tempwinchSimulation.updateNewLenSpooled(0);
   * 
   * // Set grabber open
   * BooleanSupplier isGrabberOpen = () -> true;
   * tempArmSimulation.setGrabberOpenSupplier(isGrabberOpen);
   * 
   * assertTrue(tempArmSimulation != null);
   * assertTrue(!tempwinchSimulation.getIsBroken());
   * assertTrue(!tempArmSimulation.getIsBroken());
   * 
   * // Now that grabber is set open, need to simulate one cycle
   * tempArmSimulation.simulationPeriodic();
   * 
   * assertTrue(!tempArmSimulation.getIsBroken());
   * double expect = initialPosSignedDegrees + 360;
   * double actual = m_winchAbsoluteEncoder.get() * 360;
   * assertEquals(expect, actual, UnitConversions.kAngleTolerance);
   * 
   * double targetPosSignedDegrees = breakLimitSignedDegrees + 8;
   * 
   * // Now calculate how much to turn the winch motor to get it to the target position
   * backArmAbovePivot = -1
   * (m_armLengthFromEdgeToPivot * Math.sin(targetPosSignedDegrees * Math.PI / 180));
   * lengthStringExtended = m_armHeightFromWinchToPivotPoint + backArmAbovePivot;
   * double winchTargetLenSpooled = m_winchTotalStringLenMeters - lengthStringExtended;
   * 
   * double currentWinchRotations = m_winchRelEncoderSim.getPosition();
   * double spoolCircumferenceMeters = Math.PI * m_winchSpoolDiameterMeters;
   * double deltaWinchRotations = (winchInitialLenSpooled - winchTargetLenSpooled)
   * / spoolCircumferenceMeters;
   * 
   * m_winchRelEncoderSim.setPosition(currentWinchRotations + deltaWinchRotations);
   * 
   * // Simulate one cycle for winch, so that it updates
   * tempwinchSimulation.simulationPeriodic();
   * tempArmSimulation.simulationPeriodic();
   * 
   * assertTrue(!tempwinchSimulation.getIsBroken());
   * assertTrue(!tempArmSimulation.getIsBroken());
   * 
   * // We expect that the arm gets stuck at the break limit, instead of going all the way to the
   * // target degrees
   * expect = targetPosSignedDegrees + 360;
   * actual = m_winchAbsoluteEncoder.get() * 360;
   * assertEquals(expect, actual, UnitConversions.kAngleTolerance);
   * }
   */

  /*
   * $TODO1
   * 
   * @Test
   * public void movingArmUpwardFromBreakLimitWithGrabberOpenShouldSucceed() {
   * double breakLimitSignedDegrees = (m_grabberBreaksIfOpenBelowThisLimit * 360) - 360;
   * double initialPosSignedDegrees = breakLimitSignedDegrees;
   * 
   * double backArmAbovePivot = -1
   * (m_armLengthFromEdgeToPivot * Math.sin(initialPosSignedDegrees * Math.PI / 180));
   * double lengthStringExtended = m_armHeightFromWinchToPivotPoint + backArmAbovePivot;
   * double winchInitialLenSpooled = m_winchTotalStringLenMeters - lengthStringExtended;
   * 
   * WinchSimModel tempwinchSimulation = new WinchSimModel(m_winchSpoolDiameterMeters,
   * m_winchTotalStringLenMeters, winchInitialLenSpooled, m_winchInitialStringOrientation,
   * m_winchinvertMotor);
   * 
   * // Create a DoubleSupplier that gets the value getStringUnspooledLen()
   * DoubleSupplier stringUnspooledLenSupplier = () -> {
   * return tempwinchSimulation.getStringUnspooledLen();
   * };
   * 
   * ArmSimulation tempArmSimulation = new ArmSimulation(stringUnspooledLenSupplier,
   * m_winchAbsoluteEncoderSim, m_armTopRotationsLimit, m_armBottomRotationsLimit,
   * m_armDeltaRotationsBeforeBroken, m_grabberBreaksIfOpenBelowThisLimit,
   * m_armHeightFromWinchToPivotPoint, m_armLengthFromEdgeToPivot, m_armLengthFromEdgeToPivotMin,
   * m_encoderPositionOffsetRotations);
   * 
   * // Initialize the number of rotations
   * tempwinchSimulation.updateNewLenSpooled(0);
   * 
   * // Set grabber open
   * BooleanSupplier isGrabberOpen = () -> true;
   * tempArmSimulation.setGrabberOpenSupplier(isGrabberOpen);
   * 
   * assertTrue(tempArmSimulation != null);
   * assertTrue(!tempwinchSimulation.getIsBroken());
   * assertTrue(!tempArmSimulation.getIsBroken());
   * 
   * // Now that grabber is set open, need to simulate one cycle
   * tempArmSimulation.simulationPeriodic();
   * 
   * assertTrue(!tempArmSimulation.getIsBroken());
   * double expect = initialPosSignedDegrees + 360;
   * double actual = m_winchAbsoluteEncoder.get() * 360;
   * assertEquals(expect, actual, UnitConversions.kAngleTolerance);
   * 
   * double targetPosSignedDegrees = breakLimitSignedDegrees + 8;
   * 
   * // Now calculate how much to turn the winch motor to get it to the target position
   * backArmAbovePivot = -1
   * (m_armLengthFromEdgeToPivot * Math.sin(targetPosSignedDegrees * Math.PI / 180));
   * lengthStringExtended = m_armHeightFromWinchToPivotPoint + backArmAbovePivot;
   * double winchTargetLenSpooled = m_winchTotalStringLenMeters - lengthStringExtended;
   * 
   * double currentWinchRotations = m_winchRelEncoderSim.getPosition();
   * double spoolCircumferenceMeters = Math.PI * m_winchSpoolDiameterMeters;
   * double deltaWinchRotations = (winchInitialLenSpooled - winchTargetLenSpooled)
   * / spoolCircumferenceMeters;
   * 
   * m_winchRelEncoderSim.setPosition(currentWinchRotations + deltaWinchRotations);
   * 
   * // Simulate one cycle for winch, so that it updates
   * tempwinchSimulation.simulationPeriodic();
   * tempArmSimulation.simulationPeriodic();
   * 
   * assertTrue(!tempwinchSimulation.getIsBroken());
   * assertTrue(!tempArmSimulation.getIsBroken());
   * 
   * // We expect that the arm gets stuck at the break limit, instead of going all the way to the
   * // target degrees
   * expect = targetPosSignedDegrees + 360;
   * actual = m_winchAbsoluteEncoder.get() * 360;
   * assertEquals(expect, actual, UnitConversions.kAngleTolerance);
   * }
   */

  /*
   * $TODO1
   * 
   * @Test
   * public void movingAlreadyBrokenArmShouldNotMoveArm() {
   * double breakLimitSignedDegrees = (m_grabberBreaksIfOpenBelowThisLimit * 360) - 360;
   * double initialPosSignedDegrees = breakLimitSignedDegrees - 4;
   * 
   * double backArmAbovePivot = -1
   * (m_armLengthFromEdgeToPivot * Math.sin(initialPosSignedDegrees * Math.PI / 180));
   * double lengthStringExtended = m_armHeightFromWinchToPivotPoint + backArmAbovePivot;
   * double winchInitialLenSpooled = m_winchTotalStringLenMeters - lengthStringExtended;
   * 
   * WinchSimModel tempwinchSimulation = new WinchSimModel(m_winchSpoolDiameterMeters,
   * m_winchTotalStringLenMeters, winchInitialLenSpooled, m_winchInitialStringOrientation,
   * m_winchinvertMotor);
   * 
   * // Create a DoubleSupplier that gets the value getStringUnspooledLen()
   * DoubleSupplier stringUnspooledLenSupplier = () -> {
   * return tempwinchSimulation.getStringUnspooledLen();
   * };
   * 
   * ArmSimulation tempArmSimulation = new ArmSimulation(stringUnspooledLenSupplier,
   * m_winchAbsoluteEncoderSim, m_armTopRotationsLimit, m_armBottomRotationsLimit,
   * m_armDeltaRotationsBeforeBroken, m_grabberBreaksIfOpenBelowThisLimit,
   * m_armHeightFromWinchToPivotPoint, m_armLengthFromEdgeToPivot, m_armLengthFromEdgeToPivotMin,
   * m_encoderPositionOffsetRotations);
   * 
   * // Set grabber open
   * BooleanSupplier isGrabberOpen = () -> true;
   * tempArmSimulation.setGrabberOpenSupplier(isGrabberOpen);
   * 
   * assertTrue(tempArmSimulation != null);
   * assertTrue(!tempwinchSimulation.getIsBroken());
   * assertTrue(!tempArmSimulation.getIsBroken());
   * 
   * // Now that grabber is set open, need to simulate one cycle
   * tempArmSimulation.simulationPeriodic();
   * 
   * assertTrue(tempArmSimulation.getIsBroken());
   * double expect = initialPosSignedDegrees + 360;
   * double actual = m_winchAbsoluteEncoder.get() * 360;
   * assertEquals(expect, actual, UnitConversions.kAngleTolerance);
   * 
   * double targetPosSignedDegrees = breakLimitSignedDegrees + 8;
   * 
   * // Now calculate how much to turn the winch motor to get it to the target position
   * backArmAbovePivot = -1
   * (m_armLengthFromEdgeToPivot * Math.sin(targetPosSignedDegrees * Math.PI / 180));
   * lengthStringExtended = m_armHeightFromWinchToPivotPoint + backArmAbovePivot;
   * double winchTargetLenSpooled = m_winchTotalStringLenMeters - lengthStringExtended;
   * 
   * double currentWinchRotations = m_winchRelEncoderSim.getPosition();
   * double spoolCircumferenceMeters = Math.PI * m_winchSpoolDiameterMeters;
   * double deltaWinchRotations = (winchInitialLenSpooled - winchTargetLenSpooled)
   * / spoolCircumferenceMeters;
   * 
   * m_winchRelEncoderSim.setPosition(currentWinchRotations + deltaWinchRotations);
   * 
   * // Simulate one cycle for winch, so that it updates
   * tempwinchSimulation.simulationPeriodic();
   * tempArmSimulation.simulationPeriodic();
   * 
   * assertTrue(!tempwinchSimulation.getIsBroken());
   * assertTrue(tempArmSimulation.getIsBroken());
   * 
   * // We expect that the arm gets stuck at the break limit, instead of going all the way to the
   * // target degrees
   * expect = initialPosSignedDegrees + 360;
   * actual = m_winchAbsoluteEncoder.get() * 360;
   * assertEquals(expect, actual, UnitConversions.kAngleTolerance);
   * }
   * 
   * private void createWithDegreeArmHelper(double backArmAbovePivot,
   * double expectedDegrees,
   * boolean expectArmBroken) {
   * double lengthStringExtended = m_armHeightFromWinchToPivotPoint + backArmAbovePivot;
   * double winchInitialLenSpooled = m_winchTotalStringLenMeters - lengthStringExtended;
   * 
   * WinchSimModel tempwinchSimulation = new WinchSimModel(m_winchRelEncoderSim,
   * m_winchSpoolDiameterMeters, m_winchTotalStringLenMeters, winchInitialLenSpooled,
   * m_winchInitialStringOrientation, m_winchinvertMotor);
   * 
   * // Create a DoubleSupplier that gets the value getStringUnspooledLen()
   * DoubleSupplier stringUnspooledLenSupplier = () -> {
   * return tempwinchSimulation.getStringUnspooledLen();
   * };
   * 
   * ArmSimulation tempArmSimulation = new ArmSimulation(stringUnspooledLenSupplier,
   * m_winchAbsoluteEncoderSim, m_armTopRotationsLimit, m_armBottomRotationsLimit,
   * m_armDeltaRotationsBeforeBroken, m_grabberBreaksIfOpenBelowThisLimit,
   * m_armHeightFromWinchToPivotPoint, m_armLengthFromEdgeToPivot, m_armLengthFromEdgeToPivotMin,
   * m_encoderPositionOffsetRotations);
   * 
   * assertTrue(tempArmSimulation != null);
   * assertTrue(!tempwinchSimulation.getIsBroken());
   * 
   * if (expectArmBroken) {
   * assertTrue(tempArmSimulation.getIsBroken());
   * }
   * else {
   * assertTrue(!tempArmSimulation.getIsBroken());
   * assertEquals(m_winchAbsoluteEncoder.get() * 360,
   * expectedDegrees,
   * UnitConversions.kAngleTolerance);
   * }
   * }
   * 
   * @Test
   * public void createWithLevelArmShouldSucceed() {
   * createWithDegreeArmHelper(0, 0, false);
   * }
   * 
   * @Test
   * public void createWith45DegreeArmShouldSucceed() {
   * createWithDegreeArmHelper(-0.35355, 45, false);
   * }
   * 
   * @Test
   * public void createWith30DegreeArmShouldSucceed() {
   * createWithDegreeArmHelper(-0.25, 30, false);
   * }
   * 
   * @Test
   * public void createWith90DegreeArmShouldSucceed() {
   * createWithDegreeArmHelper(-0.5, 90, false);
   * }
   * 
   * @Test
   * public void createWithNegative90DegreeArmShouldSucceed() {
   * createWithDegreeArmHelper(0.5, 360 - 90, false);
   * }
   * 
   * @Test
   * public void createWith91DegreeArmShouldFail() {
   * double amountBeyondLimit = 0.0001;
   * 
   * createWithDegreeArmHelper(-0.5 - amountBeyondLimit, 90, true);
   * }
   * 
   * @Test
   * public void createWithNegative91DegreeArmShouldNotBreakArm() {
   * double amountBeyondLimit = 0.0001;
   * 
   * createWithDegreeArmHelper(0.5 + amountBeyondLimit, 360 - 90, false);
   * }
   * 
   * @Test
   * public void createWith98DegreeArmShouldFail() {
   * double amountBeyondLimit = 0.1;
   * 
   * createWithDegreeArmHelper(-0.5 - amountBeyondLimit, 90, true);
   * }
   * 
   * @Test
   * public void createWithNegative98DegreeArmShouldNotBreakArm() {
   * double amountBeyondLimit = 0.1;
   * 
   * createWithDegreeArmHelper(0.5 + amountBeyondLimit, 360 - 90, false);
   * }
   * 
   * @Test
   * public void createWithNegative45DegreeArmShouldSucceed() {
   * createWithDegreeArmHelper(0.35355, 360 - 45, false);
   * }
   */

  // Sometimes, the absolute encoder is offset, and 0 isn't level
  @Test
  public void createWithOffsetShouldSucceed() {
    double lengthStringExtended = m_armHeightFromWinchToPivotPoint - 0.35355;
    double winchInitialLenSpooled = m_winchTotalStringLenMeters - lengthStringExtended;
    double offsetRotations = 0.25;

    WinchSimModel tempwinchSimulation = new WinchSimModel(m_winchSpoolDiameterMeters,
        m_winchTotalStringLenMeters, winchInitialLenSpooled, m_winchInitialStringOrientation,
        m_winchinvertMotor);

    // Create a DoubleSupplier that gets the value getStringUnspooledLen()
    DoubleSupplier stringUnspooledLenSupplier = () -> {
      return tempwinchSimulation.getStringUnspooledLen();
    };

    ArmSimulation tempArmSimulation = new ArmSimulation(stringUnspooledLenSupplier,
        m_winchAbsoluteEncoderSim, m_armTopRotationsLimit + offsetRotations,
        m_armBottomRotationsLimit + offsetRotations, m_armDeltaRotationsBeforeBroken,
        m_grabberBreaksIfOpenBelowThisLimit + offsetRotations, m_armHeightFromWinchToPivotPoint,
        m_armLengthFromEdgeToPivot, m_armLengthFromEdgeToPivotMin, offsetRotations);

    assertTrue(tempArmSimulation != null);
    assertTrue(!tempwinchSimulation.getIsBroken());
    assertTrue(!tempArmSimulation.getIsBroken());

    double expectedDegrees = 45 + 90;
    assertEquals(m_winchAbsoluteEncoder.get() * 360,
        expectedDegrees,
        UnitConversions.kAngleTolerance);
  }

  @Test
  public void offset0ArmRotationBy180DegreesShouldWork() {
    double position = 0;
    double offset = 0.5; // 180
    double expectedResult = 0.5;

    double actualResult = ArmSimulation.offsetArmRotationPosition(position, offset);
    assertEquals(expectedResult, actualResult);
  }

  @Test
  public void offset90ArmRotationBy180DegreesShouldWork() {
    double position = 0.25;
    double offset = 0.5; // 180
    double expectedResult = 0.75;

    double actualResult = ArmSimulation.offsetArmRotationPosition(position, offset);
    assertEquals(expectedResult, actualResult);
  }

  @Test
  public void offset216ArmRotationBy180DegreesShouldWork() {
    double position = 0.6;
    double offset = 0.5; // 180
    double expectedResult = 0.1;

    double actualResult = ArmSimulation.offsetArmRotationPosition(position, offset);
    assertEquals(expectedResult, actualResult, UnitConversions.kAngleTolerance);
  }

  @Test
  public void offset180ArmRotationByNegative90DegreesShouldWork() {
    double position = 0.5;
    double offset = -0.25; // 90
    double expectedResult = 0.25;

    double actualResult = ArmSimulation.offsetArmRotationPosition(position, offset);
    assertEquals(expectedResult, actualResult);
  }
}
