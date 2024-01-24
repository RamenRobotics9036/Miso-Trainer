package simulationlib.simulation.armangle;

import static org.junit.jupiter.api.Assertions.assertEquals;

import simulationlib.helpers.UnitConversions;
import simulationlib.simulation.armangle.PivotMechanism;
import simulationlib.simulation.armangle.PivotMechanism.Result;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests the PivotMechanism class.
 */
public class PivotMechanismTest {
  private PivotMechanism m_pivotMechanism;

  @BeforeEach
  public void setUp() {
    m_pivotMechanism = new PivotMechanism(getTestingHeightFromWinchToPivotPoint(),
        getTestingLengthFromEdgeToPivot());
  }

  private double getTestingHeightFromWinchToPivotPoint() {
    return 1;
  }

  private double getTestingLengthFromEdgeToPivot() {
    return 0.25;
  }

  private void calcDegreesHelper(double stringLen, double expectedDegrees, boolean expectIsValid) {
    Result result = m_pivotMechanism.calcSignedDegreesForStringLength(stringLen);

    assertEquals(result.m_value, expectedDegrees, UnitConversions.kAngleTolerance);
    assertEquals(result.m_isValid, expectIsValid);
  }

  @Test
  public void calcDegreesArmBeyondFullyUpShouldReturnError() {
    double amountBeyondLimit = 0.0001;
    calcDegreesHelper(getTestingHeightFromWinchToPivotPoint() - getTestingLengthFromEdgeToPivot()
        - amountBeyondLimit, 90, false);
  }

  @Test
  public void calcDegreesArmBeyondFullyDownShouldReturnSuccessSinceStringDangling() {
    double amountBeyondLimit = 0.0001;
    calcDegreesHelper(getTestingHeightFromWinchToPivotPoint() + getTestingLengthFromEdgeToPivot()
        + amountBeyondLimit, -90, true);
  }

  @Test
  public void calcDegreesArmAtFullyUpShouldReturn90Degrees() {
    calcDegreesHelper(getTestingHeightFromWinchToPivotPoint() - getTestingLengthFromEdgeToPivot(),
        90,
        true);
  }

  @Test
  public void calcDegreesArmAtFullyDownShouldReturnNegative90Degrees() {
    calcDegreesHelper(getTestingHeightFromWinchToPivotPoint() + getTestingLengthFromEdgeToPivot(),
        -90,
        true);
  }

  @Test
  public void calcDegreesLevelArmShouldReturn0Degrees() {
    calcDegreesHelper(getTestingHeightFromWinchToPivotPoint(), 0, true);
  }

  @Test
  public void calcDegreesArmAt45DegreesShouldSucceed() {
    calcDegreesHelper(getTestingHeightFromWinchToPivotPoint() - 0.17678, 45, true);
  }

  private void calcStringLenHelper(double degrees, double expectedResult, boolean expectIsValid) {
    Result result = m_pivotMechanism.calcStringLengthForSignedDegrees(degrees);

    assertEquals(result.m_value, expectedResult, UnitConversions.kDoubleTolerance);
    assertEquals(result.m_isValid, expectIsValid);
  }

  @Test
  public void calcStringLenFor0Degrees() {
    calcStringLenHelper(0, getTestingHeightFromWinchToPivotPoint(), true);
  }

  @Test
  public void calcStringLenFor90Degrees() {
    calcStringLenHelper(90,
        getTestingHeightFromWinchToPivotPoint() - getTestingLengthFromEdgeToPivot(),
        true);
  }

  @Test
  public void calcStringLenForNegative90Degrees() {
    calcStringLenHelper(-90,
        getTestingHeightFromWinchToPivotPoint() + getTestingLengthFromEdgeToPivot(),
        true);
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
    calcStringLenHelper(91,
        getTestingHeightFromWinchToPivotPoint() - getTestingLengthFromEdgeToPivot(),
        false);
  }

  @Test
  public void calcStringLenForNegative91Degrees() {
    calcStringLenHelper(-91,
        getTestingHeightFromWinchToPivotPoint() + getTestingLengthFromEdgeToPivot(),
        false);
  }
}
