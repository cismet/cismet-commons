/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cismap.commons.jtsgeometryfactories;

import com.vividsolutions.jts.geom.Geometry;

import java.sql.Connection;
import java.sql.SQLException;

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
     * @param   geometry  DOCUMENT ME!
     * @param   con       DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  SQLException  DOCUMENT ME!
     */
    Object getDbObject(Geometry geometry, Connection con) throws SQLException;

    /**
     * DOCUMENT ME!
     *
     * @param   dbObject  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    Geometry createGeometry(Object dbObject);

    /**
     * DOCUMENT ME!
     *
     * @param   dbObject  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    boolean isGeometryObject(Object dbObject);

    /**
     * DOCUMENT ME!
     *
     * @param   columnTypeName  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    boolean isGeometryColumn(String columnTypeName);

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    String getDialect();
}
