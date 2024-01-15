package frc.robot.helpers;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * DefaultLayout manages the position and size of various widgets
 * within a user interface. It maintains a mapping of widget names
 * to their respective positions and dimensions.
 * 
 * <p>
 * Each widget is represented by a {@link Widget} object which stores
 * its x and y coordinates, width and height. This mapping is stored
 * in a HashMap.
 * </p>
 *
 * <p>
 * This class is initialized with a set of predefined widget positions,
 * and a public method is provided to retrieve the position of a widget
 * by its name.
 * </p>
 *
 * <p>
 * Example usage:
 * 
 * <pre>
 * DefaultLayout layout = new DefaultLayout();
 * Widget widgetPosition = layout.getWidgetPosition("Field");
 * System.out.println("X: " + widgetPosition.getX());
 * </pre>
 * </p>
 */
public class DefaultLayout {
  /**
   * Widget position and dimensions.
   */
  public static class Widget {
    @SuppressWarnings("MemberNameCheck")
    public int x;
    @SuppressWarnings("MemberNameCheck")
    public int y;
    @SuppressWarnings("MemberNameCheck")
    public int width;
    @SuppressWarnings("MemberNameCheck")
    public int height;

    /**
     * Constructor.
     */
    public Widget(int xpos, int ypos, int width, int height) {
      this.x = xpos;
      this.y = ypos;
      this.width = width;
      this.height = height;
    }
  }

  private static final Map<String, Widget> widgetMap;

  static {
    List<AbstractMap.SimpleEntry<String, Widget>> widgets = Arrays.asList(
        new AbstractMap.SimpleEntry<>("Field", new Widget(2, 0, 5, 3)),
        new AbstractMap.SimpleEntry<>("Heading", new Widget(0, 0, 2, 2)),
        new AbstractMap.SimpleEntry<>("Arm Middle node", new Widget(10, 0, 2, 1)),
        new AbstractMap.SimpleEntry<>("Arm to ground", new Widget(10, 1, 2, 1)),
        new AbstractMap.SimpleEntry<>("Extend", new Widget(10, 2, 2, 1)),
        new AbstractMap.SimpleEntry<>("Retract extender", new Widget(10, 3, 2, 1)),
        new AbstractMap.SimpleEntry<>("Open Grabber", new Widget(10, 4, 2, 1)),
        new AbstractMap.SimpleEntry<>("Close Grabber", new Widget(10, 5, 2, 1)),
        new AbstractMap.SimpleEntry<>("Arm Functional", new Widget(0, 3, 2, 1)),
        new AbstractMap.SimpleEntry<>("Arm position", new Widget(0, 4, 2, 1)),
        new AbstractMap.SimpleEntry<>("Arm System Commands", new Widget(0, 5, 2, 1)),
        new AbstractMap.SimpleEntry<>("Winch Functional", new Widget(2, 3, 2, 1)),
        new AbstractMap.SimpleEntry<>("Winch Motor Power", new Widget(2, 4, 2, 1)),
        new AbstractMap.SimpleEntry<>("Winch String % Extended", new Widget(2, 5, 2, 1)),
        new AbstractMap.SimpleEntry<>("Winch string location", new Widget(2, 6, 2, 1)),
        new AbstractMap.SimpleEntry<>("Extender Functional", new Widget(4, 3, 2, 1)),
        new AbstractMap.SimpleEntry<>("Extender Motor Power", new Widget(4, 4, 2, 1)),
        new AbstractMap.SimpleEntry<>("Extender % Extended", new Widget(4, 5, 2, 1)),
        new AbstractMap.SimpleEntry<>("Extender Sensor", new Widget(4, 6, 2, 1)),
        new AbstractMap.SimpleEntry<>("Grabber Functional", new Widget(6, 3, 2, 1)),
        new AbstractMap.SimpleEntry<>("Grabber", new Widget(6, 4, 2, 1)),
        new AbstractMap.SimpleEntry<>("Grabber System Commands", new Widget(6, 5, 2, 1)));

    widgetMap = widgets.stream().collect(
        Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));
  }

  /**
   * Returns the position of a widget by its name.
   */
  public Widget getWidgetPosition(String widgetName) {
    Widget result = widgetMap.get(widgetName);

    if (result == null) {
      throw new IllegalArgumentException("Widget layout not found: " + widgetName);
    }

    return result;
  }
}
