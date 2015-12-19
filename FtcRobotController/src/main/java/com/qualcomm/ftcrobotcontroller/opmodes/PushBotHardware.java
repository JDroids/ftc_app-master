package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.ftccommon.DbgLog;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.GyroSensor;
import com.qualcomm.robotcore.hardware.OpticalDistanceSensor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.TouchSensor;
import com.qualcomm.robotcore.util.Range;

//------------------------------------------------------------------------------
//
// PushBotHardware
//
/**
 * Provides a single hardware access point between custom op-modes and the
 * OpMode class for the Push Bot.
 *
 * This class prevents the custom op-mode from throwing an exception at runtime.
 * If any hardware fails to map, a warning will be shown via telemetry data,
 * calls to methods will fail, but will not cause the application to crash.
 *
 * @author SSI Robotics
 * @version 2015-08-13-20-04
 */
public class PushBotHardware extends OpMode

{
    //--------------------------------------------------------------------------
    //
    // PushBotHardware
    //
    /**
     * Construct the class.
     *
     * The system calls this member when the class is instantiated.
     */
    public PushBotHardware ()

    {
        //
        // Initialize base classes.
        //
        // All via self-construction.

        //
        // Initialize class members.
        //
        // All via self-construction.

    } // PushBotHardware

    //--------------------------------------------------------------------------
    //
    // init
    //
    /**
     * Perform any actions that are necessary when the OpMode is enabled.
     *
     * The system calls this member once when the OpMode is enabled.
     */
    @Override public void init ()

    {
        //
        // Use the hardwareMap to associate class members to hardware ports.
        //
        // Note that the names of the devices (i.e. arguments to the get method)
        // must match the names specified in the configuration file created by
        // the FTC Robot Controller (Settings-->Configure Robot).
        //
        // The variable below is used to provide telemetry data to a class user.
        //
        v_warning_generated = false;
        v_warning_message = "Can't map; ";

        //
        // Connect the drive wheel motors.
        //
        // The direction of the right motor is reversed, so joystick inputs can
        // be more generically applied.
        //
        try
        {
            v_motor_left_drive_front = hardwareMap.dcMotor.get ("m4");
            v_motor_left_drive_back = hardwareMap.dcMotor.get ("m3");
        }
        catch (Exception p_exeception)
        {
            m_warning_message ("left_drive");
            DbgLog.msg(p_exeception.getLocalizedMessage());

            v_motor_left_drive_front = null;
            v_motor_left_drive_back = null;
        }

        try
        {
            v_motor_right_drive_front = hardwareMap.dcMotor.get ("m1");
            v_motor_right_drive_back = hardwareMap.dcMotor.get ("m2");
            v_motor_left_drive_front.setDirection (DcMotor.Direction.REVERSE);
            v_motor_left_drive_back.setDirection (DcMotor.Direction.REVERSE);
            turn_climbers = hardwareMap.servo.get("s4");
            turn_climbers.setPosition(0);
            drop_climbers = hardwareMap.servo.get("s5");
            drop_climbers.setPosition(.5);
            push_button = hardwareMap.servo.get("s6");
            push_button.setPosition(.5);
            rightTrigger = hardwareMap.servo.get("s1");
            rightTrigger.setPosition(1);
            leftTrigger = hardwareMap.servo.get("s2");
            leftTrigger.setPosition(0);
            plough = hardwareMap.servo.get("s3");
            plough.setPosition(.7);
        }
        catch (Exception p_exeception)
        {
            m_warning_message("right_drive");
            DbgLog.msg(p_exeception.getLocalizedMessage());

            v_motor_right_drive_front = null;
            v_motor_right_drive_back = null;
        }

        try {
            sensorGyro = hardwareMap.gyroSensor.get("gyro");
            sensorGyro.calibrate();
            while (sensorGyro.isCalibrating())  {

                telemetry.addData("Calibrating Gyro (In loop)","");
                try {
                    Thread.sleep(50);
                }
                catch (Exception e) {

                }
            }

        }
        catch (Exception e) {
            telemetry.addData("Exception while initializing Gyro","");
            sensorGyro = null;
        }

    }

    public void resetServos() {
        turn_climbers.setPosition(0);
        drop_climbers.setPosition(.5);
        push_button.setPosition(.5);
        rightTrigger.setPosition(0);
        leftTrigger.setPosition(1);
    }

    boolean a_warning_generated ()

    {
        return v_warning_generated;

    }

    String a_warning_message () {
        return v_warning_message;
    }

    void m_warning_message (String p_exception_message)

