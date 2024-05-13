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
package de.cismet.tools;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.Tag;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.drew.metadata.exif.GpsDirectory;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.PrecisionModel;

import java.io.File;
import java.io.IOException;

import java.util.Date;

/**
 * Reads exif meta data of image files.
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public class ExifReader {

    //~ Enums ------------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    public static enum Mirrored {

        //~ Enum constants -----------------------------------------------------

        NONE, HORIZONTAL, VERTICAL
    }

    //~ Instance fields --------------------------------------------------------

    private Metadata metadata;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new ExifReader object.
     *
     * @param   file  The image file
     *
     * @throws  ImageProcessingException  DOCUMENT ME!
     * @throws  IOException               DOCUMENT ME!
     */
    public ExifReader(final File file) throws ImageProcessingException, IOException {
        metadata = ImageMetadataReader.readMetadata(file);
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * Determines the coordinatesof the image in EPSG:4326.
     *
     * @return  The coordinatesof the image in EPSG:4326
     */
    public Point getGpsCoords() {
        Point p = null;
        final GpsDirectory gpsDirectory = metadata.getFirstDirectoryOfType(GpsDirectory.class);

        if (gpsDirectory != null) {
            if ((gpsDirectory.getGeoLocation() != null) && !gpsDirectory.getGeoLocation().isZero()) {
                final GeometryFactory factory = new GeometryFactory(new PrecisionModel(PrecisionModel.FLOATING), 4326);
                p = factory.createPoint(new Coordinate(
                            gpsDirectory.getGeoLocation().getLongitude(),
                            gpsDirectory.getGeoLocation().getLatitude()));
            }
        }

        return p;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Date getTimeDate() {
        final ExifIFD0Directory ifdDirectory = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);

        if (ifdDirectory != null) {
            if (ifdDirectory.getDate(ExifIFD0Directory.TAG_DATETIME_ORIGINAL) != null) {
                return ifdDirectory.getDate(ExifIFD0Directory.TAG_DATETIME_ORIGINAL);
            } else if (ifdDirectory.getDate(ExifIFD0Directory.TAG_DATETIME_DIGITIZED) != null) {
                return ifdDirectory.getDate(ExifIFD0Directory.TAG_DATETIME_DIGITIZED);
            } else if (ifdDirectory.getDate(ExifIFD0Directory.TAG_DATETIME) != null) {
                return ifdDirectory.getDate(ExifIFD0Directory.TAG_DATETIME);
            }
        }

        final ExifSubIFDDirectory subIfdDirectory = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);

        if (subIfdDirectory != null) {
            if (ifdDirectory.getDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL) != null) {
                return ifdDirectory.getDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL);
            } else if (ifdDirectory.getDate(ExifSubIFDDirectory.TAG_DATETIME_DIGITIZED) != null) {
                return ifdDirectory.getDate(ExifSubIFDDirectory.TAG_DATETIME_DIGITIZED);
            } else if (ifdDirectory.getDate(ExifSubIFDDirectory.TAG_DATETIME) != null) {
                return ifdDirectory.getDate(ExifSubIFDDirectory.TAG_DATETIME);
            }
        }

        return null;
    }

    /**
     * Determines the direction of the image.
     *
     * @return  the direction of the image
     *
     * @throws  MetadataException  If an error occurs during the reading of the image metadata
     */
    public double getGpsDirection() throws MetadataException {
        final GpsDirectory gpsDirectory = metadata.getFirstDirectoryOfType(GpsDirectory.class);

        if (gpsDirectory != null) {
            return gpsDirectory.getDouble(GpsDirectory.TAG_IMG_DIRECTION);
        }

        return 0.0;
    }

    /**
     * Determines whether the reference of the direction of the image. 'T' denotes true direction and 'M' is magnetic
     * direction.
     *
     * @return  the reference of direction of the image
     */
    public String getGpsDirectionRef() {
        final GpsDirectory gpsDirectory = metadata.getFirstDirectoryOfType(GpsDirectory.class);

        if (gpsDirectory != null) {
            return gpsDirectory.getString(GpsDirectory.TAG_IMG_DIRECTION_REF);
        }

        return null;
    }

    /**
     * Prints all exif attributes to stdout.
     */
    public void printAllAttributes() {
        for (final Directory directory : metadata.getDirectories()) {
            for (final Tag tag : directory.getTags()) {
                System.out.println(tag);
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getModel() {
        final ExifIFD0Directory exifDirectory = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);

        if (exifDirectory != null) {
            return exifDirectory.getString(ExifIFD0Directory.TAG_MODEL);
        }

        return null;
    }

    /**
     * Determine the orientation of the image from the exif data.
     *
     * @return  The orientation of image
     */
    public Integer getOrientation() {
        final ExifIFD0Directory exifDirectory = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);

        if (exifDirectory != null) {
            return exifDirectory.getInteger(ExifIFD0Directory.TAG_ORIENTATION);
        }

        return null;
    }

    /**
     * Determine the rotation of the image from the exif data.
     *
     * @return  the rotation of the image according to the exif data in deegree
     */
    public Double getOrientationRotation() {
        final Integer orientation = getOrientation();
        double angle = 0.0;

        if (orientation != null) {
            switch (orientation) {
                case 1: {
                    // Horizontal / normal
                    angle = 0.0;
                    break;
                }
                case 2: {
                    // Mirror horizontal
                    break;
                }
                case 3: {
                    // Rotate 180
                    angle = 180.0;
                    break;
                }
                case 4: {
                    // Mirror vertical
                    break;
                }
                case 5: {
                    // Mirror horizontal and rotate 270 CW (clockwise)
                    angle = 270.0;
                    break;
                }
                case 6: {
                    // Rotate 90 CW
                    angle = 90.0;
                    break;
                }
                case 7: {
                    // Mirror horizontal and rotate 90 CW (clockwise)
                    angle = 90.0;
                    break;
                }
                case 8: {
                    // Rotate 270 CW (clockwise)
                    angle = 270.0;
                    break;
                }
            }
        }

        return angle;
    }

    /**
     * Determine whether the image is mirrored.
     *
     * @return  the mirror type of the image
     */
    public Mirrored getKindOfMirrored() {
        final Integer orientation = getOrientation();
        Mirrored mirrored = Mirrored.NONE;

        if (orientation != null) {
            switch (orientation) {
                case 2: {
                    // Mirror horizontal
                    mirrored = Mirrored.HORIZONTAL;
                    break;
                }
                case 4: {
                    // Mirror vertical
                    mirrored = Mirrored.VERTICAL;
                    break;
                }
                case 5: {
                    // Mirror horizontal and rotate 270 CW (clockwise)
                    mirrored = Mirrored.HORIZONTAL;
                    break;
                }
                case 7: {
                    // Mirror horizontal and rotate 90 CW (clockwise)
                    mirrored = Mirrored.HORIZONTAL;
                    break;
                }
            }
        }

        return mirrored;
    }

    /**
     * Only for test purposes.
     *
     * @param  args  DOCUMENT ME!
     */
    public static void main(final String[] args) {
        try {
            final long time = System.currentTimeMillis();
            final File file = new File("/home/therter/Downloads/IMG_5080.jpeg");
            final ExifReader reader = new ExifReader(file);
            reader.printAllAttributes();
            final Point po = reader.getGpsCoords();
            final double direction = reader.getGpsDirection();
            System.out.println(po.toText());
            System.out.println(direction);
            System.out.println("Zeit " + (System.currentTimeMillis() - time));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
