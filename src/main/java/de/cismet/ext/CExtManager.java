/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.ext;

import org.apache.log4j.Logger;

import org.openide.util.Lookup;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Global extension manager implementation that can provide extension implementations of any type. The behavior is
 * similar to those of the global {@link Lookup}. However, this implementation provides additional facilities to collect
 * extensions for specific situations/usages using an additional {@link CExtContext} object to express this very
 * context. Extension providers can thus restrict the usage of their extensions to contexts in which they decided that
 * the specific extension is reasonable. Extension users in turn can exactly specify their needs and thus can filter
 * unsuitable extensions for their context.
 *
 * @author   thorsten
 * @author   mscholl
 * @version  1.0, 2012/08/28
 * @see      CExtContext
 * @see      CExtProvider
 * @see      Lookup
 */
// TODO: extract interface and put the extension manager on the global lookup. this way it is easy to provide implementations implmentations that make use of caching mechanism, etc.
// TODO: introduce caching
// TODO: introduce lazy loading of extensions
public class CExtManager {

    //~ Static fields/initializers ---------------------------------------------

    /** LOGGER. */
    private static final transient Logger LOG = Logger.getLogger(CExtManager.class);

    //~ Constructors -----------------------------------------------------------

    /**
     * Singleton.
     */
    private CExtManager() {
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * Returns the <code>CExtManager</code> instance (Singleton).
     *
     * @return  the <code>CExtManager</code> instance
     *
     * @since   1.0
     */
    public static CExtManager getInstance() {
        return LazyInitialiser.INSTANCE;
    }

    /**
     * Collects all matching extension implementations of the given type for the given context. It makes use of the
     * global {@link Lookup} and thus the ordering of the result depends on the order of the {@link CExtProvider}
     * implementations that in turn provide implementations of the given type.
     *
     * @param   <T>      type of the requested extensions
     * @param   c        class of the type of the requested extensions
     * @param   context  context for which the extensions are requested for
     *
     * @return  an appropriate extension collection, ordered from most relevant to least relevant, never <code>
     *          null</code>
     *
     * @since   1.0
     */
    public <T> Collection<? extends T> getExtensions(final Class<T> c, final CExtContext context) {
        final Collection<? extends CExtProvider> providers = Lookup.getDefault().lookupAll(CExtProvider.class);

        final ArrayList<T> result = new ArrayList<T>();
        for (final CExtProvider provider : providers) {
            if (provider.canProvide(c)) {
                final Collection<? extends T> extensions = provider.provideExtensions(context);

                if (extensions == null) {
                    LOG.warn(
                        "illegal CExtProvider implementation, CExtProvider.provideExtensions(CExtContext) returned " // NOI18N
                                + "null, ignoring provider: "                                                        // NOI18N
                                + provider);
                } else {
                    result.addAll(extensions);
                }
            }
        }

        return result;
    }

    /**
     * Provides the first matching extension implementation of the given type for the given context. It makes use of the
     * global {@link Lookup} and thus the first matching implementation depends on the order of the {@link CExtProvider}
     * implementations that in turn provide implementations of the given type.
     *
     * @param   <T>      type of the requested extension
     * @param   c        class of the type of the requested extension
     * @param   context  context for which the extension is requested for
     *
     * @return  an appropriate extension implementation or <code>null</code> if there is no <code>CExtProvider</code>
     *          that provides an extension implementation for the given type and context
     *
     * @since   1.0
     */
    public <T> T getExtension(final Class<T> c, final CExtContext context) {
        final Collection<? extends CExtProvider> providers = Lookup.getDefault().lookupAll(CExtProvider.class);

        for (final CExtProvider provider : providers) {
            if (provider.canProvide(c)) {
                final Collection<? extends T> extensions = provider.provideExtensions(context);

                if (extensions == null) {
                    LOG.warn(
                        "illegal CExtProvider implementation, CExtProvider.provideExtensions(CExtContext) returned " // NOI18N
                                + "null, ignoring provider: "                                                        // NOI18N
                                + provider);
                } else if (extensions.size() >= 1) {
                    return extensions.iterator().next();
                }
            }
        }

        return null;
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * LazyInitialiser {@linkplain http://en.wikipedia.org/wiki/Singleton_pattern#The_solution_of_Bill_Pugh}.
     *
     * @version  $Revision$, $Date$
     */
    private static final class LazyInitialiser {

        //~ Static fields/initializers -----------------------------------------

        private static final CExtManager INSTANCE = new CExtManager();

        //~ Constructors -------------------------------------------------------

        /**
         * No instances needed.
         */
        private LazyInitialiser() {
        }
    }
}
