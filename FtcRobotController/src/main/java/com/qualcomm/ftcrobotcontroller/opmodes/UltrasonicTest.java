package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.UltrasonicSensor;

/**
 * Created by arush on 12/24/2015.
 */
public class UltrasonicTest extends OpMode {

    UltrasonicSensor us;

    @Override
    public void init() {
        us = hardwareMap.ultrasonicSensor.get("us");
    }

    @Override
    public void loop() {
        telemetry.addData("Sensor initialized, ", "entering loop");
        telemetry.addData("Distance: ", us.getUltrasonicLevel());
    }
}
