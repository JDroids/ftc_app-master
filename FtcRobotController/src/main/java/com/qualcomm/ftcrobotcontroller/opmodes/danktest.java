package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.Range;

public class danktest extends OpMode {

    // Declare all motors being used
    DcMotor motorRightFront;
    DcMotor motorLeftFront;
    DcMotor motorLeftBack;
    DcMotor motorRightBack;
    DcMotor churroWheels;
    DcMotor spinner;

    // Current direction of the sweeper
    int sweeperDirection = 0;

    // Current direction of the robot
    String orientation = "FORWARD";

    public danktest() {

    }

    @Override
    public void init() {

        // Initialize motors being used
        motorRightFront = hardwareMap.dcMotor.get("m1");
        motorLeftFront = hardwareMap.dcMotor.get("m4");
        motorRightBack = hardwareMap.dcMotor.get("m2");
        motorLeftBack = hardwareMap.dcMotor.get("m3");

        churroWheels = hardwareMap.dcMotor.get("m5");
        spinner = hardwareMap.dcMotor.get("m6");



        // Keeps debris from getting underneath the wheels

        // Reverse direction of 2 motors so that all motors will spin in the same direction
        motorLeftFront.setDirection(DcMotor.Direction.REVERSE);
        motorLeftBack.setDirection(DcMotor.Direction.REVERSE);


    }

    @Override
    public void loop() {

        // Speeds for the left wheels and the right wheels
        float leftThrottle;
        float rightThrottle;


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

        // Dispenses the climbers
        if (gamepad1.right_bumper) {
            churroWheels.setPower(1);
            sweeperDirection = 0;
        }

        // Switches direction of sweeper
        if (gamepad1.left_bumper) {
            churroWheels.setPower(-1);
            sweeperDirection = 1;
        }

        // Stops the sweeper
        if (gamepad1.x) {
            churroWheels.setPower(0);
        }

        // Slow down the sweeper
        if (gamepad1.y) {
            if (sweeperDirection == 1)
                churroWheels.setPower(-.08);
            else
                churroWheels.setPower(.08);
        }

        if (gamepad2.right_bumper) {
            spinner.setPower(1);
        }
        if (gamepad2.left_bumper) {
            spinner.setPower(-1);
        }
        if (gamepad1.y) {
            spinner.setPower(.15);
        }
        if (gamepad1.x) {
            spinner.setPower(0);
        }


        // Telemetry on state of motors
        telemetry.addData("Text", "*** Robot Data***");

        telemetry.addData("left tgt pwr", "left  pwr: " + String.format("%.2f", leftThrottle));
        telemetry.addData("right tgt pwr", "right pwr: " + String.format("%.2f", rightThrottle));
    }

    /*
     * Code to run when the op mode is first disabled goes here
     *
     * @see com.qualcomm.robotcore.eventloop.opmode.OpMode#stop()
     */
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
