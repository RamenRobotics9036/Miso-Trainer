package frc.robot.simulation;

import java.util.function.DoubleSupplier;

/**
 * Given a string connected to the back of an arm, this class will calculate
 * the ANGLE of the arm.
 */
public class StringAngleSimulation {
  private DoubleSupplier m_stringUnspooledLenSupplier;
  private CalcArmAngleHelper m_calcArmAngleHelper;

  /**
   * Constructor.
   */
  // $TODO - Move these into a builder like in ArmSimulationParams
  public StringAngleSimulation(DoubleSupplier stringUnspooledLenSupplier,
      double heightFromWinchToPivotPoint,
      double armLengthFromEdgeToPivot,
      double armLengthFromEdgeToPivotMin) {
    if (stringUnspooledLenSupplier == null) {
      throw new IllegalArgumentException("stringUnspooledLenSupplier");
    }

    if (armLengthFromEdgeToPivot < armLengthFromEdgeToPivotMin) {
      throw new IllegalArgumentException("armLengthFromEdgeToPivot needs to be at least "
          + armLengthFromEdgeToPivotMin + " meters, otherwise the arm cant be pivoted");
    }

    m_stringUnspooledLenSupplier = stringUnspooledLenSupplier;
    m_calcArmAngleHelper = new CalcArmAngleHelper(heightFromWinchToPivotPoint,
        armLengthFromEdgeToPivot);
  }
}
