package simulationlib.simulation.drive;

/**
 * Input to the drive simulation periodic function.
 */
public class DriveInputState {
  @SuppressWarnings("MemberNameCheck")
  public boolean resetRelativeEncoders;

  @SuppressWarnings("MemberNameCheck")
  public ArcadeInputParams arcadeParams;

  /**
   * Constructor.
   */
  public DriveInputState(boolean resetRelativeEncodersInput, ArcadeInputParams arcadeParamsInput) {
    resetRelativeEncoders = resetRelativeEncodersInput;
    arcadeParams = new ArcadeInputParams(arcadeParamsInput);
  }
}
