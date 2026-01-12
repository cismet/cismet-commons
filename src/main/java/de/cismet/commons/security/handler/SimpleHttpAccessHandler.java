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
package de.cismet.commons.security.handler;

import org.apache.commons.io.IOUtils;
import org.apache.hc.client5.http.auth.AuthScope;
import org.apache.hc.client5.http.auth.Credentials;
import org.apache.hc.client5.http.auth.CredentialsProvider;
import org.apache.hc.client5.http.auth.NTCredentials;
import org.apache.hc.client5.http.auth.UsernamePasswordCredentials;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpHead;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpUriRequest;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.client5.http.impl.auth.BasicCredentialsProvider;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.io.entity.InputStreamEntity;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.apache.hc.core5.util.Timeout;

import java.io.*;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.Reader;

import java.net.BindException;
import java.net.URL;

import java.nio.charset.StandardCharsets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import de.cismet.commons.security.AccessHandler;
import de.cismet.commons.security.AccessHandler.ACCESS_HANDLER_TYPES;
import de.cismet.commons.security.AccessHandler.ACCESS_METHODS;
import de.cismet.commons.security.WebDavClient;
import de.cismet.commons.security.exceptions.BadHttpStatusCodeException;
import de.cismet.commons.security.exceptions.CannotReadFromURLException;

import de.cismet.netutil.Proxy;
import de.cismet.netutil.ProxyHandler;

/**
 * The SimpleHTTPAccessHandler is a HTTPAccessHandler that uses no Credential Provider. It can therefore be used only
 * for resources with no authentication
 *
 * @author   spuhl, thorsten
 * @version  $Revision$, $Date$
 */
public class SimpleHttpAccessHandler extends AbstractAccessHandler implements ExtendedAccessHandler, ProxyCabaple {

    //~ Static fields/initializers ---------------------------------------------

    public static ACCESS_METHODS[] SUPPORTED_ACCESS_METHODS = new ACCESS_METHODS[] {
            ACCESS_METHODS.GET_REQUEST,
            ACCESS_METHODS.POST_REQUEST
        };
    public static final ACCESS_HANDLER_TYPES ACCESS_HANDLER_TYPE = ACCESS_HANDLER_TYPES.HTTP;
    private static final String USER_AGENT_HEADER_KEY = "User-Agent";

    //~ Enums ------------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    public enum PartType {

        //~ Enum constants -----------------------------------------------------

