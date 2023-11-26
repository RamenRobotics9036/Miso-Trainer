package frc.robot.simulation.armangle;

import frc.robot.Constants;
import frc.robot.helpers.UnitConversions;

/**
 * The CalcArmAngleHelper class is responsible for calculating the signed degrees
 * for a given string length. It is based on the lengths from the winch to the pivot point
 * and from the pivot point to the arm's back end.
 * 
 * <p>
 * Parts of the robot:
 * Winch - The winch is the motor that pulls the string
 * Arm - Robot arm, which rotates around the pivot point
 * Pivot Point - The pivot point is the point where the arm rotates around
 * Arm Back End - The back end of the arm, which is the end that is furthest away from the grabber
 * Arm Angle - The angle of the arm, relative to the ground
 * String - Connects winch to arm back end
 * </p>
 */
public class CalcArmAngleHelper {
  /**
   * The Result class represents the outcome of an operation that
   * includes a validity status and a numeric result.
   * This class is immutable; once an instance is created, it cannot be changed.
   */
  public class Result {
    public final boolean m_isValid;
    public final double m_value;

    public Result(boolean isValid, double value) {
      m_isValid = isValid;
      m_value = value;
    }
  }

  private double m_lengthFromWinchToPivotPoint;
  private double m_lengthFromPivotPointToArmBackEnd;

  /**
   * Constructs a new instance of CalcArmAngleHelper.
   *
   * <p>
   * The lengths from the winch to the pivot and from the pivot point to the arm's back end are
   * used for angle calculation in the helper.
   * </p>
   *
   * @param lengthFromWinchToPivotPoint      The distance from the winch to the pivot point in
   *                                         meters.
   * @param lengthFromPivotPointToArmBackEnd Distance from the pivot to the arm's back end
   *                                         in meters.
   *
   * @throws IllegalArgumentException If lengthFromWinchToPivotPoint is less than min.
   *                                  or
   *                                  lengthFromPivotPointToArmBackEnd is less than min.
   */
  public CalcArmAngleHelper(double lengthFromWinchToPivotPoint,
      double lengthFromPivotPointToArmBackEnd) {
    if (lengthFromWinchToPivotPoint < Constants.SimConstants.klengthFromWinchToPivotPoint_Min) {
      throw new IllegalArgumentException(
          "Distance from winch to arm pivot point needs to be at least "
              + Constants.SimConstants.klengthFromWinchToPivotPoint_Min + " meters");
    }

    double pivotToArmBackEndMin = Constants.SimConstants.klengthFromPivotPointToArmBackEnd_Min;
    if (lengthFromPivotPointToArmBackEnd < pivotToArmBackEndMin) {
      throw new IllegalArgumentException(
          "Length from arm pivot point to arm back end needs to be at least "
              + Constants.SimConstants.klengthFromPivotPointToArmBackEnd_Min
              + " meters, otherwise the arm cant be rotated");
    }

    m_lengthFromWinchToPivotPoint = lengthFromWinchToPivotPoint;
    m_lengthFromPivotPointToArmBackEnd = lengthFromPivotPointToArmBackEnd;
  }

  /**
   * Calculates the signed degrees for a given string length. The result is based on
   * the height of the arm backend above the pivot point, calculated from the input.
   *
   * <p>
   * If the string length implies the arm is beyond its lowest point (string is no
   * longer taut), a Result is returned indicating the arm is down.
   * </p>
   * <p>
   * If the string length implies the arm is beyond its highest point, a Result is
   * returned indicating the arm is up and the result is invalid.
   * </p>
   *
   * @param stringLen The length of the string in meters.
   * @return A Result object indicating whether the degrees are valid and the
   *         calculated degrees. If the arm is beyond its highest or lowest point,
   *         the degrees will be 90 or -90, respectively.
   */
  public Result calcSignedDegreesForStringLength(double stringLen) {
    double heightArmBackendAbovePivot = stringLen - m_lengthFromWinchToPivotPoint;
    double up = 90;
    double down = -90;

    // Is arm beyond lowest possible point?
    // If the string is too long, it means the string is no longer taut.
    // Still, we consider this a valid position of the arm; arm is dangling down
    if (UnitConversions.greaterThanButNotEqualDouble(heightArmBackendAbovePivot,
        m_lengthFromPivotPointToArmBackEnd)) {
      System.out.println("String too long, and is no longer taut");
      return new Result(true, down);
    }

    // Is arm beyond highest possible point?
    if (UnitConversions.lessThanButNotEqualDouble(heightArmBackendAbovePivot,
        -1 * m_lengthFromPivotPointToArmBackEnd)) {
      System.out.println("Above highest point: String too short!");
      return new Result(false, up);
    }

    return new Result(true, -1
        * calcAngleOnRightTriangle(m_lengthFromPivotPointToArmBackEnd, heightArmBackendAbovePivot));
  }

  // We are calculating the angle of a right triangle at point (0,0). We know the length of the
  // hypotenus, and we know it's other side is exactly H height above the x-axis
  private double calcAngleOnRightTriangle(double lenHypotenuse, double height) {
    return Math.toDegrees(Math.asin(height / lenHypotenuse));
  }

  /**
   * Given a signed angle at which the Robot arm is pointed, calculates the string length
   * from the winch to the arm's back end.
   */
  public Result calcStringLengthForSignedDegrees(double signedDegrees) {
    double maxStringLen = m_lengthFromWinchToPivotPoint + m_lengthFromPivotPointToArmBackEnd;
    double minStringLen = m_lengthFromWinchToPivotPoint - m_lengthFromPivotPointToArmBackEnd;

    // If signedDegrees is > 90, it means arm is pointing straight up.
    if (signedDegrees > 90) {
      return new Result(false, minStringLen);
    }

    // if signedDegrees is < -90, it means arm is pointing straight down.
    if (signedDegrees < -90) {
      return new Result(false, maxStringLen);
    }

    double heightArmBackendAbovePivot = -1
        * calcHeightOnRightTriangle(m_lengthFromPivotPointToArmBackEnd, signedDegrees);
    double stringLen = m_lengthFromWinchToPivotPoint + heightArmBackendAbovePivot;
    return new Result(true, stringLen);
  }

  /**
   * Given a signed angle at which the Robot arm is pointed, calculates the string length
   * from the winch to the arm's back end.
   * Also, validates the angle is valid.
   */
  public double calcAndValidateStringLengthForSignedDegrees(double signedDegrees) {
    Result result = calcStringLengthForSignedDegrees(signedDegrees);

    if (!result.m_isValid) {
      throw new IllegalArgumentException("Angle " + signedDegrees + " is invalid");
    }

    return result.m_value;
  }

  private double calcHeightOnRightTriangle(double lenHypotenuse, double signedAngle) {
    double angleRadians = Math.toRadians(signedAngle);
    return lenHypotenuse * Math.sin(angleRadians);
  }
}
