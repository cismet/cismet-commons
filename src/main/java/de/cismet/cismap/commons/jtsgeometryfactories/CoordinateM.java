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
package de.cismet.cismap.commons.jtsgeometryfactories;

import com.vividsolutions.jts.geom.Coordinate;

/**
 * DOCUMENT ME!
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public class CoordinateM extends Coordinate {

    //~ Static fields/initializers ---------------------------------------------

    public static final int M = 3;

    //~ Instance fields --------------------------------------------------------

    /** The m-coordinate. */
    public double m;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new CoordinateM object.
     */
    public CoordinateM() {
    }

    /**
     * Creates a new CoordinateM object.
     *
     * @param  c  DOCUMENT ME!
     */
    public CoordinateM(final Coordinate c) {
        super(c);
    }

    /**
     * Creates a new CoordinateM object.
     *
     * @param  x  DOCUMENT ME!
     * @param  y  DOCUMENT ME!
     */
    public CoordinateM(final double x, final double y) {
        super(x, y);
    }

    /**
     * Creates a new CoordinateM object.
     *
     * @param  x  DOCUMENT ME!
     * @param  y  DOCUMENT ME!
     * @param  z  DOCUMENT ME!
     */
    public CoordinateM(final double x, final double y, final double z) {
        super(x, y, z);
    }

    /**
     * Creates a new CoordinateM object.
     *
     * @param  x  DOCUMENT ME!
     * @param  y  DOCUMENT ME!
     * @param  z  DOCUMENT ME!
     * @param  m  DOCUMENT ME!
     */
    public CoordinateM(final double x, final double y, final double z, final double m) {
        super(x, y, z);
        this.m = m;
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public void setOrdinate(final int ordinateIndex, final double value) {
        if (ordinateIndex == M) {
            m = value;
        } else {
            super.setOrdinate(ordinateIndex, value);
        }
    }

    @Override
    public double getOrdinate(final int ordinateIndex) {
        if (ordinateIndex == M) {
            return m;
        } else {
            return super.getOrdinate(ordinateIndex);
        }
    }

    @Override
    public Object clone() {
        return new CoordinateM(x, y, z, m);
    }
}
