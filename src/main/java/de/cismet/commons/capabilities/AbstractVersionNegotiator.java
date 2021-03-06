/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.commons.capabilities;

import org.apache.log4j.Logger;

import java.net.MalformedURLException;

import de.cismet.tools.CalculationCache;

/**
 * DOCUMENT ME!
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public abstract class AbstractVersionNegotiator {

    //~ Static fields/initializers ---------------------------------------------

    private static Logger logger = Logger.getLogger(AbstractVersionNegotiator.class);
    private static final String VERSION_STRING = "version"; // NOI18N

    //~ Instance fields --------------------------------------------------------

    // the constant SUPPORTED_VERSIONS contains all supported service versions. The array should
    // be sorted and the first elements of the array should contain the oldest version.
    protected String[] supportedVersions; // NOI18N
    protected String currentVersion = null;
    protected String serviceName;
    protected boolean headless = false;
    protected String[] basicAuthorizationTokens = null;
    protected CalculationCache<String, String> capabilitiesCache;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new AbstractVersionNegotiator object.
     *
     * @param  capabilitiesCache  DOCUMENT ME!
     */
    public AbstractVersionNegotiator(final CalculationCache<String, String> capabilitiesCache) {
        this.capabilitiesCache = capabilitiesCache;
        initVersion();
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * This m,ethod must be overidden by the sub classes and should initialize the constant SUPPORTED_VERSIONS.
     */
    protected abstract void initVersion();

    /**
     * Invokes the GetCapabilities operation of the server with an older version string.
     *
     * @param   link  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  MalformedURLException  DOCUMENT ME!
     * @throws  Exception              DOCUMENT ME!
     */
    protected String getOlderCapabilitiesDocument(String link) throws MalformedURLException, Exception {
        final StringBuilder document;
        boolean olderVersionFound = false;
        if (logger.isDebugEnabled()) {
            logger.debug("try to use an older version number"); // NOI18N
        }
        for (int i = 1; i < supportedVersions.length; ++i) {
            if (supportedVersions[i].equals(currentVersion)) {
                currentVersion = supportedVersions[i - 1];
                olderVersionFound = true;
            }
        }

        if (olderVersionFound) {
            if (logger.isDebugEnabled()) {
                logger.debug("Older version found " + currentVersion);     // NOI18N
            }
            if (link.toLowerCase().indexOf("?") != -1) {                   // NOI18N
                link = link.substring(0, link.toLowerCase().indexOf("?")); // NOI18N
            }

            link += "?SERVICE=" + serviceName + "&REQUEST=GetCapabilities&VERSION=" + currentVersion; // NOI18N
            document = readStringFromlink(link);

            return document.toString();
        }

        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   link  DOCUMENT ME!
     *
     * @return  the capabilities document of the service with the given link. If the link already contains a version,
     *          then this version will be used as start version for the version negotation. Otherwise, the most recent
     *          version, that is supported by the client will be used as start version.
     *
     * @throws  MalformedURLException  DOCUMENT ME!
     * @throws  Exception              DOCUMENT ME!
     */
    protected String getCapabilitiesDocument(String link) throws MalformedURLException, Exception {
        String startVersion = getVersionFromLink(link);
        final StringBuilder document;

        if (startVersion == null) {
            if (logger.isDebugEnabled()) {
                logger.debug("No version string found in the link: " + link); // NOI18N
            }
            // set the version to the latest one
            startVersion = supportedVersions[supportedVersions.length - 1];
        }

        if (link.indexOf("?") != -1) { // NOI18N
            // some GetCapabilities links contains special parameter, which should not cut off
            if (link.toLowerCase().indexOf("version") == -1) { // NOI18N
                link += "&VERSION=" + startVersion;
            } else {
                String linkTmp = link.substring(0, link.toLowerCase().indexOf("version") + "version".length());
                linkTmp += "=" + startVersion;
                int indexAfterVersionString = link.toLowerCase().indexOf("version");
                for (; indexAfterVersionString < link.length(); ++indexAfterVersionString) {
                    if (link.charAt(indexAfterVersionString) == '&') {
                        break;
                    }
                }
                if (indexAfterVersionString < link.length()) {
                    linkTmp += link.substring(indexAfterVersionString);
                }

                link = linkTmp;
            }
        } else {
            link += "?SERVICE=" + serviceName + "&REQUEST=GetCapabilities&VERSION=" + startVersion; // NOI18N
        }

        if (link.indexOf("?") != -1) {
            final String parameter = link.substring(link.indexOf("?"));

            if (parameter.toLowerCase().indexOf("request") == -1) {
                link += "&REQUEST=GetCapabilities";
            }

            if (parameter.toLowerCase().indexOf("service") == -1) {
                link += "&SERVICE=" + serviceName;
            }
        }

        if (logger.isDebugEnabled()) {
            logger.debug("send request = " + link);          // NOI18N
            logger.debug("start version = " + startVersion); // NOI18N
        }
        document = readStringFromlink(link);
        currentVersion = getDocumentVersion(document);

        if (currentVersion == null) {
            logger.error("No version string found in the GetCapabilities document from location " + link); // NOI18N
            // try to parse the document
            return document.toString();
        }

        if (!isVersionSupported(currentVersion)) {
            logger.error("The client does not support the version of the received Getcapabilities document." // NOI18N
                        + "\nLink: " + link + "\nresponse version " + currentVersion);              // NOI18N
        } else {
            if (logger.isDebugEnabled()) {
                logger.debug("Version negotation successfully. \nLink: "                            // NOI18N
                            + link + "\nresponse version " + currentVersion);                       // NOI18N
            }
        }

        return document.toString();
    }

    /**
     * DOCUMENT ME!
     *
     * @param   link  DOCUMENT ME!
     *
     * @return  the version string of the given link or null, if the given link does not contain any version string
     */
    protected String getVersionFromLink(final String link) {
        String version = null;

        if (link.toLowerCase().indexOf(VERSION_STRING) != -1) {
            version = link.substring(link.toLowerCase().indexOf(VERSION_STRING) + VERSION_STRING.length() + 1);

            if (version.indexOf("&") != -1) {                         // NOI18N
                version = version.substring(0, version.indexOf("&")); // NOI18N
            }
        }

        return version;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   version  the version that should be checked
     *
     * @return  true, if the given version is contained in the array constant <code>SUPPORTED_VERSIONS</code>
     */
    private boolean isVersionSupported(final String version) {
        for (int i = 0; i < supportedVersions.length; ++i) {
            if (supportedVersions[i].equals(version)) {
                return true;
            }
        }

        return false;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   document  the GetCapabilities document
     *
     * @return  the version of the given document or null, if no version string found
     */
    protected String getDocumentVersion(final StringBuilder document) {
        String documentVersion = null;
        int startIndexOfVersion = -1;
        String endCharacter = "\"";

        if (document.indexOf("?>") != -1) {                                                                   // NOI18N
            if (document.indexOf(VERSION_STRING + "=\"", document.indexOf("?>")) == -1) {
                startIndexOfVersion = document.indexOf(VERSION_STRING + "='", document.indexOf("?>"))         // NOI18N
                            + VERSION_STRING.length() + 2;                                                    // NOI18N
                endCharacter = "'";
            } else {
                startIndexOfVersion = document.indexOf(VERSION_STRING + "=\"", document.indexOf("?>"))        // NOI18N
                            + VERSION_STRING.length() + 2;                                                    // NOI18N
            }
        } else {
            if (document.indexOf(VERSION_STRING + "=\"") == -1) {
                startIndexOfVersion = document.indexOf(VERSION_STRING + "='") + VERSION_STRING.length() + 2;  // NOI18N
                endCharacter = "'";
            } else {
                startIndexOfVersion = document.indexOf(VERSION_STRING + "=\"") + VERSION_STRING.length() + 2; // NOI18N
            }
        }

        if (startIndexOfVersion != (VERSION_STRING.length() + 1)) {
            // version string in document found
            final int endIndexOfVersion = document.indexOf(endCharacter, startIndexOfVersion); // NOI18N
            documentVersion = document.substring(startIndexOfVersion, endIndexOfVersion);
        }
        if (logger.isDebugEnabled()) {
            logger.debug("version of received GetCapabilities document: " + documentVersion);  // NOI18N
        }
        return documentVersion;
    }

    /**
     * read the document from the given link.
     *
     * @param   url  DOCUMENT ME!
     *
     * @return  a StringBuilder that contains the content of the given url
     *
     * @throws  MalformedURLException  DOCUMENT ME!
     * @throws  Exception              DOCUMENT ME!
     */
    protected StringBuilder readStringFromlink(final String url) throws MalformedURLException, Exception {
        return new StringBuilder(capabilitiesCache.calcValue(url));
    }
}
