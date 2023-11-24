package frc.robot.simulation;

/**
 * Returns a pair of values for arm simulation.
 */
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
