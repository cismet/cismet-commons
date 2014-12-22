/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cismap.commons.jtsgeometryfactories;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.PrecisionModel;
import com.vividsolutions.jts.io.oracle.OraReader;

import oracle.sql.STRUCT;

import org.apache.log4j.Logger;

import org.openide.util.lookup.ServiceProvider;

import java.sql.SQLException;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  $Revision$, $Date$
 */
@ServiceProvider(service = IGeometryFactory.class)
public final class OracleGeometryFactory implements IGeometryFactory {

    //~ Static fields/initializers ---------------------------------------------

    /** LOGGER. */
    private static final transient Logger LOG = Logger.getLogger(OracleGeometryFactory.class);

    //~ Instance fields --------------------------------------------------------

    private final OraReader oraReader;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new OracleGeometryFactory object.
     */
    public OracleGeometryFactory() {
        this.oraReader = new OraReader(new GeometryFactory(new PrecisionModel(PrecisionModel.FLOATING)));
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public String getDbString(final Geometry geometry) {
        return "SDO_GEOMETRY('" + geometry.toText() + "', " + geometry.getSRID() + ")"; // NOI18N
    }

    @Override
    public Geometry createGeometry(final Object dbObject) {
        try {
            if ((dbObject instanceof STRUCT)
                        && ((STRUCT)dbObject).getDescriptor().getSQLName().getName().equals(
                            "MDSYS.SDO_GEOMETRY")) {
                return oraReader.read((STRUCT)dbObject);
            }
        } catch (final SQLException e) {
            throw new RuntimeException("cannot read geometry from dbobject: " + dbObject, e); // NOI18N
        }

        throw new IllegalArgumentException("unsupported object type: " + dbObject); // NOI18N
    }

    @Override
    public boolean isGeometryObject(final Object dbObject) {
        try {
            return (dbObject instanceof STRUCT)
                        && ((STRUCT)dbObject).getDescriptor().getSQLName().getName().equals(
                            "MDSYS.SDO_GEOMETRY");
        } catch (SQLException ex) {
            LOG.error("cannot test geometry object", ex);

            return false;
        }
    }

    @Override
    public String getDialect() {
        return "oracle_11g";
    }

    @Override
    public boolean isGeometryColumn(final String columnTypeName) {
        return "MDSYS.SDO_GEOMETRY".equals(columnTypeName); // NOI18N
    }
}
