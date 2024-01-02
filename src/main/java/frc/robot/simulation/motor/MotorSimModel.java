package frc.robot.simulation.motor;

import edu.wpi.first.math.system.plant.DCMotor;
import edu.wpi.first.wpilibj.simulation.DCMotorSim;
import frc.robot.shuffle.MultiType;
import frc.robot.simulation.framework.DashboardItem;
import frc.robot.simulation.framework.SimModelInterface;

/**
 * Does the real-world simulation for the motor.
 */
public class MotorSimModel implements SimModelInterface<Double, Double> {
  private final DCMotor m_realMotorModel;
  private final DCMotorSim m_realMotorSim;
  private final double m_gearRatio;
  private final MultiType m_dashMotorRotations = MultiType.of(0.0);

  /**
   * Constructor.
   */
  public MotorSimModel(double gearRatio) {
    m_gearRatio = gearRatio;

    // Model a NEO motor (or any other motor)
    m_realMotorModel = DCMotor.getNEO(1); // 1 motor in the gearbox

    // Create the motor simulation with motor model, gear ratio, and moment of
    // inertia
    double motorMomentInertia = 0.0005;
    m_realMotorSim = new DCMotorSim(m_realMotorModel, m_gearRatio, motorMomentInertia);
  }

  /**
   * Returns parameters to display in Shuffleboard.
   */
  public DashboardItem[] getDashboardItems() {
    return new DashboardItem[] {
        new DashboardItem("Motor Rotations", () -> m_dashMotorRotations)
    };
  }

  public boolean isModelBroken() {
    // Motor doesn't break in this simulation
    return false;
  }

  /**
   * Runs 20ms simulation of the motor, and then returns the new encoder position (in Rotations).
   */
  public Double updateSimulation(Double motorPowerPercentage) {
    // Calculate the input voltage for the motor
    double inputVoltageVolts = motorPowerPercentage * 12.0;

    // Update the motor simulation
    m_realMotorSim.setInput(inputVoltageVolts);
    m_realMotorSim.update(0.02);

    // Update the Encoder based on the simulation - the units are "number of
    // rotations"
    return m_realMotorSim.getAngularPositionRotations();
  }
}
