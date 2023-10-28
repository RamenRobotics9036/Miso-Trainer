# Miso Robot Simulation
[Ramen Robotics 9036 Homepage](https://ramenrobotics9036.com/what-we-do/)

**Ramen Robotics** is the [FIRST](https://www.firstinspires.org/robotics/frc) Robotics team from Big Picture school in Bellevue, WA.  2022 was our Rookie year, and the programming team quickly ran into a problem while working on our robot, named *Miso*:

“We only have one physical Robot, and there’s limited time per week that the programming team gets to test their code on the Robot.  So how do we help *everyone* on the programming team learn and get a chance to test and debug their code when our allocated time flies by too quickly?”

After our 1st season ended, we created the **Miso Robot Simulation** - a full simulation of our 2022 Miso Robot!
![](images/Demo%20Screenshot.PNG)


The simulation features:
* Simulation of each of our components, so that programmers on the team can test the code logic of their new features in simulation.
* Simulation tab in Shuffleboard that shows not only the robot position on the field using a standard widget, but also shows a **custom widget** we built to visualize and animate the robot arm.
* The simulation simulates real-world aspects of the robot: E.g. if the arm is lowered to the ground without first properly retracting the arm from it’s full length, then the arm will show as **broken** in the Simulation Tab.  This was a real bug our Miso robot had in the 2022 competition!
* **…This is a great opportunity for all FIRST programmers to try to fix the Miso robot software, so that the robot arm can’t be broken**! 


## How to install Miso Robot Simulation
1. We assume you have the [WPILib libraries and FIRST Robotics version of VS Code](https://docs.wpilib.org/en/stable/docs/zero-to-robot/step-2/index.html) already installed
2. Clone the [Miso-Trainer Github repository](https://github.com/RamenRobotics9036/Miso-Trainer) to your laptop/computer
3. Install the custom Robot arm Shuffleboard widget:
   * Download [ArmWidget.jar](https://github.com/RamenRobotics9036/ArmWidget/releases/tag/V1) from our [ArmWidget Github repository](https://github.com/RamenRobotics9036/ArmWidget)
   * For Windows, copy the file to this Windows directory:
     `<Your home directory>`\Shuffleboard\plugins

4. Launch Shuffleboard app, and select File->Plugins
5. Ensure that the ArmWidget plugin is shown, and there’s a checkmark indicating it’s loaded:
   ![](images/Arm%20Plugin.png)<!-- {"width":466} -->

## Running the simulation
1. Open the Miso-Trainer project in VS Code
2. Open the Command Palette (`Control+Shift+P`), and select “WPILib: Simulate Robot Code”
3. When prompted, select the default of “Sim GUI”
4. Launch Shuffleboard
5. In Shuffleboard, select Simulation tab
6. In the Simulation GUI

Now that everything is running, go back to the Robot Simulation GUI (launched in step 3), and set the robot to “Teleoperated”.  At this point the buttons on the Shuffleboard Simulation tab will let you raise/lower the arm, and the robot can be driven around using a joystick.

Feel free to also set the Robot to “Autonomous” mode, that works too.


Have fun!  We hope that the rookie (and expert) programmers in all FIRST Robotics teams are able to use this robot simulation to get more time practicing their programming skills!
