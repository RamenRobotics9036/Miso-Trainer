package frc.robot.shuffle;

import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import frc.robot.commands.ArmExtendFully;
import frc.robot.commands.ArmToGround;
import frc.robot.commands.ArmToMiddleNodeCone;
import frc.robot.commands.RetractArmCommand;
import frc.robot.helpers.DefaultLayout;
import frc.robot.helpers.DefaultLayout.Widget;
import frc.robot.subsystems.ArmSystem;

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

    addSwitchDisplay("Extender Sensor", "Extender Sensor", "ArmSystem/Extender/Sensor");
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

  private BooleanSupplier constructSupplier(BooleanSupplier supplier, boolean invertBoolValue) {
    return invertBoolValue ? () -> !supplier.getAsBoolean() : supplier;
  }

  /**
   * Adds a boolean widget to the Shuffleboard.
   */
  private void addBooleanWidget(String title,
      String layoutId,
      String dashItemKey,
      boolean invertBoolValue) {

    BooleanSupplier supplier = constructSupplier(m_helpers.getBooleanSupplier(dashItemKey),
        invertBoolValue);

    Widget pos = m_defaultLayout.getWidgetPosition(layoutId);
    m_tab.addBoolean(title, supplier).withWidget(BuiltInWidgets.kBooleanBox)
        .withProperties(Map.of("colorWhenTrue", "#C0FBC0", "colorWhenFalse", "#8B0000"))
        .withPosition(pos.x, pos.y).withSize(pos.width, pos.height);
  }

  private void addSwitchDisplay(String title, String layoutId, String dashItemKey) {

    BooleanSupplier supplier = constructSupplier(m_helpers.getBooleanSupplier(dashItemKey), false);

    Widget pos = m_defaultLayout.getWidgetPosition(layoutId);
    Shuffleboard.getTab("Simulation").addBoolean("Extender Sensor", supplier)
        .withWidget(BuiltInWidgets.kBooleanBox)
        .withProperties(Map.of("colorWhenTrue", "#C0FBC0", "colorWhenFalse", "#FFFFFF"))
        .withPosition(pos.x, pos.y).withSize(pos.width, pos.height);
  }

  /**
   * Adds macro buttons to Shuffleboard.
   */
  public void addMacros(ArmSystem armSystem) {
    // Move to to middle node cone
    Widget pos = m_defaultLayout.getWidgetPosition("Arm Middle node");
    Shuffleboard.getTab("Simulation").add("Arm Middle node", new ArmToMiddleNodeCone(armSystem))
        .withWidget(BuiltInWidgets.kCommand).withPosition(pos.x, pos.y)
        .withSize(pos.width, pos.height);

    // Lower arm to ground
    pos = m_defaultLayout.getWidgetPosition("Arm to ground");
    Shuffleboard.getTab("Simulation").add("Arm to ground", new ArmToGround(armSystem))
        .withWidget(BuiltInWidgets.kCommand).withPosition(pos.x, pos.y)
        .withSize(pos.width, pos.height);

    // Extend arm
    pos = m_defaultLayout.getWidgetPosition("Extend");
    Shuffleboard.getTab("Simulation").add("Extend", new ArmExtendFully(armSystem))
        .withWidget(BuiltInWidgets.kCommand).withPosition(pos.x, pos.y)
        .withSize(pos.width, pos.height);

    // Retract extender
    pos = m_defaultLayout.getWidgetPosition("Retract extender");
    Shuffleboard.getTab("Simulation").add("Retract extender", new RetractArmCommand(armSystem))
        .withWidget(BuiltInWidgets.kCommand).withPosition(pos.x, pos.y)
        .withSize(pos.width, pos.height);

    // Show the current running command, and the default command for the arm
    pos = m_defaultLayout.getWidgetPosition("Arm System Commands");
    Shuffleboard.getTab("Simulation").add("Arm System Commands", armSystem)
        .withPosition(pos.x, pos.y).withSize(pos.width, pos.height);
  }
}
