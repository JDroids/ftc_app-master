package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.GyroSensor;

public class GyroDrive extends LinearOpMode {

    GyroSensor gyro;

    DcMotor m1;
    DcMotor m2;
    DcMotor m3;
    DcMotor m4;

    public void runUsingEncoders() {
        m1.setChannelMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
        m2.setChannelMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
        m3.setChannelMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
        m4.setChannelMode(DcMotorController.RunMode.RUN_USING_ENCODERS);

    }

    public void resetEncoders() {
        m1.setChannelMode(DcMotorController.RunMode.RESET_ENCODERS);
        m2.setChannelMode(DcMotorController.RunMode.RESET_ENCODERS);
        m3.setChannelMode(DcMotorController.RunMode.RESET_ENCODERS);
        m4.setChannelMode(DcMotorController.RunMode.RESET_ENCODERS);
    }

    @Override
    public void runOpMode() throws InterruptedException {

        gyro = hardwareMap.gyroSensor.get("gyro");

        m1 = hardwareMap.dcMotor.get("m1");
        m2 = hardwareMap.dcMotor.get("m2");
        m3 = hardwareMap.dcMotor.get("m3");
        m4 = hardwareMap.dcMotor.get("m4");

        int x,y,z,heading;
        double rotation;

        gyro.calibrate();

        while(gyro.isCalibrating()) {
            Thread.sleep(50);
        }

        x = gyro.rawX();
        y = gyro.rawY();
        z = gyro.rawZ();

        heading = gyro.getHeading();
        rotation = gyro.getRotation();

        m1.setPower(.5);
        m2.setPower(.5);
        m3.setPower(.5);
        m4.setPower(.5);

        while (heading < 40) {
            
        }
    }

}