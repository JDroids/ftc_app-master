package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.GyroSensor;

/**
 * Created by arush on 11/21/2015.
 */
public class JDGyroTest extends LinearOpMode{


    @Override
    public void runOpMode() throws InterruptedException {
        GyroSensor gyro = hardwareMap.gyroSensor.get("gyro");
        telemetry.addData("Got gyro", "");

        while (opModeIsActive()) {

            telemetry.addData("asf", "");
            gyro.calibrate();

            telemetry.addData("waiting for start", "");

            while (gyro.isCalibrating()) {
                telemetry.addData("Calibrating...", "");
                Thread.sleep(50);
            }
            telemetry.addData("Calibrated", "gyro");

            telemetry.addData("Getting heading", "");
            int heading = gyro.getHeading();

            telemetry.addData("Heading: ", heading);
            Thread.sleep(100);
        }



    }
}
