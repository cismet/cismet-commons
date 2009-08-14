/*
 * StaticGeometryFunctions.java
 * Copyright (C) 2005 by:
 *
 *----------------------------
 * cismet GmbH
 * Goebenstrasse 40
 * 66117 Saarbruecken
 * http://www.cismet.de
 *----------------------------
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 *----------------------------
 * Author:
 * thorsten.hell@cismet.de
 *----------------------------
 *
 * Created on 18. August 2006, 13:55
 *
 */
package de.cismet.math.geometry;

import java.awt.geom.Point2D;

/**
 *
 * @author thorsten.hell@cismet.de
 */
public class StaticGeometryFunctions {

    /**
     * Berechnet den Lotpunkt des Triggers auf der Gerade durch lineStart und lineEnd.
     * !!! Momentan kann der Punkt NICHT \u00FCber die Strecke hinaus verschoben werden !!!
     * @param lineStart Anfangspunkt der Gerade
     * @param lineEnd Endpunkt der Gerade
     * @param trigger Punkt zu dem der Lotpunkt auf der Geraden berechnet wird
     * @return Punkt auf der Geraden
     */
    public static Point2D createPointOnLine(Point2D lineStart, Point2D lineEnd, Point2D trigger) {
        double maxX = Math.max(lineStart.getX(), lineEnd.getX());
        double minX = Math.min(lineStart.getX(), lineEnd.getX());
        double maxY = Math.max(lineStart.getY(), lineEnd.getY());
        double minY = Math.min(lineStart.getY(), lineEnd.getY());
        if (lineStart.getY() == lineEnd.getY()) { // Steigung 0
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
        } else { // Steigung kein Extremfall
            double m = (lineStart.getY() - lineEnd.getY()) / (lineStart.getX() - lineEnd.getX());
            double mOrth = (-1.0) / m;
            double x = (trigger.getY() - mOrth * trigger.getX() - (lineStart.getY() - m * lineStart.getX())) / (m - mOrth);
            double y = (mOrth * x + trigger.getY()) - (mOrth * trigger.getX());
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
     * @param lineStart Anfangspunkt der Gerade
     * @param lineEnd Endpunkt der Gerade
     * @param trigger Punkt dessen Abstand zur Geraden berechnet werden soll
     * @return Abstand vom Punkt zur Geraden
     */
    public static double distanceToLine(Point2D lineStart, Point2D lineEnd, Point2D trigger) {
        Point2D pointOnLine = createPointOnLine(lineStart, lineEnd, trigger);
        return Math.hypot(pointOnLine.getX() - trigger.getX(), pointOnLine.getY() - trigger.getY());
    }
}
