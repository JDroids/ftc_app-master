package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.ftccommon.DbgLog;
import com.qualcomm.robotcore.hardware.OpticalDistanceSensor;


public class BlueAutonomousWhite extends PushBotTelemetrySensors {

    private int state = 0;
    OpticalDistanceSensor ods;
    OpticalDistanceSensor ods2;

    public BlueAutonomousWhite() {

    }

    @Override
    public void start() {

        super.start();

        reset_drive_encoders();
        resetStartTime();

        ods = hardwareMap.opticalDistanceSensor.get("ods");
        ods2 = hardwareMap.opticalDistanceSensor.get("ods2");
        ods.enableLed(true);
        ods2.enableLed(true);

    }
    @Override
    public void loop() {


        if (getRuntime() >= 30) {
            run_using_encoders();
            set_drive_power(0, 0);
            stop();
        }

        DbgLog.msg("Current state: " + state);
        DbgLog.msg("ODS Value: " + ods2.getLightDetectedRaw());

        switch (state) {

            case 0:
                state++;
                break;

            case 1: //Moves forward, stops after certain distance

                run_using_encoders();
                set_drive_power(-.05, -.05);
                state++;
                break;

            case 2: //Checks color, if not recognized (if white) stops
                if (ods.getLightDetectedRaw() > 100 && ods.getLightDetectedRaw() < 125) {
                    set_drive_power(0,0);
                    reset_drive_encoders();
                    state++;
                }
                break;

            case 3: // Wait for the previous statement to finish executing...repeat after every case

                if (has_left_drive_encoder_reset())
                {
                    state++;
                }
                break;

            case 4:
                run_using_encoders();
                set_drive_power(.2, -.2);
                state++;
                break;

            case 5:
                if (ods2.getLightDetectedRaw() > 100 && ods2.getLightDetectedRaw() < 125) {
                    set_drive_power(0,0);
                    reset_drive_encoders();
                    state++;
                }
                break;

            default:
                set_drive_power(0,0);
                break;
        }

        telemetry.addData("State: ", state);
        telemetry.addData("Encoder", Math.abs(a_left_encoder_count()));
        telemetry.addData("ODS", ods.getLightDetectedRaw());

    }
}
