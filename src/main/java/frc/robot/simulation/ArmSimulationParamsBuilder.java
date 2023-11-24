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
    instance.topSignedDegreesLimit = value;
    return this;
  }

  public ArmSimulationParamsBuilder setBottomSignedDegreesLimit(double value) {
    instance.bottomSignedDegreesLimit = value;
    return this;
  }

  public ArmSimulationParamsBuilder setDeltaDegreesBeforeBroken(double value) {
    instance.deltaDegreesBeforeBroken = value;
    return this;
  }

  public ArmSimulationParamsBuilder setGrabberBreaksIfOpenBelowThisSignedDegreesLimit(
      double value) {
    instance.grabberSignedDegreesLimit = value;
    return this;
  }

  public ArmSimulationParamsBuilder setHeightFromWinchToPivotPoint(double value) {
    instance.heightFromWinchToPivotPoint = value;
    return this;
  }

  public ArmSimulationParamsBuilder setArmLengthFromEdgeToPivot(double value) {
    instance.armLengthFromEdgeToPivot = value;
    return this;
  }

  public ArmSimulationParamsBuilder setArmLengthFromEdgeToPivotMin(double value) {
    instance.armLengthFromEdgeToPivotMin = value;
    return this;
  }

  public ArmSimulationParamsBuilder setEncoderRotationsOffset(double value) {
    instance.encoderRotationsOffset = value;
    return this;
  }

  // Return the built instance
  public ArmSimulationParams build() {
    return instance;
  }
}
