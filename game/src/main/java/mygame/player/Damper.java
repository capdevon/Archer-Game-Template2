package mygame.player;

/**
 * A simple discrete-time linear control system that can be used to damp down
 * oscillations.
 *
 * @author Stephen Gold sgold@sonic.net
 */
class Damper {
    // *************************************************************************
    // constants and loggers

    /**
     * gain coefficient for the current-error term
     */
    final private float gain0;
    /**
     * gain coefficient for the previous-error term
     */
    final private float gain1;
    // *************************************************************************
    // constructors

    /**
     * current error (setpoint minus measurement)
     */
    private float currentError = 0f;
    /**
     * next error (setpoint minus measurement)
     */
    private float nextError = 0f;
    /**
     * latest control-signal value
     */
    private float output = 0f;
    /**
     * previous error (setpoint minus measurement)
     */
    private float previousError = 0f;
    // *************************************************************************
    // constructors

    /**
     * Instantiate a new damper.
     *
     * @param gain0 gain coefficient for the current-error term
     * @param gain1 gain coefficient for the previous-error term
     */
    Damper(float gain0, float gain1) {
        this.gain0 = gain0;
        this.gain1 = gain1;
    }
    // *************************************************************************
    // new methods exposed

    /**
     * Set the error value (setpoint minus measurement) for the next update.
     *
     * @param error the desired value (default=0)
     */
    void setNextError(float error) {
        this.nextError = error;
    }

    /**
     * Update the control-signal value based on the error history.
     *
     * @return the new control-signal value
     */
    float update() {
        this.previousError = currentError;
        this.currentError = nextError;
        this.nextError = 0f;

        this.output += gain0 * currentError + gain1 * previousError;

        return output;
    }
}
