package simulationlib.simulation.extender;

/**
 * Class that holds paramaters for the extender.
 */
public class ExtenderParams {
  @SuppressWarnings("checkstyle:MemberName")
  public double cylinderDiameterMeters;

  @SuppressWarnings("checkstyle:MemberName")
  public double totalExtenderLengthMeters;

  @SuppressWarnings("checkstyle:MemberName")
  public double initialExtendedLen;

  @SuppressWarnings("checkstyle:MemberName")
  public boolean invertMotor;

  /**
   * Constructor.
   *
   * @param cylinderDiameterMetersInput    The diameter of the cylinder in meters.
   * @param totalExtenderLengthMetersInput The total length of the extender in meters.
   * @param initialExtendedLenInput        The initial length of the extender.
   * @param invertMotorInput               Whether the motor should be inverted.
   */
  public ExtenderParams(double cylinderDiameterMetersInput,
      double totalExtenderLengthMetersInput,
      double initialExtendedLenInput,
      boolean invertMotorInput) {

    cylinderDiameterMeters = cylinderDiameterMetersInput;
    totalExtenderLengthMeters = totalExtenderLengthMetersInput;
    initialExtendedLen = initialExtendedLenInput;
    invertMotor = invertMotorInput;
  }
}
