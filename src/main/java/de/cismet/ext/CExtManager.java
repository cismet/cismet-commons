/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.ext;

import org.openide.util.Lookup;

import java.util.ArrayList;
import java.util.Collection;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
public class CExtManager {

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new CExtManager object.
     */
    private CExtManager() {
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static CExtManager getInstance() {
        return LazyInitialiser.INSTANCE;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   <T>      DOCUMENT ME!
     * @param   c        DOCUMENT ME!
     * @param   context  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public <T> Collection<T> getExtensionFor(final Class<T> c, final CExtContext context) {
        final ArrayList<T> extensions = new ArrayList<T>();
        // Get All Providers
        final Collection<? extends CExtProvider> allProviders = Lookup.getDefault().lookupAll(CExtProvider.class);

        for (final CExtProvider extProvider : allProviders) {
            if (c.equals(extProvider.getValidClass())) {
                extensions.addAll(extProvider.provideExtensions(context));
            }
        }

        return extensions;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   <T>      DOCUMENT ME!
     * @param   c        DOCUMENT ME!
     * @param   context  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  IllegalStateException  DOCUMENT ME!
     */
    public <T> T getExtension(final Class<T> c, final CExtContext context) {
        final Collection<? extends CExtProvider> providers = Lookup.getDefault().lookupAll(CExtProvider.class);

        for (final CExtProvider provider : providers) {
            // TODO: maybe assignable from would suit better
            if (c.equals(provider.getValidClass())) {
                final Collection<? extends T> extensions = provider.provideExtensions(context);
                if (extensions.size() == 1) {
                    return extensions.iterator().next();
                } else if (extensions.size() > 1) {
                    // TODO: what to do? shall we omit this operation? shall the caller be responsible for choosing the
                    // right instance? and how will he do that? is it legal to provide more than one extension for the
                    // same context?
                    throw new IllegalStateException();
                }
            }
        }

        return null;
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private static final class LazyInitialiser {

        //~ Static fields/initializers -----------------------------------------

        private static final CExtManager INSTANCE = new CExtManager();

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new LazyInitialiser object.
         */
        private LazyInitialiser() {
        }
    }
}
