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

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

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

        private String url;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new HttpRequester object.
         *
         * @param  url  DOCUMENT ME!
         */
        public TimeoutHttpRequester(final String url) {
            this.url = url;
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
                        URLConnection con = getCapURL.openConnection();
                        if (basicAuthenticationToken != null) {
                            con.addRequestProperty("authorization", "Basic " + basicAuthenticationToken);
                        }
                        if (con instanceof HttpURLConnection) {
                            final HttpURLConnection http = (HttpURLConnection)con;
                            if ((http.getResponseCode() == 301) || (http.getResponseCode() == 308)) {
                                con = new URL(http.getHeaderField("Location")).openConnection();
                            }
                        }
                        is = con.getInputStream();
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
