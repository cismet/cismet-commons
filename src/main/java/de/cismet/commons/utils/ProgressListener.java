/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.commons.utils;

import java.util.EventListener;

/**
 * Simple interface to indicate that a component is able to be informed about progress events.
 *
 * @author   martin.scholl@cismet.de
 * @version  1.0
 */
public interface ProgressListener extends EventListener {

    //~ Methods ----------------------------------------------------------------

    /**
     * Usually called if a progress event happened.
     *
     * @param  event  the progress event associated with the current progress
     */
    void progress(final ProgressEvent event);
}
