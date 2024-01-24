package simulationlib.simulation.simplearm;

/**
 * Class that holds paramaters for the arm simulation class.
 */
public class ArmSimParams {
  @SuppressWarnings("checkstyle:MemberName")
  public double topSignedDegreesBreak;

  @SuppressWarnings("checkstyle:MemberName")
  public double bottomSignedDegreesBreak;

  @SuppressWarnings("checkstyle:MemberName")
  public double encoderRotationsOffset;

  /**
   * Constructor with 0 params.
   */
  public ArmSimParams() {
  }

  /**
   * Constructor with all params.
   */
  public ArmSimParams(double topSignedDegreesBreakInput,
      double bottomSignedDegreesBreakInput,
      double encoderRotationsOffsetInput) {

    topSignedDegreesBreak = topSignedDegreesBreakInput;
    bottomSignedDegreesBreak = bottomSignedDegreesBreakInput;
    encoderRotationsOffset = encoderRotationsOffsetInput;
  }

  /**
   * Copy constructor.
   */
  public ArmSimParams(ArmSimParams other) {
    topSignedDegreesBreak = other.topSignedDegreesBreak;
    bottomSignedDegreesBreak = other.bottomSignedDegreesBreak;
    encoderRotationsOffset = other.encoderRotationsOffset;
  }
}
