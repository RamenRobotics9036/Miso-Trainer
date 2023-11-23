package frc.robot.simulation;

/**
 * Builder class pattern for ArmSimulationParams.
 */
public class ArmSimulationParamsBuilder {
  @SuppressWarnings("MemberNameCheck")
  private ArmSimulationParams instance;

  /**
   * Constructor.
   */
  public ArmSimulationParamsBuilder() {
    instance = new ArmSimulationParams();
  }

  /**
   * Constructor that takes defaults as an input parameter.
   */
  public ArmSimulationParamsBuilder(ArmSimulationParams defaults) {
    instance = new ArmSimulationParams(defaults);
  }

  public ArmSimulationParamsBuilder setTopSignedDegreesLimit(double value) {
    instance.m_topSignedDegreesLimit = value;
    return this;
  }

  public ArmSimulationParamsBuilder setBottomSignedDegreesLimit(double value) {
    instance.m_bottomSignedDegreesLimit = value;
    return this;
  }

  public ArmSimulationParamsBuilder setDeltaDegreesBeforeBroken(double value) {
    instance.m_deltaDegreesBeforeBroken = value;
    return this;
  }

  public ArmSimulationParamsBuilder setGrabberBreaksIfOpenBelowThisSignedDegreesLimit(
      double value) {
    instance.m_grabberBreaksIfOpenBelowThisSignedDegreesLimit = value;
    return this;
  }

  public ArmSimulationParamsBuilder setHeightFromWinchToPivotPoint(double value) {
    instance.m_heightFromWinchToPivotPoint = value;
    return this;
  }

  public ArmSimulationParamsBuilder setArmLengthFromEdgeToPivot(double value) {
    instance.m_armLengthFromEdgeToPivot = value;
    return this;
  }

  public ArmSimulationParamsBuilder setArmLengthFromEdgeToPivotMin(double value) {
    instance.m_armLengthFromEdgeToPivotMin = value;
    return this;
  }

  public ArmSimulationParamsBuilder setEncoderDegreesOffset(double value) {
    instance.m_encoderDegreesOffset = value;
    return this;
  }

  // Return the built instance
  public ArmSimulationParams build() {
    return instance;
  }
}
