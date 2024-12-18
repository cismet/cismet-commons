/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.commons.security;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import java.awt.Component;
import java.awt.image.BufferedImage;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import java.net.URLEncoder;

import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.event.IIOReadProgressListener;
import javax.imageio.stream.ImageInputStream;

import javax.swing.ProgressMonitor;
import javax.swing.ProgressMonitorInputStream;

/**
 * This class contains some static method, which are useful in conjunction with the WebDavClient.
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public class WebDavHelper {

    //~ Static fields/initializers ---------------------------------------------

    private static Logger LOG = Logger.getLogger(WebDavHelper.class);

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   prefix        DOCUMENT ME!
     * @param   originalFile  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static String generateWebDAVFileName(final String prefix, final File originalFile) {
        final String[] fileNameSplit = originalFile.getName().split("\\.");
        String webFileName = prefix + System.currentTimeMillis() + "-" + Math.abs(originalFile.getName().hashCode());
        if (fileNameSplit.length > 1) {
            final String ext = fileNameSplit[fileNameSplit.length - 1];
            webFileName += "." + ext;
        }
        return webFileName;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   fileName         DOCUMENT ME!
     * @param   toUpload         DOCUMENT ME!
     * @param   webDavDirectory  DOCUMENT ME!
     * @param   webDavClient     DOCUMENT ME!
     * @param   parent           DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  IOException  DOCUMENT ME!
     */
    public static int uploadFileToWebDAV(final String fileName,
            final File toUpload,
            final String webDavDirectory,
            final WebDavClient webDavClient,
            final Component parent) throws IOException {
        final BufferedInputStream bfis = new BufferedInputStream(new ProgressMonitorInputStream(
                    parent,
                    "Bild wird übertragen...",
                    new FileInputStream(toUpload)));
        try {
            return webDavClient.put(webDavDirectory + encodeURL(fileName), bfis);
        } finally {
            IOUtils.closeQuietly(bfis);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   fileName         DOCUMENT ME!
     * @param   toUpload         DOCUMENT ME!
     * @param   webDavDirectory  DOCUMENT ME!
     * @param   webDavClient     DOCUMENT ME!
     * @param   parent           DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  IOException  DOCUMENT ME!
     */
    public static int uploadFileToWebDAV(final String fileName,
            final InputStream toUpload,
            final String webDavDirectory,
            final WebDavClient webDavClient,
            final Component parent) throws IOException {
        final BufferedInputStream bfis = new BufferedInputStream(new ProgressMonitorInputStream(
                    parent,
                    "Daten werden übertragen...",
                    toUpload));
        try {
            return webDavClient.put(webDavDirectory + encodeURL(fileName), bfis);
        } finally {
            IOUtils.closeQuietly(bfis);
        }
    }

    /**
     * Uploads the given file and does not use a ProgressMonitorInputStream. It also use preemptive authentication. So
     * the put request will be only sent once, even if authentication is required
     *
     * @param   fileName         DOCUMENT ME!
     * @param   toUpload         DOCUMENT ME!
     * @param   webDavDirectory  DOCUMENT ME!
     * @param   webDavClient     DOCUMENT ME!
     * @param   parent           DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  IOException  DOCUMENT ME!
     */
    public static int uploadFileToWebDAVWithPreemptiveAuth(final String fileName,
            final File toUpload,
            final String webDavDirectory,
            final WebDavClient webDavClient,
            final Component parent) throws IOException {
        return webDavClient.put(webDavDirectory + encodeURL(fileName), toUpload);
    }

    /**
     * creates the given webdav collection (a sub directory in the file system), if it does not exists.
     *
     * @param   webDavDirectory  the webdav collection to create
     * @param   webDavClient     a webdav client
     *
     * @return  the http status code of the mkCol operation or 200, if the collection already exists.
     *
     * @throws  IOException  DOCUMENT ME!
     */
    public static int createFolder(final String webDavDirectory,
            final WebDavClient webDavClient) throws IOException {
        int status = webDavClient.getStatusCode(webDavDirectory);

        if (status == 404) {
            status = webDavClient.mkCol(webDavDirectory);

            if (status == 409) {
                if (webDavDirectory.endsWith("/")) {
                    String parentPath = webDavDirectory.substring(0, webDavDirectory.length() - 1);

                    if (parentPath.contains("/")) {
                        parentPath = parentPath.substring(0, parentPath.lastIndexOf("/") + 1);

                        status = createFolder(parentPath, webDavClient);

                        if (status == 201) {
                            return webDavClient.mkCol(webDavDirectory);
                        }
                    }
                }
            }
        }

        return status;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   webDavClient  DOCUMENT ME!
     * @param   url           DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static boolean isUrlAccessible(final WebDavClient webDavClient, final String url) {
        try {
            final int statusCode = webDavClient.getStatusCode(url);
            return 200 == statusCode;
        } catch (IOException ex) {
        }
        return false;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   fileName         DOCUMENT ME!
     * @param   webDavClient     DOCUMENT ME!
     * @param   webDavDirectory  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static boolean deleteFileFromWebDAV(final String fileName,
            final WebDavClient webDavClient,
            final String webDavDirectory) {
        if ((fileName != null) && (fileName.length() > 0)) {
            try {
                webDavClient.delete(webDavDirectory + encodeURL(fileName));
                return true;
            } catch (Exception ex) {
                LOG.error(ex, ex);
            }
        }
        return false;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   url  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static String encodeURL(final String url) {
        try {
            if (url == null) {
                return null;
            }
            final String[] tokens = url.split("/", -1);
            StringBuilder encodedURL = null;

            for (final String tmp : tokens) {
                if (encodedURL == null) {
                    encodedURL = new StringBuilder(URLEncoder.encode(tmp, "UTF-8"));
                } else {
                    encodedURL.append("/").append(URLEncoder.encode(tmp, "UTF-8"));
                }
            }

            if (encodedURL != null) {
                // replace all + with %20 because the method URLEncoder.encode() replaces all spaces with '+', but
                // the web dav client interprets %20 as a space.
                return encodedURL.toString().replaceAll("\\+", "%20");
            } else {
                return "";
            }
        } catch (final UnsupportedEncodingException e) {
            LOG.error("Unsupported encoding.", e);
        }
        return url;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   fileName         DOCUMENT ME!
     * @param   webDavDirectory  DOCUMENT ME!
     * @param   webDavClient     DOCUMENT ME!
     * @param   parent           DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  IOException  DOCUMENT ME!
     */
    public static BufferedImage downloadImageFromWebDAV(final String fileName,
            final String webDavDirectory,
            final WebDavClient webDavClient,
            final Component parent) throws IOException {
        return downloadImageFromWebDAV(fileName, webDavDirectory, webDavClient, parent, null);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   fileName          DOCUMENT ME!
     * @param   webDavDirectory   DOCUMENT ME!
     * @param   webDavClient      DOCUMENT ME!
     * @param   parent            DOCUMENT ME!
     * @param   progressListener  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  IOException  DOCUMENT ME!
     */
    public static BufferedImage downloadImageFromWebDAV(final String fileName,
            final String webDavDirectory,
            final WebDavClient webDavClient,
            final Component parent,
            final IIOReadProgressListener progressListener) throws IOException {
        final String encodedFileName = WebDavHelper.encodeURL(fileName);
        final InputStream iStream = webDavClient.getInputStream(webDavDirectory
                        + encodedFileName);
        if (LOG.isDebugEnabled()) {
            LOG.debug("original: " + fileName + "\nweb dav path: " + webDavDirectory + encodedFileName);
        }
        try {
            final ImageInputStream iiStream = ImageIO.createImageInputStream(iStream);
            final Iterator<ImageReader> itReader = ImageIO.getImageReaders(iiStream);
            final ImageReader reader = itReader.next();

            if (parent != null) {
                final ProgressMonitor monitor = new ProgressMonitor(parent, "Bild wird übertragen...", "", 0, 100);

                reader.addIIOReadProgressListener(new IIOReadProgressListener() {

                        @Override
                        public void sequenceStarted(final ImageReader source, final int minIndex) {
                        }

                        @Override
                        public void sequenceComplete(final ImageReader source) {
                        }

                        @Override
                        public void imageStarted(final ImageReader source, final int imageIndex) {
                            monitor.setProgress(monitor.getMinimum());
                        }

                        @Override
                        public void imageProgress(final ImageReader source, final float percentageDone) {
                            if (monitor.isCanceled()) {
                                try {
                                    iiStream.close();
                                } catch (IOException ex) {
                                    // NOP
                                }
                            } else {
                                monitor.setProgress(Math.round(percentageDone));
                            }
                        }

                        @Override
                        public void imageComplete(final ImageReader source) {
                            monitor.setProgress(monitor.getMaximum());
                        }

                        @Override
                        public void thumbnailStarted(final ImageReader source,
                                final int imageIndex,
                                final int thumbnailIndex) {
                        }

                        @Override
                        public void thumbnailProgress(final ImageReader source, final float percentageDone) {
                        }

                        @Override
                        public void thumbnailComplete(final ImageReader source) {
                        }

                        @Override
                        public void readAborted(final ImageReader source) {
                            monitor.close();
                        }
                    });
            }

            if (progressListener != null) {
                reader.addIIOReadProgressListener(progressListener);
            }

            final ImageReadParam param = reader.getDefaultReadParam();
            reader.setInput(iiStream, true, true);
            final BufferedImage result;
            try {
                result = reader.read(0, param);
            } finally {
                reader.dispose();
                iiStream.close();
            }
            return result;
        } finally {
            IOUtils.closeQuietly(iStream);
        }
    }
}
