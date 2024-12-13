package frc.robot;

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

  // defines the variable
  private MecanumDrive Mecanum_Drive;

  // defines the port for the xbox controller
  public final CommandXboxController controller = new CommandXboxController(0);

  // defines the really pins
  private final Relay fan = new Relay(0);

  // defines the navX location
  private final AHRS navX = new AHRS(SPI.Port.kMXP);

  // defines the can id for the motors
  CANSparkMax frontLeft = new CANSparkMax(2, MotorType.kBrushless);
  CANSparkMax rearLeft = new CANSparkMax(3, MotorType.kBrushless);
  CANSparkMax frontRight = new CANSparkMax(1, MotorType.kBrushless);
  CANSparkMax rearRight = new CANSparkMax(4, MotorType.kBrushless);

  // list of motors
  private final CANSparkMax[] motors = {
    frontLeft,
    rearLeft,
    frontRight,
    rearRight,
  };

  @Override
  public void robotInit() {
    // initialize the spark max/motors
    for (CANSparkMax motor : motors) {
      motor.setIdleMode(CANSparkBase.IdleMode.kBrake);
      motor.restoreFactoryDefaults();
      motor.setCANTimeout(250);
      motor.enableVoltageCompensation(12.0);
      motor.setSmartCurrentLimit(25);
      motor.burnFlash();
    }

    // inverts the motors
    frontRight.setInverted(true);
    rearRight.setInverted(true);

    // initialize the Mecanum Drive
    Mecanum_Drive = new MecanumDrive(frontLeft::set, rearLeft::set, frontRight::set, rearRight::set);
  }

  @Override
  public void teleopPeriodic() {
    // drive with field centric
    Mecanum_Drive.driveCartesian(-controller.getLeftY(), controller.getLeftX(), controller.getRightX() / 2, Rotation2d.fromDegrees(navX.getAngle()));
    // navX reset
    if (controller.y().getAsBoolean() == Boolean.TRUE) {
      navX.reset();
    }
    // fan on while right Trigger is held down
    if (controller.rightTrigger().getAsBoolean() == Boolean.TRUE) {
      fan.set(Value.kOn);
    }
    // fan off while right Trigger is not held down
    else {
      fan.set(Value.kOff);
    }
    // funny coasting!
    // if (controller.povUp().getAsBoolean() == Boolean.TRUE) {
    // for (CANSparkMax motor : motors) {
    // motor.setIdleMode(CANSparkBase.IdleMode.kBrake);
    // }
    // }
    // if (controller.povDown().getAsBoolean() == Boolean.TRUE) {
    // for (CANSparkMax motor : motors) {
    // motor.setIdleMode(CANSparkBase.IdleMode.kCoast);
    // }
    // }
  }
}