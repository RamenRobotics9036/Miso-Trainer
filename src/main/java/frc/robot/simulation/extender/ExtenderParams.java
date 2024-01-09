package frc.robot.simulation.extender;

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
