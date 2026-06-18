/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.commons.capabilities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.net.URL;

import java.util.HashMap;

import de.cismet.commons.security.AccessHandler;
import de.cismet.commons.security.handler.SimpleHttpAccessHandler;

import de.cismet.tools.CalculationCache;
import de.cismet.tools.Calculator;
import de.cismet.tools.TimeoutThread;

/**
 * Caches the result of capability requests.
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public class SimpleCapabilitiesCache extends CalculationCache<String, String> {

    //~ Static fields/initializers ---------------------------------------------

    private static String[] basicAuthorizationTokens = null;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new CapabilitiesCache object.
     */
    private SimpleCapabilitiesCache() {
        super(new TimeoutHttpRequestCalculator());
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static SimpleCapabilitiesCache getInstance() {
        return LazyInitialiser.INSTANCE;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   basicAuthorizationTokens  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static SimpleCapabilitiesCache getInstance(final String[] basicAuthorizationTokens) {
        SimpleCapabilitiesCache.basicAuthorizationTokens = basicAuthorizationTokens;
        return LazyInitialiser.INSTANCE;
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private static class TimeoutHttpRequestCalculator implements Calculator<String, String> {

        //~ Methods ------------------------------------------------------------

        @Override
        public String calculate(final String link) throws Exception {
            final TimeoutHttpRequester r = new TimeoutHttpRequester(link);
            final StringBuilder tmp;

            tmp = r.start(15000);

            return tmp.toString();
        }
    }

    /**
     * Performs a httpRequest with a maximum runtime.
     *
     * @version  $Revision$, $Date$
     */
    private static class TimeoutHttpRequester extends TimeoutThread<StringBuilder> {

        //~ Instance fields ----------------------------------------------------

        private final String url;
        private final String parameter;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new HttpRequester object.
         *
         * @param  url  DOCUMENT ME!
         */
        public TimeoutHttpRequester(final String url) {
            if (url.contains("?")) {
                this.url = url.substring(0, url.indexOf("?"));
                parameter = url.substring(url.indexOf("?") + 1);
            } else {
                this.url = url;
                parameter = "";
            }
        }

        //~ Methods ------------------------------------------------------------

        @Override
        public void run() {
            InputStream is = null;

            try {
                final StringBuilder sb = new StringBuilder("");                       // NOI18N
                if (LOG.isDebugEnabled()) {
                    LOG.debug("send Getcapabilities request to the service: " + url); // NOI18N
                }
                final URL getCapURL = new URL(url);

                int attempt = 0;
                boolean finished = true;
                String basicAuthenticationToken = null;

                do {
                    try {
                        finished = true;
                        final SimpleHttpAccessHandler simpleAccesshandler = new SimpleHttpAccessHandler();

                        if (basicAuthenticationToken != null) {
                            final HashMap<String, String> map = new HashMap<>();
                            map.put("authorization", "Basic " + basicAuthenticationToken);

                            is = simpleAccesshandler.doRequest(
                                    getCapURL,
                                    null,
                                    AccessHandler.ACCESS_METHODS.GET_REQUEST,
                                    map);
                        } else {
                            final URL CompleteUrl = (parameter.equals("") ? getCapURL : new URL(url + "?" + parameter));
                            is = simpleAccesshandler.doRequest(CompleteUrl);
                        }
                    } catch (IOException e) {
                        if (e.getMessage().contains("401")) {
                            if ((basicAuthorizationTokens == null)
                                        || (attempt >= basicAuthorizationTokens.length)) {
                                exception = e;
                                return;
                            } else {
                                basicAuthenticationToken = basicAuthorizationTokens[attempt++];
                                finished = false;
                            }
                        } else {
                            exception = e;
                            return;
                        }
                    }
                } while (!finished);

                if (Thread.interrupted()) {
                    return;
                }

                final BufferedReader br = new BufferedReader(new InputStreamReader(is));
                String buffer = null;

                while ((buffer = br.readLine()) != null) {
                    sb.append(buffer).append("\n"); // NOI18N
                }

                result = sb;
            } catch (Exception e) {
                exception = e;
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException ex) {
                        // nothing to do
                    }
                }
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private static final class LazyInitialiser {

        //~ Static fields/initializers -----------------------------------------

        private static final SimpleCapabilitiesCache INSTANCE = new SimpleCapabilitiesCache();

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new LazyInitialiser object.
         */
        private LazyInitialiser() {
        }
    }
}