        STRING, FILE, STREAM
    }

    //~ Instance fields --------------------------------------------------------

    private final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(this.getClass());
    private transient Proxy proxy;
    private final int connectionTimeout;
    private final int soTimeout;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new DefaultHTTPAccessHandler object.
     */
    public SimpleHttpAccessHandler() {
        this(ProxyHandler.getInstance().getProxy(), 0, 0);
    }

    /**
     * Creates a new SimpleHttpAccessHandler object.
     *
     * @param  proxy  DOCUMENT ME!
     */
    public SimpleHttpAccessHandler(final Proxy proxy) {
        this(proxy, 0, 0);
    }

    /**
     * Creates a new SimpleHttpAccessHandler object.
     *
     * @param  connectionTimeout  DOCUMENT ME!
     * @param  soTimeout          DOCUMENT ME!
     */
    public SimpleHttpAccessHandler(final int connectionTimeout, final int soTimeout) {
        this(ProxyHandler.getInstance().getProxy(), connectionTimeout, soTimeout);
    }

    /**
     * Creates a new SimpleHttpAccessHandler object.
     *
     * @param  proxy              DOCUMENT ME!
     * @param  connectionTimeout  DOCUMENT ME!
     * @param  soTimeout          DOCUMENT ME!
     */
    public SimpleHttpAccessHandler(final Proxy proxy, final int connectionTimeout, final int soTimeout) {
        this.proxy = proxy;
        this.connectionTimeout = connectionTimeout;
        this.soTimeout = soTimeout;
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public InputStream doRequest(final URL url,
            final Reader requestParameter,
            final ACCESS_METHODS method,
            final HashMap<String, String> options) throws Exception {
        return doRequest(url, requestParameter, method, options, null);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   url               DOCUMENT ME!
     * @param   requestParameter  DOCUMENT ME!
     * @param   method            DOCUMENT ME!
     * @param   options           DOCUMENT ME!
     * @param   credentials       DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public InputStream doRequest(final URL url,
            final Reader requestParameter,
            final ACCESS_METHODS method,
            final HashMap<String, String> options,
            final UsernamePasswordCredentials credentials) throws Exception {
        return doRequest(url, requestParameter, "text/xml", method, options, credentials);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   url               DOCUMENT ME!
     * @param   requestParameter  DOCUMENT ME!
     * @param   requestHeader     DOCUMENT ME!
     * @param   method            DOCUMENT ME!
     * @param   options           DOCUMENT ME!
     * @param   credentials       DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public InputStream doRequest(final URL url,
            final Reader requestParameter,
            final String requestHeader,
            final ACCESS_METHODS method,
            final HashMap<String, String> options,
            final UsernamePasswordCredentials credentials) throws Exception {
        return doRequest(url, requestParameter, requestHeader, method, options, credentials, false);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   url               DOCUMENT ME!
     * @param   requestParameter  DOCUMENT ME!
     * @param   requestHeader     DOCUMENT ME!
     * @param   method            DOCUMENT ME!
     * @param   options           DOCUMENT ME!
     * @param   credentials       DOCUMENT ME!
     * @param   withHeaders       DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public InputStream doRequest(final URL url,
            final Reader requestParameter,
            final String requestHeader,
            final ACCESS_METHODS method,
            final HashMap<String, String> options,
            final UsernamePasswordCredentials credentials,
            final boolean withHeaders) throws Exception {
        final CloseableHttpClient client = getSecurityEnabledHttpClient(url, credentials);
        final StringBuilder parameter = new StringBuilder();

        final BufferedReader reader = new BufferedReader(requestParameter);

        String currentLine;
        while ((currentLine = reader.readLine()) != null) {
            parameter.append(currentLine);
        }

        HttpUriRequest httpMethod;

        if (LOG.isDebugEnabled()) {
            LOG.debug("Access method: '" + method + "'."); // NOI18N
        }

        switch (method) {
            case POST_REQUEST_NO_TUNNEL:
            case POST_REQUEST: {
                httpMethod = new HttpPost(url.toString());

                httpMethod.setEntity(new StringEntity(
                        parameter.toString(),
                        ContentType.create(requestHeader, StandardCharsets.UTF_8)));
//                ((PostMethod)httpMethod).setRequestEntity(new StringRequestEntity(
//                        parameter.toString(),
//                        requestHeader,
//                        "UTF-8"));                                                          // NOI18N
                break;
            }
            case GET_REQUEST_NO_TUNNEL:
            case GET_REQUEST: {
                if (parameter.length() > 0) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("HTTP GET: '" + url.toString() + "?" + parameter + "'."); // NOI18N
                    }

                    httpMethod = new HttpGet(url.toString() + "?" + parameter);                    // NOI18N
                } else {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("No parameters specified. HTTP GET: '" + url.toString() + "'."); // NOI18N
                    }

                    httpMethod = new HttpGet(url.toString());
                }
                break;
            }
            case HEAD_REQUEST_NO_TUNNEL:
            case HEAD_REQUEST: {
                if (parameter.length() > 0) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("HTTP HEAD: '" + url.toString() + "?" + parameter + "'."); // NOI18N
                    }

                    httpMethod = new HttpHead(url.toString() + "?" + parameter);                    // NOI18N
                } else {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("No parameters specified. HTTP HEAD: '" + url.toString() + "'."); // NOI18N
                    }

                    httpMethod = new HttpHead(url.toString());
                }
                break;
            }
            default: {
                if (parameter.length() > 0) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("No method specified, switching to '" + ACCESS_METHODS.GET_REQUEST
                                    + "'. URI used: '"
                                    + url.toString() + "?" + parameter + "'."); // NOI18N
                    }

                    // httpMethod = new PostMethod(url.toString()); ((PostMethod) httpMethod).setRequestEntity(new
                    // StringRequestEntity(parameter.toString(), "text/xml", "UTF-8"));
                    httpMethod = new HttpGet(url.toString() + "?" + parameter);                           // NOI18N
                } else {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("No method specified, switching to '" + ACCESS_METHODS.GET_REQUEST
                                    + "'. No parameters specified. URI used: '" + url.toString() + "'."); // NOI18N
                    }

                    httpMethod = new HttpGet(url.toString());
                }
            }
        }

        if ((options != null) && !options.isEmpty()) {
            for (final Entry<String, String> option : options.entrySet()) {
                httpMethod.addHeader(option.getKey(), option.getValue());
            }
        }
        final boolean hasBound = false;

        while (!hasBound) {
            try {
                // some urls are not reachable without a user agent
                httpMethod.addHeader(USER_AGENT_HEADER_KEY, "wunda");
                final int statuscode;
                final CloseableHttpResponse response;

                response = client.execute(httpMethod);
                statuscode = response.getCode();

                switch (statuscode) {
                    case (HttpStatus.SC_UNAUTHORIZED): {
                        if (LOG.isInfoEnabled()) {
                            LOG.info("HTTP status code from server: SC_UNAUTHORIZED (" + HttpStatus.SC_UNAUTHORIZED
                                        + ")."); // NOI18N
                        }

                        response.close();
                        throw new CannotReadFromURLException("You are not authorized to access this URL."); // NOI18N
                    }
                    case (HttpStatus.SC_OK): {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("HTTP status code from server: OK.");                                 // NOI18N
                        }
                        if ((method == ACCESS_METHODS.HEAD_REQUEST)
                                    || (method == ACCESS_METHODS.HEAD_REQUEST_NO_TUNNEL)) {
                            // returning the HTTP Header as InputStream, because some valid InputStream has to be
                            // returned. The HTTP body can not be returned because it does not exist for HEAD requests.
                            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            final ObjectOutputStream oos = new ObjectOutputStream(baos);

                            oos.writeObject(response.getHeaders());

                            oos.flush();
                            oos.close();

                            final InputStream is = new ByteArrayInputStream(baos.toByteArray());
                            baos.close();

                            response.close();
                            return is;
                        } else {
                            if (withHeaders) {
                                String contentType = "";
                                final InputStream is = response.getEntity().getContent();

                                for (final Header h : response.getHeaders()) {
                                    if (h.getName().equalsIgnoreCase("Content-Type")) {
                                        contentType = h.getValue();
                                    }
                                }

                                final ByteArrayOutputStream result = new ByteArrayOutputStream();

                                result.write(contentType.getBytes());
                                result.write("\n".getBytes("utf-8"));
                                result.write(IOUtils.toByteArray(is));

                                response.close();
                                return new ByteArrayInputStream(result.toByteArray());
                            } else {
                                return new BufferedInputStream(new WebDavClient.ResponseInputStream(
                                            response.getEntity().getContent(),
                                            response));
                            }
                        }
                    }
                    default: {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Unhandled HTTP status code: " + statuscode + " ("
                                        + response.getReasonPhrase()
                                        + ")"); // NOI18N
                        }
                        final String content = new String(IOUtils.toByteArray(response.getEntity().getContent()));

                        response.close();

                        throw new BadHttpStatusCodeException(httpMethod.getRequestUri(),
                            statuscode,
                            response.getReasonPhrase(),
                            content); // NOI18N
                    }
                }
            } catch (BindException e) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Catched Bind Exception. Will try again in 50 ms", e);
                }
                Thread.sleep(50);
            }
        }
