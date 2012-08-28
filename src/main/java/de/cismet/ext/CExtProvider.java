/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.ext;

import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

import java.util.Collection;

/**
 * This interface can be used to provide extensions that can be loaded into applications that support the extension
 * mechanism. It is the main extension point of cismet applications. Implementations shall be put on the global
 * {@link Lookup} and will be collected on demand by the {@link CExtManager}. It is crucial that implementers take care
 * about the ordering/positioning within the <code>Lookup</code>. Thus they shall make appropriate use of the
 * {@link ServiceProvider#position()} property.
 *
 * @author   thorsten
 * @author   mscholl
 * @version  1.0, 2012/08/28
 * @see      Lookup
 * @see      ServiceProvider
 */
public interface CExtProvider<T> {

    //~ Methods ----------------------------------------------------------------

    /**
     * Shall provide extensions for the given context. Implementers are obliged to provide implementations of the
     * requested extension in the correct order if they intend to provide more than one implementation. "Correct order"
     * means that the implementations are ordered from most relevant to least relevant. E.g. an implementation of <code>
     * CExtProvider</code> plans to provide two implementations of <code>MyService</code>, a default one
     * (<code>DefaultMyService</code>) and a specific one for a special kind of context
     * (<code>SpecificMyService</code>). If this CExtProvider is requested to provide extensions with the afore
     * mentioned special kind of context, so that it matches the <code>DefaultMyService</code> and the <code>
     * SpecificMyService</code> requirements, the returned collection shall have the order <code>[SpecificMyService,
     * DefaultMyService]</code>. Additionally, if the extension provider will not provide any implementation for a
     * certain context it shall return an empty collection. This implies that this operation shall never return <code>
     * null</code>.
     *
     * @param   context  the context for which the extensions are requested, may be <code>null</code>
     *
     * @return  a collection of extension implementations, never <code>null</code>
     */
    Collection<? extends T> provideExtensions(CExtContext context);

    /**
     * Shall return a {@link Class} that indicates which kind of extensions this implementation of <code>
     * CExtProvider</code> delivers.
     *
     * @return  the type of the provided extensions, never <code>null</code>
     */
    Class<? extends T> getType();
}
