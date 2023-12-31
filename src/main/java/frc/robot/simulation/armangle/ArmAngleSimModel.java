package frc.robot.simulation.armangle;

import frc.robot.simulation.framework.SimModelInterface;

/**
 * Given a string connected to the back of an arm, this class will calculate
 * the ANGLE of the arm.
 */
public class ArmAngleSimModel implements SimModelInterface<Double, ArmAngleState> {
  private PivotMechanism m_pivotMechanism;
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

    m_pivotMechanism = new PivotMechanism(armAngleParams.heightFromWinchToPivotPoint,
        armAngleParams.armLengthFromEdgeToPivot);

    m_angleSignedDegrees = 0;
    m_isBroken = false;
  }

  public boolean isModelBroken() {
    return m_isBroken;
  }

  /**
   * Called every 20ms to calculate the new arm angle.
   */
  public ArmAngleState updateSimulation(Double newStringLen) {
    ArmAngleState armAngleResult = new ArmAngleState();

    // If the arm-angle-calculator is broken, there's nothing to update
    if (m_isBroken) {
      armAngleResult.setAngleSignedDegrees(m_angleSignedDegrees);
      return armAngleResult;
    }

    PivotMechanism.Result resultPair = m_pivotMechanism
        .calcSignedDegreesForStringLength(newStringLen);

    // Check if we got back that string length was invalid
    if (!resultPair.m_isValid) {
      System.out.println("ARM: Angle is out of bounds, needs to be in right half plane");
      m_isBroken = true;
    }

    m_angleSignedDegrees = resultPair.m_value;
    armAngleResult.setAngleSignedDegrees(m_angleSignedDegrees);

    return armAngleResult;
  }
}
