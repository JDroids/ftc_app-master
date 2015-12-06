package com.qualcomm.ftcrobotcontroller.opmodes;

//------------------------------------------------------------------------------
//
// PushBotAuto
//

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.GyroSensor;

/**
 * Provide a basic autonomous operational mode that uses the left and right
 * drive motors and associated encoders implemented using a state machine for
 * the Push Bot.
 *
 * @author SSI Robotics
 * @version 2015-08-01-06-01
 */
public class Autonomous extends PushBotTelemetrySensors {
    //--------------------------------------------------------------------------
    //
    // PushBotAuto
    //

    /**
     * This class member remembers which state is currently active.  When the
     * start method is called, the state will be initialized (0).  When the loop
     * starts, the state will change from initialize to state_1.  When state_1
     * actions are complete, the state will change to state_2.  This implements
     * a state machine for the loop method.
     */
    private int v_state = 0;


    /**
     * Construct the class.
     * <p/>
     * The system calls this member when the class is instantiated.
     */

    // DcMotor sweeper;
    GyroSensor sensorGyro;
    int heading = 0;

    public Autonomous() {
        //
        // Initialize base classes.
        //
        // All via self-construction.

        //
        // Initialize class members.
        //
        // All via self-construction.

    } // PushBotAuto


    /**
     * Perform any actions that are necessary when the OpMode is enabled.
     * <p/>
     * The system calls this member once when the OpMode is enabled.
     */


    @Override
    public void start() {

        telemetry.addData("Initializing ...", "In start()");

        super.start();
        reset_drive_encoders();



    } // start

    //--------------------------------------------------------------------------
    //
    // loop
    //

    /**
     * Implement a state machine that controls the robot during auto-operation.
     * The state machine uses a class member and encoder input to transition
     * between states.
     * <p/>
     * The system calls this member repeatedly while the OpMode is running.
     */


    @Override
    public void loop() {

        // double value = ods.getLightDetectedRaw();
        telemetry.addData("pre-state", "");
        double value = 0;
        if (sensorGyro == null) {
            sensorGyro = hardwareMap.gyroSensor.get("gyro");
        }
        telemetry.addData("Gyro initialized", "");

        /*int xVal = sensorGyro.rawX();
        int yVal = sensorGyro.rawY();
        int zVal = sensorGyro.rawZ();*/

        // get the heading info.
        // the Modern Robotics' gyro sensor keeps
        // track of the current heading for the Z axis only.

        if (v_state != 0) {
            heading = sensorGyro.getHeading();
        }

        telemetry.addData("Entering switch case next", "");

        switch (v_state) {

            case 0: //first step, initialize here

                sensorGyro.calibrate();
                while (sensorGyro.isCalibrating())  {

                    telemetry.addData("Calibrating Gyro (In loop)","");
                    try {
                        Thread.sleep(50);
                    }
                    catch (Exception e) {

                    }
                }

                telemetry.addData("Gyro Calibrated successfully","");


                // sweeper = hardwareMap.dcMotor.get("m5");
                // sweeper.setPower(-1);
                reset_drive_encoders();
                v_state++;
                break;

            case 1:
                telemetry.addData("Starting to turn 45 degree ...","");
                telemetry.addData("Current heading = ",heading);

                run_using_encoders();
                set_drive_power(.4, -.4);
                v_state++;
                break;

            case 2:
                telemetry.addData("Current heading = ",heading);
                if (heading >= 45) {

                    telemetry.addData("Turn Completed, Current heading = ",heading);

                    reset_drive_encoders();
                    set_drive_power(0,0);
                    // v_state++;
                    v_state = 15; //stop
                }
                /* else {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } */
                break;

            case 3: // Move forward until ods detects
                run_using_encoders();
                set_drive_power(-.4, -.4);
                if (value > 12) {
                    reset_drive_encoders();
                    set_drive_power(0,0);
                    v_state++;
                }

                break;

            case 4: // Wait for the previous statement to finish executing...repeat after every case
                if (has_left_drive_encoder_reset())
                {
                    v_state++;
                }
                break;

            case 5: // Turn left
                run_using_encoders();
                set_drive_power(-.4, .4);
                if (have_drive_encoders_reached(800,800)) {
                    reset_drive_encoders();
                    set_drive_power(0,0);
                    v_state++;
                }

                break;

            case 6: // Wait for the previous statement to finish executing...repeat after every case
                if (have_drive_encoders_reset())
                {
                    v_state++;
                }
                break;

            case 7: // Move forward using the color sensor
                run_using_encoders();
                set_drive_power(-.1, -.1);
                /*if (colorSensor.blue() >= 1 || colorSensor.red() >= 1) {
                    reset_drive_encoders();
                    set_drive_power(0,0);
                    v_state++;
                }*/

                break;

            case 8: // Move forward
                run_using_encoders();
                set_drive_power(-.1, -.1);
                if (have_drive_encoders_reached(500,500)) {
                    reset_drive_encoders();
                    set_drive_power(0,0);
                    v_state++;
                }

                break;

            case 9: // Wait for the previous statement to finish executing...repeat after every case
                if (has_left_drive_encoder_reset())
                {
                    v_state++;
                }
                break;

            case 10:
                run_using_encoders();
                set_drive_power(-.4, .4);
                if (have_drive_encoders_reached(2200, 2200)) {
                    reset_drive_encoders();
                    set_drive_power(0,0);
                    v_state++;
                }

                break;

            case 11: // Wait for the previous statement to finish executing...repeat after every case
                if (has_left_drive_encoder_reset())
                {
                    v_state++;
                }
                break;

            case 12:
                run_using_encoders();
                set_drive_power(.1, .1);
                telemetry.addData("Reading ods2", "");
                /*if (ods2.getLightDetectedRaw() > 35) {
                    reset_drive_encoders();
                    set_drive_power(0,0);
                    v_state++;
                }*/

                break;

            case 13:
                //drop_climbers();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                v_state++;
                break;

            case 14:
                //reset_climbers();
                v_state++;
                break;


            default:

                break;
        }


        update_telemetry(); // Update common telemetry
        telemetry.addData("State: ", v_state);
        telemetry.addData("Light: ", value);
        /*telemetry.addData("Light2: ", ods2.getLightDetectedRaw());
        telemetry.addData("Red: ", colorSensor.red());
        telemetry.addData("Blue: ", colorSensor.blue());*/
        telemetry.addData("Heading: ", heading);

    } // loop

    //--------------------------------------------------------------------------
    //
    // v_state
    //
} // PushBotAuto
