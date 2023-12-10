package frc.robot.simulation.simplearm;

/**
 * Builder class pattern for ArmSimulationParams.
 */
public class ArmSimParamsBuilder {
  @SuppressWarnings("MemberNameCheck")
  private ArmSimParams instance;

  /**
   * Constructor.
   */
  public ArmSimParamsBuilder() {
    instance = new ArmSimParams();
  }

  /**
   * Constructor that takes defaults as an input parameter.
   */
  public ArmSimParamsBuilder(ArmSimParams defaults) {
    instance = new ArmSimParams(defaults);
  }

  public ArmSimParamsBuilder setTopSignedDegreesBreak(double value) {
    instance.topSignedDegreesBreak = value;
    return this;
  }

  public ArmSimParamsBuilder setBottomSignedDegreesBreak(double value) {
    instance.bottomSignedDegreesBreak = value;
    return this;
  }

  public ArmSimParamsBuilder setEncoderRotationsOffset(double value) {
    instance.encoderRotationsOffset = value;
    return this;
  }

  // Return the built instance
  public ArmSimParams build() {
    return instance;
  }
}
