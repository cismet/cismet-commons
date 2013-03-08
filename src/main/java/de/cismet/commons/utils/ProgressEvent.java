/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.commons.utils;

import java.util.EventObject;

/**
 * Simple class to indicate that some event occurred at a currently progressing operation.
 *
 * @author   martin.scholl@cismet.de
 * @version  1.0
 */
public final class ProgressEvent extends EventObject {

    //~ Enums ------------------------------------------------------------------

    /**
     * The possible states of a progress event.
     *
     * @version  1.0
     */
    public enum State {

        //~ Enum constants -----------------------------------------------------

        STARTED, PROGRESSING, FINISHED, BROKEN, CANCELED, UNKNOWN
    }

    //~ Instance fields --------------------------------------------------------

    private final State state;
    private final int step;
    private final int maxSteps;

    private final String message;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new <code>ProgressEvent</code> object. Equal to calling
     * {@link #ProgressEvent(java.lang.Object, de.cismet.commons.utils.ProgressEvent.State, int, int) } with 0 as step
     * and 0 as maxSteps.
     *
     * @param  source  the source of the event
     * @param  state   the state of the event
     */
    public ProgressEvent(final Object source, final State state) {
        this(source, state, 0, 0);
    }

    /**
     * Creates a new <code>ProgressEvent</code> object. Equal to calling
     * {@link #ProgressEvent(java.lang.Object, de.cismet.commons.utils.ProgressEvent.State, int, int, java.lang.String) }
     * with 0 as step and 0 as maxSteps.
     *
     * @param  source   the source of the event
     * @param  state    the state of the event
     * @param  message  the message to indicate what this event is all about, usually localised
     */
    public ProgressEvent(final Object source, final State state, final String message) {
        this(source, state, 0, 0, message);
    }

    /**
     * Creates a new <code>ProgressEvent</code> object. Equal to calling
     * {@link #ProgressEvent(java.lang.Object, de.cismet.commons.utils.ProgressEvent.State, int, int, java.lang.String) }
     * with <code>null</code> as message.
     *
     * @param  source    the source of the event
     * @param  state     the state of the event
     * @param  step      the current step or 0 if it is isIndeterminate
     * @param  maxSteps  the current maxsteps of 0 if it is isIndeterminate
     */
    public ProgressEvent(final Object source, final State state, final int step, final int maxSteps) {
        this(source, state, step, maxSteps, null);
    }

    /**
     * Creates a new <code>ProgressEvent</code> object.
     *
     * @param  source    the source of the event
     * @param  state     the state of the event
     * @param  step      the current step or 0 if it is isIndeterminate
     * @param  maxSteps  the current maxsteps of 0 if it is isIndeterminate
     * @param  message   the message to indicate what this event is all about, usually localised
     */
    public ProgressEvent(final Object source,
            final State state,
            final int step,
            final int maxSteps,
            final String message) {
        super(source);

        this.state = state;
        this.step = step;
        this.maxSteps = maxSteps;
        this.message = message;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * Gets the <code>State</code> of this <code>ProgressEvent.</code>
     *
     * @return  the <code>State</code> of this <code>ProgressEvent</code>
     */
    public State getState() {
        return state;
    }

    /**
     * Gets the finish percentage.
     *
     * @return  0 &lt;= percent &lt;= 100 or -1 if the event {@link #isIndeterminate()}
     */
    public int getPercentFinished() {
        final int percent;

        if (isIndeterminate()) {
            percent = -1;
        } else {
            percent = step * 100 / maxSteps;
        }

        return percent;
    }

    /**
     * Returns the isIndeterminate state of the <code>ProgressEvent</code>. A <code>ProgressEvent</code> is considered
     * isIndeterminate if either {@link #getStep()} or {@link #getMaxSteps()} is 0.
     *
     * @return  <code>true</code> if either {@link #getStep()} or {@link #getMaxSteps()} is 0
     */
    public boolean isIndeterminate() {
        return (step == 0) || (maxSteps == 0);
    }

    /**
     * The current step of the observed progressing process.
     *
     * @return  the current step or 0if current step is unknown and thus the progress {@link #isIndeterminate() }.
     */
    public int getStep() {
        return step;
    }

    /**
     * The maximal amount of steps of the observed progressing process.
     *
     * @return  the maximal amount of steps or 0 if it is unknown and thus the progress {@link #isIndeterminate() }.
     */
    public int getMaxSteps() {
        return maxSteps;
    }

    /**
     * Gets the message associated with this event.
     *
     * @return  the message associated with this event
     */
    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return this.getState() + ": " + this.getMessage(); // NOI18N
    }
}
