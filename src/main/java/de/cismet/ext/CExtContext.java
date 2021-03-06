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
 * Context that can be used to request extensions from the {@link CExtManager}. Users of extensions are asked to provide
 * specific information how and for which purpose they intend to use the requested extension. Implementers of
 * {@link CExtProvider} may in turn decide on basis of the given context object which implementation to provide or
 * whether to provide an implementation at all. This context is backed by a {@link HashMap}. Thus the behavior of the
 * operations of this class are the same as their <code>HashMap</code> equivalents (if applicable).
 *
 * @author   thorsten
 * @author   mscholl
 * @version  1.0, 2012/08/28
 * @see      CExtManager
 * @see      CExtProvider
 * @see      HashMap
 */
public class CExtContext {

    //~ Static fields/initializers ---------------------------------------------

    /**
     * Constant that shall be used as key to put a reference object. This object shall be a concrete instance for which
     * the extension is requested.
     */
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
     * Creates a new CExtContext object and put the given object in the property map.
     *
     * @param  key    the key for the given value
     * @param  value  the value for the given key
     */
    public CExtContext(final String key, final Object value) {
        this();

        propertyBag.put(key, value);
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * Appends the specified <code>key</code> with the specified mapping <code>value</code> to the <code>
     * propertyBag</code>. If the specified <code>key</code> already has an associated <code>value</code>, this <code>
     * value</code> would get removed by the specified <code>value</code>
     *
     * @param   key    the key for the given value
     * @param   value  the value for the given key
     *
     * @return  the previous value associated with <code>key</code>, or <code>null</code> if there was no mapping for
     *          the specified <code>key</code>.
     *
     * @see     Map#put(java.lang.Object, java.lang.Object)
     */
    public Object putProperty(final String key, final Object value) {
        return propertyBag.put(key, value);
    }

    /**
     * Get the associated <code>value</code> for the specified <code>key</code>.
     *
     * @param   key  the key, whose mapping is searched
     *
     * @return  the <code>value</code> for the specified <code>key</code>, or <code>null</code> if there was no mapping
     *          for the specified <code>key</code>.
     *
     * @see     Map#get(java.lang.Object)
     */
    public Object getProperty(final String key) {
        return propertyBag.get(key);
    }

    /**
     * Return the whole backing map implementation.
     *
     * @return  the backing map implementation.
     */
    public Map<String, Object> getPropertyBag() {
        return propertyBag;
    }

    /**
     * Removes the <code>value</code> for the specified <code>key</code> from this map.
     *
     * @param   key  the key, whose mapping is removed
     *
     * @return  the <code>value</code>, which is removed
     *
     * @see     Map#remove(java.lang.Object)
     */
    public Object clearProperty(final String key) {
        return propertyBag.remove(key);
    }
}
