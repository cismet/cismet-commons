/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.math.geometry;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

/**
 * Geometry Functions.
 *
 * @author   thorsten.hell@cismet.de
 * @version  $Revision$, $Date$
 */
public class StaticGeometryFunctions {

    //~ Methods ----------------------------------------------------------------

    /**
     * Calculates Perpendicular Point of the Trigger on the Line, which goes through lineStart and lineEnd. !Warning:
     * Can't move the Point over the Line, yet.
     *
     * @param   lineStart  startPoint of the Line
     * @param   lineEnd    endPoint of the Line
     * @param   trigger    Point to which the Perpendicular Point will get calculated
     *
     * @return  Perpendicular Point on the Line
     */
    public static Point2D createPointOnLine(final Point2D lineStart, final Point2D lineEnd, final Point2D trigger) {
        final double maxX = Math.max(lineStart.getX(), lineEnd.getX());
        final double minX = Math.min(lineStart.getX(), lineEnd.getX());
        final double maxY = Math.max(lineStart.getY(), lineEnd.getY());
        final double minY = Math.min(lineStart.getY(), lineEnd.getY());
        if (lineStart.getY() == lineEnd.getY()) {        // Steigung 0
            if (trigger.getX() > maxX) {
                return new Point2D.Double(maxX, lineStart.getY());
            } else if (trigger.getX() < minX) {
                return new Point2D.Double(minX, lineStart.getY());
            } else {
                return new Point2D.Double(trigger.getX(), lineStart.getY());
            }
        } else if (lineStart.getX() == lineEnd.getX()) { // Steigung unendlich
            if (trigger.getY() > maxY) {
                return new Point2D.Double(lineStart.getX(), maxY);
            } else if (trigger.getY() < minY) {
                return new Point2D.Double(lineStart.getX(), minY);
            } else {
                return new Point2D.Double(lineStart.getX(), trigger.getY());
            }
        } else {                                         // Steigung kein Extremfall
            final double m = (lineStart.getY() - lineEnd.getY()) / (lineStart.getX() - lineEnd.getX());
            final double mOrth = (-1.0) / m;
            double x = (trigger.getY() - (mOrth * trigger.getX()) - (lineStart.getY() - (m * lineStart.getX())))
                        / (m - mOrth);
            double y = ((mOrth * x) + trigger.getY()) - (mOrth * trigger.getX());
            if (x > maxX) {
                x = maxX;
            } else if (x < minX) {
                x = minX;
            }
            if (y > maxY) {
                y = maxY;
            } else if (y < minY) {
                y = minY;
            }
            return new Point2D.Double(x, y);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   line1  DOCUMENT ME!
     * @param   line2  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static Point2D.Double createIntersectionPoint(final Line2D line1, final Line2D line2) {
        if (!line1.intersectsLine(line2)) {
            return null;
        }
        final double x1 = line1.getX1();
        final double y1 = line1.getY1();
        final double x1d = line1.getX2() - x1;
        final double y1d = line1.getY2() - y1;
        final double x2 = line2.getX1();
        final double y2 = line2.getY1();
        final double x2d = line2.getX2() - x2;
        final double y2d = line2.getY2() - y2;

        final double det = (x2d * y1d) - (y2d * x1d);
        if (det == 0) {
            return null;
        } else {
            final double z = ((x2d * (y2 - y1)) + (y2d * (x1 - x2))) / det;
            if ((z == 0) || (z == 1)) {
                return null;
            }
            return new Point2D.Double(x1 + (z * x1d), y1 + (z * y1d));
        }
    }

    /**
     * Calculates the Distance between a specified Point and a specified Line, which goes through lineStart and lineEnd.
     *
     * @param   lineStart  startPoint of the Line
     * @param   lineEnd    endPoint of the Line
     * @param   trigger    Point, whose distance to the Line should get Calculated
     *
     * @return  Distance between the Point and the Line
     */
    public static double distanceToLine(final Point2D lineStart, final Point2D lineEnd, final Point2D trigger) {
        final Point2D pointOnLine = createPointOnLine(lineStart, lineEnd, trigger);
        return Math.hypot(pointOnLine.getX() - trigger.getX(), pointOnLine.getY() - trigger.getY());
    }
}
