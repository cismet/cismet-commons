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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
public class CExtContext {

    //~ Instance fields --------------------------------------------------------

    private HashMap<String, Object> propertyBag = new HashMap<String, Object>();

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new CExtContext object.
     *
     * @param  key    DOCUMENT ME!
     * @param  value  DOCUMENT ME!
     */
    public CExtContext(final String key, final Object value) {
        propertyBag.put(key, value);
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   key    DOCUMENT ME!
     * @param   value  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public CExtContext addContextProperty(final String key, final Object value) {
        propertyBag.put(key, value);
        return this;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public HashMap<String, Object> getPropertyBag() {
        return propertyBag;
    }
}
