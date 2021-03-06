/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.commons.wms.capabilities;

/**
 * This interface represents a wms layer. This interface should be used to eliminate the deegree dependency for the
 * capabilities parsing.
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public interface Layer {

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    String getTitle();
    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    String getName();
    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    String getAbstract();

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    String[] getKeywords();
    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    boolean isQueryable();
    /**
     * DOCUMENT ME!
     *
     * @param   srs  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    boolean isSrsSupported(String srs);
    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    String[] getSrs();
    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    double getScaleDenominationMax();
    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    double getScaleDenominationMin();
    /**
     * DOCUMENT ME!
     *
     * @param   name  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    Style getStyleResource(String name);
    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    Style[] getStyles();
    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    Layer[] getChildren();
    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    LayerBoundingBox[] getBoundingBoxes();
    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    Envelope getLatLonBoundingBoxes();

    /**
     * DOCUMENT ME!
     *
     * @param  filterString  DOCUMENT ME!
     */
    void setFilterString(String filterString);

    /**
     * DOCUMENT ME!
     *
     * @return  the capabilities
     */
    WMSCapabilities getCapabilities();

    /**
     * DOCUMENT ME!
     *
     * @param  capabilities  the capabilities to set
     */
    void setCapabilities(WMSCapabilities capabilities);
}
