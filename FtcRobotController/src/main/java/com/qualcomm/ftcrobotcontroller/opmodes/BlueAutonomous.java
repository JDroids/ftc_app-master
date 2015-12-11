package com.qualcomm.ftcrobotcontroller.opmodes;

import android.graphics.Color;

import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;

public class BlueAutonomous extends PushBotTelemetrySensors {

    private int state = 0;
    private int heading = 0;

    double hue;
    float hsvValues[] = {0,0,0};
    final float values[] = hsvValues;

    String firstColorDetected = "";
    String colorDetected = "";
    ColorSensor cs;
    boolean sweeperStalled= false;

    DcMotor sweeper;
    public BlueAutonomous() {

    }

    @Override
    public void start() {

        super.start();

        reset_drive_encoders();
        colorSensor = hardwareMap.colorSensor.get("color");
    }

    @Override
    public void loop() {

        if (state != 0) {
            heading = sensorGyro.getHeading();
        }

        //check if the sweeper is stalled
        if ( sweeper != null )
        {
           if ( !sweeper.isBusy()  ) //not busy means it is stalled or not moving yet
           {
               if  ( sweeperStalled = false ) {  //flag indicates not stalled, but motor is not busy - means stalled, stop the motor
                   sweeper.setMode(DcMotorController.RunMode.RESET_ENCODERS);
                   sweeper.setPower(0);
                   sweeperStalled = true;
               }
               else {  //flag is true, the motor was stalled and stopped, now its okay to run it again
                   sweeper.setMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
                   sweeper.setPower(1);
                   sweeperStalled = false;
               }
           }
        }

        switch (state) {

            case 0: // Initialize
                sweeper = hardwareMap.dcMotor.get("m5");
                sweeper.setMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
                sweeper.setPower(1);
                state++;
                break;


            case 1: // Move forward a little bit

                run_using_encoders();
                set_drive_power(-.4, -.4);

                if (has_left_drive_encoder_reached(2500)) {
                    reset_drive_encoders();
                    set_drive_power(0,0);
                    state++;
                }

                break;

            case 2: // Wait for the previous statement to finish executing...repeat after every case

                if (has_left_drive_encoder_reset())
                {
                    state++;
                }
                break;

            case 3: // Turn right

                run_using_encoders();
                set_drive_power(.2, -.2);
                state++;

                break;

            case 4:

                if (heading >= 45 && !(heading > 270 && heading <= 360)) {
                    reset_drive_encoders();
                    set_drive_power(0,0);
                    state++;
                }

                break;

            case 5:
                if (have_drive_encoders_reset())
                {
                    state++;
                }
                break;

            case 6:
                run_using_encoders();
                set_drive_power(-.5, -.5);
                state++;
                break;

            case 7:
                cs = hardwareMap.colorSensor.get("color2");
                cs.enableLed(true);
                Color.RGBToHSV((colorSensor.red() * 255) / 800, (colorSensor.green() * 255) / 800, (colorSensor.blue() * 255) / 800, hsvValues);
                if (cs.blue() >= 5) {
                    reset_drive_encoders();
                    set_drive_power(0,0);
                    state++;
                }
                break;

            /*case 8:
                run_using_encoders();
                set_drive_power(0, .2);
                state++;
                break;

            case 9:
                if (have_drive_encoders_reset())
                {
                    state++;
                }
                break;*/


            /*case 9:
                run_using_encoders();
                set_drive_power(.2, -.2);
                state++;
                break;


            case 10:
                if (heading >= 90) {
                    reset_drive_encoders();
                    set_drive_power(0,0);
                    state++;
                }

                break;

            case 11:
                if (has_left_drive_encoder_reset())
                {
                    state++;
                }
                break;

            case 12:
                run_using_encoders();
                set_drive_power(-.3, -.3);
                state++;
                break;

            case 13:
                if (has_left_drive_encoder_reached(3000)) {
                    reset_drive_encoders();
                    set_drive_power(0, 0);
                    state++;
                }

                break;

            case 14:
                if (have_drive_encoders_reset()) {
                    state++;
                }
                break;

            */

            case 8:
                if (have_drive_encoders_reset()) {
                    state++;
                }
                break;

            case 9: // Move forward a little bit

                run_using_encoders();
                set_drive_power(-.4, -.4);

                if (has_left_drive_encoder_reached(1600)) {
                    reset_drive_encoders();
                    set_drive_power(0,0);
                    state++;
                }

                break;

            case 10: // Wait for the previous statement to finish executing...repeat after every case

                if (has_left_drive_encoder_reset())
                {
                    state++;
                }
                break;

            case 11:
                run_using_encoders();
                set_drive_power(-.2, 0);
                state++;
                break;

            case 12:
                if (heading == 0 || (heading > 350 && heading < 360)) {
                    reset_drive_encoders();
                    set_drive_power(0, 0);
                    state++;
                }
                break;

            case 13:
                run_using_encoders();
                set_drive_power(-.2,-.2);
                state++;
                break;

            case 14:  //identify the first color on the beacon
                Color.RGBToHSV((colorSensor.red() * 255) / 800, (colorSensor.green() * 255) / 800, (colorSensor.blue() * 255) / 800, hsvValues);
                telemetry.addData("Hue", hsvValues[0]);
                hue = hsvValues[0];
                if ((hue >= 330 && hue <= 360) || (hue >= 220 && hue <= 240)) {
                    if (hue > 330 && hue <= 360) {
                        colorDetected = "red";
                        if (firstColorDetected.length() == 0) {
                            firstColorDetected = "red";
                        }
                    }
                    else {
                        colorDetected = "blue";
                        if (firstColorDetected.length() == 0) {
                            firstColorDetected = "blue";
                        }

                    }
                    reset_drive_encoders();
                    set_drive_power(0,0);
                    state++;
                }
                break;

            case 15:  //extend the beacon out
                if (colorDetected.equals("blue")) {
                    push_button();
                    state++;
                }
                else {
                    state = 15;
                }
                break;

            case 16:
                if (buttonWait()) {
                    state++;
                }
                break;

            case 17:
                run_using_encoders();
                set_drive_power(-.2, -.2);
                state++;
                break;

            case 18:
                Color.RGBToHSV((colorSensor.red() * 255) / 800, (colorSensor.green() * 255) / 800, (colorSensor.blue() * 255) / 800, hsvValues);
                telemetry.addData("Hue", hsvValues[0]);
                hue = hsvValues[0];
                if (firstColorDetected.equals("red")) {
                    if (!(hue >= 330 && hue <= 360)) {
                        reset_drive_encoders();
                        set_drive_power(0, 0);
                        state++;
                    }
                }
                else {
                    if (!(hue >= 220 && hue <= 240)) {
                        reset_drive_encoders();
                        set_drive_power(0, 0);
                        state++;
                    }
                }
                break;

            case 19:
                if (have_drive_encoders_reset()) {
                    state++;
                }
                break;

            case 20:
                retract_button();
                state++;
                break;

            case 21:
                if (buttonWait()) {
                    state++;
                }
                break;

            case 22:
                turn_climbers();
                state++;
                break;

            case 23:
                if (climbersWait()) {
                    state++;
                }
                break;

            case 24:
                drop_climbers();
                state++;
                break;

            case 25:
                if (dropWait()) {
                    state++;
                }
                break;

            case 26:
                retract_climbers();
                state++;
                break;

            case 27:
                if (climbersWait()) {
                    state++;
                }
                break;

            default:

                break;
        }

        telemetry.addData("State: ", state);
        telemetry.addData("Heading", heading);
        telemetry.addData("Encoder", Math.abs(a_left_encoder_count()));
        telemetry.addData("First Color Detected", firstColorDetected);
        telemetry.addData("Color Detected", colorDetected);
        telemetry.addData("CurrentColor", hsvValues[0]);

    }
}
