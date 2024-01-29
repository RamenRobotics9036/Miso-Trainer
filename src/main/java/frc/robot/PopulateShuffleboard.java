package frc.robot;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.smartdashboard.Field2d;
import frc.robot.commands.ArmExtendFully;
import frc.robot.commands.ArmToGround;
import frc.robot.commands.ArmToMiddleNodeCone;
import frc.robot.commands.RetractArmCommand;
import frc.robot.helpers.DefaultLayout;
import frc.robot.helpers.DefaultLayout.Widget;
import frc.robot.subsystems.ArmSystem;
import simulationlib.shuffle.ShuffleboardHelpers;

import java.util.Map;
import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;
import java.util.function.Supplier;

/**
 * Adds Shuffleboard widgets to Simulation tab.
 */
public class PopulateShuffleboard {
  private final DefaultLayout m_defaultLayout;
  private ShuffleboardTab m_tab;
  private ShuffleboardHelpers m_helpers;
  private final Field2d m_fieldSim = new Field2d();
  private Pose2d m_previousPose = new Pose2d(0, 0, new Rotation2d());
  private boolean m_previousPoseSet = false;
  private Supplier<Pose2d> m_poseSupplier = null;
  private boolean m_isSwerve = false;
  private boolean m_queriedSwerve = false;

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
    addArmToDash();

    if (isSwerve()) {
      addSwerveToDash();
    }
    else {
      addDriveToDash();
    }
  }

  /**
   * Called every 20ms to update Shuffleboard widgets.
   * Note that most widgets are automatically updated by the Shuffleboard
   * since we passed in lambdas to update values. This function
   * updates the remaining widgets that didn't support lambdas.
   */
  public void updateDashOnRobotPeriodic() {
    if (isSwerve()) {
      updateSwerveDrive();
    }
    else {
      updateTankDrive();
    }
  }

  private void addDriveToDash() {
    addHeadingWidget("Heading", "Heading", "DriveSystem/GyroHeadingDegrees", 90.0);

    Widget pos = m_defaultLayout.getWidgetPosition("Field");
    Shuffleboard.getTab("Simulation").add("Field", m_fieldSim).withWidget(BuiltInWidgets.kField)
        .withPosition(pos.x, pos.y).withSize(pos.width, pos.height);
  }

  /**
   * $TODO - This is wrong, simulationPeriodic is last
   * We update the dashboard LAST in our various periodic loops.
   * This way, teleOpPeriodic() runs first, then simulationPeriodic(), then
   * robotPeriodic(). Since robotPeriodic() runs last, it will display the
   * most up-to-date values each cycle.
   */
  private void updateTankDrive() {
    if (m_poseSupplier == null) {
      m_poseSupplier = m_helpers.getPoseSupplier("DriveSystem/RobotPose");
    }

    Pose2d newPose = m_poseSupplier.get();

    // Only update the pose if it has changed.
    if (!m_previousPoseSet || !newPose.equals(m_previousPose)) {

      m_fieldSim.setRobotPose(newPose);

      m_previousPose = newPose;
      m_previousPoseSet = true;
    }
  }

  private void addSwerveToDash() {
    // $TODO
    // Supplier<Pose2d> supplier = m_helpers.getPoseSupplier("SwerveSystem/RobotPose");
    // System.out.println("Pose: " + supplier.get());
  }

  private void updateSwerveDrive() {
  }

  private boolean isSwerve() {
    if (m_queriedSwerve) {
      return m_isSwerve;
    }

    // We check to see if SwerveSystem/RobotPose exists. If it does, we assume
    // that we are running swerve. Otherwise, we assume tank drive.
    try {
      m_helpers.getPoseSupplier("SwerveSystem/RobotPose");
      m_isSwerve = true;
      System.out.println("Shuffleboard display: Swerve detected");
    }
    catch (Exception e) {
      m_isSwerve = false;
      System.out.println("Shuffleboard display: Tank drive detected");
    }

    m_queriedSwerve = true;

    return m_isSwerve;
  }

  private void addArmToDash() {
    addDoubleAsTextWidget("Arm position", "Arm position", "ArmSystem/Arm/ArmPosition");

    addBooleanWidget("Arm Functional", "Arm Functional", "ArmSystem/Arm/IsBroken", true);
  }

  private void addWinchToDash() {
    addBooleanWidget("Winch Functional", "Winch Functional", "ArmSystem/Winch/IsBroken", true);

    addWidgetRange("Winch Motor Power",
        "Winch Motor Power",
        "ArmSystem/WinchMotor/InputPower",
        -1.0,
        1.0);

    addWidgetRange("Winch String % Extended",
        "Winch String % Extended",
        "ArmSystem/Winch/UnspooledPercent",
        0,
        1.0);

    addStringWidget("Winch string location",
        "Winch string location",
        "ArmSystem/Winch/WindingOrientation");
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

  private void addHeadingWidget(String title,
      String layoutId,
      String dashItemKey,
      double startingAngle) {

    DoubleSupplier supplier = m_helpers.getDoubleSupplier(dashItemKey);
    Widget pos = m_defaultLayout.getWidgetPosition(layoutId);

    m_tab.addDouble(title, supplier).withWidget(BuiltInWidgets.kGyro).withPosition(pos.x, pos.y)
        .withSize(pos.width, pos.height).withProperties(Map.of("Starting angle", startingAngle));
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

  private void addStringWidget(String title, String layoutId, String dashItemKey) {

    Supplier<String> supplier = m_helpers.getStringSupplier(dashItemKey);
    Widget pos = m_defaultLayout.getWidgetPosition(layoutId);

    m_tab.addString(title, supplier).withWidget(BuiltInWidgets.kTextView).withPosition(pos.x, pos.y)
        .withSize(pos.width, pos.height);
  }

  private void addDoubleAsTextWidget(String title, String layoutId, String dashItemKey) {

    DoubleSupplier supplier = m_helpers.getDoubleSupplier(dashItemKey);
    Widget pos = m_defaultLayout.getWidgetPosition(layoutId);

    m_tab.addDouble(title, supplier).withWidget(BuiltInWidgets.kTextView).withPosition(pos.x, pos.y)
        .withSize(pos.width, pos.height);
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
