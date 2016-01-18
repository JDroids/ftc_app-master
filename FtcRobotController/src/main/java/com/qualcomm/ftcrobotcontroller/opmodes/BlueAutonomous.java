package com.qualcomm.ftcrobotcontroller.opmodes;

import android.graphics.Color;

import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

public class BlueAutonomous extends PushBotTelemetrySensors {

    boolean hasWriterClosed = false;

    PrintWriter writer;
    String text;

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

    DcMotor sweeper;
    public BlueAutonomous() {

    }

    @Override
    public void start() {

        super.start();

        reset_drive_encoders();
        resetStartTime();
        colorSensor = hardwareMap.colorSensor.get("color");

        try {
            writer = new PrintWriter("/sdcard/testout.txt", "UTF-8");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        //Write some text to the file
        text = "JDroids Test Logs";

        writer.println(text);
    }

    @Override
    public void loop() {

        if (getRuntime() > 29) {
            run_using_encoders();
            set_drive_power(0,0);
            sweeper.setPower(0);
            stop();
        }

        if (state != 0) {
            heading = sensorGyro.getHeading();
            writer.println("Gyro Heading = " + heading);
        }

        switch (state) {

            case 0: //Initialize, starts sweeper?,
                sweeper = hardwareMap.dcMotor.get("m5");
                sweeper.setMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
                sweeper.setPower(1);
                state++;
                break;

            case 1: //Moves forward

                run_using_encoders();
                set_drive_power(-.5, -.5);
                writer.println("Encoder count = " + a_left_encoder_count());
                if (has_left_drive_encoder_reached(2500)) {
                    reset_drive_encoders();
                    set_drive_power(0, 0);
                    writer.println("Stopped robot, encoders reached 2500");
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

            case 4: //if ___, stops

                if (heading >= 48 && !(heading <= 360 && heading >= 270)) {
                    reset_drive_encoders();
                    set_drive_power(0,0);
                    state++;
                }
                break;

            case 5: //
                if (have_drive_encoders_reset())
                {
                    state++;
                }
                break;

            case 6: //Moves forward
                run_using_encoders();
                set_drive_power(-.5, -.5);
                state++;
                break;

            case 7: //if ____, slows
                if (has_left_drive_encoder_reached(6000)) {
                    set_drive_power(-.2,-.2);
                    writer.println("Slowed robot, encoders reached 6000");
                    state++;
                }
                break;

            case 8: //Checks color, if ___ stops
                cs = hardwareMap.colorSensor.get("color2");
                Color.RGBToHSV((cs.red() * 255) / 800, (cs.green() * 255) / 800, (cs.blue() * 255) / 800, hsvValues2);
                writer.println("Hue = " + hsvValues2[0]);
                if (hsvValues2[0] > 340 || hsvValues2[0] < 9) {
                    set_drive_power(0,0);
                    reset_drive_encoders();
                    state++;
                }
                break;

            case 9: //
                if (have_drive_encoders_reset()) {
                    state++;
                }
                break;

            case 10: // Move forward a little bit, if ____ stops

                run_using_encoders();
                set_drive_power(-.4, -.4);
                if (has_left_drive_encoder_reached(1000)) {
                    reset_drive_encoders();
                    set_drive_power(0, 0);
                    writer.println("Stopped robot, encoders reached 1000");
                    state++;
                }
                break;

            case 11: // Wait for the previous statement to finish executing...repeat after every case

                if (has_left_drive_encoder_reset())
                {
                    state++;
                }
                break;

            case 12: //Turns left
                run_using_encoders();
                set_drive_power(-.3, 0);
                state++;
                break;

            case 13://if___, stops
                if (heading == 0 || (heading > 350 && heading < 360)) {
                    reset_drive_encoders();
                    set_drive_power(0, 0);
                    state++;
                }
                break;

            case 14: //Moves forward
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

            case 17: //moves forward, if detects blue or red, stops
                run_using_encoders();
                set_drive_power(.2,.2);
                if (firstColorDetected.equals("blue")) {
                    if (has_left_drive_encoder_reached(400)) {
                        reset_drive_encoders();
                        set_drive_power(0, 0);
                        state++;
                    }
                }
                else if (firstColorDetected.equals("red")) {
                    if (has_left_drive_encoder_reached(600)) {
                        reset_drive_encoders();
                        set_drive_power(0, 0);
                        state++;
                    }
                } else { // Something is wrong

                    state++;
                }
                break;

            case 18:  //extends the pusher out
                push_button();
                state++;
                break;

            case 19: //waits, moves on after 2 seconds
                if (buttonWait()) {
                    state++;
                }
                break;

            case 20: //retracts pusher
                retract_button();
                state++;
                break;

            case 21: //waits, moves on after 2 seconds
                if (buttonWait()) {
                    state++;
                }
                break;

            case 22: //
                if (have_drive_encoders_reset()) {
                    state++;
                }
                break;

            case 23: //moves forward, after certain dist., stops
                telemetry.addData("Moving Forward to adjust for climber drop", "");
                run_using_encoders();
                set_drive_power(-.3,-.3);
                if (firstColorDetected.equals("blue")) {
                    if (has_left_drive_encoder_reached(200)) {
                        reset_drive_encoders();
                        set_drive_power(0, 0);
                        state++;
                    }
                }
                else {
                    state++;
                }
                break;

            case 24: //rotates climber arm
                turn_climbers();
                state++;
                break;

            case 25: //waits two seconds for climber arm to extend, moves on
                if (climbersWait()) {
                    state++;
                }
                break;

            case 26: //rotates climber arm
                drop_climbers();
                state++;
                break;

            case 27: //waits for two seconds for climbers to drop, moves on
                if (dropWait()) {
                    state++;
                }
                break;

            case 28: //retracts climber arm
                retract_climbers();
                state++;
                sweeper.setPower(0);
                break;

            default:
                if (!hasWriterClosed) {
                    writer.close();
                }
                hasWriterClosed = true;
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
