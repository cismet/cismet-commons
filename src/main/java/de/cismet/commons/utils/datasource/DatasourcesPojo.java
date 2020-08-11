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
public class DatasourcesPojo {

    //~ Instance fields --------------------------------------------------------

    private ServicePojo[] services;

    //~ Methods ----------------------------------------------------------------

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
}
