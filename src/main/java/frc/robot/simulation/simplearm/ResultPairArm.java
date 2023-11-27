package frc.robot.simulation.simplearm;

/**
 * Returns a pair of values for arm simulation.
 */
// $TODO - Remove this file, use a Pair instead
public class ResultPairArm {
  @SuppressWarnings("checkstyle:MemberName")
  public boolean isValid;
  @SuppressWarnings("checkstyle:MemberName")
  public double value;

  public ResultPairArm(boolean isValidInput, double valueInput) {
    isValid = isValidInput;
    value = valueInput;
  }
}
