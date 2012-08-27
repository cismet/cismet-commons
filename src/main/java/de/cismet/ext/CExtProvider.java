/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.ext;

import java.util.Collection;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
public interface CExtProvider<T> {

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   context  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    Collection<T> provideExtensions(CExtContext context);
    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    Class<T> getValidClass();
}
