/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.commons.utils.datasource;

/**
 * DOCUMENT ME!
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public class LayerPojo {

    //~ Instance fields --------------------------------------------------------

    private String title;
    private String name;
    private String abstractText;
    private String[] keywords;
    private LayerPojo[] layers;

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  the abstractText
     */
    public String getAbstractText() {
        return abstractText;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  abstractText  the abstractText to set
     */
    public void setAbstractText(final String abstractText) {
        this.abstractText = abstractText;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  the layers
     */
    public LayerPojo[] getLayers() {
        return layers;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  layers  the layers to set
     */
    public void setLayers(final LayerPojo[] layers) {
        this.layers = layers;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  layer  DOCUMENT ME!
     */
    public void addLayer(final LayerPojo layer) {
        if (this.layers == null) {
            this.layers = new LayerPojo[1];
            this.layers[0] = layer;
        } else {
            final LayerPojo[] tmp = new LayerPojo[this.layers.length + 1];

            System.arraycopy(this.layers, 0, tmp, 0, this.layers.length);
            tmp[this.layers.length] = layer;

            this.layers = tmp;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  title  the title to set
     */
    public void setTitle(final String title) {
        this.title = title;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  the name
     */
    public String getName() {
        return name;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  name  the name to set
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  the keywords
     */
    public String[] getKeywords() {
        return keywords;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  keywords  the keywords to set
     */
    public void setKeywords(final String[] keywords) {
        this.keywords = keywords;
    }
}
