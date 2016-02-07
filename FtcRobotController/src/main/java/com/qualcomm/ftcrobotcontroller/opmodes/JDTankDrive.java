package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

public class JDTankDrive extends OpMode {

    DcMotor motorRight;
    DcMotor motorLeft;

    DcMotor motorTapeMeasure;
    DcMotor motorTapePivot;

    DcMotor motorWinch;
    DcMotor motorChurroWheels;

    Servo rightWheelGuard;
    Servo leftWheelGuard;

    Servo rightPusher;
    Servo leftPusher;

    Servo climberSwinger;
    Servo climberDrop;

    Servo ziplineTrigger;

    String orientation = "FORWARD";

    float leftThrottle;
    float rightThrottle;

    public JDTankDrive() {

    }

    @Override
    public void init() {

        // Initialize motors being used
        motorRight = hardwareMap.dcMotor.get("mRight");
        motorLeft = hardwareMap.dcMotor.get("mLeft");

        motorTapeMeasure = hardwareMap.dcMotor.get("mTape");
        motorWinch = hardwareMap.dcMotor.get("mWinch");

        motorTapePivot = hardwareMap.dcMotor.get("mPivot");
        motorChurroWheels = hardwareMap.dcMotor.get("mChurro");

        motorLeft.setDirection(DcMotor.Direction.REVERSE);

        leftWheelGuard = hardwareMap.servo.get("sLeftGuard");
        rightWheelGuard = hardwareMap.servo.get("sRightGuard");

        leftPusher = hardwareMap.servo.get("sLeftPusher");
        rightPusher = hardwareMap.servo.get("sRightPusher");

        climberSwinger = hardwareMap.servo.get("sSwinger");
        climberDrop = hardwareMap.servo.get("sDropper");

        ziplineTrigger = hardwareMap.servo.get("sZipline");

    }

    @Override
    public void loop() {

        // Allows the driver to alter controls of the robot depending on the orientation of the bot
        if (orientation.equals("FORWARD")) {
            leftThrottle = gamepad1.left_stick_y;
            rightThrottle = gamepad1.right_stick_y;
        }
        else {
            rightThrottle = -gamepad1.left_stick_y;
            leftThrottle = -gamepad1.right_stick_y;
        }

        leftThrottle = Range.clip(leftThrottle, -1, 1);
        rightThrottle = Range.clip(rightThrottle, -1, 1);

        leftThrottle = (float)scaleInput(leftThrottle);
        rightThrottle = (float)scaleInput(rightThrottle);

        motorRight.setPower(rightThrottle);
        motorLeft.setPower(leftThrottle);

        if (gamepad1.dpad_right) {
            orientation = "FORWARD";
        }
        if (gamepad1.dpad_left) {
            orientation = "BACKWARD";
        }
        if (gamepad1.right_bumper) {
            motorChurroWheels.setPower(-1);
        }
        if (gamepad1.left_bumper) {
            motorChurroWheels.setPower(1);
        }
        if (gamepad1.x) {
            motorChurroWheels.setPower(0);
        }


        motorTapePivot.setPower(gamepad1.right_stick_y);
        motorTapeMeasure.setPower(gamepad1.left_stick_y);

        if (gamepad2.right_bumper) {
            motorWinch.setPower(1);
        }
        if (gamepad2.left_bumper) {
            motorWinch.setPower(-1);
        }
        if (gamepad2.x) {
            motorWinch.setPower(0);
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
