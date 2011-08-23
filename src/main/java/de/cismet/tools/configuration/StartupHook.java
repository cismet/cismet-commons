/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.tools.configuration;

/**
 * This interface can be used to register components that are interested in application startup.
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public interface StartupHook {

    //~ Methods ----------------------------------------------------------------

    /**
     * This operation is called when the application is completely loaded.
     */
    void applicationStarted();
}
