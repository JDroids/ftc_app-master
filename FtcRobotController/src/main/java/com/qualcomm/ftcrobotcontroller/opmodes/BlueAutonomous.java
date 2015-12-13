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

    float hsvValues2[] = {0,0,0};
    final float values2[] = hsvValues2;

    String firstColorDetected = "";
    String colorDetected = "";
    ColorSensor cs;
    boolean sweeperStalled= false;
    int sweeperDirection = 0;

    DcMotor sweeper;
    public BlueAutonomous() {

    }

    @Override
    public void start() {

        super.start();

        reset_drive_encoders();
        resetStartTime();
        colorSensor = hardwareMap.colorSensor.get("color");
    }

    @Override
    public void loop() {

        /*if ((int)(getRuntime()+.5)%6 == 0 || (int)(getRuntime()+.5)%6 == 1) {
            sweeper.setPower(1);
        }
        else if ((int)(getRuntime()+.5)%6 == 2 || (int)(getRuntime()+.5)%6 == 3) {
            sweeper.setPower(0);
        }
        else {
            sweeper.setPower(-1);
        }

        if (getRuntime() > 28) {
            set_drive_power(0, 0);
            sweeper.setPower(0);
            state = 50;
        }*/

        if (state != 0) {
            heading = sensorGyro.getHeading();
        }
           /*if ( !sweeper.isBusy()  ) //not busy means it is stalled or not moving yet
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
           }*/

        switch (state) {

            case 0: // Initialize
                sweeper = hardwareMap.dcMotor.get("m5");
                sweeper.setMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
                sweeper.setPower(1);
                state++;
                break;


            case 1: // Move forward a little bit

                run_using_encoders();
                set_drive_power(-.7, -.7);

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
                set_drive_power(.1, -.1);
                state++;

                break;

            case 4:

                if (heading >= 48 && !(heading <= 360 && heading >= 270)) {
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
                set_drive_power(-.7, -.7);
                state++;
                break;

            case 7:
                if (has_left_drive_encoder_reached(6000)) {
                    set_drive_power(-.2,-.2);
                    state++;
                }
                break;

            case 8:
                cs = hardwareMap.colorSensor.get("color2");
                Color.RGBToHSV((cs.red() * 255) / 800, (cs.green() * 255) / 800, (cs.blue() * 255) / 800, hsvValues2);
                if (hsvValues2[0] > 340 || hsvValues2[0] < 9) {
                    set_drive_power(0,0);
                    reset_drive_encoders();
                    state++;
                }
                break;

            case 9:
                if (have_drive_encoders_reset()) {
                    state++;
                }
                break;

            case 10: // Move forward a little bit

                run_using_encoders();
                set_drive_power(-.4, -.4);

                if (has_left_drive_encoder_reached(1000)) {
                    reset_drive_encoders();
                    set_drive_power(0,0);
                    state++;
                }

                break;

            case 11: // Wait for the previous statement to finish executing...repeat after every case

                if (has_left_drive_encoder_reset())
                {
                    state++;
                }
                break;

            case 12:
                run_using_encoders();
                set_drive_power(-.3, 0);
                state++;
                break;

            case 13:
                if (heading == 0 || (heading > 350 && heading < 360)) {
                    reset_drive_encoders();
                    set_drive_power(0, 0);
                    state++;
                }
                break;

            case 14:
                run_using_encoders();
                set_drive_power(-.2,-.2);
                state++;
                break;

            case 15:  //identify the first color on the beacon
                Color.RGBToHSV((colorSensor.red() * 255) / 800, (colorSensor.green() * 255) / 800, (colorSensor.blue() * 255) / 800, hsvValues);
                telemetry.addData("Hue", hsvValues[0]);
                hue = hsvValues[0];
                if ((hue >= 330 && hue <= 360) || (hue >= 210 && hue <= 235)) {
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
                    set_drive_power(0, 0);
                    telemetry.addData("Stopped robot", "");
                    state++;
                }
                break;

            case 16:
                if (have_drive_encoders_reset()) {
                    state++;
                }
                break;

            case 17:
                run_using_encoders();
                set_drive_power(.2,.2);
                if (firstColorDetected.equals("blue")) {
                    if (has_left_drive_encoder_reached(700)) {
                        reset_drive_encoders();
                        set_drive_power(0, 0);
                        state++;
                    }
                }
                else if (firstColorDetected.equals("red")) {
                    if (has_left_drive_encoder_reached(100)) {
                        reset_drive_encoders();
                        set_drive_power(0, 0);
                        state++;
                    }
                } else { // Something is wrong

                    state++;
                }
                break;

            case 18:  //extend the pusher out
                push_button();
                state++;
                break;

            case 19:
                if (buttonWait()) {
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
                if (have_drive_encoders_reset()) {
                    state++;
                }
                break;

            /**case 22:
                run_using_encoders();
                set_drive_power(-.3, -.3);
                telemetry.addData("Driving forward for 2nd Push Button", "");
                if (has_left_drive_encoder_reached(200)) {
                    telemetry.addData("Reached 200 unit forward for 2nd Push Button", "");
                    reset_drive_encoders();
                    set_drive_power(0, 0);
                    push_button();
                    telemetry.addData("Started 2nd Push Button", "");
                    state++;
                }
                break;

            case 23:
                if (buttonWait()) {
                    telemetry.addData("Finished 2nd Push Button, starting to retract", "");
                    state++;
                }
                break;

            case 24:
                retract_button();
                state++;
                break;

            case 25:
                if (buttonWait()) {
                    telemetry.addData("Retraction of 2nd Push completed", "");
                    state++;
                }
                break;

            case 26:
                if (have_drive_encoders_reset()) {
                    state++;
                }
                break;**/

            case 23:
                telemetry.addData("Moving Forward to adjust for climber drop", "");
                run_using_encoders();
                set_drive_power(-.3,-.3);
                if (firstColorDetected.equals("blue")) {
                    if (has_left_drive_encoder_reached(1000)) {
                        reset_drive_encoders();
                        set_drive_power(0, 0);
                        state++;
                    }
                }
                else {
                    if (has_left_drive_encoder_reached(700)) {
                        reset_drive_encoders();
                        set_drive_power(0, 0);
                        state++;
                    }
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
                if (dropWait()) {
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
        telemetry.addData("First Color Detected", firstColorDetected);
        telemetry.addData("Color Detected", colorDetected);
        telemetry.addData("CurrentColor", hsvValues[0]);
        telemetry.addData("Blue found", hsvValues[0] > 230 && hsvValues[0] < 250);
        telemetry.addData("Sweeper power", sweeper.getPower());
        telemetry.addData("Runtime", getRuntime());
        telemetry.addData("Color sensor 2 hue", hsvValues2[0]);

    }
}
