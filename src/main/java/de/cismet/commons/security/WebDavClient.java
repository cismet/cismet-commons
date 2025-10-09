/***************************************************
 *
 * cismet GmbH, Saarbruecken, Germany
 *
 *              ... and it just works.
 *
 ****************************************************/
package de.cismet.commons.security;

import org.apache.hc.client5.http.auth.AuthCache;
import org.apache.hc.client5.http.auth.AuthScope;
import org.apache.hc.client5.http.auth.NTCredentials;
import org.apache.hc.client5.http.auth.StandardAuthScheme;
import org.apache.hc.client5.http.auth.UsernamePasswordCredentials;
import org.apache.hc.client5.http.classic.methods.HttpDelete;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpHead;
import org.apache.hc.client5.http.classic.methods.HttpPut;
import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.auth.BasicAuthCache;
import org.apache.hc.client5.http.impl.auth.BasicCredentialsProvider;
import org.apache.hc.client5.http.impl.auth.BasicScheme;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.client5.http.utils.URIUtils;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.io.entity.FileEntity;
import org.apache.hc.core5.http.io.entity.InputStreamEntity;
import org.apache.hc.core5.util.Timeout;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.cismet.netutil.Proxy;

/**
 * Communicates with a web dav server.
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public class WebDavClient {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger log = Logger.getLogger(WebDavClient.class);
    private static final int MAX_HOST_CONNECTIONS = 20;

    //~ Instance fields --------------------------------------------------------

    private String username;
    private String password;
    private CloseableHttpClient client = null;
    private String currentHost = null;
    private Proxy proxy = null;
    private boolean useNTAuth;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new WebDavClient object.
     *
     * @param  proxy     the currently used proxy or null, if no proxy is used
     * @param  username  can be null, if no authentication is required
     * @param  password  can be null, if no authentication is required
     */
    public WebDavClient(final Proxy proxy, final String username, final String password) {
        this(proxy, username, password, false);
    }

    /**
     * Creates a new WebDavClient object.
     *
     * @param  proxy      DOCUMENT ME!
     * @param  username   DOCUMENT ME!
     * @param  password   DOCUMENT ME!
     * @param  useNTAuth  DOCUMENT ME!
     */
    public WebDavClient(final Proxy proxy, final String username, final String password, final boolean useNTAuth) {
        this.username = username;
        this.password = password;
        this.proxy = proxy;
        this.useNTAuth = useNTAuth;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param  host  DOCUMENT ME!
     */
    public void init(final String host) {
        init(host, 5000);
    }

    /**
     * initialises the http client for the given host.
     *
     * @param  host               DOCUMENT ME!
     * @param  connectionTimeout  DOCUMENT ME!
     */
    public void init(final String host, final int connectionTimeout) {
        if (log.isDebugEnabled()) {
            log.debug("initialise WebDavClient");
        }

        final PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager();

        connManager.setDefaultMaxPerRoute(MAX_HOST_CONNECTIONS);
        connManager.setMaxTotal(MAX_HOST_CONNECTIONS * 2);

        final RequestConfig.Builder requestConfigBuilder = RequestConfig.custom()
                    .setConnectTimeout(Timeout.ofMilliseconds(connectionTimeout))
                    .setResponseTimeout(Timeout.ofSeconds(60));

        final List<String> authSchemes = new ArrayList<>();
        authSchemes.add(StandardAuthScheme.DIGEST);
        authSchemes.add(StandardAuthScheme.BASIC);
        if (useNTAuth) {
            authSchemes.add(StandardAuthScheme.NTLM);
        }
        requestConfigBuilder.setTargetPreferredAuthSchemes(authSchemes);

        final BasicCredentialsProvider credsProvider = new BasicCredentialsProvider();

        if ((username != null) && (password != null)) {
            if (useNTAuth) {
                credsProvider.setCredentials(
                    new AuthScope(null, -1),
                    new NTCredentials(
                        username,
                        password.toCharArray(),
                        null,
                        null));
            } else {
                credsProvider.setCredentials(
                    new AuthScope(null, -1),
                    new UsernamePasswordCredentials(
                        username,
                        password.toCharArray()));
            }
        }

        if ((proxy != null) && proxy.isValid() && proxy.isEnabled()) {
            final HttpHost proxyHost = new HttpHost(proxy.getHost(), proxy.getPort());

            requestConfigBuilder.setProxy(proxyHost);

            if (proxy.getUsername() != null) {
                credsProvider.setCredentials(
                    new AuthScope(proxy.getHost(), proxy.getPort()),
                    new NTCredentials(
                        proxy.getUsername(),
                        proxy.getPassword().toCharArray(),
                        null,
                        proxy.getDomain()));
            }
        }

        client = HttpClients.custom().setConnectionManager(connManager).setDefaultCredentialsProvider(credsProvider)
                    .setDefaultRequestConfig(requestConfigBuilder.build())
                    .build();

        currentHost = host;

//        final HostConfiguration hostConfig = new HostConfiguration();
//        hostConfig.setHost(host);
//        final HttpConnectionManager connectionManager = new MultiThreadedHttpConnectionManager();
//        final HttpConnectionManagerParams params = new HttpConnectionManagerParams();
//        params.setMaxConnectionsPerHost(hostConfig, MAX_HOST_CONNECTIONS);
//        params.setConnectionTimeout(connectionTimeout);
//        connectionManager.setParams(params);
//        client = new HttpClient(connectionManager);
//        client.setHostConfiguration(hostConfig);
//        final List authPrefs = new ArrayList();
//        authPrefs.add(AuthPolicy.DIGEST);
//        authPrefs.add(AuthPolicy.BASIC);
//        if (useNTAuth) {
//            authPrefs.add(AuthPolicy.NTLM);
//        }
//        client.getParams().setParameter(AuthPolicy.AUTH_SCHEME_PRIORITY, authPrefs);
//
//        if ((username != null) && (password != null)) {
//            if (useNTAuth) {
//                final Credentials credentials = new NTCredentials(username, password, "", "");
//                client.getState().setCredentials(AuthScope.ANY, credentials);
//            } else {
//                final Credentials creds = new UsernamePasswordCredentials(username, password);
//                client.getState().setCredentials(AuthScope.ANY, creds);
//            }
//        }
//
//        if ((proxy != null) && proxy.isValid() && proxy.isEnabled()) {
//            if (log.isDebugEnabled()) {
//                log.debug("use proxy");
//            }
//            client.getHostConfiguration().setProxy(proxy.getHost(), proxy.getPort());
//
//            if (proxy.getUsername() != null) {
//                final AuthScope scope = new AuthScope(proxy.getHost(), proxy.getPort());
//                final Credentials credentials = new NTCredentials(
//                    proxy.getUsername(),
//                    proxy.getPassword(),
//                    "",
//                    proxy.getDomain()
//                );
//                client.getState().setProxyCredentials(scope, credentials);
//            }
//        }
//        currentHost = host;
    }

    /**
     * delete the given path.
     *
     * @param   path  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  MalformedURLException  DOCUMENT ME!
     * @throws  IOException            DOCUMENT ME!
     */
    public int delete(final String path) throws MalformedURLException, IOException {
        lazyInitialise(path);
        if (log.isDebugEnabled()) {
            log.debug("delete: " + path);
        }

        final HttpDelete delete = new HttpDelete(path);

        try(final CloseableHttpResponse response = client.execute(delete)) {
            return response.getCode();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   path  DOCUMENT ME!
     *
     * @return  an InputStream from the given path
     *
     * @throws  MalformedURLException  DOCUMENT ME!
     * @throws  IOException            DOCUMENT ME!
     * @throws  URISyntaxException     DOCUMENT ME!
     */
    public InputStream getInputStream(final String path) throws MalformedURLException, IOException, URISyntaxException {
        return getInputStream(path, null, null);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   path             DOCUMENT ME!
     * @param   responseHeaders  if this is not null, then the given map will be filled with the response headers
     * @param   statusValues     responseHeaders if this is not null, then the given map will be filled with status
     *                           code, line, text
     *
     * @return  an InputStream from the given path
     *
     * @throws  MalformedURLException  DOCUMENT ME!
     * @throws  IOException            DOCUMENT ME!
     * @throws  URISyntaxException     DOCUMENT ME!
     */
    public InputStream getInputStream(
            final String path,
            final Map<String, String> responseHeaders,
            final Map<String, Object> statusValues) throws MalformedURLException, IOException, URISyntaxException {
        lazyInitialise(path);
        if (log.isDebugEnabled()) {
            log.debug("get: " + path);
        }

        final HttpGet get = new HttpGet(new URI(path));

        final CloseableHttpResponse response = client.execute(get);

        // Response-Header
        if (responseHeaders != null) {
            for (final Header h : response.getHeaders()) {
                responseHeaders.put(h.getName(), h.getValue());
            }
        }

        // Statuswerte
        if (statusValues != null) {
            statusValues.put("code", response.getCode());
            statusValues.put("line", response.getReasonPhrase());
            statusValues.put("text", response.getReasonPhrase());
        }

        // ⚠️ WICHTIG:
        // Stream schließen schließt auch die Response
        return new ResponseInputStream(
                response.getEntity().getContent(),
                response);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   path             DOCUMENT ME!
     * @param   requestHeaders   DOCUMENT ME!
     * @param   responseHeaders  if this is not null, then the given map will be filled with the response headers
     * @param   statusValues     responseHeaders if this is not null, then the given map will be filled with status
     *                           code, line, text
     *
     * @return  an InputStream from the given path
     *
     * @throws  MalformedURLException  DOCUMENT ME!
     * @throws  IOException            DOCUMENT ME!
     * @throws  URISyntaxException     DOCUMENT ME!
     */
    public InputStream getInputStream(
            final String path,
            final Map<String, String> requestHeaders,
            final Map<String, String> responseHeaders,
            final Map<String, Object> statusValues) throws MalformedURLException, IOException, URISyntaxException {
        lazyInitialise(path);
        if (log.isDebugEnabled()) {
            log.debug("get: " + path);
        }

        final HttpGet get = new HttpGet(new URI(path));

        // Request-Header
        if (requestHeaders != null) {
            for (final Map.Entry<String, String> e : requestHeaders.entrySet()) {
                get.addHeader(e.getKey(), e.getValue());
            }
        }

        // Preemptive Auth (wie früher setAuthenticationPreemptive(true))
        final HttpClientContext context = createPreemptiveAuthContext(new URI(path));

        final CloseableHttpResponse response = client.execute(get, context);

        // Response-Header
        if (responseHeaders != null) {
            for (final Header h : response.getHeaders()) {
                responseHeaders.put(h.getName(), h.getValue());
            }
        }

        // Statuswerte
        if (statusValues != null) {
            statusValues.put("code", response.getCode());
            statusValues.put("line", response.getReasonPhrase());
            statusValues.put("text", response.getReasonPhrase());
        }

        // ⚠️ WICHTIG:
        // Stream schließen schließt auch die Response
        return new ResponseInputStream(
                response.getEntity().getContent(),
                response);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   uri  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private HttpClientContext createPreemptiveAuthContext(final URI uri) {
        final HttpHost targetHost = URIUtils.extractHost(uri);

        final AuthCache authCache = new BasicAuthCache();
        authCache.put(targetHost, new BasicScheme());

        final HttpClientContext context = HttpClientContext.create();
        context.setAuthCache(authCache);

        return context;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   url  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  IOException  DOCUMENT ME!
     */
    public int mkCol(final String url) throws IOException {
        lazyInitialise(url);

        try {
            final HttpMkCol mkcol = new HttpMkCol(new URI(url));

            try(final CloseableHttpResponse response = client.execute(mkcol)) {
                return response.getCode();
            }
        } catch (URISyntaxException e) {
            log.error("Wrong uri syntax", e);

            return 500;
        }
    }

    /**
     * Gets the http status code via an head request.
     *
     * @param   url  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  IOException  DOCUMENT ME!
     */
    public int getStatusCode(final String url) throws IOException {
        lazyInitialise(url);
        final HttpHead head = new HttpHead(url);

        try(final CloseableHttpResponse response = client.execute(head)) {
            return response.getCode();
        }
    }

    /**
     * copies the content of the given InputStream to the given path.
     *
     * @param   path   DOCUMENT ME!
     * @param   input  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  MalformedURLException  DOCUMENT ME!
     * @throws  IOException            DOCUMENT ME!
     */
    public int put(final String path, final InputStream input) throws MalformedURLException, IOException {
        lazyInitialise(path);

        if (log.isDebugEnabled()) {
            log.debug("put: " + path);
        }

        final HttpPut put = new HttpPut(path);
        put.setEntity(new InputStreamEntity(input, ContentType.APPLICATION_OCTET_STREAM));

        try(final CloseableHttpResponse response = client.execute(put)) {
            return response.getCode();
        }
    }

    /**
     * copies the content of the given file to the given path. This method does use preemptive authentication. So the
     * put request will be only sent once, even if authentication is required
     *
     * @param   path   DOCUMENT ME!
     * @param   input  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  MalformedURLException  DOCUMENT ME!
     * @throws  IOException            DOCUMENT ME!
     * @throws  URISyntaxException     DOCUMENT ME!
     */
    public int put(final String path, final File input) throws MalformedURLException, IOException, URISyntaxException {
        lazyInitialise(path);
        if (log.isDebugEnabled()) {
            log.debug("put: " + path);
        }

        final HttpPut put = new HttpPut(path);
        put.setEntity(new FileEntity(input, ContentType.APPLICATION_OCTET_STREAM));

        final HttpHost targetHost = URIUtils.extractHost(new URI(path));

        final AuthCache authCache = new BasicAuthCache();
        authCache.put(targetHost, new BasicScheme());

        try(final CloseableHttpResponse response = client.execute(put)) {
            return response.getCode();
        }
    }

    /**
     * checks if the HttpClient is initialised with the host of the given path and initialises the client if required.
     *
     * @param   path  DOCUMENT ME!
     *
     * @throws  MalformedURLException  DOCUMENT ME!
     */
    private void lazyInitialise(final String path) throws MalformedURLException {
        final int startIndex = path.indexOf("://") + "://".length();
        String host = null;

        if (startIndex != -1) {
            int endIndex = path.indexOf("/", startIndex);
            if (endIndex == -1) {
                endIndex = path.length();
            }
            host = path.substring(0, endIndex);
        } else {
            throw new MalformedURLException("Protocol not found in url " + path);
        }

        if (log.isDebugEnabled()) {
            log.debug("WebDav host: " + host);
        }

        // initialises the Httpclient if it is not initialised, yet, or the host was changed
        if ((client == null) || ((currentHost != null) && !currentHost.equals(host))) {
            init(host);
        }
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private static class HttpMkCol extends HttpUriRequestBase {

        //~ Static fields/initializers -----------------------------------------

        public static final String METHOD_NAME = "MKCOL";

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new HttpMkCol object.
         *
         * @param  uri  DOCUMENT ME!
         */
        public HttpMkCol(final URI uri) {
            super(METHOD_NAME, uri);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    public static class ResponseInputStream extends FilterInputStream {

        //~ Instance fields ----------------------------------------------------

        private final CloseableHttpResponse response;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new ResponseInputStream object.
         *
         * @param  in        DOCUMENT ME!
         * @param  response  DOCUMENT ME!
         */
        public ResponseInputStream(final InputStream in, final CloseableHttpResponse response) {
            super(in);
            this.response = response;
        }

        //~ Methods ------------------------------------------------------------

        @Override
        public void close() throws IOException {
            try {
                super.close();
            } finally {
                response.close();
            }
        }
    }
}
