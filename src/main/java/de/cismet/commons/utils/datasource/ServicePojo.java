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
public class ServicePojo {

    //~ Instance fields --------------------------------------------------------

    private String name;
    private String type;
    private String url;
    private ServicePojo[] services;
    private LayerPojo[] layers;
    private String abstractText;
    private String accessError;
    private String subparent;

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  the subparent
     */
    public String getSubparent() {
        return subparent;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  subparent  the subparent to set
     */
    public void setSubparent(final String subparent) {
        this.subparent = subparent;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  the accessError
     */
    public String getAccessError() {
        return accessError;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  accessError  the accessError to set
     */
    public void setAccessError(final String accessError) {
        this.accessError = accessError;
    }

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
     * @return  the type
     */
    public String getType() {
        return type;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  type  the type to set
     */
    public void setType(final String type) {
        this.type = type;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  the services
     */
    public ServicePojo[] getServices() {
        return services;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  services  the services to set
     */
    public void setServices(final ServicePojo[] services) {
        this.services = services;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  service  DOCUMENT ME!
     */
    public void addService(final ServicePojo service) {
        if (this.services == null) {
            this.services = new ServicePojo[1];
            this.services[0] = service;
        } else {
            final ServicePojo[] tmp = new ServicePojo[this.services.length + 1];

            System.arraycopy(this.services, 0, tmp, 0, this.services.length);
            tmp[this.services.length] = service;

            this.services = tmp;
        }
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
     * @return  the url
     */
    public String getUrl() {
        return url;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  url  the url to set
     */
    public void setUrl(final String url) {
        this.url = url;
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
}
