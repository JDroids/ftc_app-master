package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

public class JDTankDrive extends OpMode {

    // Declare all motors being used
    DcMotor motorRightFront;
    DcMotor motorLeftFront;
    DcMotor motorLeftBack;
    DcMotor motorRightBack;
    DcMotor sweeper;
    DcMotor churroWheels;

    Servo dispenseClimbers;
    Servo leftTrigger;
    Servo rightTrigger;
    Servo plough;
    Servo turnClimbers;
    Servo dropClimbers;

    // Current direction of the sweeper
    int sweeperDirection = 0;

    // Current direction of the robot
    String orientation = "FORWARD";

    public JDTankDrive() {

    }

    @Override
    public void init() {

        // Initialize motors being used
        motorRightFront = hardwareMap.dcMotor.get("m1");
        motorLeftFront = hardwareMap.dcMotor.get("m4");
        motorRightBack = hardwareMap.dcMotor.get("m2");
        motorLeftBack = hardwareMap.dcMotor.get("m3");

        // Sweeps debris out of the way
        sweeper = hardwareMap.dcMotor.get("m5");
        churroWheels = hardwareMap.dcMotor.get("m6");

        plough = hardwareMap.servo.get("s3");

        motorLeftFront.setDirection(DcMotor.Direction.REVERSE);
        motorLeftBack.setDirection(DcMotor.Direction.REVERSE);

        rightTrigger = hardwareMap.servo.get("s1");
        leftTrigger = hardwareMap.servo.get("s2");

        turnClimbers = hardwareMap.servo.get("s4");
        dropClimbers = hardwareMap.servo.get("s5");

        leftTrigger.setPosition(0);
        rightTrigger.setPosition(1);
        dropClimbers.setPosition(.5);
        turnClimbers.setPosition(1);
        plough.setPosition(.3);

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

        // Drop servo to hit trigger
        if(gamepad1.left_bumper) {
            leftTrigger.setPosition(.6);
        }

        // Drop servo to hit trigger
        if(gamepad1.right_bumper) {
            ;
            rightTrigger.setPosition(.4);
        }

        // Resets the servos used to hit the triggers
        if(gamepad1.x) {
            leftTrigger.setPosition(0);
            rightTrigger.setPosition(1);
        }
        if (gamepad1.a) {
            churroWheels.setPower(-1);
        }
        if (gamepad1.y) {
            churroWheels.setPower(.25);
        }
        if (gamepad1.b) {
            churroWheels.setPower(0);
        }

        // Powers up sweeper
        if (gamepad2.right_bumper) {
            sweeper.setPower(1);
            sweeperDirection = 0;
        }

        // Switches direction of sweeper
        if (gamepad2.left_bumper) {
            sweeper.setPower(-1);
            sweeperDirection = 1;
        }

        // Stops the sweeper
        if (gamepad2.x) {
            sweeper.setPower(0);
        }

        // Slow down the sweeper
        if (gamepad2.y) {
            if (sweeperDirection == 1)
                sweeper.setPower(-.08);
            else
                sweeper.setPower(.08);
        }

        // Drop down the plough
        if (gamepad2.dpad_down) {
            plough.setPosition(0);
        }

        // Raise the plough
        if (gamepad2.dpad_up) {
            plough.setPosition(1);
        }
        if (gamepad2.a) {

            dropClimbers.setPosition(.15);
            leftTrigger.setPosition(1);
            rightTrigger.setPosition(0);
        }

        // Resets the servo
        if (gamepad2.b) {
            dispenseClimbers.setPosition(1);
        }


        if (gamepad2.dpad_down) {
            plough.setPosition(.7);
        }
        if (gamepad2.dpad_up) {
            plough.setPosition(.5);
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
