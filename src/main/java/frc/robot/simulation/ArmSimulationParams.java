package frc.robot.simulation;

/**
 * Class that holds paramaters for the arm simulation class.
 */
public class ArmSimulationParams {
  @SuppressWarnings("checkstyle:MemberName")
  public double topSignedDegreesBreak;

  @SuppressWarnings("checkstyle:MemberName")
  public double bottomSignedDegreesBreak;

  @SuppressWarnings("checkstyle:MemberName")
  public double heightFromWinchToPivotPoint;

  @SuppressWarnings("checkstyle:MemberName")
  public double armLengthFromEdgeToPivot;

  @SuppressWarnings("checkstyle:MemberName")
  public double armLengthFromEdgeToPivotMin;

  @SuppressWarnings("checkstyle:MemberName")
  public double encoderRotationsOffset;

  /**
   * Constructor with 0 params.
   */
  public ArmSimulationParams() {
  }

  /**
   * Constructor with all params.
   */
  public ArmSimulationParams(double topSignedDegreesBreakInput,
      double bottomSignedDegreesBreakInput,
      double heightFromWinchToPivotPointInput,
      double armLengthFromEdgeToPivotInput,
      double armLengthFromEdgeToPivotMinInput,
      double encoderRotationsOffsetInput) {

    topSignedDegreesBreak = topSignedDegreesBreakInput;
    bottomSignedDegreesBreak = bottomSignedDegreesBreakInput;
    heightFromWinchToPivotPoint = heightFromWinchToPivotPointInput;
    armLengthFromEdgeToPivot = armLengthFromEdgeToPivotInput;
    armLengthFromEdgeToPivotMin = armLengthFromEdgeToPivotMinInput;
    encoderRotationsOffset = encoderRotationsOffsetInput;
  }

  /**
   * Copy constructor.
   */
  public ArmSimulationParams(ArmSimulationParams other) {
    topSignedDegreesBreak = other.topSignedDegreesBreak;
    bottomSignedDegreesBreak = other.bottomSignedDegreesBreak;
    heightFromWinchToPivotPoint = other.heightFromWinchToPivotPoint;
    armLengthFromEdgeToPivot = other.armLengthFromEdgeToPivot;
    armLengthFromEdgeToPivotMin = other.armLengthFromEdgeToPivotMin;
    encoderRotationsOffset = other.encoderRotationsOffset;
  }
}
