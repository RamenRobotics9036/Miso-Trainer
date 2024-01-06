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
    addBooleanWidget("Winch Functional", "Winch Functional", "ArmSystem/Winch/IsBroken", true);

    addWidgetNegative1To1("Winch Motor Power",
        "Winch Motor Power",
        "ArmSystem/WinchMotor/InputPower");

    addWidgetNegative1To1("Extender Motor Power",
        "Extender Motor Power",
        "ArmSystem/ExtenderMotor/InputPower");
  }

  /**
   * Adds a widget that shows a range of -1 to 1, with a particular value selected.
   */
  private void addWidgetNegative1To1(String title, String layoutId, String dashItemKey) {
    DoubleSupplier supplier = m_helpers.getDoubleSupplier(dashItemKey);
    Widget pos = m_defaultLayout.getWidgetPosition(layoutId);

    m_tab.addDouble(title, supplier).withWidget(BuiltInWidgets.kNumberBar)
        .withProperties(Map.of("min", -1.0, "max", 1.0, "show text", false))
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