    {
        if (v_warning_generated)
        {
            v_warning_message += ", ";
        }
        v_warning_generated = true;
        v_warning_message += p_exception_message;

    } // m_warning_message

    //--------------------------------------------------------------------------
    //
    // start
    //
    /**
     * Perform any actions that are necessary when the OpMode is enabled.
     *
     * The system calls this member once when the OpMode is enabled.
     */
    @Override public void start ()

    {
        //
        // Only actions that are common to all Op-Modes (i.e. both automatic and
        // manual) should be implemented here.
        //
        // This method is designed to be overridden.
        //

    } // start

    //--------------------------------------------------------------------------
    //
    // loop
    //
    /**
     * Perform any actions that are necessary while the OpMode is running.
     *
     * The system calls this member repeatedly while the OpMode is running.
     */
    @Override public void loop ()

    {
        //
        // Only actions that are common to all OpModes (i.e. both auto and\
        // manual) should be implemented here.
        //
        // This method is designed to be overridden.
        //

    } // loop

    //--------------------------------------------------------------------------
    //
    // stop
    //
    /**
     * Perform any actions that are necessary when the OpMode is disabled.
     *
     * The system calls this member once when the OpMode is disabled.
     */
    @Override public void stop ()
    {
        //
        // Nothing needs to be done for this method.
        //

    } // stop

    //--------------------------------------------------------------------------
    //
    // scale_motor_power
    //
    /**
     * Scale the joystick input using a nonlinear algorithm.
     */
    float scale_motor_power (float p_power)
    {
        //
        // Assume no scaling.
        //
        float l_scale = 0.0f;

        //
        // Ensure the values are legal.
        //
        float l_power = Range.clip (p_power, -1, 1);

        float[] l_array =
                { 0.00f, 0.05f, 0.09f, 0.10f, 0.12f
                        , 0.15f, 0.18f, 0.24f, 0.30f, 0.36f
                        , 0.43f, 0.50f, 0.60f, 0.72f, 0.85f
                        , 1.00f, 1.00f
                };

        //
        // Get the corresponding index for the specified argument/parameter.
        //
        int l_index = (int)(l_power * 16.0);
        if (l_index < 0)
        {
            l_index = -l_index;
        }
        else if (l_index > 16)
        {
            l_index = 16;
        }

        if (l_power < 0)
        {
            l_scale = -l_array[l_index];
        }
        else
        {
            l_scale = l_array[l_index];
        }

        return l_scale;

    } // scale_motor_power

    //--------------------------------------------------------------------------
    //
    // a_left_drive_power
    //
    /**
     * Access the left drive motor's power level.
     */
    double a_left_drive_power ()
    {
        double l_return = 0.0;

        if (v_motor_left_drive_front != null)
        {
            l_return = v_motor_left_drive_front.getPower ();
        }

        return l_return;

    } // a_left_drive_power

    //--------------------------------------------------------------------------
    //
    // a_right_drive_power
    //
    /**
     * Access the right drive motor's power level.
     */
    double a_right_drive_power ()
    {
        double l_return = 0.0;

        if (v_motor_right_drive_front != null)
        {
            l_return = v_motor_right_drive_front.getPower ();
        }

        return l_return;

    } // a_right_drive_power

    //--------------------------------------------------------------------------
    //
    // set_drive_power
    //
    /**
     * Scale the joystick input using a nonlinear algorithm.
     */
    void set_drive_power (double p_left_power, double p_right_power)

    {
        if (v_motor_left_drive_front != null)
        {
            v_motor_left_drive_front.setPower (p_left_power);
            v_motor_left_drive_back.setPower (p_left_power);
        }
        if (v_motor_right_drive_front != null)
        {
            v_motor_right_drive_front.setPower (p_right_power);
            v_motor_right_drive_back.setPower (p_right_power);
        }

    } // set_drive_power

    //--------------------------------------------------------------------------
    //
    // run_using_left_drive_encoder
    //
    /**
     * Set the left drive wheel encoder to run, if the mode is appropriate.
     */
    public void run_using_left_drive_encoder ()

    {
        if (v_motor_left_drive_front != null)
        {
            v_motor_left_drive_front.setChannelMode
                    ( DcMotorController.RunMode.RUN_USING_ENCODERS
                    );
            v_motor_left_drive_back.setChannelMode
                    ( DcMotorController.RunMode.RUN_USING_ENCODERS
                    );
        }

    } // run_using_left_drive_encoder

    //--------------------------------------------------------------------------
    //
    // run_using_right_drive_encoder
    //
    /**
     * Set the right drive wheel encoder to run, if the mode is appropriate.
     */
    public void run_using_right_drive_encoder ()

