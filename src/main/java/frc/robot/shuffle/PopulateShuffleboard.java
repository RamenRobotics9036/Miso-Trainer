package frc.robot.shuffle;

import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import frc.robot.helpers.DefaultLayout;
import frc.robot.helpers.DefaultLayout.Widget;
import java.util.Map;
import java.util.function.DoubleSupplier;
import java.util.function.Supplier;

/**
 * Adds Shuffleboard widgets to Simulation tab.
 */
public class PopulateShuffleboard {
  private final DefaultLayout m_defaultLayout;
  PrefixedConcurrentMap<Supplier<MultiType>> m_globalMap;

  /**
   * Constructor.
   */
  public PopulateShuffleboard() {
    m_globalMap = SupplierMapFactory.getGlobalInstance();
    m_defaultLayout = new DefaultLayout();
  }

  /**
   * Adds Shuffleboard widgets.
   */
  public void addShuffleboardWidgets() {
    // Extender motor power
    Widget pos = m_defaultLayout.getWidgetPosition("Extender Motor Power");
    // $TODO - Move this into a helper to get a Double or default 0.0. Change it so it doesnt throw
    // too
    Supplier<MultiType> extenderMotorPowerSupplier = m_globalMap
        .get("ArmSystem/ExtenderMotor/InputPower");
    DoubleSupplier supplierDouble = () -> extenderMotorPowerSupplier.get().getDouble().orElse(0.0);

    Shuffleboard.getTab("Simulation").addDouble("Extender Motor Power", supplierDouble)
        .withWidget(BuiltInWidgets.kNumberBar)
        .withProperties(Map.of("min", -1.0, "max", 1.0, "show text", false))
        .withPosition(pos.x, pos.y).withSize(pos.width, pos.height);
  }
}
