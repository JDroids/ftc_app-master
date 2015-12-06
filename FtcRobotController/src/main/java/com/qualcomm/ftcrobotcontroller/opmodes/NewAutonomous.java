package com.qualcomm.ftcrobotcontroller.opmodes;

import android.graphics.Color;

import com.qualcomm.robotcore.hardware.DcMotor;

public class NewAutonomous extends PushBotTelemetrySensors {

    private int state = 0;
    private int heading = 0;

    double hue;
    float hsvValues[] = {0,0,0};
    final float values[] = hsvValues;

    String colorDetected = "";

    public NewAutonomous() {

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

        switch (state) {

            case 0: // Initialize
                //sweeper = hardwareMap.dcMotor.get("m5");
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
                set_drive_power(.4, -.4);
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
                if (has_left_drive_encoder_reached(9400)) {
                    reset_drive_encoders();
                    set_drive_power(0,0);
                    state++;
                }
                break;

            case 8:

                if (have_drive_encoders_reset())
                {
                    state++;
                }
                break;


            case 9:
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

            case 15:
                run_using_encoders();
                set_drive_power(-.2, .2);
                state++;
                break;

            case 16:
                if (heading == 0 || (heading > 350 && heading < 360)) {
                    reset_drive_encoders();
                    set_drive_power(0, 0);
                    state++;
                }
                break;

            case 17:
                run_using_encoders();
                set_drive_power(-.2,-.2);
                state++;
                break;

            case 18:
                Color.RGBToHSV((colorSensor.red() * 255) / 800, (colorSensor.green() * 255) / 800, (colorSensor.blue() * 255) / 800, hsvValues);
                telemetry.addData("Hue", hsvValues[0]);
                hue = hsvValues[0];
                if ((hue >= 330 && hue <= 360) || (hue >= 220 && hue <= 240)) {
                    if (hue > 330 && hue <= 360) {
                        colorDetected = "red";
                    }
                    else {
                        colorDetected = "blue";
                    }
                    reset_drive_encoders();
                    set_drive_power(0,0);
                    state++;
                }
                break;

            case 19:
                push_button();
                state++;
                break;

            case 20:
                if (climbersWait()) {
                    state++;
                }
                set_drive_power(-.2, -.2);
                break;

            case 21:
                if (colorDetected.equals("red")) {
                    if (hue >= 220 && hue <= 240) {
                        set_drive_power(0,0);
                    }
                }
                else {
                    if (hue >= 330 && hue <= 360) {
                        set_drive_power(0,0);
                    }
                }

            case 22:
                retract_button();
                state++;
                break;

            case 23:
                if (climbersWait()) {
                    state++;
                }
                break;

            case 24:
                turn_climbers();
                state++;
                break;

            case 25:
                if (climbersWait()) {
                    state++;
                }
                break;

            case 26:
                drop_climbers();
                state++;
                break;

            case 27:
                if (climbersWait()) {
                    state++;
                }
                break;

            case 28:
                retract_climbers();
                state++;
                break;

            case 29:
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

    }
}
