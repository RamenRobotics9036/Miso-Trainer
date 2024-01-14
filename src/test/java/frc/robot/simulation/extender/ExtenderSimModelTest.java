package frc.robot.simulation.extender;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import frc.robot.simulation.framework.SimManager;
import frc.robot.simulation.framework.inputoutputs.CopySimOutput;
import frc.robot.simulation.framework.inputoutputs.LambdaSimInput;
import java.util.function.Supplier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

class ExtenderSimModelTest {
  private ExtenderSimModel m_extenderSimModel_old;
  private SimManager<Double, ExtenderState> m_simManager;

  @BeforeEach
  void setup() {
    // Initialize with some default values
    // $TODO - Need to change this to use SimManager throughout this file
    m_extenderSimModel_old = new ExtenderSimModel(0.0, new ExtenderParams(0.1, 0.5, 0.2, false));

    m_simManager = new SimManager<Double, ExtenderState>(
        new ExtenderSimModel(0.0, new ExtenderParams(0.1, 0.5, 0.2, false)), null, null, true);
  }

  @Test
  public void constructor_ValidParameters_ShouldNotThrow() {
    Executable action = () -> new ExtenderSimModel(0.0, new ExtenderParams(0.1, 0.5, 0.2, false));
    assertDoesNotThrow(action);
  }

  @Test
  public void constructor_InvalidCylinderDiameter_ShouldThrowIllegalArgumentException() {
    Executable action = () -> new ExtenderSimModel(0.0, new ExtenderParams(-0.1, 0.5, 0.2, false));
    assertThrows(IllegalArgumentException.class, action);
  }

  @Test
  public void constructor_InvalidTotalExtenderLength_ShouldThrowIllegalArgumentException() {
    Executable action = () -> new ExtenderSimModel(0.0, new ExtenderParams(0.1, -0.5, 0.2, false));
    assertThrows(IllegalArgumentException.class, action);
  }

  @Test
  public void constructor_InvalidInitialExtendedLen_ShouldThrowIllegalArgumentException() {
    Executable action = () -> new ExtenderSimModel(0.0, new ExtenderParams(0.1, 0.5, -0.2, false));
    assertThrows(IllegalArgumentException.class, action);
  }

  @Test
  public void constructor_InitialLenGreaterThanTotalExtenderLength_ShouldThrow() {
    Executable action = () -> new ExtenderSimModel(0.0, new ExtenderParams(0.1, 0.5, 0.6, false));
    assertThrows(IllegalArgumentException.class, action);
  }

  @Test
  void constructor_InitialMotorRotationsCanBeNegative() {
    Executable action = () -> new ExtenderSimModel(-0.1, new ExtenderParams(0.1, 0.5, 0.2, false));
    assertDoesNotThrow(action);
  }

  // More constructor tests for other invalid parameters...

  @Test
  public void getExtendedLen_AfterConstruction_ShouldReturnInitialExtendedLength() {
    assertEquals(0.2, m_extenderSimModel_old.getExtendedLen());
  }

  @Test
  public void getExtendedPercent_WithKnownValues_ShouldReturnCorrectPercentage() {
    double extendedPercent = m_extenderSimModel_old.getExtendedPercent();
    assertEquals(0.4, extendedPercent); // 0.2 / 0.5
  }

  @Test
  public void getIsBroken_NewInstance_ShouldReturnFalse() {
    assertFalse(m_extenderSimModel_old.getIsBroken());
  }

  @Test
  public void updateNewExtendedLen_NormalOperation_ShouldUpdateLength() {
    m_extenderSimModel_old.updateNewExtendedLen(1.0);
    assertTrue(m_extenderSimModel_old.getExtendedLen() > 0.2); // $TODO
  }

  @Test
  public void updateNewExtendedLen_OverExtension_ShouldSetIsBrokenTrue() {
    m_extenderSimModel_old.updateNewExtendedLen(10.0);
    assertTrue(m_extenderSimModel_old.getIsBroken());
  }

  @Test
  public void updateNewExtenderLen_OverExtension_ShouldHaveExpectedLen() {
    m_extenderSimModel_old.updateNewExtendedLen(10.0);
    assertEquals(0.5, m_extenderSimModel_old.getExtendedLen());
  }

