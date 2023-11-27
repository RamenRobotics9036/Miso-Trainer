package frc.robot.simulation.armangle;

/**
 * Class that holds paramaters for the arm angle calculator.
 */
public class ArmAngleParams {
  @SuppressWarnings("checkstyle:MemberName")
  public double heightFromWinchToPivotPoint;

  @SuppressWarnings("checkstyle:MemberName")
  public double armLengthFromEdgeToPivot;

  @SuppressWarnings("checkstyle:MemberName")
  public double armLengthFromEdgeToPivotMin;

  /**
   * Constructor.
   */
  public ArmAngleParams(double heightFromWinchToPivotPointInput,
      double armLengthFromEdgeToPivotInput,
      double armLengthFromEdgeToPivotMinInput) {

    heightFromWinchToPivotPoint = heightFromWinchToPivotPointInput;
    armLengthFromEdgeToPivot = armLengthFromEdgeToPivotInput;
    armLengthFromEdgeToPivotMin = armLengthFromEdgeToPivotMinInput;
  }
}