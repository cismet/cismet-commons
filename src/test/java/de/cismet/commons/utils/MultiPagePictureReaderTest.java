package de.cismet.commons.utils;



import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Ignore;

/**
 *
 * @author martin.scholl@cismet.de
 */
public class MultiPagePictureReaderTest
{
    
    private final static Logger LOGGER = Logger.getLogger(MultiPagePictureReaderTest.class);
    
    public MultiPagePictureReaderTest()
    {
    }

    @BeforeClass
    public static void setUpClass() throws Exception
    {
    }

    @AfterClass
    public static void tearDownClass() throws Exception
    {
    }

    @Before
    public void setUp()
    {
    }

    @After
    public void tearDown()
    {
    }


    private String getCurrentMethodName()
    {
        return new Throwable().getStackTrace()[1].getMethodName();
    }

    /**
     * Testr fails on OpenJDK due to missing deprecated com/sun/image/codec/jpeg 
     * classes.See #62
     * 
     * @throws IOException
     * @throws URISyntaxException 
     */
    @Test
    @Ignore
    public void test010readMultiPageTiffSmallJpeg() throws IOException, URISyntaxException
    {
        LOGGER.info("TEST " + this.getCurrentMethodName());
        
        assertNotNull(this.getClass().getResource("multipage_tif_example_small_jpeg.tif"));
        File file = new File(this.getClass().getResource("multipage_tif_example_small_jpeg.tif").toExternalForm());
        if(!file.canRead()) {
            file = new File(this.getClass().getResource("multipage_tif_example_small_jpeg.tif").toURI());
        }
        
        assertTrue(file.canRead());
        final MultiPagePictureReader multiPagePictureReader 
                = new MultiPagePictureReader(file);
        
        assertEquals("tiff", multiPagePictureReader.getCodec());
        assertEquals(10, multiPagePictureReader.getNumberOfPages());
        
        for(int i = 0; i < multiPagePictureReader.getNumberOfPages(); i++) {
            assertNotNull(multiPagePictureReader.loadPage(i));
        }
    }
    
    @Test
    public void test020readMultiPageTiffSmallLzw() throws IOException, URISyntaxException
    {
        LOGGER.info("TEST " + this.getCurrentMethodName());
        
        assertNotNull(this.getClass().getResource("multipage_tif_example_small_lzw.tif"));
        
        // toExternalForm() is required when upstream projects reuse the tests!
        // See http://stackoverflow.com/questions/941754/how-to-get-a-path-to-a-resource-in-a-java-jar-file/27149287#27149287
        File file = new File(this.getClass().getResource("multipage_tif_example_small_lzw.tif").toExternalForm());
        if(!file.canRead()) {
            file = new File(this.getClass().getResource("multipage_tif_example_small_lzw.tif").toURI());
        }
        
        assertTrue(file.canRead());
        final MultiPagePictureReader multiPagePictureReader 
                = new MultiPagePictureReader(file);
        
        assertEquals("tiff", multiPagePictureReader.getCodec());
        assertEquals(10, multiPagePictureReader.getNumberOfPages());
        
        for(int i = 0; i < multiPagePictureReader.getNumberOfPages(); i++) {
            assertNotNull(multiPagePictureReader.loadPage(i));
        }
    }
    
    @Test
    public void test030readMultiPageTiffSmallZip() throws IOException, URISyntaxException
    {
        LOGGER.info("TEST " + this.getCurrentMethodName());
        
        assertNotNull(this.getClass().getResource("multipage_tif_example_small_zip.tif"));
        File file = new File(this.getClass().getResource("multipage_tif_example_small_zip.tif").toExternalForm());
        if(!file.canRead()) {
            file = new File(this.getClass().getResource("multipage_tif_example_small_zip.tif").toURI());
        }
        
        assertTrue(file.canRead());
        final MultiPagePictureReader multiPagePictureReader 
                = new MultiPagePictureReader(file);
        
        assertEquals("tiff", multiPagePictureReader.getCodec());
        assertEquals(10, multiPagePictureReader.getNumberOfPages());
        
        for(int i = 0; i < multiPagePictureReader.getNumberOfPages(); i++) {
            assertNotNull(multiPagePictureReader.loadPage(i));
        }
    }
    
    /**
     * Testr fails on OpenJDK due to missing deprecated com/sun/image/codec/jpeg 
     * classes.See #62
     * 
     * @throws IOException
     * @throws URISyntaxException 
     */
    @Test
    @Ignore
    public void test040readMultiPageTiffBigJpeg() throws IOException, URISyntaxException
    {
        LOGGER.info("TEST " + this.getCurrentMethodName());
        
        assertNotNull(this.getClass().getResource("multipage_tif_example_big_jpeg.tif"));
        File file = new File(this.getClass().getResource("multipage_tif_example_big_jpeg.tif").toExternalForm());
        if(!file.canRead()) {
            file = new File(this.getClass().getResource("multipage_tif_example_big_jpeg.tif").toURI());
        }
        
        assertTrue(file.canRead());
        final MultiPagePictureReader multiPagePictureReader 
                = new MultiPagePictureReader(file);
        
        assertEquals("tiff", multiPagePictureReader.getCodec());
        assertEquals(10, multiPagePictureReader.getNumberOfPages());

        assertNotNull(multiPagePictureReader.loadPage(0));
    }
    
    /**
     * Testr fails on OpenJDK due to missing deprecated com/sun/image/codec/jpeg 
     * classes.See #62
     * 
     * @throws IOException
     * @throws URISyntaxException 
     */
    @Test
    @Ignore
    public void test050readSinglePageJpeg() throws IOException, URISyntaxException
    {
        LOGGER.info("TEST " + this.getCurrentMethodName());
        
        LOGGER.debug(this.getClass().getResource("/de/cismet/tools/SX720_2280.jpg"));
        assertNotNull(this.getClass().getResource("/de/cismet/tools/SX720_2280.jpg"));
        
        // toExternalForm() is required when upstream projects reuse the tests!
        // See http://stackoverflow.com/questions/941754/how-to-get-a-path-to-a-resource-in-a-java-jar-file/27149287#27149287
        File file = new File(this.getClass().getResource("/de/cismet/tools/SX720_2280.jpg").toExternalForm());
        if(!file.canRead()) {
            file = new File(this.getClass().getResource("/de/cismet/tools/SX720_2280.jpg").toURI());
        }
        
        assertTrue(file.canRead());
        final MultiPagePictureReader multiPagePictureReader 
                = new MultiPagePictureReader(file);
        
        assertEquals(MultiPagePictureReader.CODEC_JPEG, multiPagePictureReader.getCodec());
        assertEquals(1, multiPagePictureReader.getNumberOfPages());
        
        for(int i = 0; i < multiPagePictureReader.getNumberOfPages(); i++) {
            assertNotNull(multiPagePictureReader.loadPage(i));
        }
    }
}