    {
        if (v_motor_right_drive_front != null)
        {
            v_motor_right_drive_front.setChannelMode
                    ( DcMotorController.RunMode.RUN_USING_ENCODERS
                    );
            v_motor_right_drive_back.setChannelMode
                    ( DcMotorController.RunMode.RUN_USING_ENCODERS
                    );
        }

    } // run_using_right_drive_encoder

    //--------------------------------------------------------------------------
    //
    // run_using_encoders
    //
    /**
     * Set both drive wheel encoders to run, if the mode is appropriate.
     */
    public void run_using_encoders ()

    {
        //
        // Call other members to perform the action on both motors.
        //
        run_using_left_drive_encoder ();
        run_using_right_drive_encoder ();

    } // run_using_encoders

    //--------------------------------------------------------------------------
    //
    // run_without_left_drive_encoder
    //
    /**
     * Set the left drive wheel encoder to run, if the mode is appropriate.
     */
    public void run_without_left_drive_encoder ()

    {
        if (v_motor_left_drive_front != null)
        {
            if (v_motor_left_drive_front.getChannelMode () ==
                    DcMotorController.RunMode.RESET_ENCODERS)
            {
                v_motor_left_drive_front.setChannelMode
                        ( DcMotorController.RunMode.RUN_WITHOUT_ENCODERS
                        );
                v_motor_left_drive_back.setChannelMode
                        ( DcMotorController.RunMode.RUN_WITHOUT_ENCODERS
                        );
            }
        }

    } // run_without_left_drive_encoder

    //--------------------------------------------------------------------------
    //
    // run_without_right_drive_encoder
    //
    /**
     * Set the right drive wheel encoder to run, if the mode is appropriate.
     */
    public void run_without_right_drive_encoder ()

    {
        if (v_motor_right_drive_front != null)
        {
            if (v_motor_right_drive_front.getChannelMode () ==
                    DcMotorController.RunMode.RESET_ENCODERS)
            {
                v_motor_right_drive_front.setChannelMode
                        ( DcMotorController.RunMode.RUN_WITHOUT_ENCODERS
                        );
                v_motor_right_drive_back.setChannelMode
                        ( DcMotorController.RunMode.RUN_WITHOUT_ENCODERS
                        );
            }
        }

    } // run_without_right_drive_encoder

    //--------------------------------------------------------------------------
    //
    // run_without_drive_encoders
    //
    /**
     * Set both drive wheel encoders to run, if the mode is appropriate.
     */
    public void run_without_drive_encoders ()

    {
        //
        // Call other members to perform the action on both motors.
        //
        run_without_left_drive_encoder ();
        run_without_right_drive_encoder ();

    } // run_without_drive_encoders

    //--------------------------------------------------------------------------
    //
    // reset_left_drive_encoder
    //
    /**
     * Reset the left drive wheel encoder.
     */
    public void reset_left_drive_encoder ()

    {
        if (v_motor_left_drive_front != null)
        {
            v_motor_left_drive_front.setChannelMode
                    ( DcMotorController.RunMode.RESET_ENCODERS
                    );
            v_motor_left_drive_back.setChannelMode
                    ( DcMotorController.RunMode.RESET_ENCODERS
                    );
        }

    } // reset_left_drive_encoder

    //--------------------------------------------------------------------------
    //
    // reset_right_drive_encoder
    //
    /**
     * Reset the right drive wheel encoder.
     */
    public void reset_right_drive_encoder ()

    {
        if (v_motor_right_drive_front != null)
        {
            v_motor_right_drive_front.setChannelMode
                    ( DcMotorController.RunMode.RESET_ENCODERS
                    );
            v_motor_right_drive_back.setChannelMode
                    ( DcMotorController.RunMode.RESET_ENCODERS
                    );
        }

    } // reset_right_drive_encoder

    //--------------------------------------------------------------------------
    //
    // reset_drive_encoders
    //
    /**
     * Reset both drive wheel encoders.
     */
    public void reset_drive_encoders ()

    {
        //
        // Reset the motor encoders on the drive wheels.
        //
        reset_left_drive_encoder ();
        reset_right_drive_encoder ();

    } // reset_drive_encoders

    //--------------------------------------------------------------------------
    //
    // a_left_encoder_count
    //
    /**
     * Access the left encoder's count.
     */
    int a_left_encoder_count ()
    {
        int l_return = 0;

        if (v_motor_left_drive_front != null)
        {
            l_return = v_motor_left_drive_front.getCurrentPosition ();
        }

        return l_return;

    } // a_left_encoder_count

