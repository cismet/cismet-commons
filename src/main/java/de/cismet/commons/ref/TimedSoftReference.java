/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.commons.ref;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Special implementation of {@link SoftReference} that purges the referent after the given idle time. Idle time in this
 * case means time without access to the actual referent via {@link #get()}
 *
 * @author   martin.scholl@cismet.de
 * @version  1.0
 * @see      {@link SoftReference}
 */
public final class TimedSoftReference<T> extends SoftReference<T> {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Timer TIMER = new Timer("TimedSoftReference-idle-timer"); // NOI18N

    //~ Instance fields --------------------------------------------------------

    private final transient long purgeAfterIdleTime;

    private transient TimerTask currentTask;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new TimedSoftReference object. Actually using this constructor is equal to calling <code>new
     * TimedSoftReference(referent, null, purgeAfterIdleTime)</code>
     *
     * @param  referent            the actual value
     * @param  purgeAfterIdleTime  the time after a non-accessed referent will be removed
     *
     * @see    {@link #TimedSoftReference(java.lang.Object, java.lang.ref.ReferenceQueue, long)}
     */
    public TimedSoftReference(final T referent, final long purgeAfterIdleTime) {
        this(referent, null, purgeAfterIdleTime);
    }

    /**
     * Creates a new TimedSoftReference object.
     *
     * @param  referent            the actual value
     * @param  q                   a reference queue to be used
     * @param  purgeAfterIdleTime  the time after a non-accessed referent will be removed
     *
     * @see    {@link SoftReference}
     * @see    {@link #get()}
     */
    public TimedSoftReference(final T referent, final ReferenceQueue<? super T> q, final long purgeAfterIdleTime) {
        super(referent, q);

        this.purgeAfterIdleTime = purgeAfterIdleTime;

        reschedule();
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * Gets the actual value of this {@link SoftReference} (the referent). The actual value may not be present anymore
     * for reasons explained in <code>SoftReference</code> javadoc or because the value has not been accessed for <code>
     * purgeAfterIdleTime</code> milliseconds. As soon as the value has been accessed (and the referent is still
     * existing) the purge operation is rescheduled to take place after <code>purgeAfterIdleTime</code> milliseconds.
     *
     * @return  the actual value (the referent) or <code>null</code> if it has been cleaned for any reason
     *
     * @see     {@link SoftReference#get()}
     */
    @Override
    public T get() {
        final T obj = super.get();

        assert currentTask != null : "current task must never be null"; // NOI18N

        // we don't care if cancel was successful or not
        currentTask.cancel();

        if (obj != null) {
            reschedule();
        }

        return obj;
    }

    /**
     * Reschedules the clear timer task. Should only be called by the {@link #get()} operation. Otherwise
     * synchronization has to be done.
     */
    private void reschedule() {
        currentTask = new ClearTimerTask();
        TIMER.schedule(currentTask, purgeAfterIdleTime);
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * Simple timer task that clears the referent.
     *
     * @version  $Revision$, $Date$
     */
    private final class ClearTimerTask extends TimerTask {

        //~ Methods ------------------------------------------------------------

        @Override
        public void run() {
            TimedSoftReference.this.clear();
        }
    }
}
