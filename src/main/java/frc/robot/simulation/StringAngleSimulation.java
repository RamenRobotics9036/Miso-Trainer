package frc.robot.simulation;

import java.util.function.DoubleConsumer;
import java.util.function.DoubleSupplier;

/**
 * Given a string connected to the back of an arm, this class will calculate
 * the ANGLE of the arm.
 */
public class StringAngleSimulation {
  private DoubleSupplier m_stringUnspooledLenSupplier;
  private DoubleConsumer m_armAngleConsumer;
  private CalcArmAngleHelper m_calcArmAngleHelper;
  private boolean m_isBroken;

  /**
   * Constructor.
   */
  // $TODO - Move these into a builder like in ArmSimulationParams
  public StringAngleSimulation(DoubleSupplier stringUnspooledLenSupplier,
      DoubleConsumer armAngleConsumer,
      double heightFromWinchToPivotPoint,
      double armLengthFromEdgeToPivot,
      double armLengthFromEdgeToPivotMin) {

    if (stringUnspooledLenSupplier == null) {
      throw new IllegalArgumentException("stringUnspooledLenSupplier");
    }

    if (armAngleConsumer == null) {
      throw new IllegalArgumentException("armAngleConsumer");
    }

    if (armLengthFromEdgeToPivot < armLengthFromEdgeToPivotMin) {
      throw new IllegalArgumentException("armLengthFromEdgeToPivot needs to be at least "
          + armLengthFromEdgeToPivotMin + " meters, otherwise the arm cant be pivoted");
    }

    m_stringUnspooledLenSupplier = stringUnspooledLenSupplier;
    m_armAngleConsumer = armAngleConsumer;
    m_calcArmAngleHelper = new CalcArmAngleHelper(heightFromWinchToPivotPoint,
        armLengthFromEdgeToPivot);
    m_isBroken = false;
  }

  public boolean getIsBroken() {
    return m_isBroken;
  }

  private void updateStringAngle() {
    // If the arm-angle-calculator is broken, there's nothing to update
    if (m_isBroken) {
      return;
    }

    double newStringLen = m_stringUnspooledLenSupplier.getAsDouble();
    CalcArmAngleHelper.Result resultPair = m_calcArmAngleHelper
        .calcSignedDegreesForStringLength(newStringLen);

    // Check if we got back that string length was invalid
    if (!resultPair.m_isValid) {
      System.out.println("ARM: Angle is out of bounds, needs to be in right half plane");
      m_isBroken = true;
    }

    double newArmAngleSignedDegrees = resultPair.m_value;

    // Set new arm angle on Consumer
    m_armAngleConsumer.accept(newArmAngleSignedDegrees);
  }

  public void simulationPeriodic() {
    updateStringAngle();
  }
}
