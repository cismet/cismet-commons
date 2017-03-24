package de.cismet.tools;

import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.MetadataException;
import com.vividsolutions.jts.geom.Point;
import de.cismet.commons.utils.*;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.BeforeClass;

/**
 *
 * @author martin.scholl@cismet.de
 */
public class ExifReaderTest {

    public ExifReaderTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    private String getCurrentMethodName() {
        return new Throwable().getStackTrace()[1].getMethodName();
    }

    @Test
    public void test010ExifReader() throws IOException, URISyntaxException, ImageProcessingException, MetadataException {
        final String filename = "SX720_2280.jpg";
        System.out.println("TEST " + this.getCurrentMethodName() + ": " + filename);
        
        assertNotNull(this.getClass().getResource(filename));
        File file = new File(this.getClass().getResource(filename).toExternalForm());
        if (!file.canRead()) {
            file = new File(this.getClass().getResource(filename).toURI());
        }

        assertTrue(file.canRead());
        final ExifReader exifReader
                = new ExifReader(file);

        final Point point = exifReader.getGpsCoords();
        assertEquals("POINT (6.875419690014315 49.60623062999053)", point.toText());
        
        //final double gpsDirection = exifReader.getGpsDirection();
        //System.out.println(gpsDirection);
        
        exifReader.printAllAttributes();
    }
    
    @Test
    public void test020ExifReaderWithDirection() throws IOException, URISyntaxException, ImageProcessingException, MetadataException {
        final String filename = "iPhone5s_5560.jpg";
        System.out.println("TEST " + this.getCurrentMethodName() + ": " + filename);
        
        assertNotNull(this.getClass().getResource(filename));
        File file = new File(this.getClass().getResource(filename).toExternalForm());
        if (!file.canRead()) {
            file = new File(this.getClass().getResource(filename).toURI());
        }

        assertTrue(file.canRead());
        final ExifReader exifReader
                = new ExifReader(file);

        final Point point = exifReader.getGpsCoords();
        System.out.println(point.toText());
        assertEquals("POINT (2.841460049937578 39.848122920021474)", point.toText());
        
        final double gpsDirection = exifReader.getGpsDirection();
        //System.out.println(gpsDirection);
        assertEquals(265.25d, gpsDirection, 0);
        
        //exifReader.printAllAttributes();
    }
}
