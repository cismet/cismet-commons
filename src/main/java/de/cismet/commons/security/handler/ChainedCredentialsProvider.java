/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.commons.security.handler;

import org.apache.hc.client5.http.auth.AuthScope;
import org.apache.hc.client5.http.auth.Credentials;
import org.apache.hc.client5.http.auth.CredentialsProvider;
import org.apache.hc.core5.http.protocol.HttpContext;

import java.util.List;

/**
 * DOCUMENT ME!
 *
 * @version  $Revision$, $Date$
 */
public class ChainedCredentialsProvider implements CredentialsProvider {

    //~ Instance fields --------------------------------------------------------

    private final List<CredentialsProvider> providers;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new ChainedCredentialsProvider object.
     *
     * @param  providers  DOCUMENT ME!
     */
    public ChainedCredentialsProvider(final List<CredentialsProvider> providers) {
        this.providers = providers;
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public Credentials getCredentials(final AuthScope authScope, final HttpContext context) {
        for (final CredentialsProvider credsProvider : providers) {
            final Credentials creds = credsProvider.getCredentials(authScope, context);

            if (creds != null) {
                return creds;
            }
        }

        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public InteractiveCredentialsProvider getInteractiveCredentialsProvider() {
        for (final CredentialsProvider credsProvider : providers) {
            if (credsProvider instanceof InteractiveCredentialsProvider) {
                return (InteractiveCredentialsProvider)credsProvider;
            }
        }

        return null;
    }
}
