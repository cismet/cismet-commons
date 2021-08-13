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

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.NTCredentials;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScheme;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.auth.CredentialsNotAvailableException;
import org.apache.commons.httpclient.auth.CredentialsProvider;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.HeadMethod;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;

import java.io.*;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.Reader;

import java.net.BindException;
import java.net.URL;

import java.util.HashMap;
import java.util.Map.Entry;

import de.cismet.commons.security.AccessHandler;
import de.cismet.commons.security.AccessHandler.ACCESS_HANDLER_TYPES;
import de.cismet.commons.security.AccessHandler.ACCESS_METHODS;
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
        
    public InputStream doRequest(final URL url,
            final Reader requestParameter,
            final String requestHeader,
            final ACCESS_METHODS method,
            final HashMap<String, String> options,
            final UsernamePasswordCredentials credentials) throws Exception {
        
        final HttpClient client = getSecurityEnabledHttpClient(url);
        final StringBuilder parameter = new StringBuilder();

        final BufferedReader reader = new BufferedReader(requestParameter);

        String currentLine;
        while ((currentLine = reader.readLine()) != null) {
            parameter.append(currentLine);
        }

        HttpMethod httpMethod;

        if (LOG.isDebugEnabled()) {
            LOG.debug("Access method: '" + method + "'."); // NOI18N
        }

        switch (method) {
            case POST_REQUEST_NO_TUNNEL:
            case POST_REQUEST: {
                httpMethod = new PostMethod(url.toString());
                ((PostMethod)httpMethod).setRequestEntity(new StringRequestEntity(
                        parameter.toString(),
                        requestHeader,
                        "UTF-8"));                                                          // NOI18N
                break;
            }
            case GET_REQUEST_NO_TUNNEL:
            case GET_REQUEST: {
                if (parameter.length() > 0) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("HTTP GET: '" + url.toString() + "?" + parameter + "'."); // NOI18N
                    }

                    httpMethod = new GetMethod(url.toString() + "?" + parameter);                  // NOI18N
                } else {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("No parameters specified. HTTP GET: '" + url.toString() + "'."); // NOI18N
                    }

                    httpMethod = new GetMethod(url.toString());
                }
                break;
            }
            case HEAD_REQUEST_NO_TUNNEL:
            case HEAD_REQUEST: {
                if (parameter.length() > 0) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("HTTP HEAD: '" + url.toString() + "?" + parameter + "'."); // NOI18N
                    }

                    httpMethod = new HeadMethod(url.toString() + "?" + parameter);                  // NOI18N
                } else {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("No parameters specified. HTTP HEAD: '" + url.toString() + "'."); // NOI18N
                    }

                    httpMethod = new HeadMethod(url.toString());
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
                    httpMethod = new GetMethod(url.toString() + "?" + parameter);                         // NOI18N
                } else {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("No method specified, switching to '" + ACCESS_METHODS.GET_REQUEST
                                    + "'. No parameters specified. URI used: '" + url.toString() + "'."); // NOI18N
                    }

                    httpMethod = new GetMethod(url.toString());
                }
            }
        }

        if (credentials != null) {
            client.getState().setCredentials(AuthScope.ANY, credentials);
        }
        if ((options != null) && !options.isEmpty()) {
            for (final Entry<String, String> option : options.entrySet()) {
                httpMethod.addRequestHeader(option.getKey(), option.getValue());
            }
        }
        final boolean hasBound = false;
        while (!hasBound) {
            try {
                httpMethod.setDoAuthentication(true);

                final int statuscode = client.executeMethod(httpMethod);
                switch (statuscode) {
                    case (HttpStatus.SC_UNAUTHORIZED): {
                        if (LOG.isInfoEnabled()) {
                            LOG.info("HTTP status code from server: SC_UNAUTHORIZED (" + HttpStatus.SC_UNAUTHORIZED
                                        + ")."); // NOI18N
                        }

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

                            oos.writeObject(httpMethod.getResponseHeaders());

                            oos.flush();
                            oos.close();

                            final InputStream is = new ByteArrayInputStream(baos.toByteArray());
                            baos.close();
                            return is;
                        } else {
                            return new BufferedInputStream(httpMethod.getResponseBodyAsStream());
                        }
                    }
                    default: {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Unhandled HTTP status code: " + statuscode + " ("
                                        + HttpStatus.getStatusText(statuscode)
                                        + ")"); // NOI18N
                        }

                        throw new BadHttpStatusCodeException(httpMethod.getURI().toString(),
                            statuscode,
                            HttpStatus.getStatusText(statuscode),
                            httpMethod.getResponseBodyAsString()); // NOI18N
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
        final PostMethod postMethod = new PostMethod(url.toString());
        postMethod.setRequestEntity(new InputStreamRequestEntity(requestParameter));
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
        final PostMethod postMethod = new PostMethod(url.toString());
        final MultipartRequestEntity requestEntity = new MultipartRequestEntity(parts, postMethod.getParams());
        postMethod.addRequestHeader("Content-Type", requestEntity.getContentType());
        postMethod.setRequestEntity(requestEntity);
        return doRequest(url, postMethod, requestHeader);
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
            final PostMethod postMethod,
            final HashMap<String, String> requestHeader) throws Exception {
        final HttpClient client = getSecurityEnabledHttpClient(url);

        if ((requestHeader != null) && !requestHeader.isEmpty()) {
            for (final Entry<String, String> option : requestHeader.entrySet()) {
                postMethod.addRequestHeader(option.getKey(), option.getValue());
            }
        }
        final boolean hasBound = false;
        while (!hasBound) {
            try {
                postMethod.setDoAuthentication(true);

                final int statuscode = client.executeMethod(postMethod);
                switch (statuscode) {
                    case (HttpStatus.SC_UNAUTHORIZED): {
                        if (LOG.isInfoEnabled()) {
                            LOG.info("HTTP status code from server: SC_UNAUTHORIZED (" + HttpStatus.SC_UNAUTHORIZED
                                        + ")."); // NOI18N
                        }

                        throw new CannotReadFromURLException("You are not authorized to access this URL."); // NOI18N
                    }
                    case (HttpStatus.SC_OK): {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("HTTP status code from server: OK.");                                 // NOI18N
                        }

                        return new BufferedInputStream(postMethod.getResponseBodyAsStream());
                    }
                    default: {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Unhandled HTTP status code: " + statuscode + " ("
                                        + HttpStatus.getStatusText(statuscode)
                                        + ")."); // NOI18N
                        }

                        throw new BadHttpStatusCodeException(postMethod.getURI().toString(),
                            statuscode,
                            HttpStatus.getStatusText(statuscode),
                            postMethod.getResponseBodyAsString()); // NOI18N
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
     * DOCUMENT ME!
     *
     * @param   url  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    protected HttpClient getSecurityEnabledHttpClient(final URL url) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("getSecurityEnabledHttpClient"); // NOI18N
        }
        final HttpClient client = getConfiguredHttpClient();
        client.getParams().setParameter(CredentialsProvider.PROVIDER, new CredentialsProvider() {

                @Override
                public Credentials getCredentials(final AuthScheme scheme,
                        final String host,
                        final int port,
                        final boolean proxy) throws CredentialsNotAvailableException {
                    return null;
                }
            });

        if (connectionTimeout >= 0) {
            client.getHttpConnectionManager().getParams().setConnectionTimeout(connectionTimeout);
        }
        if (soTimeout >= 0) {
            client.getHttpConnectionManager().getParams().setSoTimeout(soTimeout);
        }
        return client;
    }

    /**
     * Returns a configured HttpClient with (if set) proxy settings.
     *
     * @return  configured HttpClient
     */
    protected HttpClient getConfiguredHttpClient() {
        if (LOG.isDebugEnabled()) {
            LOG.debug("getConfiguredHttpClient"); // NOI18N
        }

        final MultiThreadedHttpConnectionManager connectionManager = new MultiThreadedHttpConnectionManager();
        final HttpClient client = new HttpClient(connectionManager);
        if (proxy != null) {
            client.getHostConfiguration().setProxy(proxy.getHost(), proxy.getPort());

            // proxy needs authentication
            if ((proxy.getUsername() != null) && (proxy.getPassword() != null)) {
                final AuthScope authscope = new AuthScope(proxy.getHost(), proxy.getPort());
                final Credentials credentials = new NTCredentials(proxy.getUsername(),
                        proxy.getPassword(),
                        "", // NOI18N
                        (proxy.getDomain() == null) ? "" : proxy.getDomain());
                client.getState().setProxyCredentials(authscope, credentials);
            }
        }

        return client;
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
}
