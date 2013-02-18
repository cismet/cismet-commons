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

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * A Triangulation on vertices (generic type V). A Triangulation is a set of Simplices (see Simplex below). For
 * efficiency, we keep track of the neighbors of each Simplex. Two Simplices are neighbors of they share a facet.
 *
 * @author   Paul Chew
 *
 *           <p>Created July 2005. Derived from an earlier, messier version.</p>
 * @version  $Revision$, $Date$
 */
public class Triangulation<V> implements Iterable<Simplex<V>> {

    //~ Instance fields --------------------------------------------------------

    private HashMap<Simplex<V>, HashSet<Simplex<V>>> neighbors; // Maps Simplex to neighbors

    //~ Constructors -----------------------------------------------------------

    /**
     * Constructor.
     *
     * @param  simplex  the initial Simplex.
     */
    public Triangulation(final Simplex<V> simplex) {
        neighbors = new HashMap<Simplex<V>, HashSet<Simplex<V>>>();
        neighbors.put(simplex, new HashSet<Simplex<V>>());
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * String representation. Shows number of simplices currently in the Triangulation.
     *
     * @return  a String representing the Triangulation
     */
    @Override
    public String toString() {
        return "Triangulation (with " + neighbors.size() + " elements)"; // NOI18N
    }

    /**
     * Size (# of Simplices) in Triangulation.
     *
     * @return  the number of Simplices in this Triangulation
     */
    public int size() {
        return neighbors.size();
    }

    /**
     * True if the simplex is in this Triangulation.
     *
     * @param   simplex  the simplex to check
     *
     * @return  true iff the simplex is in this Triangulation
     */
    public boolean contains(final Simplex<V> simplex) {
        return this.neighbors.containsKey(simplex);
    }

    /**
     * Iterator.
     *
     * @return  an iterator for every Simplex in the Triangulation
     */
    @Override
    public Iterator<Simplex<V>> iterator() {
        return Collections.unmodifiableSet(this.neighbors.keySet()).iterator();
    }

    /**
     * Print stuff about a Triangulation. Used for debugging.
     */
    public void printStuff() {
        final boolean remember = Simplex.moreInfo;
        System.out.println("Neighbor data for " + this); // NOI18N
        for (final Simplex<V> simplex : neighbors.keySet()) {
            Simplex.moreInfo = true;
            System.out.print("    " + simplex + ":");    // NOI18N
            Simplex.moreInfo = false;
            for (final Simplex neighbor : neighbors.get(simplex)) {
                System.out.print(" " + neighbor);        // NOI18N
            }
            System.out.println();
        }
        Simplex.moreInfo = remember;
    }

    /* Navigation */

    /**
     * Report neighbor opposite the given vertex of simplex.
     *
     * @param   vertex   a vertex of simplex
     * @param   simplex  we want the neighbor of this Simplex
     *
     * @return  the neighbor opposite vertex of simplex; null if none
     *
     * @throws  IllegalArgumentException  if vertex is not in this Simplex
     */
    public Simplex<V> neighborOpposite(final Object vertex, final Simplex<V> simplex) {
        if (!simplex.contains(vertex)) {
            throw new IllegalArgumentException("Bad vertex; not in simplex"); // NOI18N
        }
SimplexLoop:
        for (final Simplex<V> s : neighbors.get(simplex)) {
            for (final V v : simplex) {
                if (v.equals(vertex)) {
                    continue;
                }
                if (!s.contains(v)) {
                    continue SimplexLoop;
                }
            }
            return s;
        }
        return null;
    }

    /**
     * Report neighbors of the given simplex.
     *
     * @param   simplex  a Simplex
     *
     * @return  the Set of neighbors of simplex
     */
    public Set<Simplex<V>> neighbors(final Simplex<V> simplex) {
        return new HashSet<Simplex<V>>(this.neighbors.get(simplex));
    }

    /* Modification */

    /**
     * Update by replacing one set of Simplices with another. Both sets of simplices must fill the same "hole" in the
     * Triangulation.
     *
     * @param  oldSet  set of Simplices to be replaced
     * @param  newSet  set of replacement Simplices
     */
    public void update(final Set<? extends Simplex<V>> oldSet, final Set<? extends Simplex<V>> newSet) {
        // Collect all simplices neighboring the oldSet
        final Set<Simplex<V>> allNeighbors = new HashSet<Simplex<V>>();
        for (final Simplex<V> simplex : oldSet) {
            allNeighbors.addAll(neighbors.get(simplex));
        }
        // Delete the oldSet
        for (final Simplex<V> simplex : oldSet) {
            for (final Simplex<V> n : neighbors.get(simplex)) {
                neighbors.get(n).remove(simplex);
            }
            neighbors.remove(simplex);
            allNeighbors.remove(simplex);
        }
        // Include the newSet simplices as possible neighbors
        allNeighbors.addAll(newSet);
        // Create entries for the simplices in the newSet
        for (final Simplex<V> s : newSet) {
            neighbors.put(s, new HashSet<Simplex<V>>());
        }
        // Update all the neighbors info
        for (final Simplex<V> s1 : newSet) {
            for (final Simplex<V> s2 : allNeighbors) {
                if (!s1.isNeighbor(s2)) {
                    continue;
                }
                neighbors.get(s1).add(s2);
                neighbors.get(s2).add(s1);
            }
        }
    }
}
