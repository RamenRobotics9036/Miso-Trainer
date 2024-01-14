package frc.robot.simulation.extender;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

class ExtenderSimModelTest {
  private ExtenderSimModel m_extender;

  @BeforeEach
  void setup() {
    // Initialize with some default values
    // $TODO - Need to change this to use SimManager throughout this file
    m_extender = new ExtenderSimModel(0.0, new ExtenderParams(0.1, 0.5, 0.2, false));
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
    assertEquals(0.2, m_extender.getExtendedLen());
  }

  @Test
  public void getExtendedPercent_WithKnownValues_ShouldReturnCorrectPercentage() {
    double extendedPercent = m_extender.getExtendedPercent();
    assertEquals(0.4, extendedPercent); // 0.2 / 0.5
  }

  @Test
  public void getIsBroken_NewInstance_ShouldReturnFalse() {
    assertFalse(m_extender.getIsBroken());
  }

  @Test
  public void updateNewExtendedLen_NormalOperation_ShouldUpdateLength() {
    m_extender.updateNewExtendedLen(1.0);
    assertTrue(m_extender.getExtendedLen() > 0.2); // $TODO
  }

  @Test
  public void updateNewExtendedLen_OverExtension_ShouldSetIsBrokenTrue() {
    m_extender.updateNewExtendedLen(10.0);
    assertTrue(m_extender.getIsBroken());
  }

  @Test
  public void updateNewExtenderLen_OverExtension_ShouldHaveExpectedLen() {
    m_extender.updateNewExtendedLen(10.0);
    assertEquals(0.5, m_extender.getExtendedLen());
  }

  @Test
  public void updateNewExtendedLen_UnderExtension_ShouldSetIsBrokenTrue() {
    m_extender.updateNewExtendedLen(-10.0);
    assertTrue(m_extender.getIsBroken());
  }

  @Test
  public void updateNewExtenderLen_UnderExtension_ShouldHaveExpectedLen() {
    m_extender.updateNewExtendedLen(-10.0);
    assertEquals(0.0, m_extender.getExtendedLen());
  }

  @Test
  public void updateNewExtenderLen_MaxLen_ShouldSucceed() {
    m_extender.updateNewExtendedLen(0.952);
    assertEquals(0.5, m_extender.getExtendedLen(), 0.001);
    assertFalse(m_extender.getIsBroken());
  }

  @Test
  public void updateNewExtenderLen_InitialLenHalfWay_RotatingToMinLen_ShouldSucceed() {
    ExtenderSimModel invertedExtender = new ExtenderSimModel(0.0,
        new ExtenderParams(0.1, 1, 0.3, false));
    invertedExtender.updateNewExtendedLen(-0.952);
    assertEquals(0.0, invertedExtender.getExtendedLen(), 0.001);
    assertFalse(invertedExtender.getIsBroken());
  }

  @Test
  public void updateNewExtendedLen_WhenBroken_ShouldNotChangeLength() {
    m_extender.updateNewExtendedLen(10.0); // This should break the extender
    double lengthAfterBreak = m_extender.getExtendedLen();
    m_extender.updateNewExtendedLen(5.0); // This should not change the length
    assertEquals(lengthAfterBreak, m_extender.getExtendedLen());
  }

  @Test
  public void testUpdateNewExtendedLenGivesExpectedLength() {
    assertEquals(0.2, m_extender.getExtendedLen(), 0.001);
    m_extender.updateNewExtendedLen(0.1);
    assertEquals(0.231, m_extender.getExtendedLen(), 0.001);
  }

  @Test
  public void testUpdateNewExtendedLenValidNegativeValue() {
    m_extender.updateNewExtendedLen(-0.1);
    assertEquals(0.168, m_extender.getExtendedLen(), 0.001);
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
    m_extender.updateNewExtendedLen(0.1);
    m_extender.updateNewExtendedLen(0.1);
    assertEquals(0.231, m_extender.getExtendedLen(), 0.001);
  }
}
