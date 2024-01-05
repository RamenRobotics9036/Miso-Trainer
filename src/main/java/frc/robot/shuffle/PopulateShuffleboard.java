package frc.robot.shuffle;

import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import frc.robot.helpers.DefaultLayout;
import frc.robot.helpers.DefaultLayout.Widget;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Adds Shuffleboard widgets to Simulation tab.
 */
public class PopulateShuffleboard {
  private final DefaultLayout m_defaultLayout;
  PrefixedConcurrentMap<Supplier<MultiType>> m_globalMap;
  ShuffleboardTab m_tab;
  ShuffleboardHelpers m_helpers;

  /**
   * Constructor.
   */
  public PopulateShuffleboard(PrefixedConcurrentMap<Supplier<MultiType>> globalMap,
      DefaultLayout defaultLayout,
      ShuffleboardTab tab) {

    m_globalMap = globalMap;
    m_helpers = new ShuffleboardHelpers(globalMap);
    m_defaultLayout = defaultLayout;
    m_tab = tab;
  }

  /**
   * Adds Shuffleboard widgets.
   */
  public void addShuffleboardWidgets() {
    addWinchWidgets();
    addExtenderWidgets();
  }

  private void addWinchWidgets() {
    // Winch functional display
    Widget pos = m_defaultLayout.getWidgetPosition("Winch Functional");
    Shuffleboard.getTab("Simulation")
        .addBoolean("Winch Functional",
            () -> !m_helpers.getBooleanSupplier("ArmSystem/Winch/IsBroken").getAsBoolean())
        .withWidget(BuiltInWidgets.kBooleanBox)
        .withProperties(Map.of("colorWhenTrue", "#C0FBC0", "colorWhenFalse", "#8B0000"))
        .withPosition(pos.x, pos.y).withSize(pos.width, pos.height);

    // Winch motor power
    pos = m_defaultLayout.getWidgetPosition("Winch Motor Power");
    Shuffleboard.getTab("Simulation")
        .addDouble("Winch Motor Power",
            m_helpers.getDoubleSupplier("ArmSystem/WinchMotor/InputPower"))
        .withWidget(BuiltInWidgets.kNumberBar)
        .withProperties(Map.of("min", -1.0, "max", 1.0, "show text", false))
        .withPosition(pos.x, pos.y).withSize(pos.width, pos.height);
  }

  private void addExtenderWidgets() {
    // Extender motor power
    Widget pos = m_defaultLayout.getWidgetPosition("Extender Motor Power");
    Shuffleboard.getTab("Simulation")
        .addDouble("Extender Motor Power",
            m_helpers.getDoubleSupplier("ArmSystem/ExtenderMotor/InputPower"))
        .withWidget(BuiltInWidgets.kNumberBar)
        .withProperties(Map.of("min", -1.0, "max", 1.0, "show text", false))
        .withPosition(pos.x, pos.y).withSize(pos.width, pos.height);
  }
}
