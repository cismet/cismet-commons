/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.remote;

/**
 * DOCUMENT ME!
 *
 * @author   Benjamin Friedrich (benjamin.friedrich@cismet.de)
 * @version  $Revision$, $Date$
 */
public interface RESTRemoteControlMethod {

    //~ Methods ----------------------------------------------------------------

    /**
     * Returns target port for this service
     *
     * @return  port
     */
    int getPort();
    
    /**
     * Returns context path for this service
     *
     * @return  context path
     */
    String getPath();

//    /**
//     * DOCUMENT ME!
//     *
//     * @param   args  DOCUMENT ME!
//     *
//     * @return  DOCUMENT ME!
//     */
//    Runnable getAction(Object... args);
}
