package frc.robot.simulation.extender;

import frc.robot.shuffle.MultiType;
import frc.robot.simulation.framework.DashboardItem;
import frc.robot.simulation.framework.DashboardPluginInterface;

// $TODO - This class isnt used yet!
/**
 * For Extender Sim Model, exposes the properties we show on Shuffleboard dashboard.
 */
// $TODO - Wrong types!
public class ExtenderDashboardPlugin implements DashboardPluginInterface<Integer, Integer> {

  @Override
  public DashboardItem[] queryListOfDashboardPropertiesWithInitValues() {
    /*
     * return new DashboardItem[] {
     * new DashboardItem("Accumulator", MultiType.of(0))
     * };
     */
    return null;
  }

  @Override
  // $TODO - Wrong types!
  public MultiType[] getDashboardPropertiesFromInputOutput(Integer input, Integer output) {
    /*
     * MultiType[] result = new MultiType[1];
     * 
     * result[0] = MultiType.of(output);
     * 
     * return result;
     */
    return null;
  }
}
