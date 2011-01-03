/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.math.geometry;

import java.awt.geom.Point2D;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten.hell@cismet.de
 * @version  $Revision$, $Date$
 */
public class StaticGeometryFunctions {

    //~ Methods ----------------------------------------------------------------

    /**
     * Berechnet den Lotpunkt des Triggers auf der Gerade durch lineStart und lineEnd. !!! Momentan kann der Punkt NICHT
     * \u00FCber die Strecke hinaus verschoben werden !!!
     *
     * @param   lineStart  Anfangspunkt der Gerade
     * @param   lineEnd    Endpunkt der Gerade
     * @param   trigger    Punkt zu dem der Lotpunkt auf der Geraden berechnet wird
     *
     * @return  Punkt auf der Geraden
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
     * Berechnet den Abstand eines Punktes von der Geraden durch lineStart und lineEnd.
     *
     * @param   lineStart  Anfangspunkt der Gerade
     * @param   lineEnd    Endpunkt der Gerade
     * @param   trigger    Punkt dessen Abstand zur Geraden berechnet werden soll
     *
     * @return  Abstand vom Punkt zur Geraden
     */
    public static double distanceToLine(final Point2D lineStart, final Point2D lineEnd, final Point2D trigger) {
        final Point2D pointOnLine = createPointOnLine(lineStart, lineEnd, trigger);
        return Math.hypot(pointOnLine.getX() - trigger.getX(), pointOnLine.getY() - trigger.getY());
    }
}
