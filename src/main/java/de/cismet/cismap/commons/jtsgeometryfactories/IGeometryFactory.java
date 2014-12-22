/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cismap.commons.jtsgeometryfactories;

import com.vividsolutions.jts.geom.Geometry;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
public interface IGeometryFactory {

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   geometry  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    String getDbString(Geometry geometry);
    /**
     * DOCUMENT ME!
     *
     * @param   dbObject  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    Geometry createGeometry(final Object dbObject);

    /**
     * DOCUMENT ME!
     *
     * @param   dbObject  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    boolean isGeometryObject(final Object dbObject);

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    String getDialect();
}
