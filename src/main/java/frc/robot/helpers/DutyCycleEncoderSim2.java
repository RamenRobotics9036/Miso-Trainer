package frc.robot.helpers;

import edu.wpi.first.hal.SimDouble;
import edu.wpi.first.wpilibj.DutyCycleEncoder;
import edu.wpi.first.wpilibj.simulation.DutyCycleEncoderSim;
import edu.wpi.first.wpilibj.simulation.SimDeviceSim;

/**
 * DutyCycleEncoderSim class sets the "position" as a double, so that the base DutyCycleEncoder
 * class can read it. Unfortunately, it doesn't set the "absPosition" as a double too, even though
 * some Robot code accesses the absolute position by calling
 * DutyCycleEncoder->getAbsolutePosition(). So this wrapper adds that functionality
 */
public class DutyCycleEncoderSim2 extends DutyCycleEncoderSim {
  private final SimDouble m_simAbsolutePosition;

  /**
   * Constructor.
   */
  // $TODO - Seriously, do I still need this?
  public DutyCycleEncoderSim2(DutyCycleEncoder encoder) {
    // FIRST, we call superclass
    super(encoder);

    SimDeviceSim wrappedSimDevice = new SimDeviceSim(
        "DutyCycle:DutyCycleEncoder" + "[" + encoder.getSourceChannel() + "]");
    m_simAbsolutePosition = wrappedSimDevice.getDouble("absPosition");
  }

  @Override
  public void set(double turns) {
    super.set(turns);

    m_simAbsolutePosition.set(turns);
  }
}
