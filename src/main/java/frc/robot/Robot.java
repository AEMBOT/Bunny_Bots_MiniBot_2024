package frc.robot;

import edu.wpi.first.util.sendable.SendableRegistry;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.drive.MecanumDrive;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.Relay.Value;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkBase;
import com.revrobotics.CANSparkLowLevel.MotorType;
import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.math.geometry.Rotation2d;

public class Robot extends TimedRobot {

  private MecanumDrive m_robotDrive;

  public final CommandXboxController controller = new CommandXboxController(0);

  private final Relay fan = new Relay(0);
  private final AHRS navX = new AHRS(SPI.Port.kMXP); // 200Hz update rate

  CANSparkMax frontLeft = new CANSparkMax(2, MotorType.kBrushless);
  CANSparkMax rearLeft = new CANSparkMax(3, MotorType.kBrushless);
  CANSparkMax frontRight = new CANSparkMax(1, MotorType.kBrushless);
  CANSparkMax rearRight = new CANSparkMax(4, MotorType.kBrushless);

  private final CANSparkMax[] motors = {
      frontLeft,
      rearLeft,
      frontRight,
      rearRight,
  };

  @Override
  public void robotInit() {

    SendableRegistry.addChild(m_robotDrive, frontLeft);
    SendableRegistry.addChild(m_robotDrive, rearLeft);
    SendableRegistry.addChild(m_robotDrive, frontRight);
    SendableRegistry.addChild(m_robotDrive, rearRight);

    for (CANSparkMax motor : motors) {
      motor.setIdleMode(CANSparkBase.IdleMode.kBrake);
      motor.restoreFactoryDefaults();
      motor.setCANTimeout(250);
      motor.enableVoltageCompensation(12.0);
      motor.setSmartCurrentLimit(25);
      motor.burnFlash();
    }

    frontRight.setInverted(true);
    rearRight.setInverted(true);

    m_robotDrive = new MecanumDrive(frontLeft::set, rearLeft::set, frontRight::set, rearRight::set);
  }

  @Override
  public void teleopPeriodic() {
    m_robotDrive.driveCartesian(-controller.getLeftY(), controller.getLeftX(), controller.getRightX(), Rotation2d.fromDegrees(navX.getAngle()));
    if (controller.y().getAsBoolean() == Boolean.TRUE) {
      navX.reset();
    }
    if (controller.rightTrigger().getAsBoolean() == Boolean.TRUE) {
      fan.set(Value.kOn);
    } else {
      fan.set(Value.kOff);
    }
  }
}