  @Test
  public void updateNewExtendedLen_UnderExtension_ShouldSetIsBrokenTrue() {
    m_extenderSimModel_old.updateNewExtendedLen(-10.0);
    assertTrue(m_extenderSimModel_old.getIsBroken());
  }

  @Test
  public void updateNewExtenderLen_UnderExtension_ShouldHaveExpectedLen() {
    m_extenderSimModel_old.updateNewExtendedLen(-10.0);
    assertEquals(0.0, m_extenderSimModel_old.getExtendedLen());
  }

  @Test
  public void updateNewExtenderLen_MaxLen_ShouldSucceed() {
    m_extenderSimModel_old.updateNewExtendedLen(0.952);
    assertEquals(0.5, m_extenderSimModel_old.getExtendedLen(), 0.001);
    assertFalse(m_extenderSimModel_old.getIsBroken());
  }

  @Test
  public void updateNewExtenderLen_InitialLenHalfWay_RotatingToMinLen_ShouldSucceed() {
    ExtenderSimModel invertedExtender = new ExtenderSimModel(0.0,
        new ExtenderParams(0.1, 1, 0.3, false));
    invertedExtender.updateNewExtendedLen(-0.952);
    assertEquals(0.0, invertedExtender.getExtendedLen(), 0.001);
    assertFalse(invertedExtender.getIsBroken());
  }

  private void setInputsAndOutputs(SimManager<Double, ExtenderState> simManager,
      Double[] inputArray,
      ExtenderState output) {

    // inputArray should not be null, and must be exactly len == 1
    if (inputArray == null || inputArray.length != 1) {
      throw new IllegalArgumentException("inputArray must be exactly len == 1");
    }

    Supplier<Double> inputSupplier = () -> inputArray[0];

    m_simManager.setInputHandler(new LambdaSimInput<Double>(inputSupplier));
    m_simManager.setOutputHandler(new CopySimOutput<ExtenderState>(output));
  }

  @Test
  public void updateNewExtendedLen_WhenBroken_ShouldNotChangeLength() {
    Double[] inputMotorRotations = new Double[] {
        0.0
    };
    ExtenderState outputState = new ExtenderState();
    setInputsAndOutputs(m_simManager, inputMotorRotations, outputState);

    assertEquals(0.2, outputState.getExtendedLen());
    assertFalse(m_simManager.isBroken());

    inputMotorRotations[0] = 10.0; // This should break the extender
    m_simManager.simulationPeriodic();

    double lengthAfterBreak = outputState.getExtendedLen();

    inputMotorRotations[0] = 5.0; // This should break the extender
    m_simManager.simulationPeriodic(); // This should not change the length

    assertEquals(lengthAfterBreak, outputState.getExtendedLen());
  }

  @Test
  public void testUpdateNewExtendedLenGivesExpectedLength() {
    Double[] inputMotorRotations = new Double[] {
        0.0
    };
    ExtenderState outputState = new ExtenderState();
    setInputsAndOutputs(m_simManager, inputMotorRotations, outputState);

    assertEquals(0.2, outputState.getExtendedLen(), 0.001);

    inputMotorRotations[0] = 0.1;
    m_simManager.simulationPeriodic();

    assertEquals(0.231, outputState.getExtendedLen(), 0.001);
  }

  @Test
  public void testUpdateNewExtendedLenValidNegativeValue() {
    m_extenderSimModel_old.updateNewExtendedLen(-0.1);
    assertEquals(0.168, m_extenderSimModel_old.getExtendedLen(), 0.001);
  }

  @Test
  public void testInvertedMotor() {
    ExtenderSimModel invertedExtender = new ExtenderSimModel(0,
        new ExtenderParams(0.1, 1, 0.5, true));
    invertedExtender.updateNewExtendedLen(0.1);
    assertEquals(0.468, invertedExtender.getExtendedLen(), 0.001);
  }

  @Test
  public void testUpdateNewExtenderLenTwoTimesExtendsCorrectValue() {
    m_extenderSimModel_old.updateNewExtendedLen(0.1);
    m_extenderSimModel_old.updateNewExtendedLen(0.1);
    assertEquals(0.231, m_extenderSimModel_old.getExtendedLen(), 0.001);
  }
}
