/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.commons.utils;

import java.awt.EventQueue;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Simple helper class that takes care of registering {@link ProgressListener}s and firing {@link ProgressEvent}s.
 *
 * @author   martin.scholl@cismet.de
 * @version  1.0
 */
public final class ProgressSupport {

    //~ Instance fields --------------------------------------------------------

    private final transient Set<ProgressListener> listeners;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new <code>ProgressSupport</code> object.
     */
    public ProgressSupport() {
        this.listeners = new HashSet<ProgressListener>(3);
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * Add a new <code>ProgressListener</code> to this support object. If the object already exists nothing is done.
     *
     * @param  progressL  the <code>ProgressListener</code> to add
     */
    public void addProgressListener(final ProgressListener progressL) {
        listeners.add(progressL);
    }

    /**
     * Removes the given <code>ProgressListener</code> from this support object. If the object does not exist nothing is
     * done.
     *
     * @param  progressL  the <code>ProgressListener</code> to remove
     */
    public void removeProgressListener(final ProgressListener progressL) {
        listeners.remove(progressL);
    }

    /**
     * Fires a <code>ProgressEvent</code> to all currently registered <code>ProgressListener</code>s.
     *
     * @param  event  the event to fire
     */
    public void fireEvent(final ProgressEvent event) {
        final Iterator<ProgressListener> it;

        synchronized (listeners) {
            it = new HashSet<ProgressListener>(listeners).iterator();
        }

        while (it.hasNext()) {
            final ProgressListener next = it.next();
            EventQueue.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        next.progress(event);
                    }
                });
        }
    }
}
