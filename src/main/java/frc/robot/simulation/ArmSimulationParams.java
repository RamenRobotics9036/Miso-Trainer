package frc.robot.simulation;

/**
 * Class that holds paramaters for the arm simulation class.
 */
public class ArmSimulationParams {
  public double m_topSignedDegreesLimit;
  public double m_bottomSignedDegreesLimit;
  public double m_deltaDegreesBeforeBroken;
  public double m_grabberBreaksIfOpenBelowThisSignedDegreesLimit;
  public double m_heightFromWinchToPivotPoint;
  public double m_armLengthFromEdgeToPivot;
  public double m_armLengthFromEdgeToPivotMin;
  public double m_encoderDegreesOffset;

  /**
   * Constructor with 0 params.
   */
  public ArmSimulationParams() {
  }

  /**
   * Constructor with all params.
   */
  public ArmSimulationParams(double topSignedDegreesLimit,
      double bottomSignedDegreesLimit,
      double deltaDegreesBeforeBroken,
      double grabberBreaksIfOpenBelowThisSignedDegreesLimit,
      double heightFromWinchToPivotPoint,
      double armLengthFromEdgeToPivot,
      double armLengthFromEdgeToPivotMin,
      double encoderDegreesOffset) {

    m_topSignedDegreesLimit = topSignedDegreesLimit;
    m_bottomSignedDegreesLimit = bottomSignedDegreesLimit;
    m_deltaDegreesBeforeBroken = deltaDegreesBeforeBroken;
    m_grabberBreaksIfOpenBelowThisSignedDegreesLimit = grabberBreaksIfOpenBelowThisSignedDegreesLimit;
    m_heightFromWinchToPivotPoint = heightFromWinchToPivotPoint;
    m_armLengthFromEdgeToPivot = armLengthFromEdgeToPivot;
    m_armLengthFromEdgeToPivotMin = armLengthFromEdgeToPivotMin;
    m_encoderDegreesOffset = encoderDegreesOffset;
  }

  /**
   * Copy constructor.
   */
  public ArmSimulationParams(ArmSimulationParams other) {
    m_topSignedDegreesLimit = other.m_topSignedDegreesLimit;
    m_bottomSignedDegreesLimit = other.m_bottomSignedDegreesLimit;
    m_deltaDegreesBeforeBroken = other.m_deltaDegreesBeforeBroken;
    m_grabberBreaksIfOpenBelowThisSignedDegreesLimit = other.m_grabberBreaksIfOpenBelowThisSignedDegreesLimit;
    m_heightFromWinchToPivotPoint = other.m_heightFromWinchToPivotPoint;
    m_armLengthFromEdgeToPivot = other.m_armLengthFromEdgeToPivot;
    m_armLengthFromEdgeToPivotMin = other.m_armLengthFromEdgeToPivotMin;
    m_encoderDegreesOffset = other.m_encoderDegreesOffset;
  }
}
