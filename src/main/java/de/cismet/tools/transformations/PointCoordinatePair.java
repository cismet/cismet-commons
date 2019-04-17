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
package de.cismet.tools.transformations;

import com.vividsolutions.jts.geom.Coordinate;


import java.awt.Point;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class PointCoordinatePair {

    //~ Instance fields --------------------------------------------------------

    private Point point;
    private Coordinate coordinate;

    //~ Methods ----------------------------------------------------------------

    public PointCoordinatePair() {
        
    }

    public PointCoordinatePair(final Point point, final Coordinate coordinate) {
        this.point = point;
        this.coordinate = coordinate;
    }
        
    @Override
    public Object clone() {
        return new PointCoordinatePair((getPoint() != null) ? (Point)getPoint().clone() : null,
                (getCoordinate() != null) ? (Coordinate)getCoordinate().clone() : null);
    }

    public Point getPoint() {
        return point;
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }

    public void setPoint(final Point point) {
        this.point = point;
    }

    public void setCoordinate(final Coordinate coordinate) {
        this.coordinate = coordinate;
    }
    
    
}
