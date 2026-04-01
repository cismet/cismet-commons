/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.commons.utils;

import org.apache.commons.io.IOUtils;

import java.awt.image.BufferedImage;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import java.lang.ref.SoftReference;

import java.net.URL;

import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import de.cismet.commons.security.handler.ExtendedAccessHandler;
import de.cismet.commons.security.handler.SimpleHttpAccessHandler;

/**
 * DOCUMENT ME!
 *
 * @version  $Revision$, $Date$
 */
public class MultiPagePictureReader {

    //~ Static fields/initializers ---------------------------------------------

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(MultiPagePictureReader.class);
    private static final int MB = 1024 * 1024;
    public static final String CODEC_JPEG = "jpeg"; // NOI18N
    public static final String CODEC_TIFF = "tiff"; // NOI18N

    //~ Instance fields --------------------------------------------------------

    private final String pathOfImage;
    private final int pageCount;
    private final SoftReference<BufferedImage>[] cache;
    private final boolean caching;
    private final boolean checkHeapSize;
    private final String codec;
    private final ImageReader reader;
    private ImageInputStream iis;
    private File imageFile = null;
    private ByteArrayInputStream bais = null;
    private InputStream stream;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new MultiPagePictureReader object.
     *
     * @param   imageFile  DOCUMENT ME!
     *
     * @throws  IOException  DOCUMENT ME!
     */
    public MultiPagePictureReader(final File imageFile) throws IOException {
        this(imageFile, new SimpleHttpAccessHandler());
    }

    /**
     * Creates a new MultiPagePictureReader object.
     *
     * @param   imageURL  DOCUMENT ME!
     *
     * @throws  IOException  DOCUMENT ME!
     */
    public MultiPagePictureReader(final URL imageURL) throws IOException {
        this(imageURL, new SimpleHttpAccessHandler());
    }

    /**
     * Creates a new MultiPagePictureReader object.
     *
     * @param   imageFile              DOCUMENT ME!
     * @param   extendedAccessHandler  DOCUMENT ME!
     *
     * @throws  IOException  DOCUMENT ME!
     */
    public MultiPagePictureReader(final File imageFile, final ExtendedAccessHandler extendedAccessHandler)
            throws IOException {
        this(imageFile, true, false, extendedAccessHandler);
    }

    /**
     * Creates a new MultiPagePictureReader object.
     *
     * @param   imageURL               DOCUMENT ME!
     * @param   extendedAccessHandler  DOCUMENT ME!
     *
     * @throws  IOException  DOCUMENT ME!
     */
    public MultiPagePictureReader(final URL imageURL, final ExtendedAccessHandler extendedAccessHandler)
            throws IOException {
        this(imageURL, true, false, extendedAccessHandler);
    }

    /**
     * Creates a new MultiPagePictureReader object.
     *
     * @param   imageFile      DOCUMENT ME!
     * @param   caching        DOCUMENT ME!
     * @param   checkHeapSize  DOCUMENT ME!
     *
     * @throws  IOException  DOCUMENT ME!
     */
    public MultiPagePictureReader(final File imageFile, final boolean caching, final boolean checkHeapSize)
            throws IOException {
        this(imageFile, caching, checkHeapSize, new SimpleHttpAccessHandler());
    }

    /**
     * Creates a new MultiPagePictureReader object.
     *
     * @param   imageURL       DOCUMENT ME!
     * @param   caching        DOCUMENT ME!
     * @param   checkHeapSize  DOCUMENT ME!
     *
     * @throws  IOException  DOCUMENT ME!
     */
    public MultiPagePictureReader(final URL imageURL, final boolean caching, final boolean checkHeapSize)
            throws IOException {
        this(imageURL, caching, checkHeapSize, new SimpleHttpAccessHandler());
    }

    /**
     * Creates a new MultiPagePictureReader object.
     *
     * @param   imageFile              DOCUMENT ME!
     * @param   caching                DOCUMENT ME!
     * @param   checkHeapSize          DOCUMENT ME!
     * @param   extendedAccessHandler  DOCUMENT ME!
     *
     * @throws  IOException  DOCUMENT ME!
     */
    public MultiPagePictureReader(final File imageFile,
            final boolean caching,
            final boolean checkHeapSize,
            final ExtendedAccessHandler extendedAccessHandler) throws IOException {
        if ((imageFile == null) || !imageFile.isFile() || !imageFile.canRead()) {
            throw new IOException("Could not open file: " + imageFile); // NOI18N
        }

        codec = getCodecString(imageFile.getName());
        if (codec == null) {
            throw new IOException("Unsupported filetype: " + imageFile.getAbsolutePath()
                        + " is not a tiff or jpeg file!"); // NOI18N
        }

        pathOfImage = imageFile.getAbsolutePath();
        this.caching = caching;
        this.checkHeapSize = checkHeapSize;

        iis = ImageIO.createImageInputStream(imageFile);
        this.imageFile = imageFile;

        final Iterator<ImageReader> readers = ImageIO.getImageReaders(iis);
        reader = readers.next();
        reader.setInput(iis);

        pageCount = reader.getNumImages(true);

        if (this.caching) {
            cache = new SoftReference[pageCount];
            for (int i = 0; i < cache.length; ++i) {
                cache[i] = new SoftReference<>(null);
            }
        } else {
            cache = null;
        }
    }

