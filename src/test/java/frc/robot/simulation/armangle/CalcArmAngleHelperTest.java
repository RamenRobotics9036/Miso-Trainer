package frc.robot.simulation.armangle;

import static org.junit.jupiter.api.Assertions.assertEquals;

import frc.robot.helpers.UnitConversions;
import frc.robot.simulation.armangle.CalcArmAngleHelper.Result;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests the CalcArmAngleHelper class.
 */
public class CalcArmAngleHelperTest {
  private CalcArmAngleHelper m_calcArmAngleHelper;
  private final double m_armHeightFromWinchToPivotPoint = 1;
  private final double m_armLengthFromEdgeToPivot = 0.25;

  @BeforeEach
  public void setUp() {
    m_calcArmAngleHelper = new CalcArmAngleHelper(m_armHeightFromWinchToPivotPoint,
        m_armLengthFromEdgeToPivot);
  }

  private void calcDegreesHelper(double stringLen, double expectedDegrees, boolean expectIsValid) {
    Result result = m_calcArmAngleHelper.calcSignedDegreesForStringLength(stringLen);

    assertEquals(result.m_value, expectedDegrees, UnitConversions.kAngleTolerance);
    assertEquals(result.m_isValid, expectIsValid);
  }

  @Test
  public void calcDegreesArmBeyondFullyUpShouldReturnError() {
    double amountBeyondLimit = 0.0001;
    calcDegreesHelper(m_armHeightFromWinchToPivotPoint - m_armLengthFromEdgeToPivot
        - amountBeyondLimit, 90, false);
  }

  @Test
  public void calcDegreesArmBeyondFullyDownShouldReturnSuccessSinceStringDangling() {
    double amountBeyondLimit = 0.0001;
    calcDegreesHelper(m_armHeightFromWinchToPivotPoint + m_armLengthFromEdgeToPivot
        + amountBeyondLimit, -90, true);
  }

  @Test
  public void calcDegreesArmAtFullyUpShouldReturn90Degrees() {
    calcDegreesHelper(m_armHeightFromWinchToPivotPoint - m_armLengthFromEdgeToPivot, 90, true);
  }

  @Test
  public void calcDegreesArmAtFullyDownShouldReturnNegative90Degrees() {
    calcDegreesHelper(m_armHeightFromWinchToPivotPoint + m_armLengthFromEdgeToPivot, -90, true);
  }

  @Test
  public void calcDegreesLevelArmShouldReturn0Degrees() {
    calcDegreesHelper(m_armHeightFromWinchToPivotPoint, 0, true);
  }

  @Test
  public void calcDegreesArmAt45DegreesShouldSucceed() {
    calcDegreesHelper(m_armHeightFromWinchToPivotPoint - 0.17678, 45, true);
  }

  private void calcStringLenHelper(double degrees, double expectedResult, boolean expectIsValid) {
    Result result = m_calcArmAngleHelper.calcStringLengthForSignedDegrees(degrees);

    assertEquals(result.m_value, expectedResult, UnitConversions.kDoubleTolerance);
    assertEquals(result.m_isValid, expectIsValid);
  }

  @Test
  public void calcStringLenFor0Degrees() {
    calcStringLenHelper(0, m_armHeightFromWinchToPivotPoint, true);
  }

  @Test
  public void calcStringLenFor90Degrees() {
    calcStringLenHelper(90, m_armHeightFromWinchToPivotPoint - m_armLengthFromEdgeToPivot, true);
  }

  @Test
  public void calcStringLenForNegative90Degrees() {
    calcStringLenHelper(-90, m_armHeightFromWinchToPivotPoint + m_armLengthFromEdgeToPivot, true);
  }

  @Test
  public void calcStringLenFor45Degrees() {
    calcStringLenHelper(45, 0.8232233047033631, true);
  }

  @Test
  public void calcStringLenForNegative45Degrees() {
    calcStringLenHelper(-45, 1.1767766952966369, true);
  }

  @Test
  public void calcStringLenFor91Degrees() {
    calcStringLenHelper(91, m_armHeightFromWinchToPivotPoint - m_armLengthFromEdgeToPivot, false);
  }

  @Test
  public void calcStringLenForNegative91Degrees() {
    calcStringLenHelper(-91, m_armHeightFromWinchToPivotPoint + m_armLengthFromEdgeToPivot, false);
  }
}