//        throw new RuntimeException("Should never happen");
    }

    @Override
    public InputStream doRequest(final URL url,
            final InputStream requestParameter,
            final HashMap<String, String> requestHeader) throws Exception {
        final HttpPost postMethod = new HttpPost(url.toString());
        postMethod.setEntity(new InputStreamEntity(requestParameter, ContentType.APPLICATION_OCTET_STREAM));
        return doRequest(url, postMethod, requestHeader);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   url            DOCUMENT ME!
     * @param   parts          DOCUMENT ME!
     * @param   requestHeader  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public InputStream doMultipartRequest(final URL url,
            final Part[] parts,
            final HashMap<String, String> requestHeader) throws Exception {
        final HttpPost post = new HttpPost(url.toURI());
        final MultipartEntityBuilder builder = MultipartEntityBuilder.create();

        for (final Part part : parts) {
            if (PartType.STRING.equals(part.getType())) {
                builder.addTextBody(
                    part.getName(),
                    (String)part.getContent(),
                    ContentType.TEXT_PLAIN.withCharset(StandardCharsets.UTF_8));
            } else if (PartType.STREAM.equals(part.getType())) {
                builder.addBinaryBody(
                    part.getName(),
                    (InputStream)part.getContent());
            } else if (PartType.FILE.equals(part.getType())) {
                builder.addBinaryBody(
                    part.getName(),
                    (File)part.getContent(),
                    ContentType.APPLICATION_OCTET_STREAM,
                    ((File)part.getContent()).getName());
            }
        }

        final HttpEntity multipartEntity = builder.build();
        post.setEntity(multipartEntity);
        return doRequest(url, post, requestHeader);
    }

    @Override
    public void setProxy(final Proxy proxy) {
        this.proxy = proxy;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   url            DOCUMENT ME!
     * @param   postMethod     requestEntity DOCUMENT ME!
     * @param   requestHeader  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    private InputStream doRequest(final URL url,
            final HttpPost postMethod,
            final HashMap<String, String> requestHeader) throws Exception {
        final CloseableHttpClient client = getSecurityEnabledHttpClient(url, null);
        boolean hasUserAgent = false;

        if ((requestHeader != null) && !requestHeader.isEmpty()) {
            for (final Entry<String, String> option : requestHeader.entrySet()) {
                if (option.getKey().equalsIgnoreCase(USER_AGENT_HEADER_KEY)) {
                    hasUserAgent = true;
                }
                postMethod.addHeader(option.getKey(), option.getValue());
            }
        }
        final boolean hasBound = false;
        while (!hasBound) {
            try {
//                postMethod.setDoAuthentication(true);     //in version 5 not needed enymore
                // some urls are not reachable without a user agent
                if (!hasUserAgent) {
                    postMethod.addHeader(USER_AGENT_HEADER_KEY, "wunda");
                }

                final CloseableHttpResponse response = client.execute(postMethod);

                final int statuscode = response.getCode();

                switch (statuscode) {
                    case (HttpStatus.SC_UNAUTHORIZED): {
                        if (LOG.isInfoEnabled()) {
                            LOG.info("HTTP status code from server: SC_UNAUTHORIZED (" + HttpStatus.SC_UNAUTHORIZED
                                        + ")."); // NOI18N
                        }

                        response.close();
                        throw new CannotReadFromURLException("You are not authorized to access this URL."); // NOI18N
                    }
                    case (HttpStatus.SC_OK): {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("HTTP status code from server: OK.");                                 // NOI18N
                        }

                        return new BufferedInputStream(new WebDavClient.ResponseInputStream(
                                    response.getEntity().getContent(),
                                    response));
                    }
                    default: {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Unhandled HTTP status code: " + statuscode + " ("
                                        + response.getReasonPhrase()
                                        + ")"); // NOI18N
                        }

                        final String content = new String(IOUtils.toByteArray(response.getEntity().getContent()));

                        response.close();

                        throw new BadHttpStatusCodeException(postMethod.getRequestUri(),
                            statuscode,
                            response.getReasonPhrase(),
                            content); // NOI18N
                    }
                }
            } catch (BindException e) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Catched Bind Exception. Will try again in 50 ms", e);
                }
                Thread.sleep(50);
            }
        }
    }

    @Override
    public boolean isAccessMethodSupported(final ACCESS_METHODS method) {
        for (final ACCESS_METHODS curMethod : SUPPORTED_ACCESS_METHODS) {
            if (curMethod == method) {
                return true;
            }
        }
        return false;
    }

    @Override
    public ACCESS_HANDLER_TYPES getHandlerType() {
        return ACCESS_HANDLER_TYPE;
    }

    /**
     * Returns a configured HttpClient with (if set) proxy settings.
     *
     * @param   url          DOCUMENT ME!
     * @param   credentials  DOCUMENT ME!
     *
     * @return  configured HttpClient
     */
    protected CloseableHttpClient getSecurityEnabledHttpClient(final URL url,
            final UsernamePasswordCredentials credentials) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("getSecurityEnabledHttpClient"); // NOI18N
        }

        final PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager();

