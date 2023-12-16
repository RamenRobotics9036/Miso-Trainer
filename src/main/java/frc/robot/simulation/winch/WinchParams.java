package frc.robot.simulation.winch;

import frc.robot.simulation.winch.WinchSimModel.WindingOrientation;

/**
 * Class that holds paramaters for the winch.
 */
public class WinchParams {
  @SuppressWarnings("checkstyle:MemberName")
  public double spoolDiameterMeters;

  @SuppressWarnings("checkstyle:MemberName")
  public double totalStringLenMeters;

  @SuppressWarnings("checkstyle:MemberName")
  public double initialLenSpooled;

  @SuppressWarnings("checkstyle:MemberName")
  public WindingOrientation initialWindingOrientation;

  @SuppressWarnings("checkstyle:MemberName")
  public boolean invertMotor;

  /**
   * Constructor.
   */
  public WinchParams(double spoolDiameterMetersInput,
      double totalStringLenMetersInput,
      double initialLenSpooledInput,
      WindingOrientation initialWindingOrientationInput,
      boolean invertMotorInput) {

    spoolDiameterMeters = spoolDiameterMetersInput;
    totalStringLenMeters = totalStringLenMetersInput;
    initialLenSpooled = initialLenSpooledInput;
    initialWindingOrientation = initialWindingOrientationInput;
    invertMotor = invertMotorInput;
  }
}
