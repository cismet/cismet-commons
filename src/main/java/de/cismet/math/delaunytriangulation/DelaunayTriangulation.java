/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.math.delaunytriangulation;
/*
 * Copyright (c) 2005 by L. Paul Chew.
 *
 * Permission is hereby granted, without written agreement and without
 * license or royalty fees, to use, copy, modify, and distribute this
 * software and its documentation for any purpose, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.Set;

/**
 * A 2D Delaunay Triangulation (DT) with incremental site insertion. This is not the fastest way to build a DT, but it's
 * a reasonable way to build the DT incrementally and it makes a nice interactive display. There are several O(n log n)
 * methods, but they require that either (1) the sites are all known initially or (2) the sites are inserted in random
 * order.
 *
 * @author   Paul Chew
 *
 *           <p>Created July 2005. Derived from an earlier, messier version.</p>
 * @version  $Revision$, $Date$
 */
public class DelaunayTriangulation extends Triangulation<Pnt> {

    //~ Instance fields --------------------------------------------------------

    public boolean debug = false; // Used for debugging

    private Simplex<Pnt> mostRecent = null; // Most recently inserted triangle

    //~ Constructors -----------------------------------------------------------

    /**
     * Constructor. All sites must fall within the initial triangle.
     *
     * @param  triangle  the initial triangle
     */
    public DelaunayTriangulation(final Simplex<Pnt> triangle) {
        super(triangle);
        mostRecent = triangle;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * Locate the triangle with point (a Pnt) inside (or on) it.
     *
     * @param   point  the Pnt to locate
     *
     * @return  triangle (Simplex<Pnt>) that holds the point; null if no such triangle
     */
    public Simplex<Pnt> locate(final Pnt point) {
        Simplex<Pnt> triangle = mostRecent;
        if (!this.contains(triangle)) {
            triangle = null;
        }

        // Try a directed walk (this works fine in 2D, but can fail in 3D)
        final Set<Simplex<Pnt>> visited = new HashSet<Simplex<Pnt>>();
        while (triangle != null) {
            if (visited.contains(triangle)) {                           // This should never happen
                System.out.println("Warning: Caught in a locate loop"); // NOI18N
                break;
            }
            visited.add(triangle);
            // Corner opposite point
            final Pnt corner = point.isOutside(triangle.toArray(new Pnt[0]));
            if (corner == null) {
                return triangle;
            }
            triangle = this.neighborOpposite(corner, triangle);
        }
        // No luck; try brute force
        System.out.println("Warning: Checking all triangles for " + point); // NOI18N
        for (final Simplex<Pnt> tri : this) {
            if (point.isOutside(tri.toArray(new Pnt[0])) == null) {
                return tri;
            }
        }
        // No such triangle
        System.out.println("Warning: No triangle holds " + point); // NOI18N
        return null;
    }

    /**
     * Place a new point site into the DT.
     *
     * @param   site  the new Pnt
     *
     * @return  set of all new triangles created
     */
    public Set<Simplex<Pnt>> delaunayPlace(final Pnt site) {
        final Set<Simplex<Pnt>> newTriangles = new HashSet<Simplex<Pnt>>();
        final Set<Simplex<Pnt>> oldTriangles = new HashSet<Simplex<Pnt>>();
        final Set<Simplex<Pnt>> doneSet = new HashSet<Simplex<Pnt>>();
        final Queue<Simplex<Pnt>> waitingQ = new LinkedList<Simplex<Pnt>>();

        // Locate containing triangle
        if (debug) {
            System.out.println("Locate"); // NOI18N
        }
        Simplex<Pnt> triangle = locate(site);

        // Give up if no containing triangle or if site is already in DT
        if ((triangle == null) || triangle.contains(site)) {
            return newTriangles;
        }

        // Find Delaunay cavity (those triangles with site in their circumcircles)
        if (debug) {
            System.out.println("Cavity"); // NOI18N
        }
        waitingQ.add(triangle);
        while (!waitingQ.isEmpty()) {
            triangle = waitingQ.remove();
            if (site.vsCircumcircle(triangle.toArray(new Pnt[0])) == 1) {
                continue;
            }
            oldTriangles.add(triangle);
            for (final Simplex<Pnt> tri : this.neighbors(triangle)) {
                if (doneSet.contains(tri)) {
                    continue;
                }
                doneSet.add(tri);
                waitingQ.add(tri);
            }
        }
        // Create the new triangles
        if (debug) {
            System.out.println("Create"); // NOI18N
        }
        for (final Set<Pnt> facet : Simplex.boundary(oldTriangles)) {
            facet.add(site);
            newTriangles.add(new Simplex<Pnt>(facet));
        }
        // Replace old triangles with new triangles
        if (debug) {
            System.out.println("Update"); // NOI18N
        }
        this.update(oldTriangles, newTriangles);

        // Update mostRecent triangle
        if (!newTriangles.isEmpty()) {
            mostRecent = newTriangles.iterator().next();
        }
        return newTriangles;
    }

    /**
     * Main program; used for testing.
     *
     * @param  args  DOCUMENT ME!
     */
    public static void main(final String[] args) {
        final Simplex<Pnt> tri = new Simplex<Pnt>(new Pnt(-10, 10), new Pnt(10, 10), new Pnt(0, -10));
        System.out.println("Triangle created: " + tri);                                    // NOI18N
        final DelaunayTriangulation dt = new DelaunayTriangulation(tri);
        System.out.println("DelaunayTriangulation created: " + dt);                        // NOI18N
        dt.delaunayPlace(new Pnt(0, 0));
        dt.delaunayPlace(new Pnt(1, 0));
        dt.delaunayPlace(new Pnt(0, 1));
        System.out.println("After adding 3 points, the DelaunayTriangulation is a " + dt); // NOI18N
        dt.printStuff();
    }
}
