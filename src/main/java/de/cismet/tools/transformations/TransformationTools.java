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
import com.vividsolutions.jts.geom.util.AffineTransformation;
import com.vividsolutions.jts.geom.util.AffineTransformationBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class TransformationTools {

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   completePairs  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static AffineTransformation calculateAvgTransformation(final PointCoordinatePair[] completePairs) {
        final List<AffineTransformation> transforms = new ArrayList<>();
        if (completePairs.length >= 3) {
            for (final Object[] arr : getCombinations(completePairs, 3)) {
                final PointCoordinatePair pair0 = (PointCoordinatePair)arr[0];
                final PointCoordinatePair pair1 = (PointCoordinatePair)arr[1];
                final PointCoordinatePair pair2 = (PointCoordinatePair)arr[2];

                final AffineTransformationBuilder builder = new AffineTransformationBuilder(
                        new Coordinate(pair0.getPoint().getX(), pair0.getPoint().getY()),
                        new Coordinate(pair1.getPoint().getX(), pair1.getPoint().getY()),
                        new Coordinate(pair2.getPoint().getX(), pair2.getPoint().getY()),
                        pair0.getCoordinate(),
                        pair1.getCoordinate(),
                        pair2.getCoordinate());

                final AffineTransformation transform = builder.getTransformation();
                if (transform != null) {
                    transforms.add(transform);
                }
            }

            final AffineTransformation avgTransform = createAverageTransformation(transforms);
            return avgTransform;
        }
        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   transforms  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static AffineTransformation createAverageTransformation(final List<AffineTransformation> transforms) {
        final double[] avg = new double[6];
        for (final AffineTransformation transform : transforms) {
            final double[] matrix = transform.getMatrixEntries();
            for (int i = 0; i < avg.length; i++) {
                avg[i] += matrix[i] / transforms.size();
            }
        }
        return new AffineTransformation(avg);
    }

    /**
     * public static List<Object[]> comb(final Object[] input, final int k) { final List<int[]> indices = comb(input,
     * k); }.
     *
     * @param   input    DOCUMENT ME!
     * @param   setSize  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static List<Object[]> getCombinations(final Object[] input, final int setSize) {
        final List<Object[]> subsets = new ArrayList<>();

        final int[] indices = new int[setSize]; // here we'll keep indices
        // pointing to elements in input array

        if (setSize <= input.length) {
            // store first 'setSize' number of indices
            for (int index = 0; index < setSize; index++) {
                indices[index] = index;
            }
            subsets.add(getSubset(input, indices));

            int index;
            do {
                // find position of item that can be incremented
                index = setSize - 1;
                while ((index >= 0) && (indices[index] == (input.length - setSize + index))) {
                    index--;
                }
                if (index >= 0) {
                    indices[index]++;                         // increment this item
                    for (++index; index < setSize; index++) { // fill up remaining items
                        indices[index] = indices[index - 1] + 1;
                    }
                    subsets.add(getSubset(input, indices));
                }
            } while (index >= 0);
        }
        return subsets;
    }

    /**
     * generate actual subset by index sequence.
     *
     * @param   input    DOCUMENT ME!
     * @param   indices  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private static Object[] getSubset(final Object[] input, final int[] indices) {
        final Object[] result = new Object[indices.length];
        for (int index = 0; index < indices.length; index++) {
            result[index] = input[indices[index]];
        }
        return result;
    }
}
