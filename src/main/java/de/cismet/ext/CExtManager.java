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

    //~ Methods ----------------------------------------------------------------

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
}
