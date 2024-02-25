package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.GrabberSystem;

/**
 * Command to close the grabber.
 */
public class CloseGrabberCommand extends Command {
  private boolean m_finished = false;
  private GrabberSystem m_grabber;

  public CloseGrabberCommand(GrabberSystem grabber) {
    m_grabber = grabber;

    addRequirements(m_grabber);
  }

  @Override
  public void initialize() {
  }

  @Override
  public void execute() {
    m_grabber.closeGrabber();
    m_finished = true;
  }

  @Override
  public boolean isFinished() {
    return m_finished;
  }

  @Override
  public void end(boolean interrupted) {

  }
}
