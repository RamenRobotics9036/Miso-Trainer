package frc.robot.simulation.simplearm;

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

  public ArmSimulationParamsBuilder setTopSignedDegreesBreak(double value) {
    instance.topSignedDegreesBreak = value;
    return this;
  }

  public ArmSimulationParamsBuilder setBottomSignedDegreesBreak(double value) {
    instance.bottomSignedDegreesBreak = value;
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
