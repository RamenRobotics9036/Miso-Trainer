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

  private DoubleSupplier getDoubleSupplier(String key) {
    Supplier<MultiType> supplier = m_globalMap.get(key);

    // $TODO - What if supplier is null? What if we call getDouble and it's actually a string?
    return () -> supplier.get().getDouble().orElse(0.0);
  }

  /**
   * Adds Shuffleboard widgets.
   */
  public void addShuffleboardWidgets() {
    // Extender motor power
    Widget pos = m_defaultLayout.getWidgetPosition("Extender Motor Power");
    Shuffleboard.getTab("Simulation")
        .addDouble("Extender Motor Power", getDoubleSupplier("ArmSystem/ExtenderMotor/InputPower"))
        .withWidget(BuiltInWidgets.kNumberBar)
        .withProperties(Map.of("min", -1.0, "max", 1.0, "show text", false))
        .withPosition(pos.x, pos.y).withSize(pos.width, pos.height);
  }
}
