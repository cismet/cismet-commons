/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cismap.commons.jtsgeometryfactories;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.PrecisionModel;

import org.openide.util.lookup.ServiceProvider;

import org.postgis.PGgeometry;

import java.sql.Connection;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * Factory for org.postgis Geometries.
 *
 * @author   hell
 * @version  $Revision$, $Date$
 */
@ServiceProvider(service = IGeometryFactory.class)
public class PostGisGeometryFactory implements IGeometryFactory {

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new instance of PostgisGeometryFactory.
     */
    public PostGisGeometryFactory() {
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * Getter for PostCompliantDBString.
     *
     * @param   g  <code>Geometry</code>
     *
     * @return  <code>String</code> "SRID= <code>SRID</code>;Text"
     */
    public static String getPostGisCompliantDbString(final Geometry g) {
        if (g == null) {
            return null;
        } else {
            return "SRID=" + g.getSRID() + ";" + g.toText(); // NOI18N
        }
    }

    /**
     * Creates new <code>JtsPoint</code> by using <code>point</code>.
     *
     * @param   point            <code>point</code>
     * @param   geometryFactory  <code>geometryFactory</code>
     *
     * @return  <code>point</code>
     */
    private static Point createJtsPoint(final org.postgis.Point point, final GeometryFactory geometryFactory) {
        return geometryFactory.createPoint(new Coordinate(point.getX(), point.getY()));
    }

    /**
     * Creates new <code>JtsLineString</code> by using <code>lineString</code>.
     *
     * @param   lineString       <code>lineString</code>
     * @param   geometryFactory  <code>geometryFactory</code>
     *
     * @return  <code>LineString</code>
     */
    private static LineString createJtsLineString(final org.postgis.LineString lineString,
            final GeometryFactory geometryFactory) {
        final Coordinate[] ca = new Coordinate[lineString.numPoints()];
        for (int i = 0; i < lineString.numPoints(); ++i) {
            ca[i] = new Coordinate(lineString.getPoint(i).x, lineString.getPoint(i).y);
        }
        return (LineString)geometryFactory.createLineString(ca);
    }

    /**
     * Creates new <code>JtsLinearRing</code> by using <code>linearRing</code>.
     *
     * @param   linearRing       <code>linearRing</code>
     * @param   geometryFactory  <code>geometryFactory</code>
     *
     * @return  <code>linearRing</code>
     */
    private static LinearRing createJtsLinearRing(final org.postgis.LinearRing linearRing,
            final GeometryFactory geometryFactory) {
        final int numberOfPoints = linearRing.numPoints();
        if (numberOfPoints > 0) {
            final Collection<Coordinate> coords = new ArrayList<Coordinate>(numberOfPoints);
            for (final org.postgis.Point point : linearRing.getPoints()) {
                coords.add(new Coordinate(point.getX(), point.getY()));
            }
            return geometryFactory.createLinearRing(coords.toArray(new Coordinate[0]));
        } else {
            return null;
        }
    }

    /**
     * Creates new <code>JtsPolygon</code> by using <code>polygon</code>.
     *
     * @param   polygon          <code>polygon</code>
     * @param   geometryFactory  <code>geometryFactory</code>
     *
     * @return  <code>polygon</code>
     *
     * @see     #createJtsLinearRing(org.postgis.LinearRing, com.vividsolutions.jts.geom.GeometryFactory)
     */
    private static Polygon createJtsPolygon(final org.postgis.Polygon polygon, final GeometryFactory geometryFactory) {
        final int ringcount = polygon.numRings();
        if (ringcount > 0) {
            // erster Ring wird als Hülle verwendet
            final LinearRing shell = (LinearRing)createJtsGeometry(polygon.getRing(0));

            // der Rest sind Löcher
            final LinearRing[] holes = new LinearRing[ringcount - 1];
            for (int i = 1; i < ringcount; ++i) {
                holes[i - 1] = (LinearRing)createJtsGeometry(polygon.getRing(i));
            }
            return new Polygon(shell, holes, geometryFactory);
        } else {
            return null;
        }
    }

    /**
     * Creates new <code>JtsMultiPoint</code> by using <code>MultiPoint</code>.
     *
     * @param   multiPoint       <code>MultiPoint</code>
     * @param   geometryFactory  <code>geometryFactory</code>
     *
     * @return  <code>MultiPoint</code>
     *
     * @see     #createJtsPoint(org.postgis.Point, com.vividsolutions.jts.geom.GeometryFactory)
     */
    private static MultiPoint createJtsMultiPoint(final org.postgis.MultiPoint multiPoint,
            final GeometryFactory geometryFactory) {
        final int numPoints = multiPoint.numPoints();
        if (numPoints == 0) {
            return null;
        } else {
            final Collection<Point> jtsPoints = new ArrayList<Point>(numPoints);
            for (final org.postgis.Point point : multiPoint.getPoints()) {
                final Point jtsPoint = createJtsPoint(point, geometryFactory);
                jtsPoints.add(jtsPoint);
            }
            return new MultiPoint(jtsPoints.toArray(new Point[0]), geometryFactory);
        }
    }

    /**
     * Creates new <code>JtsMultiLineString</code> by using <code>MultiLineString</code>.
     *
     * @param   multiLineString  <code>MultiLineString</code>
     * @param   geometryFactory  <code>geometryFactory</code>
     *
     * @return  <code>MultiLineString</code>
     *
     * @see     #createJtsLineString(org.postgis.LineString, com.vividsolutions.jts.geom.GeometryFactory)
     */
    private static MultiLineString createJtsMultiLineString(final org.postgis.MultiLineString multiLineString,
            final GeometryFactory geometryFactory) {
        final int numLines = multiLineString.numLines();
        if (numLines == 0) {
            return null;
        } else {
            final Collection<LineString> jtsLineStrings = new ArrayList<LineString>(numLines);
            for (final org.postgis.LineString lineString : multiLineString.getLines()) {
                final LineString jtsLinesString = createJtsLineString(lineString, geometryFactory);
                jtsLineStrings.add(jtsLinesString);
            }
            return new MultiLineString(jtsLineStrings.toArray(new LineString[0]), geometryFactory);
        }
    }

    /**
     * Creates new <code>JtsMultiPolygon</code> by using <code>MultiPolygon</code>.
     *
     * @param   multiPolygon     <code>MultiPolygon</code>!
     * @param   geometryFactory  <code>geometryFactory</code>
     *
     * @return  <code>MultiPolygon</code>
     *
     * @see     #createJtsPolygon(org.postgis.Polygon, com.vividsolutions.jts.geom.GeometryFactory)
     */
    private static MultiPolygon createJtsMultiPolygon(final org.postgis.MultiPolygon multiPolygon,
            final GeometryFactory geometryFactory) {
        final int numPoly = multiPolygon.numPolygons();
        if (numPoly == 0) {
            return null;
        } else {
            final Collection<Polygon> jtsPolygons = new ArrayList<Polygon>(numPoly);
            for (final org.postgis.Polygon polygon : multiPolygon.getPolygons()) {
                final Polygon jtsPolygon = createJtsPolygon(polygon, geometryFactory);
                jtsPolygons.add(jtsPolygon);
            }
            return new MultiPolygon(jtsPolygons.toArray(new Polygon[0]), geometryFactory);
        }
    }

    /**
     * Creates new <code>JtsGeometryCollection</code> by using <code>GeometryCollection</code>.
     *
     * @param   geometryCollection  <code>GeometryCollection</code>
     * @param   geometryFactory     <code>geometryFactory</code>
     *
     * @return  <code>GeometryCollection</code>
     */
    private static GeometryCollection createJtsGeometryCollection(
            final org.postgis.GeometryCollection geometryCollection,
            final GeometryFactory geometryFactory) {
        final int numGeom = geometryCollection.numGeoms();
        if (numGeom == 0) {
            return null;
        } else {
            String type = null;
            final Collection<Geometry> jtsGeometries = new ArrayList<Geometry>(numGeom);
            boolean isMixed = false;
            for (final org.postgis.Geometry geometry : geometryCollection.getGeometries()) {
                final Geometry jtsGeometry = (Geometry)createJtsGeometry(geometry);
                jtsGeometries.add(jtsGeometry);
                final String localType = jtsGeometry.getGeometryType();
                if (type == null) {
                    type = localType;
                } else if (!type.equals(localType)) {
                    isMixed = true;
                }
            }
            final Geometry[] jtsGeometrieArray = jtsGeometries.toArray(new Geometry[0]);
            try {
                if (isMixed) {
                    return new GeometryCollection(jtsGeometrieArray, geometryFactory);
                } else if (type.equals("Polygon")) {
                    final Polygon[] polygonArray = (Polygon[])Arrays.copyOf(
                            jtsGeometrieArray,
                            jtsGeometrieArray.length,
                            new Polygon[0].getClass());
                    return new MultiPolygon(polygonArray, geometryFactory);
                } else if (type.equals("LineString")) {
                    final LineString[] lineStringArray = (LineString[])Arrays.copyOf(
                            jtsGeometrieArray,
                            jtsGeometrieArray.length,
                            new LineString[0].getClass());
                    return new MultiLineString(lineStringArray, geometryFactory);
                } else if (type.equals("Point")) {
                    final Point[] pointArray = (Point[])Arrays.copyOf(
                            jtsGeometrieArray,
                            jtsGeometrieArray.length,
                            new Point[0].getClass());
                    return new MultiPoint(pointArray, geometryFactory);
                } else {
                    return new GeometryCollection(jtsGeometrieArray, geometryFactory);
                }
            } catch (Exception e) {
                return new GeometryCollection(jtsGeometrieArray, geometryFactory);
            }
        }
    }

    /**
     * Creates new <code>JtsGeometry</code> by using <code>Geometry</code>.
     *
     * @param   geom  <code>Geometry</code>
     *
     * @return  <code>Geometry</code>
     */
    public static Geometry createJtsGeometry(final org.postgis.Geometry geom) {
        final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(PrecisionModel.FLOATING),
                geom.getSrid());
        if (geom instanceof org.postgis.Point) {
            final org.postgis.Point point = (org.postgis.Point)geom;
            return createJtsPoint(point, geometryFactory);
        } else if (geom instanceof org.postgis.LineString) {
            final org.postgis.LineString lineString = (org.postgis.LineString)geom;
            return createJtsLineString(lineString, geometryFactory);
        } else if (geom instanceof org.postgis.LinearRing) {
            final org.postgis.LinearRing linearRing = (org.postgis.LinearRing)geom;
            return createJtsLinearRing(linearRing, geometryFactory);
        } else if (geom instanceof org.postgis.Polygon) {
            final org.postgis.Polygon polygon = (org.postgis.Polygon)geom;
            return createJtsPolygon(polygon, geometryFactory);
        } else if (geom instanceof org.postgis.MultiPoint) {
            final org.postgis.MultiPoint multiPoint = (org.postgis.MultiPoint)geom;
            if (multiPoint.numPoints() == 1) {
                return createJtsPoint(multiPoint.getPoint(0), geometryFactory);
            } else {
                return createJtsMultiPoint(multiPoint, geometryFactory);
            }
        } else if (geom instanceof org.postgis.MultiLineString) {
            final org.postgis.MultiLineString multiLineString = (org.postgis.MultiLineString)geom;
            if (multiLineString.numLines() == 1) {
                return createJtsLineString(multiLineString.getLine(0), geometryFactory);
            } else {
                return createJtsMultiLineString(multiLineString, geometryFactory);
            }
        } else if (geom instanceof org.postgis.MultiPolygon) {
            final org.postgis.MultiPolygon multiPolygon = (org.postgis.MultiPolygon)geom;
            if (multiPolygon.numPolygons() == 1) {
                return createJtsPolygon(multiPolygon.getPolygon(0), geometryFactory);
            } else {
                return createJtsMultiPolygon(multiPolygon, geometryFactory);
            }
        } else if (geom instanceof org.postgis.GeometryCollection) {
            final org.postgis.GeometryCollection geometryCollection = (org.postgis.GeometryCollection)geom;
            if (geometryCollection.numGeoms() == 1) {
                final Geometry geometry = createJtsGeometry(geometryCollection.getSubGeometry(0));
                geometry.setSRID(geom.getSrid());
                return geometry;
            } else {
                return createJtsGeometryCollection(geometryCollection, geometryFactory);
            }
        }
        return null;
    }

    @Override
    public String getDbString(final Geometry geometry) {
        return getPostGisCompliantDbString(geometry);
    }

    @Override
    public Geometry createGeometry(final Object dbObject) {
        if (dbObject instanceof org.postgis.PGgeometry) {
            return createJtsGeometry(((org.postgis.PGgeometry)dbObject).getGeometry());
        }

        throw new IllegalArgumentException("unsupported object type: " + dbObject);
    }

    @Override
    public boolean isGeometryObject(final Object dbObject) {
        return dbObject instanceof org.postgis.PGgeometry;
    }

    @Override
    public String getDialect() {
        // should be postgis, however choosing this one to be in line with the way the server handles sql
        return "postgres_9";
    }

    @Override
    public boolean isGeometryColumn(final String columnTypeName) {
        return "geometry".equals(columnTypeName);
    }

    @Override
    public Object getDbObject(final Geometry geometry, final Connection con) throws SQLException {
        return new PGgeometry(getDbString(geometry));
    }
}
