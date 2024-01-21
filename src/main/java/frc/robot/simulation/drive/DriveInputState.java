package frc.robot.simulation.drive;

/**
 * Input to the drive simulation periodic function.
 */
public class DriveInputState {
  @SuppressWarnings("MemberNameCheck")
  public boolean resetRelativeEncoders;

  /**
   * Constructor.
   */
  public DriveInputState(boolean resetRelativeEncodersInput) {
    resetRelativeEncoders = resetRelativeEncodersInput;
  }
}
