package frc.robot.shuffle;

import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import frc.robot.helpers.DefaultLayout;
import frc.robot.helpers.DefaultLayout.Widget;
import java.util.Map;
import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;

/**
 * Adds Shuffleboard widgets to Simulation tab.
 */
public class PopulateShuffleboard {
  private final DefaultLayout m_defaultLayout;
  ShuffleboardTab m_tab;
  ShuffleboardHelpers m_helpers;

  /**
   * Constructor.
   */
  public PopulateShuffleboard(ShuffleboardHelpers helpers,
      DefaultLayout defaultLayout,
      ShuffleboardTab tab) {

    m_helpers = helpers;
    m_defaultLayout = defaultLayout;
    m_tab = tab;
  }

  /**
   * Adds Shuffleboard widgets.
   */
  public void addShuffleboardWidgets() {
    addWinchToDash();
    addExtenderToDash();
  }

  private void addWinchToDash() {
    addBooleanWidget("Winch Functional", "Winch Functional", "ArmSystem/Winch/IsBroken", true);

    addWidgetRange("Winch Motor Power",
        "Winch Motor Power",
        "ArmSystem/WinchMotor/InputPower",
        -1.0,
        1.0);
  }

  private void addExtenderToDash() {
    addBooleanWidget("Extender Functional",
        "Extender Functional",
        "ArmSystem/Extender/IsBroken",
        true);

    addWidgetRange("Extender Motor Power",
        "Extender Motor Power",
        "ArmSystem/ExtenderMotor/InputPower",
        -1.0,
        1.0);

    addWidgetRange("Extender % Extended",
        "Extender % Extended",
        "ArmSystem/Extender/PercentExtended",
        0.0,
        1.0);

    addBooleanWidget("Extender Sensor", "Extender Sensor", "ArmSystem/Extender/Sensor", false);
  }

  /**
   * Adds a widget that shows a range of -1 to 1, with a particular value selected.
   */
  private void addWidgetRange(String title,
      String layoutId,
      String dashItemKey,
      double min,
      double max) {
    DoubleSupplier supplier = m_helpers.getDoubleSupplier(dashItemKey);
    Widget pos = m_defaultLayout.getWidgetPosition(layoutId);

    m_tab.addDouble(title, supplier).withWidget(BuiltInWidgets.kNumberBar)
        .withProperties(Map.of("min", min, "max", max, "show text", false))
        .withPosition(pos.x, pos.y).withSize(pos.width, pos.height);
  }

  /**
   * Adds a boolean widget to the Shuffleboard.
   */
  private void addBooleanWidget(String title,
      String layoutId,
      String dashItemKey,
      boolean invertBoolValue) {

    BooleanSupplier supplier = invertBoolValue
        ? () -> !m_helpers.getBooleanSupplier(dashItemKey).getAsBoolean()
        : m_helpers.getBooleanSupplier(dashItemKey);

    Widget pos = m_defaultLayout.getWidgetPosition(layoutId);
    m_tab.addBoolean(title, supplier).withWidget(BuiltInWidgets.kBooleanBox)
        .withProperties(Map.of("colorWhenTrue", "#C0FBC0", "colorWhenFalse", "#8B0000"))
        .withPosition(pos.x, pos.y).withSize(pos.width, pos.height);
  }
}
