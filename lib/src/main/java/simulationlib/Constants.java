package simulationlib;

/**
 * Constants for simulationlib.
 */
public class Constants {
  /**
   * Constants for the shuffleboard arm widget.
   */
  public static class WidgetConstants {
    // Arm widget for shuffleboard to load
    public static final String kAnimatedArmWidget = "AnimatedArm";
  }

  // $TODO - I wish these weren't public, but they're currently used for both
  // the unit-tests within simulationlib/simulation AND in the subsystem Sim
  // child classes. I need to figure out how to separate these two uses.
  /**
   * Constants that we specifically use in simulation mode.
   */
  public static class SimConstants {
    // Winch
    public static double kTotalStringLenMeters = 1;
    public static double kCurrentLenSpooled = 0.25;
    public static double kwinchSimGearRatio = 20.0; // 20:1

    // Extender
    public static double kTotalExtenderLenMeters = 0.75;
    public static double kInitialExtendedLen = 0.2;
    public static double kextenderSimGearRatio = 2.0; // 2:1
    public static double kcylinderDiameterMeters = 0.00155;
    public static double kextenderFullyRetractedLen = 0.05;

    // Arm
    public static double karmLengthFromEdgeToPivot = 0.25;
    public static double karmHeightFromWinchToPivotPoint = 0.75;

    public static double klengthFromWinchToPivotPoint_Min = 0.1;
    public static double klengthFromEdgeToPivot_Min = 0.1;
    public static double karmEncoderRotationsOffset = 0.56;

    public static double kdeltaRotationsBeforeBroken = .01;
    public static double kgrabberBreaksIfOpenBelowThisLimit = 0.60;

    // Grabber
    public static boolean kgrabberInitiallyOpened = false;
  }
}
