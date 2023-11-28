package frc.robot.simulation.armangle;

/**
 * Given a string connected to the back of an arm, this class will calculate
 * the ANGLE of the arm.
 */
public class ArmAngleSimModel {
  private CalcArmAngleHelper m_calcArmAngleHelper;
  private double m_angleSignedDegrees;
  private boolean m_isBroken;

  /**
   * Constructor.
   */
  public ArmAngleSimModel(ArmAngleParams armAngleParams) {

    if (armAngleParams == null) {
      throw new IllegalArgumentException("armAngleParams cannot be null");
    }

    if (armAngleParams.armLengthFromEdgeToPivot < armAngleParams.armLengthFromEdgeToPivotMin) {
      throw new IllegalArgumentException("armLengthFromEdgeToPivot needs to be at least "
          + armAngleParams.armLengthFromEdgeToPivotMin
          + " meters, otherwise the arm cant be pivoted");
    }

    m_calcArmAngleHelper = new CalcArmAngleHelper(armAngleParams.heightFromWinchToPivotPoint,
        armAngleParams.armLengthFromEdgeToPivot);

    m_angleSignedDegrees = 0;
    m_isBroken = false;
  }

  public double getAngleSignedDegrees() {
    return m_angleSignedDegrees;
  }

  public boolean getIsBroken() {
    return m_isBroken;
  }

  /**
   * Called every 20ms to calculate the new arm angle.
   */
  public void updateArmAngle(double newStringLen) {
    // If the arm-angle-calculator is broken, there's nothing to update
    if (m_isBroken) {
      return;
    }

    CalcArmAngleHelper.Result resultPair = m_calcArmAngleHelper
        .calcSignedDegreesForStringLength(newStringLen);

    // Check if we got back that string length was invalid
    if (!resultPair.m_isValid) {
      System.out.println("ARM: Angle is out of bounds, needs to be in right half plane");
      m_isBroken = true;
    }

    m_angleSignedDegrees = resultPair.m_value;
  }
}
