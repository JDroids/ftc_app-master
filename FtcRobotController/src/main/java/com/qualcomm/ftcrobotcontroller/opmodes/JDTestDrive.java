package com.qualcomm.ftcrobotcontroller.opmodes;

import android.provider.Telephony;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.Range;

public class JDTestDrive extends OpMode {

    DcMotor motorRight;
    DcMotor motorLeft;
    DcMotor churroWheels;
    DcMotor motorTapeMeasure;
    DcMotor motorTapePivot;

    int power = 0;

    String orientation = "FORWARD";

    // Speeds for the left wheels and the right wheels
    float leftThrottle;
    float rightThrottle;

    public JDTestDrive() {

    }

    @Override
    public void init() {

        // Initialize motors being used
        motorRight = hardwareMap.dcMotor.get("m2");
        motorLeft = hardwareMap.dcMotor.get("m1");
        motorTapeMeasure = hardwareMap.dcMotor.get("m4");
        motorTapePivot = hardwareMap.dcMotor.get("m5");

        churroWheels = hardwareMap.dcMotor.get("m3");

        motorLeft.setDirection(DcMotor.Direction.REVERSE);

    }

    @Override
    public void loop() {

        // Allows the driver to alter controls of the robot depending on the orientation of the bot
        if (orientation.equals("FORWARD")) {
            leftThrottle = -gamepad1.left_stick_y;
            rightThrottle = -gamepad1.right_stick_y;
        }
        else {
            rightThrottle = gamepad1.left_stick_y;
            leftThrottle = gamepad1.right_stick_y;
        }

        // Set churro wheels power to .5
        if (gamepad1.dpad_up) {
            churroWheels.setPower(.5);
        }
        // Set churro wheels to power -.5
        if (gamepad1.dpad_down) {
            churroWheels.setPower(-.5);
            // Set churro wheels to power 0 (stop)
        }
        if (gamepad1.dpad_right) {
            churroWheels.setPower(0);
        }
        // Swap orientation of robot so the front becomes the back
        if (gamepad1.dpad_left) {
            orientation = "BACKWARD";
        }
        // Set motorTapeMeasure to power -.5

        if (gamepad1.a){
            motorTapeMeasure.setPower(-.5);
        }
        // Set motorTapeMeasure to power 0 (stop)

        if (gamepad1.b) {
            motorTapeMeasure.setPower(.5);
        }

        if (gamepad1.x) {
            motorTapeMeasure.setPower(0);
        }

        motorTapePivot.setPower(gamepad1.right_trigger);
        motorTapePivot.setPower(-gamepad1.left_trigger);

        if (gamepad1.left_bumper) {
            motorTapePivot.setPower(.5);
        }

        // Ensure values are not greater than 1 or less than -1
        leftThrottle = Range.clip(leftThrottle, -1, 1);
        rightThrottle = Range.clip(rightThrottle, -1, 1);

        // Scale throttle values so it is easier to control the robot
        leftThrottle = (float)scaleInput(leftThrottle);
        rightThrottle = (float)scaleInput(rightThrottle);

        // Set power to motors based on position of the analog sticks
        motorRight.setPower(rightThrottle);
        motorLeft.setPower(leftThrottle);

        telemetry.addData("m5", motorTapePivot.getDeviceName());

    }

    @Override
    public void stop() {

    }

    double scaleInput(double dVal)  {
        double[] scaleArray = { 0.0, 0.05, 0.09, 0.10, 0.12, 0.15, 0.18, 0.24,
                0.30, 0.36, 0.43, 0.50, 0.60, 0.72, 0.85, 1.00, 1.00 };

        // get the corresponding index for the scaleInput array.
        int index = (int) (dVal * 16.0);

        // index should be positive.
        if (index < 0) {
            index = -index;
        }

        // index cannot exceed size of array minus 1.
        if (index > 16) {
            index = 16;
        }

        // get value from the array.
        double dScale = 0.0;
        if (dVal < 0) {
            dScale = -scaleArray[index];
        } else {
            dScale = scaleArray[index];
        }

        // return scaled value.
        return dScale;
    }

}
