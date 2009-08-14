/*
 * PostgisGeometryFactory.java
 *
 * Created on 4. M\u00E4rz 2005, 14:55
 */
package de.cismet.cismap.commons.jtsgeometryfactories;

import com.vividsolutions.jts.geom.*;
import java.util.Arrays;

/**
 *
 * @author hell
 */
public class PostGisGeometryFactory {

    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger("de.cismet.cismap.commons.jtsgeometryfactories.PostGisGeometryFactory");

    /** Creates a new instance of PostgisGeometryFactory */
    public PostGisGeometryFactory() {
    }

    public static String getPostGisCompliantDbString(Geometry g) {
        if (g == null) {
            return null;
        } else {
            //DEFAULT
            return "SRID=-1;" + g.toText();
            //SHEN return "SRID=26918;" + g.toText();
        }
    }

    public static Geometry createJtsGeometry(org.postgis.Geometry geom) {
        if (geom instanceof org.postgis.LinearRing) {
            log.debug("org.postgis.LinearRing2JtsGeometry");
            org.postgis.LinearRing lr = (org.postgis.LinearRing) geom;
            int numberOfPoints = lr.numPoints();
            if (numberOfPoints > 0) {
                Coordinate[] coordArr = new Coordinate[numberOfPoints];
                for (int i = 0; i < numberOfPoints; ++i) {
                    coordArr[i] = new Coordinate(lr.getPoint(i).getX(), lr.getPoint(i).getY());
                }
                return new GeometryFactory().createLinearRing(coordArr);
            } else {
                return null;
            }
        } else if (geom instanceof org.postgis.Polygon) {
            log.debug("org.postgis.Polygon2JtsGeometry");
            org.postgis.Polygon p = (org.postgis.Polygon) geom;
            int ringcount = p.numRings();
            if (ringcount > 0) {
                //erster Ring wird als H\u00FClle verwendet
                LinearRing shell = (LinearRing) createJtsGeometry(p.getRing(0));
                //der Rest sind L\u00F6cher
                LinearRing[] holes = new LinearRing[ringcount - 1];
                for (int i = 1; i < ringcount; ++i) {
                    holes[i - 1] = (LinearRing) createJtsGeometry(p.getRing(i));
                }
                return new com.vividsolutions.jts.geom.Polygon(shell, holes, new GeometryFactory());
            } else {
                return null;
            }
        } // Multipolygon (liefert Polygon zur\u00FCck wenn nur ein Polygon enthalten)
        else if (geom instanceof org.postgis.MultiPolygon) {
            log.debug("org.postgis.MultiPolygon2JtsGeometry");
            org.postgis.MultiPolygon mp = (org.postgis.MultiPolygon) geom;
            int numPoly = mp.numPolygons();
            if (numPoly > 0) {
                if (numPoly == 1) {
                    return createJtsGeometry(mp.getPolygon(0));
                } else {
                    Polygon[] polyArr = new Polygon[numPoly];
                    for (int i = 0; i < numPoly; ++i) {
                        polyArr[i] = (Polygon) createJtsGeometry(mp.getPolygon(i));
                    }
                    return new MultiPolygon(polyArr, new GeometryFactory());
                }
            } else {
                return null;
            }
        } else if (geom instanceof org.postgis.Point) {
            org.postgis.Point p = (org.postgis.Point) geom;
            GeometryFactory gf = new GeometryFactory();
            return gf.createPoint(new Coordinate(p.getX(), p.getY()));
        } else if (geom instanceof org.postgis.LineString) {
            org.postgis.LineString ls = (org.postgis.LineString) geom;
            GeometryFactory gf = new GeometryFactory();
            Coordinate[] ca = new Coordinate[ls.numPoints()];
            for (int i = 0; i < ls.numPoints(); ++i) {
                ca[i] = new Coordinate(ls.getPoint(i).x, ls.getPoint(i).y);
            }
            return gf.createLineString(ca);
        } else if (geom instanceof org.postgis.MultiLineString) {
            org.postgis.MultiLineString mls = (org.postgis.MultiLineString) geom;
            int numLines = mls.numLines();
            if (numLines > 0) {
                if (numLines == 1) {
                    return createJtsGeometry(mls.getLine(0));
                } else {
                    LineString[] lsArr = new LineString[numLines];
                    for (int i = 0; i < numLines; ++i) {
                        lsArr[i] = (LineString) createJtsGeometry(mls.getLine(i));
                    }
                    return new MultiLineString(lsArr, new GeometryFactory());
                }
            }
        } else if (geom instanceof org.postgis.GeometryCollection) {
            org.postgis.GeometryCollection gc = (org.postgis.GeometryCollection) geom;
            int numGeom = gc.numGeoms();
            String type = null;
            if (numGeom > 0) {
                if (numGeom == 1) {
                    return createJtsGeometry(gc.getSubGeometry(0));
                } else {
                    Geometry[] gArr = new Geometry[numGeom];
                    for (int i = 0; i < numGeom; ++i) {
                        gArr[i] = (Geometry) createJtsGeometry(gc.getSubGeometry(i));
                        String localType = gArr[i].getGeometryType();
                        if (type == null) {
                            type = localType;
                        } else if (!type.equals(localType)) {
                            type = "DIFFERENTTYPES";
                        }
                    }
                    try {
                        if (type.equals("Polygon")) {
                            Polygon[] pa=(Polygon[])Arrays.copyOf(gArr,gArr.length, new Polygon[0].getClass());
                            return new MultiPolygon((Polygon[]) pa, new GeometryFactory());
                        } else if (type.equals("LineString")) {
                            LineString[] lsa=(LineString[])Arrays.copyOf(gArr,gArr.length, new LineString[0].getClass());
                            return new MultiLineString( lsa, new GeometryFactory());
                        } else if (type.equals("LineString")) {
                            Point[] pa=(Point[])Arrays.copyOf(gArr,gArr.length, new Point[0].getClass());
                            return new MultiPoint( pa, new GeometryFactory());
                        } else {
                            return new GeometryCollection(gArr, new GeometryFactory());
                        }

                    } catch (Exception e) {
                       
                        return new GeometryCollection(gArr, new GeometryFactory());
                    }
                }

            } else {
                return null;
            }

        }
        return null;
    }
}
