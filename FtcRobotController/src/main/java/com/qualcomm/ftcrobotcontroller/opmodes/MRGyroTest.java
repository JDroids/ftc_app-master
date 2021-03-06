package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.GyroSensor;

public class MRGyroTest extends LinearOpMode {


  @Override
  public void runOpMode() throws InterruptedException {

    GyroSensor sensorGyro;
    int xVal, yVal, zVal = 0;
    int heading = 0;

    // write some device information (connection info, name and type)
    // to the log file.
    hardwareMap.logDevices();

    // get a reference to our GyroSensor object.
    sensorGyro = hardwareMap.gyroSensor.get("gyro");

    // calibrate the gyro.
    sensorGyro.calibrate();

    // wait for the start button to be pressed.
    waitForStart();

    // make sure the gyro is calibrated.
    while (sensorGyro.isCalibrating())  {
      Thread.sleep(50);
    }

    while (opModeIsActive())  {
      // if the A and B buttons are pressed, reset Z heading.
      if(gamepad1.a && gamepad1.b)  {
        // reset heading.
        sensorGyro.resetZAxisIntegrator();
      }

      // get the x, y, and z values (rate of change of angle).
      xVal = sensorGyro.rawX();
      yVal = sensorGyro.rawY();
      zVal = sensorGyro.rawZ();

      // get the heading info.
      // the Modern Robotics' gyro sensor keeps
      // track of the current heading for the Z axis only.
      heading = sensorGyro.getHeading();

      telemetry.addData("1. x", String.format("%03d", xVal));
      telemetry.addData("2. y", String.format("%03d", yVal));
      telemetry.addData("3. z", String.format("%03d", zVal));
      telemetry.addData("4. h", String.format("%03d", heading));

      Thread.sleep(100);
    }
  }
}