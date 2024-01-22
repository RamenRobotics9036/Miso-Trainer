package frc.robot.simulation.drive;

/**
 * Joystick movement parameters.
 */
public class ArcadeInputParams {

  @SuppressWarnings("MemberNameCheck")
  public double xspeed;

  @SuppressWarnings("MemberNameCheck")
  public double zrotation;

  @SuppressWarnings("MemberNameCheck")
  public boolean squareInputs;

  /**
   * Constructor.
   */
  public ArcadeInputParams(double xspeedInput, double zrotationInput, boolean squareInputsInput) {
    xspeed = xspeedInput;
    zrotation = zrotationInput;
    squareInputs = squareInputsInput;
  }

  /**
   * Copy constructor.
   */
  public ArcadeInputParams(ArcadeInputParams other) {
    xspeed = other.xspeed;
    zrotation = other.zrotation;
    squareInputs = other.squareInputs;
  }
}
