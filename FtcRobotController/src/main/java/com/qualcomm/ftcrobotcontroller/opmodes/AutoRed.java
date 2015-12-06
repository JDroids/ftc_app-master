/* Copyright (c) 2015 Qualcomm Technologies Inc

All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted (subject to the limitations in the disclaimer below) provided that
the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list
of conditions and the following disclaimer.

Redistributions in binary form must reproduce the above copyright notice, this
list of conditions and the following disclaimer in the documentation and/or
other materials provided with the distribution.

Neither the name of Qualcomm Technologies Inc nor the names of its contributors
may be used to endorse or promote products derived from this software without
specific prior written permission.

NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. */

package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.GyroSensor;
import com.qualcomm.robotcore.hardware.OpticalDistanceSensor;
import com.qualcomm.robotcore.hardware.Servo;

public class AutoRed extends LinearOpMode {

    // Declare all motors and sensors being used

    DcMotor m1; // Right-front motor
    DcMotor m2; // Right-back motor
    DcMotor m3; // Left-back motor
    DcMotor m4; // Left-front motor

    Servo dropClimbers;

    OpticalDistanceSensor ods; // Used to stop robot if on collision course
    OpticalDistanceSensor ods2;

    ColorSensor colorSensor; // Used to position the robot to dispense the climbers

    boolean colorDected = false;

    // Holds values of color sensor
    int red;
    int blue;

    public void runUsingEncoders() {
        m1.setChannelMode( DcMotorController.RunMode.RUN_USING_ENCODERS);
        m2.setChannelMode( DcMotorController.RunMode.RUN_USING_ENCODERS);
        m3.setChannelMode( DcMotorController.RunMode.RUN_USING_ENCODERS);
        m4.setChannelMode( DcMotorController.RunMode.RUN_USING_ENCODERS);
    }

    public void resetEncoders() {
        m1.setChannelMode(DcMotorController.RunMode.RESET_ENCODERS);
        m2.setChannelMode(DcMotorController.RunMode.RESET_ENCODERS);
        m3.setChannelMode(DcMotorController.RunMode.RESET_ENCODERS);
        m4.setChannelMode(DcMotorController.RunMode.RESET_ENCODERS);
    }

    // Provide desired power to the motors
    public void setDrivePower(double left, double right) {

        m1.setPower(right);
        m2.setPower(right);
        m3.setPower(left);
        m4.setPower(left);
    }

    @Override
    public void runOpMode() throws InterruptedException {

        // Initialize all motors and sensors
        m1 = hardwareMap.dcMotor.get("m1");
        m2 = hardwareMap.dcMotor.get("m2");
        m3 = hardwareMap.dcMotor.get("m3");
        m4 = hardwareMap.dcMotor.get("m4");

        dropClimbers = hardwareMap.servo.get("s1");

        m3.setDirection(DcMotor.Direction.REVERSE);
        m4.setDirection(DcMotor.Direction.REVERSE);

        ods = hardwareMap.opticalDistanceSensor.get("ODS");
        ods2 = hardwareMap.opticalDistanceSensor.get("ODS2");
        colorSensor = hardwareMap.colorSensor.get("CS");


        // Declare gyroscope
        GyroSensor sensorGyro;

        // Declare variables that will hold values received from gyroscope
        int xVal, yVal, zVal;
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

        while (opModeIsActive()) {

            // Get values from gyro
            xVal = sensorGyro.rawX();
            yVal = sensorGyro.rawY();
            zVal = sensorGyro.rawZ();

            // Print gyro values to the driver station
            telemetry.addData("1. x", String.format("%03d", xVal));
            telemetry.addData("2. y", String.format("%03d", yVal));
            telemetry.addData("3. z", String.format("%03d", zVal));
            telemetry.addData("4. h", String.format("%03d", heading));

            // Wait a bit
            Thread.sleep(50);

            // Reset motor encoders
            resetEncoders();

            // Run motors using encoder values
           runUsingEncoders();

            // Move forward to line up dispensing mechanism with basket behind beacon
            while (Math.abs (m1.getCurrentPosition ()) < 800) {
                setDrivePower(-.4, -.4);
            }

            // Turn the robot right by 35 degrees
            while (heading < 35) {

                // Keep the power low to ensure the robot does not turn past 35 degrees
                setDrivePower(-.4, .4);

                // Update the angle turned
                heading = sensorGyro.getHeading();
            }

            //Stop robot
            setDrivePower(0, 0);

            while (ods.getLightDetectedRaw() < 12) {

                // Move the robot forward until it reaches a barrier
                setDrivePower(-.4, -.4);

                // Update the angle of the robot
                heading = sensorGyro.getHeading();
            }

            // Stop robot
            setDrivePower(0,0);

            while (heading > 0) {

                // Turn the robot so that it is parallel to the beacon
                setDrivePower(-.4, .4);

                // Update angle turned
                heading = sensorGyro.getHeading();
            }

            // Stop robot
            setDrivePower(0,0);

            resetEncoders();
            runUsingEncoders();
            while (red < 1 && blue < 1 && m1.getCurrentPosition() < 10000) {
                // Keep going straight until either the blue or red light is detected from the beacon
                setDrivePower(-.1, -.1);
                red = colorSensor.red();
                blue = colorSensor.blue();
                if (red > 0 || blue > 0) {
                    colorDected = true;
                }
            }

            // Stop the robot
            setDrivePower(0, 0);

            // Reset motor encoders
            resetEncoders();

            // Run motors using encoder values
            runUsingEncoders();

            // Move forward to line up dispensing mechanism with basket behind beacon
            while (Math.abs (m1.getCurrentPosition ()) < 500 && colorDected == true) {
                setDrivePower(-.4, -.4);
            }

            while (heading < 90 && colorDected == true) {

                // Turn 90 degrees to face the beacon
                setDrivePower(-.4, .4);

                // Update angle turned
                heading = sensorGyro.getHeading();
            }

            // Stop robot
            setDrivePower(0, 0);

            while (ods.getLightDetectedRaw() < 30 && colorDected == true) {

                // Move as close to the beacon as possible
                setDrivePower(.1, .1);
            }

            setDrivePower(0, 0);

            // Rotate servo to drop climbers
            if (colorDected) {
                dropClimbers.setPosition(1);
            }

            // Reset servo position
            if (colorDected == true) {
                dropClimbers.setPosition(0);
            }

        }
    }
}
