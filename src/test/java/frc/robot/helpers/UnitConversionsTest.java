package frc.robot.helpers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests the UnitConversions class.
 */
public class UnitConversionsTest {
  @BeforeEach
  public void setUp() {
  }

  @Test
  public void toSignedDegreesTestZeroDegrees() {
    double input = 0;
    double expected = 0;
    assertEquals(expected, UnitConversions.toSignedDegreesFromUnsignedDegrees(input), 0.001);
  }

  @Test
  public void toSignedDegreesTestPositiveDegrees() {
    double input = 45;
    double expected = 45;
    assertEquals(expected, UnitConversions.toSignedDegreesFromUnsignedDegrees(input), 0.001);
  }

  @Test
  public void toSignedDegreesTestNegativeDegrees() {
    double input = -45;
    double expected = -45;
    assertEquals(expected, UnitConversions.toSignedDegreesFromUnsignedDegrees(input), 0.001);
  }

  @Test
  public void toSignedDegreesTestBelowAxis() {
    double input = 350;
    double expected = -10;
    assertEquals(expected, UnitConversions.toSignedDegreesFromUnsignedDegrees(input), 0.001);
  }

  @Test
  public void toSignedDegreesTestWrapAroundPositiveDegrees() {
    double input = 370;
    double expected = 10;
    assertEquals(expected, UnitConversions.toSignedDegreesFromUnsignedDegrees(input), 0.001);
  }

  @Test
  public void toSignedDegreesTestWrapAroundNegativeDegrees() {
    double input = -370;
    double expected = -10;
    assertEquals(expected, UnitConversions.toSignedDegreesFromUnsignedDegrees(input), 0.001);
  }

  @Test
  void toUnSignedDegreesTestToUnsignedDegrees_Positive() {
    double signedDegrees = 45;
    double expectedUnsignedDegrees = 45;
    assertEquals(expectedUnsignedDegrees,
        UnitConversions.toUnsignedDegreesFromSignedDegrees(signedDegrees),
        0.001);
  }

  @Test
  void toUnSignedDegreesTestToUnsignedDegrees_Negative() {
    double signedDegrees = -45;
    double expectedUnsignedDegrees = 315;
    assertEquals(expectedUnsignedDegrees,
        UnitConversions.toUnsignedDegreesFromSignedDegrees(signedDegrees),
        0.001);
  }

  @Test
  void toUnSignedDegreesTestToUnsignedDegrees_GreaterThan360() {
    double signedDegrees = 361;
    double expectedUnsignedDegrees = 1;
    assertEquals(expectedUnsignedDegrees,
        UnitConversions.toUnsignedDegreesFromSignedDegrees(signedDegrees),
        0.001);
  }

  @Test
  void toUnSignedDegreesTestToUnsignedDegrees_LessThanNegative360() {
    double signedDegrees = -361;
    double expectedUnsignedDegrees = 359;
    assertEquals(expectedUnsignedDegrees,
        UnitConversions.toUnsignedDegreesFromSignedDegrees(signedDegrees),
        0.001);
  }

  @Test
  void toUnSignedDegreesTestToUnsignedDegrees_MultipleOf360() {
    double signedDegrees = 720;
    double expectedUnsignedDegrees = 0;
    assertEquals(expectedUnsignedDegrees,
        UnitConversions.toUnsignedDegreesFromSignedDegrees(signedDegrees),
        0.001);
  }

  @Test
  void rotationToSignedDegreesTestZeroRotation() {
    double rotation = 0;
    double expectedSignedDegrees = 0;
    assertEquals(expectedSignedDegrees, UnitConversions.rotationToSignedDegrees(rotation), 0.001);
  }

  @Test
  void rotationToSignedDegreesTestPositiveRotation() {
    double rotation = 0.25;
    double expectedSignedDegrees = 90;
    assertEquals(expectedSignedDegrees, UnitConversions.rotationToSignedDegrees(rotation), 0.001);
  }

  @Test
  void rotationToSignedDegreesTestNegativeRotation() {
    double rotation = -0.25;
    double expectedSignedDegrees = -90;
    assertEquals(expectedSignedDegrees, UnitConversions.rotationToSignedDegrees(rotation), 0.001);
  }

  @Test
  void rotationToSignedDegreesTestGreaterThanOneRotation() {
    double rotation = 1.25;
    double expectedSignedDegrees = 90;
    assertEquals(expectedSignedDegrees, UnitConversions.rotationToSignedDegrees(rotation), 0.001);
  }

  @Test
  void rotationToSignedDegreesTestLessThanNegativeOneRotation() {
    double rotation = -1.25;
    double expectedSignedDegrees = -90;
    assertEquals(expectedSignedDegrees, UnitConversions.rotationToSignedDegrees(rotation), 0.001);
  }

