/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.commons.utils;

/**
 * Simple interface that indicates that a component is willing to propagate progress of a certain process to interested
 * parties.
 *
 * @author   mscholl
 * @version  1.0
 */
public interface ProgressObservable {

    //~ Methods ----------------------------------------------------------------

    /**
     * Adds a <code>ProgressListener.</code>
     *
     * @param  pl  the <code>ProgressListener</code> to add
     */
    void addProgressListener(final ProgressListener pl);

    /**
     * Removes a <code>ProgressListener.</code>
     *
     * @param  pl  the <code>ProgressListener</code> to remove
     */
    void removeProgressListener(final ProgressListener pl);
}
