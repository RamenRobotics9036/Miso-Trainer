package simulationlib.simulation.extender;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.function.Supplier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import simulationlib.simulation.extender.ExtenderParams;
import simulationlib.simulation.extender.ExtenderSimModel;
import simulationlib.simulation.extender.ExtenderState;
import simulationlib.simulation.framework.SimManager;
import simulationlib.simulation.framework.inputoutputs.CopySimOutput;
import simulationlib.simulation.framework.inputoutputs.LambdaSimInput;

class ExtenderSimModelTest {
  private SimManager<Double, ExtenderState> m_simManager;

  @BeforeEach
  void setup() {
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
    Double[] inputMotorRotations = new Double[] {
        0.0
    };
    ExtenderState outputState = new ExtenderState();
    setInputsAndOutputs(m_simManager, inputMotorRotations, outputState);

    assertEquals(0.2, outputState.getExtendedLen());
  }

  @Test
  public void getExtendedPercent_WithKnownValues_ShouldReturnCorrectPercentage() {
    Double[] inputMotorRotations = new Double[] {
        0.0
    };
    ExtenderState outputState = new ExtenderState();
    setInputsAndOutputs(m_simManager, inputMotorRotations, outputState);

    double extendedPercent = outputState.getExtendedPercent();
    assertEquals(0.4, extendedPercent); // 0.2 / 0.5
  }

  @Test
  public void getIsBroken_NewInstance_ShouldReturnFalse() {
    Double[] inputMotorRotations = new Double[] {
        0.0
    };
    ExtenderState outputState = new ExtenderState();
    setInputsAndOutputs(m_simManager, inputMotorRotations, outputState);

    assertFalse(m_simManager.isBroken());
  }

  @Test
  public void updateNewExtendedLen_NormalOperation_ShouldUpdateLength() {
    Double[] inputMotorRotations = new Double[] {
        0.0
    };
    ExtenderState outputState = new ExtenderState();
    setInputsAndOutputs(m_simManager, inputMotorRotations, outputState);

    inputMotorRotations[0] = 1.0;
    m_simManager.simulationPeriodic();
    assertEquals(0.5, outputState.getExtendedLen(), 0.001);
  }

  @Test
  public void updateNewExtendedLen_OverExtension_ShouldSetIsBrokenTrue() {
    Double[] inputMotorRotations = new Double[] {
        0.0
    };
    ExtenderState outputState = new ExtenderState();
    setInputsAndOutputs(m_simManager, inputMotorRotations, outputState);

    inputMotorRotations[0] = 10.0;
    m_simManager.simulationPeriodic();
    assertTrue(m_simManager.isBroken());
  }

  @Test
  public void updateNewExtenderLen_OverExtension_ShouldHaveExpectedLen() {
    Double[] inputMotorRotations = new Double[] {
        0.0
    };
    ExtenderState outputState = new ExtenderState();
    setInputsAndOutputs(m_simManager, inputMotorRotations, outputState);

    inputMotorRotations[0] = 10.0;
    m_simManager.simulationPeriodic();
    assertEquals(0.5, outputState.getExtendedLen());
  }

  @Test
  public void updateNewExtendedLen_UnderExtension_ShouldSetIsBrokenTrue() {
    Double[] inputMotorRotations = new Double[] {
        0.0
    };
    ExtenderState outputState = new ExtenderState();
    setInputsAndOutputs(m_simManager, inputMotorRotations, outputState);

    inputMotorRotations[0] = -10.0;
    m_simManager.simulationPeriodic();
    assertTrue(m_simManager.isBroken());
  }

  @Test
  public void updateNewExtenderLen_UnderExtension_ShouldHaveExpectedLen() {
    Double[] inputMotorRotations = new Double[] {
        0.0
    };
    ExtenderState outputState = new ExtenderState();
    setInputsAndOutputs(m_simManager, inputMotorRotations, outputState);

    inputMotorRotations[0] = -10.0;
    m_simManager.simulationPeriodic();
    assertEquals(0.0, outputState.getExtendedLen());
  }

  @Test
  public void updateNewExtenderLen_MaxLen_ShouldSucceed() {
    Double[] inputMotorRotations = new Double[] {
        0.0
    };
    ExtenderState outputState = new ExtenderState();
    setInputsAndOutputs(m_simManager, inputMotorRotations, outputState);

    inputMotorRotations[0] = 0.952;
    m_simManager.simulationPeriodic();
    assertEquals(0.5, outputState.getExtendedLen(), 0.001);
    assertFalse(m_simManager.isBroken());
  }

  @Test
  public void updateNewExtenderLen_InitialLenHalfWay_RotatingToMinLen_ShouldSucceed() {
    Double[] inputMotorRotations = new Double[] {
        0.0
    };
    ExtenderState outputState = new ExtenderState();

    ExtenderSimModel extenderModelLocal = new ExtenderSimModel(0.0,
        new ExtenderParams(0.1, 1, 0.3, false));
    SimManager<Double, ExtenderState> simManagerLocal = new SimManager<Double, ExtenderState>(
        extenderModelLocal, null, null, true);

    setInputsAndOutputs(simManagerLocal, inputMotorRotations, outputState);

    inputMotorRotations[0] = -0.952;
    simManagerLocal.simulationPeriodic();
    assertEquals(0.0, outputState.getExtendedLen(), 0.001);
    assertFalse(simManagerLocal.isBroken());
  }

  private void setInputsAndOutputs(SimManager<Double, ExtenderState> simManager,
      Double[] inputArray,
      ExtenderState output) {

    // inputArray should not be null, and must be exactly len == 1
    if (inputArray == null || inputArray.length != 1) {
      throw new IllegalArgumentException("inputArray must be exactly len == 1");
    }

    Supplier<Double> inputSupplier = () -> inputArray[0];

    simManager.setInputHandler(new LambdaSimInput<Double>(inputSupplier));
    simManager.setOutputHandler(new CopySimOutput<ExtenderState>(output));
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
    Double[] inputMotorRotations = new Double[] {
        0.0
    };
    ExtenderState outputState = new ExtenderState();
    setInputsAndOutputs(m_simManager, inputMotorRotations, outputState);

    inputMotorRotations[0] = -0.1;
    m_simManager.simulationPeriodic();
    assertEquals(0.168, outputState.getExtendedLen(), 0.001);
  }

  @Test
  public void testInvertedMotor() {
    Double[] inputMotorRotations = new Double[] {
        0.0
    };
    ExtenderState outputState = new ExtenderState();

    ExtenderSimModel extenderModelLocal = new ExtenderSimModel(0,
        new ExtenderParams(0.1, 1, 0.5, true));
    SimManager<Double, ExtenderState> simManagerLocal = new SimManager<Double, ExtenderState>(
        extenderModelLocal, null, null, true);

    setInputsAndOutputs(simManagerLocal, inputMotorRotations, outputState);

    inputMotorRotations[0] = 0.1;
    simManagerLocal.simulationPeriodic();
    assertEquals(0.468, outputState.getExtendedLen(), 0.001);
  }

  @Test
  public void testUpdateNewExtenderLenTwoTimesExtendsCorrectValue() {
    Double[] inputMotorRotations = new Double[] {
        0.0
    };
    ExtenderState outputState = new ExtenderState();
    setInputsAndOutputs(m_simManager, inputMotorRotations, outputState);

    inputMotorRotations[0] = 0.1;
    m_simManager.simulationPeriodic();

    inputMotorRotations[0] = 0.1;
    m_simManager.simulationPeriodic();

    assertEquals(0.231, outputState.getExtendedLen(), 0.001);
  }
}
