package frc.robot.simulation.simplearm;

import frc.robot.simulation.simplearm.ramenarmlogic.RamenArmSimLogic;

/**
 * Return value from createArmSimulation.
 */
public class CreateArmResult {
  @SuppressWarnings("MemberName")
  public ArmSimulation armSimulation;
  @SuppressWarnings("MemberName")
  public RamenArmSimLogic ramenArmSimLogic;

  /**
   * Constructor.
   */
  public CreateArmResult(ArmSimulation armSimulation, RamenArmSimLogic ramenArmSimLogic) {
    if (armSimulation == null) {
      throw new IllegalArgumentException("armSimulation");
    }
    if (ramenArmSimLogic == null) {
      throw new IllegalArgumentException("ramenArmSimLogic");
    }

    this.armSimulation = armSimulation;
    this.ramenArmSimLogic = ramenArmSimLogic;
  }
}