//        connManager.setDefaultMaxPerRoute(MAX_HOST_CONNECTIONS);
//        connManager.setMaxTotal(MAX_HOST_CONNECTIONS * 2);

        final RequestConfig.Builder requestConfig = RequestConfig.custom();

        final BasicCredentialsProvider credsProvider = new BasicCredentialsProvider();

        if ((proxy != null) && proxy.isEnabled()) {
            final HttpHost proxyHost = new HttpHost(proxy.getHost(), proxy.getPort());

            requestConfig.setProxy(proxyHost);

            // Proxy-Authentifizierung
            if ((proxy.getUsername() != null) && (proxy.getPassword() != null)) {
                credsProvider.setCredentials(
                    new AuthScope(proxy.getHost(), proxy.getPort()),
                    new NTCredentials(
                        proxy.getUsername(),
                        proxy.getPassword().toCharArray(),
                        null,
                        proxy.getDomain()));
            }
        }

        if (connectionTimeout >= 0) {
            requestConfig.setConnectTimeout(
                Timeout.ofMilliseconds(connectionTimeout));
        }

        if (soTimeout >= 0) {
            requestConfig.setResponseTimeout(
                Timeout.ofMilliseconds(soTimeout));
        }

        if (credentials != null) {
            final List<CredentialsProvider> providers = new ArrayList<>();
            final BasicCredentialsProvider customCredsProvider = new BasicCredentialsProvider();

            customCredsProvider.setCredentials(new AuthScope(null, -1), credentials); // = AuthScope.ANY

            providers.add(credsProvider);
            providers.add(customCredsProvider);

            final ChainedCredentialsProvider chainedProvider = new ChainedCredentialsProvider(providers);

            return HttpClients.custom()
                        .setConnectionManager(connManager)
                        .setDefaultCredentialsProvider(chainedProvider)
                        .setDefaultRequestConfig(requestConfig.build())
                        .build();
        } else {
            return HttpClients.custom()
                        .setConnectionManager(connManager)
                        .setDefaultCredentialsProvider(credsProvider)
                        .setDefaultRequestConfig(requestConfig.build())
                        .build();
        }
    }

    @Override
    public InputStream doRequest(final URL url) throws Exception {
        if (LOG.isDebugEnabled()) {
            LOG.debug("URL: " + url + "... trying to retrieve parameters automatically by HTTP_GET");       // NOI18N
        }
        URL serviceURL;
        String requestParameter;
        try {
            final String urlString = url.toString();
            if (urlString.indexOf('?') != -1) {
                serviceURL = new URL(urlString.substring(0, urlString.indexOf('?')));                       // NOI18N
                if (LOG.isDebugEnabled()) {
                    LOG.debug("service URL: " + serviceURL);                                                // NOI18N
                }
                if ((urlString.indexOf('?') + 1) < urlString.length()) {                                    // NOI18N
                    requestParameter = urlString.substring(urlString.indexOf('?') + 1, urlString.length()); // NOI18N
                    if (requestParameter.toLowerCase().contains("service=wss")) {                           // NOI18N
                        // TODO muss auch wfs fähig sein
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("query default WMS");                       // NOI18N
                        }
                        requestParameter = "REQUEST=GetCapabilities&service=WMS"; // NOI18N
                    }
                } else {
                    requestParameter = "";                                        // NOI18N
                }

                if (LOG.isDebugEnabled()) {
                    LOG.debug("requestParameter: " + requestParameter);               // NOI18N
                }
            } else {
                LOG.warn("Not able to parse requestparameter (no ?) trying without"); // NOI18N
                serviceURL = url;
                requestParameter = "";                                                // NOI18N
            }
        } catch (Exception ex) {
            // final String errorMessage = "Exception während dem bestimmen der Request Parameter";
            final String errorMessage = "Request parameters coud not be parsed: " + ex.getMessage(); // NOI18N
            LOG.error(errorMessage);
            throw new Exception(errorMessage, ex);
        }
        return doRequest(serviceURL, new StringReader(requestParameter), AccessHandler.ACCESS_METHODS.GET_REQUEST);
    }

    @Override
    public boolean checkIfURLaccessible(final URL url) {
        boolean urlAccessible = false;
        InputStream inputStream = null;
        try {
            inputStream = doRequest(url, new StringReader(""), AccessHandler.ACCESS_METHODS.HEAD_REQUEST);
            urlAccessible = inputStream != null;
        } catch (final Exception ex) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("An exception occurred while opening URL '" + url.toExternalForm()
                            + "'.",
                    ex);
            }
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException ex) {
                    LOG.warn("Could not close stream.", ex);
                }
            }
        }
        return urlAccessible;
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    public static class Part {

        //~ Instance fields ----------------------------------------------------

        private final String name;
        private final Object content;
        private final PartType type;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new Part object.
         *
         * @param  name     DOCUMENT ME!
         * @param  content  DOCUMENT ME!
         * @param  type     DOCUMENT ME!
         */
        public Part(final String name, final Object content, final PartType type) {
            this.name = name;
            this.content = content;
            this.type = type;
        }

        //~ Methods ------------------------------------------------------------

        /**
         * DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        public String getName() {
            return name;
        }

        /**
         * DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        public Object getContent() {
            return content;
        }

        /**
         * DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        public PartType getType() {
            return type;
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
