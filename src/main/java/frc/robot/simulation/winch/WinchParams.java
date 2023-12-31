package frc.robot.simulation.winch;

import frc.robot.simulation.winch.WinchSimModel.WindingOrientation;

/**
 * Class that holds paramaters for the winch.
 */
public class WinchParams {
  @SuppressWarnings("checkstyle:MemberName")
  public double spoolDiameterMeters;

  @SuppressWarnings("checkstyle:MemberName")
  public WinchCable winchCable;

  @SuppressWarnings("checkstyle:MemberName")
  public boolean invertMotor;

  /**
   * Constructor.
   */
  public WinchParams(double spoolDiameterMetersInput,
      WinchCable winchCableInput,
      boolean invertMotorInput) {

    spoolDiameterMeters = spoolDiameterMetersInput;
    winchCable = winchCableInput;
    invertMotor = invertMotorInput;
  }
}
