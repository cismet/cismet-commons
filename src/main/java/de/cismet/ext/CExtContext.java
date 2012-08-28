/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.ext;

import java.util.HashMap;
import java.util.Map;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
public class CExtContext {

    //~ Static fields/initializers ---------------------------------------------

    public static final String CTX_REFERENCE = "__ctx_prop_reference__"; // NOI18N

    //~ Instance fields --------------------------------------------------------

    private final Map<String, Object> propertyBag;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new CExtContext object.
     */
    public CExtContext() {
         propertyBag = new HashMap<String, Object>();
    }

    /**
     * Creates a new CExtContext object.
     *
     * @param  key    DOCUMENT ME!
     * @param  value  DOCUMENT ME!
     */
    public CExtContext(final String key, final Object value) {
        this();
        
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
    public CExtContext putProperty(final String key, final Object value) {
        propertyBag.put(key, value);

        return this;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   key  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Object getProperty(final String key) {
        return propertyBag.get(key);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Map<String, Object> getPropertyBag() {
        return propertyBag;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   key  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Object clearProperty(final String key) {
        return propertyBag.remove(key);
    }
}
