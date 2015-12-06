package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.TouchSensor;

public class JDTouchTest extends OpMode {

    DcMotor leftMotor;
    DcMotor rightMotor;
    TouchSensor touchsensor;

    boolean runOpMode = true;

    @Override
    public void init() {
        touchsensor = hardwareMap.touchSensor.get("touch_sensor");
    }

    @Override
    public void loop() {
        telemetry.addData("isPressed",String.valueOf(touchsensor.isPressed()));

    }
}