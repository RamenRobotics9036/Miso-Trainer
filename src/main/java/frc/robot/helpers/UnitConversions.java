package frc.robot.helpers;

/**
 * This class provides utility methods for performing various unit conversions.
 */
public class UnitConversions {
  public static final double kDoubleTolerance = 1E-10;
  public static final double kAngleTolerance = 1E-2;

  /**
   * Checks if two double values are equal within a specified tolerance.
   *
   * @param a The first double value to compare.
   * @param b The second double value to compare.
   * @return True if the absolute difference between the two values is less than the specified
   *         tolerance.
   */
  public static boolean areEqual(double a, double b) {
    return Math.abs(a - b) < kDoubleTolerance;
  }

  /**
   * Checks if a double value is less than or equal to another.
   *
   * @param a The first double value to compare.
   * @param b The second double value to compare.
   * @return True if a is less than or equal to b.
   */
  public static boolean lessThanOrEqualDouble(double a, double b) {
    if (areEqual(a, b)) {
      return true;
    }

    return a <= b;
  }

  /**
   * Checks if a double value is greater than or equal to another.
   *
   * @param a The first double value to compare.
   * @param b The second double value to compare.
   * @return True if a is greater than or equal to b.
   */
  public static boolean greaterThanOrEqualDouble(double a, double b) {
    if (areEqual(a, b)) {
      return true;
    }

    return a >= b;
  }

  /**
   * Checks if a double value is less than but not equal to another.
   *
   * @param a The first double value to compare.
   * @param b The second double value to compare.
   * @return True if a is less than and not equal to b.
   */
  public static boolean lessThanButNotEqualDouble(double a, double b) {
    if (areEqual(a, b)) {
      return false;
    }

    return a < b;
  }

  /**
   * Checks if a double value is greater than but not equal to another.
   *
   * @param a The first double value to compare.
   * @param b The second double value to compare.
   * @return True if a is greater than and not equal to b.
   */
  public static boolean greaterThanButNotEqualDouble(double a, double b) {
    if (areEqual(a, b)) {
      return false;
    }

    return a > b;
  }

  /**
   * Converts a given angle in degrees to signed degrees in the range (-180, 180].
   *
   * @param degrees The angle in degrees to be converted.
   * @return The converted angle in signed degrees.
   */
  public static double toSignedDegreesFromUnsignedDegrees(double degrees) {
    return clampSignedDegrees(degrees);
  }

  /**
   * Converts a given angle in signed degrees to unsigned degrees in the range [0, 360).
   *
   * @param signedDegrees The angle in signed degrees to be converted.
   */
  public static double toUnsignedDegreesFromSignedDegrees(double signedDegrees) {
    return clampUnsignedDegrees(signedDegrees);
  }

  /**
   * Converts from Rotation [0, 1] to signed degrees in the range [-180, 180].
   */
  public static double rotationToSignedDegrees(double rotation) {
    double unsignedDegrees = rotation * 360;
    return UnitConversions.toSignedDegreesFromUnsignedDegrees(unsignedDegrees);
  }

  /**
   * Converts from signed degrees in the range [-180, 180] to Rotation [0, 1].
   */
  public static double signedDegreesToRotation(double signedDegrees) {
    double unsignedDegrees = toUnsignedDegreesFromSignedDegrees(signedDegrees);
    return unsignedDegrees / 360.0;
  }

  public static double rotationToUnsignedDegrees(double rotation) {
    double signedDegrees = rotationToSignedDegrees(rotation);
    return toUnsignedDegreesFromSignedDegrees(signedDegrees);
  }

  public static double unsignedDegreesToRotation(double unsignedDegrees) {
    double signedDegrees = toSignedDegreesFromUnsignedDegrees(unsignedDegrees);
    return signedDegreesToRotation(signedDegrees);
  }

  /**
   * Ensures that the given angle is in the range [-180, 180].
   */
  public static double clampSignedDegrees(double signedDegrees) {
    double result = signedDegrees % 360;

    if (result > 180) {
      result -= 360;
    }
    else if (result < -180) {
      result += 360;
    }

    return result;
  }

  /**
   * Ensures that the given angle is in the range [0, 360).
   */
  public static double clampUnsignedDegrees(double unsignedDegrees) {
    unsignedDegrees = unsignedDegrees % 360;

    if (unsignedDegrees < 0) {
      unsignedDegrees += 360;
    }
    return unsignedDegrees;
  }

  /**
   * Ensure that the rotation is in the range [0, 1].
   */
  public static double clampRotation(double rotation) {
    double result = rotation % 1;

    if (result < 0) {
      result += 1;
    }

    return result;
  }

  /**
   * Check if signed degrees is between [-180, 180].
   */
  public static boolean isSignedDegreesValid(double signedDegrees) {
    return signedDegrees >= -180 && signedDegrees <= 180;
  }

  /**
   * Check if unisgned degrees is between [0, 360).
   */
  public static boolean isUnsignedDegreesValid(double unsignedDegrees) {
    return unsignedDegrees >= 0 && unsignedDegrees < 360;
  }

  /**
   * Check if rotation is between [0, 1).
   */
  public static boolean isRotationValid(double rotation) {
    return rotation >= 0 && rotation < 1;
  }

  /**
   * Returns true if the signed angle is between [-180, 180], known as the
   * right half plane.
   *
   * @param signedDegrees The angle in signed degrees.
   */
  public static boolean isInRightHalfPlane(double signedDegrees) {
    return signedDegrees >= -90 && signedDegrees <= 90;
  }
}
