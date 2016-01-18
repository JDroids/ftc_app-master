package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.Range;

public class JDTestDrive extends OpMode {

    DcMotor motorRightFront;
    DcMotor motorLeftFront;
    DcMotor motorLeftBack;
    DcMotor motorRightBack;

    String orientation = "FORWARD";

    // Speeds for the left wheels and the right wheels
    float leftThrottle;
    float rightThrottle;

    public JDTestDrive() {

    }

    @Override
    public void init() {

        // Initialize motors being used
        motorRightFront = hardwareMap.dcMotor.get("m1");
        motorRightBack = hardwareMap.dcMotor.get("m2");
        motorLeftFront = hardwareMap.dcMotor.get("m4");
        motorLeftBack = hardwareMap.dcMotor.get("m3");

        motorLeftFront.setDirection(DcMotor.Direction.REVERSE);
        motorLeftBack.setDirection(DcMotor.Direction.REVERSE);

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


        // Ensure values are not greater than 1 or less than -1
        leftThrottle = Range.clip(leftThrottle, -1, 1);
        rightThrottle = Range.clip(rightThrottle, -1, 1);

        // Scale throttle values so it is easier to control the robot
        leftThrottle = (float)scaleInput(leftThrottle);
        rightThrottle = (float)scaleInput(rightThrottle);

        // Set power to motors based on position of the analog sticks
        motorRightFront.setPower(rightThrottle);
        motorRightBack.setPower(rightThrottle);
        motorLeftFront.setPower(leftThrottle);
        motorLeftBack.setPower(leftThrottle);

        // Sets the orientation of the robot so the front side is forward
        if (gamepad1.dpad_up) {
            orientation = "FORWARD";
        }

        // Sets the orientation of the robot so the back side is forward
        if (gamepad1.dpad_down) {
            orientation = "BACKWARD";
        }

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