    //--------------------------------------------------------------------------
    //
    // a_right_encoder_count
    //
    /**
     * Access the right encoder's count.
     */
    int a_right_encoder_count ()

    {
        int l_return = 0;

        if (v_motor_right_drive_front != null)
        {
            l_return = v_motor_right_drive_front.getCurrentPosition ();
        }

        return l_return;

    } // a_right_encoder_count

    //--------------------------------------------------------------------------
    //
    // has_left_drive_encoder_reached
    //
    /**
     * Indicate whether the left drive motor's encoder has reached a value.
     */
    boolean has_left_drive_encoder_reached (double p_count)

    {
        //
        // Assume failure.
        //
        boolean l_return = false;

        if (v_motor_left_drive_front != null)
        {
            //
            // Has the encoder reached the specified values?
            //
            // TODO Implement stall code using these variables.
            //
            if (Math.abs (v_motor_left_drive_front.getCurrentPosition ()) > p_count)
            {
                //
                // Set the status to a positive indication.
                //
                l_return = true;
            }
        }

        //
        // Return the status.
        //
        return l_return;

    } // has_left_drive_encoder_reached

    //--------------------------------------------------------------------------
    //
    // has_right_drive_encoder_reached
    //
    /**
     * Indicate whether the right drive motor's encoder has reached a value.
     */
    boolean has_right_drive_encoder_reached (double p_count)

    {
        //
        // Assume failure.
        //
        boolean l_return = false;

        if (v_motor_right_drive_front != null)
        {
            //
            // Have the encoders reached the specified values?
            //
            // TODO Implement stall code using these variables.
            //
            if (Math.abs (v_motor_right_drive_front.getCurrentPosition ()) > p_count)
            {
                //
                // Set the status to a positive indication.
                //
                l_return = true;
            }
        }

        //
        // Return the status.
        //
        return l_return;

    } // has_right_drive_encoder_reached

    //--------------------------------------------------------------------------
    //
    // have_drive_encoders_reached
    //
    /**
     * Indicate whether the drive motors' encoders have reached a value.
     */
    boolean have_drive_encoders_reached
    ( double p_left_count
            , double p_right_count
    )

    {
        //
        // Assume failure.
        //
        boolean l_return = false;

        //
        // Have the encoders reached the specified values?
        //
        if (has_left_drive_encoder_reached (p_left_count) &&
                has_right_drive_encoder_reached (p_right_count))
        {
            //
            // Set the status to a positive indication.
            //
            l_return = true;
        }

        //
        // Return the status.
        //
        return l_return;

    } // have_encoders_reached

    //--------------------------------------------------------------------------
    //
    // drive_using_encoders
    //
    /**
     * Indicate whether the drive motors' encoders have reached a value.
     */
    boolean drive_using_encoders
    ( double p_left_power
            , double p_right_power
            , double p_left_count
            , double p_right_count
    )

    {
        //
        // Assume the encoders have not reached the limit.
        //
        boolean l_return = false;

        //
        // Tell the system that motor encoders will be used.
        //
        run_using_encoders ();

        //
        // Start the drive wheel motors at full power.
        //
        set_drive_power (p_left_power, p_right_power);

        //
        // Have the motor shafts turned the required amount?
        //
        // If they haven't, then the op-mode remains in this state (i.e this
        // block will be executed the next time this method is called).
        //
        if (have_drive_encoders_reached (p_left_count, p_right_count))
        {
            //
            // Reset the encoders to ensure they are at a known good value.
            //
            reset_drive_encoders ();

            //
            // Stop the motors.
            //
            set_drive_power (0.0f, 0.0f);

            //
            // Transition to the next state when this method is called
            // again.
            //
            l_return = true;
        }

        //
        // Return the status.
        //
        return l_return;

    } // drive_using_encoders

    //--------------------------------------------------------------------------
    //
    // has_left_drive_encoder_reset
    //
    /**
     * Indicate whether the left drive encoder has been completely reset.
     */
    boolean has_left_drive_encoder_reset ()
    {
        //
        // Assume failure.
        //
        boolean l_return = false;

        //
        // Has the left encoder reached zero?
        //
        if (a_left_encoder_count () == 0)
        {
            //
            // Set the status to a positive indication.
            //
            l_return = true;
        }

        //
        // Return the status.
        //
        return l_return;

    } // has_left_drive_encoder_reset

