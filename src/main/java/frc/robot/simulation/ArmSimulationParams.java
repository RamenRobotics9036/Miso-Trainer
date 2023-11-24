package frc.robot.simulation;

/**
 * Class that holds paramaters for the arm simulation class.
 */
public class ArmSimulationParams {
  @SuppressWarnings("checkstyle:MemberName")
  public double topSignedDegreesLimit;

  @SuppressWarnings("checkstyle:MemberName")
  public double bottomSignedDegreesLimit;

  @SuppressWarnings("checkstyle:MemberName")
  public double deltaDegreesBeforeBroken;

  @SuppressWarnings("checkstyle:MemberName")
  public double grabberSignedDegreesLimit;

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
  public ArmSimulationParams(double topSignedDegreesLimitInput,
      double bottomSignedDegreesLimitInput,
      double deltaDegreesBeforeBrokenInput,
      double grabberSignedDegreesLimitInput,
      double heightFromWinchToPivotPointInput,
      double armLengthFromEdgeToPivotInput,
      double armLengthFromEdgeToPivotMinInput,
      double encoderRotationsOffsetInput) {

    topSignedDegreesLimit = topSignedDegreesLimitInput;
    bottomSignedDegreesLimit = bottomSignedDegreesLimitInput;
    deltaDegreesBeforeBroken = deltaDegreesBeforeBrokenInput;
    grabberSignedDegreesLimit = grabberSignedDegreesLimitInput;
    heightFromWinchToPivotPoint = heightFromWinchToPivotPointInput;
    armLengthFromEdgeToPivot = armLengthFromEdgeToPivotInput;
    armLengthFromEdgeToPivotMin = armLengthFromEdgeToPivotMinInput;
    encoderRotationsOffset = encoderRotationsOffsetInput;
  }

  /**
   * Copy constructor.
   */
  public ArmSimulationParams(ArmSimulationParams other) {
    topSignedDegreesLimit = other.topSignedDegreesLimit;
    bottomSignedDegreesLimit = other.bottomSignedDegreesLimit;
    deltaDegreesBeforeBroken = other.deltaDegreesBeforeBroken;
    grabberSignedDegreesLimit = other.grabberSignedDegreesLimit;
    heightFromWinchToPivotPoint = other.heightFromWinchToPivotPoint;
    armLengthFromEdgeToPivot = other.armLengthFromEdgeToPivot;
    armLengthFromEdgeToPivotMin = other.armLengthFromEdgeToPivotMin;
    encoderRotationsOffset = other.encoderRotationsOffset;
  }
}
