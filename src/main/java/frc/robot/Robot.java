package frc.robot;

import edu.wpi.first.util.sendable.SendableRegistry;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.drive.MecanumDrive;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.Relay.Value;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkLowLevel.MotorType;

public class Robot extends TimedRobot {

  private MecanumDrive m_robotDrive;

  private final CommandXboxController controller = new CommandXboxController(0);

  private final Relay fan = new Relay(0);

  @Override
  public void robotInit() {
    CANSparkMax frontLeft = new CANSparkMax(1, MotorType.kBrushless);
    CANSparkMax rearLeft = new CANSparkMax(2, MotorType.kBrushless);
    CANSparkMax frontRight = new CANSparkMax(3, MotorType.kBrushless);
    CANSparkMax rearRight = new CANSparkMax(4, MotorType.kBrushless);

    SendableRegistry.addChild(m_robotDrive, frontLeft);
    SendableRegistry.addChild(m_robotDrive, rearLeft);
    SendableRegistry.addChild(m_robotDrive, frontRight);
    SendableRegistry.addChild(m_robotDrive, rearRight);

    frontRight.setInverted(true);
    rearRight.setInverted(true);

    m_robotDrive = new MecanumDrive(frontLeft::set, rearLeft::set, frontRight::set, rearRight::set);
    // TODO: make joystick use .getgetLeftX etc. instead of the "old joystick-only package"
    // m_stick = new Joystick(kJoystickChannel);
  }

  @Override
  public void teleopPeriodic() {
    m_robotDrive.driveCartesian(-controller.getLeftY(), -controller.getLeftX(), -controller.getRightY());
    controller.rightBumper().whileTrue(Commands.run(() -> fan.set(Value.kOn)));
    controller.rightBumper().whileFalse(Commands.run(() -> fan.set(Value.kOff)));
  }
}