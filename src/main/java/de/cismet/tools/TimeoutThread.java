/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.tools;

import org.openide.util.NbBundle;

import java.util.concurrent.TimeoutException;

/**
 * Execute code that will be interrupted after a given time.
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public abstract class TimeoutThread<T extends Object> implements Runnable {

    //~ Static fields/initializers ---------------------------------------------

    private static int openUserDialogCount = 0;

    //~ Instance fields --------------------------------------------------------

    /** the result of the thread should be saved here. */
    protected T result;
    /**
     * if an exception is thrown during the execution of the thread, the result variable should be null and the
     * exception should be saved here.
     */
    protected Exception exception;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new TimeoutThread object.
     */
    public TimeoutThread() {
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * Starts the code of the run method. After the given time, the execution of the run method will be interrupted and
     * a TimeoutException is thrown.
     *
     * @param   timeInMillis  the time to wait in milliseconds
     *
     * @return  result
     *
     * @throws  Exception         an exception during the execution of the thread ocured
     * @throws  TimeoutException  Execution is timeouted and interrupted.
     */
    public T start(final long timeInMillis) throws Exception {
        final Thread t = new Thread(this);

        t.start();

        do {
            t.join(timeInMillis);
        } while ((getOpenUserDialogCount() > 0) && t.isAlive());

        if (t.isAlive()) {
            t.interrupt();
            throw new TimeoutException(NbBundle.getMessage(
                    TimeoutThread.class,
                    "TimeoutThread.start().timelimitExceeded"));
        } else if (exception != null) {
            throw exception;
        } else {
            return result;
        }
    }

    /**
     * DOCUMENT ME!
     */
    public static synchronized void increaseOpenUserDialogCount() {
        ++openUserDialogCount;
    }

    /**
     * DOCUMENT ME!
     */
    public static synchronized void decreaseOpenUserDialogCount() {
        --openUserDialogCount;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static synchronized int getOpenUserDialogCount() {
        return openUserDialogCount;
    }
}
