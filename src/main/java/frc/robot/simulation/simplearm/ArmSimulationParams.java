package frc.robot.simulation.simplearm;

/**
 * Class that holds paramaters for the arm simulation class.
 */
public class ArmSimulationParams {
  @SuppressWarnings("checkstyle:MemberName")
  public double topSignedDegreesBreak;

  @SuppressWarnings("checkstyle:MemberName")
  public double bottomSignedDegreesBreak;

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
      double encoderRotationsOffsetInput) {

    topSignedDegreesBreak = topSignedDegreesBreakInput;
    bottomSignedDegreesBreak = bottomSignedDegreesBreakInput;
    encoderRotationsOffset = encoderRotationsOffsetInput;
  }

  /**
   * Copy constructor.
   */
  public ArmSimulationParams(ArmSimulationParams other) {
    topSignedDegreesBreak = other.topSignedDegreesBreak;
    bottomSignedDegreesBreak = other.bottomSignedDegreesBreak;
    encoderRotationsOffset = other.encoderRotationsOffset;
  }
}
