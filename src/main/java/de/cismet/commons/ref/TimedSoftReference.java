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
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public final class TimedSoftReference<T> extends SoftReference<T> {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Timer TIMER = new Timer("TimedSoftReference-idle-timer"); // NOI18N

    //~ Instance fields --------------------------------------------------------

    private final transient long clearAfterIdleTime;

    private transient TimerTask currentTask;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new TimedSoftReference object.
     *
     * @param  referent            DOCUMENT ME!
     * @param  clearAfterIdleTime  DOCUMENT ME!
     */
    public TimedSoftReference(final T referent, final long clearAfterIdleTime) {
        this(referent, null, clearAfterIdleTime);
    }

    /**
     * Creates a new TimedSoftReference object.
     *
     * @param  referent            DOCUMENT ME!
     * @param  q                   DOCUMENT ME!
     * @param  clearAfterIdleTime  DOCUMENT ME!
     */
    public TimedSoftReference(final T referent, final ReferenceQueue<? super T> q, final long clearAfterIdleTime) {
        super(referent, q);

        this.clearAfterIdleTime = clearAfterIdleTime;

        reschedule();
    }

    //~ Methods ----------------------------------------------------------------

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
     * DOCUMENT ME!
     */
    private void reschedule() {
        currentTask = new ClearTimerTask();
        TIMER.schedule(currentTask, clearAfterIdleTime);
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
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