    //--------------------------------------------------------------------------
    //
    // has_right_drive_encoder_reset
    //
    /**
     * Indicate whether the left drive encoder has been completely reset.
     */
    boolean has_right_drive_encoder_reset ()
    {
        //
        // Assume failure.
        //
        boolean l_return = false;

        //
        // Has the right encoder reached zero?
        //
        if (a_right_encoder_count () == 0)
        {
            //
            // Set the status to a positive indication.
            //
            l_return = true;
        }

        //
        // Return the status.
        //
        return l_return;

    } // has_right_drive_encoder_reset

    //--------------------------------------------------------------------------
    //
    // have_drive_encoders_reset
    //
    /**
     * Indicate whether the encoders have been completely reset.
     */
    boolean have_drive_encoders_reset ()
    {
        //
        // Assume failure.
        //
        boolean l_return = false;

        //
        // Have the encoders reached zero?
        //
        if (has_left_drive_encoder_reset () && has_right_drive_encoder_reset ())
        {
            //
            // Set the status to a positive indication.
            //
            l_return = true;
        }

        //
        // Return the status.
        //
        return l_return;

    } // have_drive_encoders_reset

    void turn_climbers() {
        turn_climbers.setPosition(1);
        resetStartTime();
    }

    void retract_climbers() {
        turn_climbers.setPosition(0);
        resetStartTime();
    }

    void drop_climbers() {
        drop_climbers.setDirection(Servo.Direction.REVERSE);
        drop_climbers.setPosition(1);
        resetStartTime();
    }

    void push_button() {
        push_button.setPosition(1);
        turn_climbers.setPosition(0);
        drop_climbers.setPosition(.5);
        leftTrigger.setPosition(1);
        rightTrigger.setPosition(0);
        resetStartTime();
    }

    void retract_button() {
        push_button.setDirection(Servo.Direction.REVERSE);
        push_button.setPosition(1);
        resetStartTime();

    }

    boolean buttonWait() {
        if (timeCalled) {
            timeToStop = getRuntime() + 2;
            timeCalled = false;
        }
        if (getRuntime() >= timeToStop) {
            timeCalled = true;
            push_button.setPosition(.5);
            return true;
        }
        telemetry.addData("Runtime", getRuntime());
        return false;
    }

    boolean buttonWait2() {
        if (getRuntime() >= 2) {
            push_button.setPosition(.5);
            return true;
        }
        telemetry.addData("Runtime", getRuntime());
        return false;
    }

    boolean climbersWait2() {
        if (getRuntime() >= 2) {
            return true;
        }
        telemetry.addData("Runtime", getRuntime());
        return false;

    }

    boolean climbersWait() {
        if (timeCalled) {
            timeToStop = getRuntime() + 2;
            timeCalled = false;
        }
        if (getRuntime() >= timeToStop) {
            timeCalled = true;
            return true;
        }
        telemetry.addData("Runtime", getRuntime());
        return false;

    }

    boolean dropWait() {
        if (timeCalled2) {
            timeToStop2 = getRuntime() + 2;
            timeCalled2 = false;
        }
        if (getRuntime() >= timeToStop2) {
            drop_climbers.setPosition(.5);
            timeCalled2 = true;
            return true;
        }
        telemetry.addData("Runtime", getRuntime());
        return false;

    }

    boolean dropWait2() {
        if (getRuntime() >= 2) {
            drop_climbers.setPosition(.5);
            return true;
        }
        telemetry.addData("Runtime", getRuntime());
        return false;

    }

    private boolean v_warning_generated = false;

    //--------------------------------------------------------------------------
    //
    // v_warning_message
    //
    /**
     * Store a message to the user if one has been generated.
     */
    private String v_warning_message;

    //--------------------------------------------------------------------------
    //
    // v_motor_left_drive
    //
    /**
     * Manage the aspects of the left drive motor.
     */
    private DcMotor v_motor_left_drive_front;
    private DcMotor v_motor_left_drive_back;

    //--------------------------------------------------------------------------
    //
    // v_motor_right_drive
    //
    /**
     * Manage the aspects of the right drive motor.
     */
    private DcMotor v_motor_right_drive_front;
    private DcMotor v_motor_right_drive_back;

    private Servo turn_climbers;
    private Servo drop_climbers;
    private Servo push_button;
    public GyroSensor sensorGyro;
    private Servo leftTrigger;
    private Servo rightTrigger;
    private Servo plough;
    public ColorSensor colorSensor;
    boolean timeCalled = true;
    boolean timeCalled2 = true;
    double timeToStop = 0;
    double timeToStop2 = 0;

} // PushBotHardware