    /**
     * Creates a new MultiPagePictureReader object.
     *
     * @param   imageURL               DOCUMENT ME!
     * @param   caching                DOCUMENT ME!
     * @param   checkHeapSize          DOCUMENT ME!
     * @param   extendedAccessHandler  DOCUMENT ME!
     *
     * @throws  IOException               DOCUMENT ME!
     * @throws  IllegalArgumentException  DOCUMENT ME!
     */
    public MultiPagePictureReader(final URL imageURL,
            final boolean caching,
            final boolean checkHeapSize,
            final ExtendedAccessHandler extendedAccessHandler) throws IOException {
        if (imageURL == null) {
            throw new IllegalArgumentException("Cannot open a null URL.");
        }

        codec = getCodecString(imageURL.toExternalForm());

        if (codec == null) {
            throw new IOException("Unsupported filetype: '" + imageURL.toExternalForm()
                        + "' doesn't point to a tiff or jpeg file!");
        }

        pathOfImage = imageURL.toExternalForm();
        this.caching = caching;
        this.checkHeapSize = checkHeapSize;

        try {
            final InputStream stream = extendedAccessHandler.doRequest(imageURL);
            final byte[] inputBytes = IOUtils.toByteArray(stream);
            stream.close();
            bais = new ByteArrayInputStream(inputBytes);
            iis = ImageIO.createImageInputStream(bais);
            final Iterator<ImageReader> readers = ImageIO.getImageReaders(iis);
            reader = readers.next();
            reader.setInput(iis);

            pageCount = reader.getNumImages(true);
        } catch (final Exception ex) {
            throw new IOException("Could not open '" + imageURL.toExternalForm() + "'.", ex);
        }

        if (this.caching) {
            cache = new SoftReference[pageCount];
            for (int i = 0; i < cache.length; ++i) {
                cache[i] = new SoftReference<BufferedImage>(null);
            }
        } else {
            cache = null;
        }
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * This is not thread safe.
     *
     * @return  DOCUMENT ME!
     */
    public InputStream getInputStream() {
        try {
            if (stream != null) {
                try {
                    stream.close();
                } catch (Exception e) {
                    LOG.error("Cannot close Stream", e);
                }
            }

            if (imageFile != null) {
                stream = new FileInputStream(imageFile);

                return stream;
            } else if (bais != null) {
                bais.reset();
                stream = bais;

                return stream;
            }
        } catch (Exception e) {
            LOG.error("Error while retrieving stream", e);
        }

        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   imagePath  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private String getCodecString(final String imagePath) {
        final String filename = imagePath.toLowerCase();
        final String extension = filename.substring(filename.lastIndexOf(".") + 1); // NOI18N
        if (extension.matches("(tiff|tif)")) {                                      // NOI18N
            return CODEC_TIFF;
        } else if (extension.matches("(jpg|jpeg|jpe)")) {                           // NOI18N
            return CODEC_JPEG;
        }
        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   position  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private BufferedImage getFromCache(final int position) {
        BufferedImage result = null;
        if (!caching || (cache == null) || (position < 0) || (position >= cache.length)) {
            return result;
        }

        final SoftReference<BufferedImage> cacheItem = cache[position];
        if (cacheItem != null) {
            result = cacheItem.get();
        }

        return result;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  position  DOCUMENT ME!
     * @param  image     DOCUMENT ME!
     */
    private void addToCache(final int position, final BufferedImage image) {
        cache[position] = new SoftReference<BufferedImage>(image);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  IOException  DOCUMENT ME!
     */
    public final int getNumberOfPages() throws IOException {
        return pageCount;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   page  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  IOException  DOCUMENT ME!
     */
    public final BufferedImage loadPage(final int page) throws IOException {
        if ((page <= -1) || (page >= pageCount)) {
            throw new IOException("Could not find page " + page + " in file. Range is [0.." + (pageCount - 1) + "]."); // NOI18N
        }

        BufferedImage result = getFromCache(page);

        if (result != null) {
            return result;
        }

        final long freeMemory = (Runtime.getRuntime().maxMemory()
                        - (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory())) / MB;

        final int width = reader.getWidth(page);
        final int height = reader.getHeight(page);

        long estimatedSize = (long)width * height * 4;
        estimatedSize = estimatedSize / MB;

        if (checkHeapSize) {
            if (estimatedSize > freeMemory) {
                LOG.warn("Not enough memory for image page " + page);
                return null;
            }
        }

        if (checkHeapSize && (estimatedSize > freeMemory)) {
            LOG.warn("Couldn't read page '" + page + "' from image '" + pathOfImage
                        + "', since there's no memory left.");
        } else {
            result = reader.read(page);
        }

        if (caching) {
            addToCache(page, result);
        }

        return result;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getCodec() {
        return codec;
    }

    /**
     * DOCUMENT ME!
     */
    public final void close() {
        try {
            if (stream != null) {
                stream.close();
            }
        } catch (IOException ex) {
            LOG.warn(ex, ex);
        }
        reader.dispose();
    }
}
