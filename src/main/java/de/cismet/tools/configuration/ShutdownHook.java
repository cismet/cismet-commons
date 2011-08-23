/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.tools.configuration;

/**
 * This interface can be used to register components that are interested in application shutdown.
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public interface ShutdownHook {

    //~ Methods ----------------------------------------------------------------

    /**
     * This operation is called when the application is about to be unloaded.
     */
    void applicationFinished();
}
