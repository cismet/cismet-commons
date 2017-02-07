package de.cismet.commons.utils;



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
public class MultiPagePictureReaderTest
{

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

    @Test
    public void test010readMultiPageTiffSmallJpeg() throws IOException, URISyntaxException
    {
        System.out.println("TEST " + this.getCurrentMethodName());
        
        assertNotNull(this.getClass().getResource("multipage_tif_example_small_jpeg.tif"));
        final File file = new File(this.getClass().getResource("multipage_tif_example_small_jpeg.tif").toURI());
        
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
        System.out.println("TEST " + this.getCurrentMethodName());
        
        assertNotNull(this.getClass().getResource("multipage_tif_example_small_lzw.tif"));
        final File file = new File(this.getClass().getResource("multipage_tif_example_small_lzw.tif").toURI());
        
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
        System.out.println("TEST " + this.getCurrentMethodName());
        
        assertNotNull(this.getClass().getResource("multipage_tif_example_small_zip.tif"));
        final File file = new File(this.getClass().getResource("multipage_tif_example_small_zip.tif").toURI());
        
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
    public void test040readMultiPageTiffBigJpeg() throws IOException, URISyntaxException
    {
        System.out.println("TEST " + this.getCurrentMethodName());
        
        assertNotNull(this.getClass().getResource("multipage_tif_example_big_jpeg.tif"));
        final File file = new File(this.getClass().getResource("multipage_tif_example_big_jpeg.tif").toURI());
        
        assertTrue(file.canRead());
        final MultiPagePictureReader multiPagePictureReader 
                = new MultiPagePictureReader(file);
        
        assertEquals("tiff", multiPagePictureReader.getCodec());
        assertEquals(10, multiPagePictureReader.getNumberOfPages());

        assertNotNull(multiPagePictureReader.loadPage(0));
    }
}