  @Test
  void rotationToSignedDegreesTestMultipleOfOneRotation() {
    double rotation = 2;
    double expectedSignedDegrees = 0;
    assertEquals(expectedSignedDegrees, UnitConversions.rotationToSignedDegrees(rotation), 0.001);
  }

  @Test
  void rotationToSignedDegreesTestMultipleOfNegativeOneRotation() {
    double rotation = -2;
    double expectedSignedDegrees = 0;
    assertEquals(expectedSignedDegrees, UnitConversions.rotationToSignedDegrees(rotation), 0.001);
  }

  @Test
  void signedDegreesToRotationTestZeroDegrees() {
    double signedDegrees = 0;
    double expectedRotation = 0;
    assertEquals(expectedRotation, UnitConversions.signedDegreesToRotation(signedDegrees), 0.001);
  }

  @Test
  void signedDegreesToRotationTestPositiveDegrees() {
    double signedDegrees = 90;
    double expectedRotation = 0.25;
    assertEquals(expectedRotation, UnitConversions.signedDegreesToRotation(signedDegrees), 0.001);
  }

  @Test
  void signedDegreesToRotationTestNegativeDegrees() {
    double signedDegrees = -90;
    double expectedRotation = 0.75;
    assertEquals(expectedRotation, UnitConversions.signedDegreesToRotation(signedDegrees), 0.001);
  }

  @Test
  void signedDegreesToRotationTestGreaterThan360Degrees() {
    double signedDegrees = 450;
    double expectedRotation = 0.25;
    assertEquals(expectedRotation, UnitConversions.signedDegreesToRotation(signedDegrees), 0.001);
  }

  @Test
  void signedDegreesToRotationTestLessThanNegative360Degrees() {
    double signedDegrees = -450;
    double expectedRotation = 0.75;
    assertEquals(expectedRotation, UnitConversions.signedDegreesToRotation(signedDegrees), 0.001);
  }

  @Test
  void signedDegreesToRotationTestMultipleOf360Degrees() {
    double signedDegrees = 720;
    double expectedRotation = 0;
    assertEquals(expectedRotation, UnitConversions.signedDegreesToRotation(signedDegrees), 0.001);
  }

  @Test
  void signedDegreesToRotationTestMultipleOfNegative360Degrees() {
    double signedDegrees = -720;
    double expectedRotation = 0;
    assertEquals(expectedRotation, UnitConversions.signedDegreesToRotation(signedDegrees), 0.001);
  }

  @Test
  void clampSignedDegreesTestNegative180() {
    double signedDegrees = -180;
    double expectedSignedDegrees = -180;
    assertEquals(expectedSignedDegrees, UnitConversions.clampSignedDegrees(signedDegrees), 0.001);
  }

  @Test
  void clampSignedDegreesTestNegative180Point1() {
    double signedDegrees = -180.1;
    double expectedSignedDegrees = 179.9;
    assertEquals(expectedSignedDegrees, UnitConversions.clampSignedDegrees(signedDegrees), 0.001);
  }

  @Test
  void clampSignedDegreesTestNegative360() {
    double signedDegrees = -360;
    double expectedSignedDegrees = 0;
    assertEquals(expectedSignedDegrees, UnitConversions.clampSignedDegrees(signedDegrees), 0.001);
  }

  @Test
  void clampSignedDegreesTestNegative360Point1() {
    double signedDegrees = -360.1;
    double expectedSignedDegrees = -0.1;
    assertEquals(expectedSignedDegrees, UnitConversions.clampSignedDegrees(signedDegrees), 0.001);
  }

  @Test
  void clampSignedDegreesTest270() {
    double signedDegrees = 270;
    double expectedSignedDegrees = -90;
    assertEquals(expectedSignedDegrees, UnitConversions.clampSignedDegrees(signedDegrees), 0.001);
  }

  @Test
  void clampRotation0() {
    double rotation = 0;
    double expectedRotation = 0;
    assertEquals(expectedRotation, UnitConversions.clampRotation(rotation), 0.001);
  }

  @Test
  void clampRotation1() {
    double rotation = 1;
    double expectedRotation = 0;
    assertEquals(expectedRotation, UnitConversions.clampRotation(rotation), 0.001);
  }

  @Test
  void clampRotationNegative1() {
    double rotation = -1;
    double expectedRotation = 0;
    assertEquals(expectedRotation, UnitConversions.clampRotation(rotation), 0.001);
  }

  @Test
  void clampRotationNegative0Point1() {
    double rotation = -0.1;
    double expectedRotation = 0.9;
    assertEquals(expectedRotation, UnitConversions.clampRotation(rotation), 0.001);
  }

  @Test
  void clampRotation5() {
    double rotation = 5;
    double expectedRotation = 0;
    assertEquals(expectedRotation, UnitConversions.clampRotation(rotation), 0.001);
  }

  @Test
  void clampRotationNegative5() {
    double rotation = -5;
    double expectedRotation = 0;
    assertEquals(expectedRotation, UnitConversions.clampRotation(rotation), 0.001);
  }
}
