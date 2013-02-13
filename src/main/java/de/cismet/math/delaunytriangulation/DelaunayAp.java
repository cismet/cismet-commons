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
 * Permission is hereby granted, without written agreement and without license or royalty fees, to use, copy, modify, and
 * distribute this software and its documentation for any purpose, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the
 * Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

import java.awt.*;
import java.awt.event.*;

import java.util.HashSet;
import java.util.Set;

import javax.swing.*;

/**
 * The Delauany applet. Creates and displays a Delaunay Triangulation (DT) or a Voronoi Diagram (VoD). Has a main
 * program so it is an application as well as an applet.
 *
 * @author   Paul Chew
 *
 *           <p>Created July 2005. Derived from an earlier, messier version.</p>
 * @version  $Revision$, $Date$
 */
public class DelaunayAp extends javax.swing.JApplet implements Runnable {

    //~ Methods ----------------------------------------------------------------

    /**
     * Initialize the applet. As recommended, the actual use of Swing components takes place in the event-dispatching
     * thread.
     */
    @Override
    public void init() {
        try {
            SwingUtilities.invokeAndWait(this);
        } catch (Exception e) {
            System.err.println("Initialization failure");
        } // NOI18N
    }

    /**
     * Set up the applet's GUI. As recommended, the init method executes this in the event-dispatching thread.
     */
    @Override
    public void run() {
        setLayout(new BorderLayout());

        // Build the button controls
        final JRadioButton voronoiButton = new JRadioButton("Voronoi Diagram");         // NOI18N
        voronoiButton.setActionCommand("voronoi");                                      // NOI18N
        final JRadioButton delaunayButton = new JRadioButton("Delaunay Triangulation"); // NOI18N
        delaunayButton.setActionCommand("delaunay");                                    // NOI18N
        final JButton clearButton = new JButton("Clear");                               // NOI18N
        clearButton.setActionCommand("clear");                                          // NOI18N
        final ButtonGroup group = new ButtonGroup();
        group.add(voronoiButton);
        group.add(delaunayButton);
        final JPanel buttonPanel = new JPanel();
        buttonPanel.add(voronoiButton);
        buttonPanel.add(delaunayButton);
        buttonPanel.add(clearButton);
        this.add(buttonPanel, "North");                                                 // NOI18N

        // Build the mouse-entry switches
        final JLabel circleLabel = new JLabel("Show Empty Circles");    // NOI18N
        circleLabel.setName("circles");                                 // NOI18N
        final JLabel delaunayLabel = new JLabel("Show Delaunay Edges"); // NOI18N
        delaunayLabel.setName("delaunay");                              // NOI18N
        final JLabel voronoiLabel = new JLabel("Show Voronoi Edges");   // NOI18N
        voronoiLabel.setName("voronoi");                                // NOI18N
        final JPanel switchPanel = new JPanel();
        switchPanel.add(circleLabel);
        switchPanel.add(new Label("     "));                            // NOI18N
        switchPanel.add(delaunayLabel);
        switchPanel.add(new Label("     "));                            // NOI18N
        switchPanel.add(voronoiLabel);
        this.add(switchPanel, "South");                                 // NOI18N

        // Build the graphics panel
        final DelaunayPanel graphicsPanel = new DelaunayPanel();
        graphicsPanel.setBackground(Color.gray);
        this.add(graphicsPanel, "Center"); // NOI18N

        // Register the listeners
        voronoiButton.addActionListener(graphicsPanel);
        delaunayButton.addActionListener(graphicsPanel);
        clearButton.addActionListener(graphicsPanel);
        graphicsPanel.addMouseListener(graphicsPanel);
        circleLabel.addMouseListener(graphicsPanel);
        delaunayLabel.addMouseListener(graphicsPanel);
        voronoiLabel.addMouseListener(graphicsPanel);

        // Initialize the radio buttons
        voronoiButton.doClick();
    }

    /**
     * Main program (used when run as application instead of applet).
     *
     * @param  args  arguments
     */
    public static void main(final String[] args) {
        final DelaunayAp applet = new DelaunayAp();  // Create applet
        applet.init();                               // Perform applet initialization
        final JFrame dWindow = new JFrame();         // Create window
        dWindow.setSize(700, 500);                   // Set window size
        dWindow.setTitle("Voronoi/Delaunay Window"); // NOI18N
        // Set window title
        dWindow.setLayout(new BorderLayout()); // Specify layout manager
        dWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // Specify closing behavior
        dWindow.add(applet, "Center"); // NOI18N       // Place applet into window
        dWindow.setVisible(true);      // Show the window
    }
}

/**
 * Graphics Panel for DelaunayAp.
 *
 * @version  $Revision$, $Date$
 */
class DelaunayPanel extends JPanel implements ActionListener, MouseListener {

    //~ Instance fields --------------------------------------------------------

    public boolean debug = false; // True iff printing info for debugging

    public Color voronoiColor = Color.magenta;
    public Color delaunayColor = Color.green;
    public int pointRadius = 3;

    private DelaunayTriangulation dt;     // The Delaunay triangulation
    private Simplex<Pnt> initialTriangle; // The large initial triangle
    private int initialSize = 10000;      // Controls size of initial triangle
    private boolean isVoronoi;            // True iff VoD instead of DT
    private boolean showCircles = false;  // True iff showing empty circles
    private boolean showDelaunay = false; // True iff showing Delaunay edges
    private boolean showVoronoi = false;  // True iff showing Voronoi edges
    private Graphics g;                   // Stored graphics context

    //~ Constructors -----------------------------------------------------------

    /**
     * Create and initialize the DT.
     */
    public DelaunayPanel() {
        initialTriangle = new Simplex<Pnt>(new Pnt(-initialSize, -initialSize),
                new Pnt(initialSize, -initialSize),
                new Pnt(0, initialSize));
        dt = new DelaunayTriangulation(initialTriangle);
    }

    /* Events */

    //~ Methods ----------------------------------------------------------------

    /**
     * Actions for button presses.
     *
     * @param  e  the ActionEvent
     */
    @Override
    public void actionPerformed(final ActionEvent e) {
        final String command = e.getActionCommand();
        if (debug) {
            System.out.println(command);
        }
        if (command.equals("voronoi")) {
            isVoronoi = true;                 // NOI18N
        } else if (command.equals("delaunay")) {
            isVoronoi = false;                // NOI18N
        } else if (command.equals("clear")) { // NOI18N
            dt = new DelaunayTriangulation(initialTriangle);
        }
        repaint();
    }

    /**
     * Mouse press.
     *
     * @param  e  the MouseEvent
     */
    @Override
    public void mousePressed(final MouseEvent e) {
        if (e.getComponent() != this) {
            return;
        }
        final Pnt point = new Pnt(e.getX(), e.getY());
        if (debug) {
            System.out.println("Click " + point); // NOI18N
        }
        dt.delaunayPlace(point);
        repaint();
    }

    /**
     * MouseEnter events.
     *
     * @param  e  the MouseEvent
     */
    @Override
    public void mouseEntered(final MouseEvent e) {
        if (e.getComponent() == this) {
            return;
        }
        final String name = e.getComponent().getName();
        if (debug) {
            System.out.println("Entering " + name); // NOI18N
        }
        showCircles = (name == "circles");          // NOI18N
        showDelaunay = (name == "delaunay");        // NOI18N
        showVoronoi = (name == "voronoi");          // NOI18N
        repaint();
    }

    /**
     * MouseExit events.
     *
     * @param  e  the MouseEvent
     */
    @Override
    public void mouseExited(final MouseEvent e) {
        if (e.getComponent() == this) {
            return;
        }
        if (debug) {
            System.out.println("Exiting"); // NOI18N
        }
        showCircles = false;
        showDelaunay = false;
        showVoronoi = false;
        repaint();
    }

    /**
     * MouseClick event (not used, but needed for MouseListener).
     *
     * @param  e  MouseClick Event
     */
    @Override
    public void mouseClicked(final MouseEvent e) {
    }

    /**
     * MouseRelease event (not used, but needed for MouseListener).
     *
     * @param  e  MouseRelease Event
     */
    @Override
    public void mouseReleased(final MouseEvent e) {
    }

    /* Basic Drawing Methods */

    /**
     * Draw a point.
     *
     * @param  point  the Pnt to draw
     */
    public void draw(final Pnt point) {
        final int r = pointRadius;
        final int x = (int)point.coord(0);
        final int y = (int)point.coord(1);
        g.fillOval(x - r, y - r, r + r, r + r);
    }

    /**
     * Draw a line segment.
     *
     * @param  endA  one endpoint
     * @param  endB  the other endpoint
     */
    public void draw(final Pnt endA, final Pnt endB) {
        g.drawLine((int)endA.coord(0), (int)endA.coord(1), (int)endB.coord(0), (int)endB.coord(1));
    }

    /**
     * Draw a circle.
     *
     * @param  center     the center of the circle
     * @param  radius     the circle's radius
     * @param  fillColor  null implies no fill
     */
    public void draw(final Pnt center, final double radius, final Color fillColor) {
        final int x = (int)center.coord(0);
        final int y = (int)center.coord(1);
        final int r = (int)radius;
        if (fillColor != null) {
            final Color temp = g.getColor();
            g.setColor(fillColor);
            g.fillOval(x - r, y - r, r + r, r + r);
            g.setColor(temp);
        }
        g.drawOval(x - r, y - r, r + r, r + r);
    }

    /* Higher Level Drawing Methods */

    /**
     * Handles painting entire contents of DelaunayPanel. Called automatically; requested via call to repaint().
     *
     * @param  g  the Graphics context
     */
    @Override
    public void paintComponent(final Graphics g) {
        super.paintComponent(g);
        this.g = g;

        // Flood the drawing area with a "background" color
        Color temp = g.getColor();
        if (!isVoronoi) {
            g.setColor(delaunayColor);
        } else if (dt.contains(initialTriangle)) {
            g.setColor(this.getBackground());
        } else {
            g.setColor(voronoiColor);
        }
        g.fillRect(0, 0, this.getWidth(), this.getHeight());
        g.setColor(temp);

        // Draw the appropriate picture
        if (isVoronoi) {
            drawAllVoronoi();
            drawAllSites();
        } else {
            drawAllDelaunay();
        }

        // Draw any extra info due to the mouse-entry switches
        temp = g.getColor();
        if (isVoronoi) {
            g.setColor(delaunayColor);
        } else {
            g.setColor(voronoiColor);
        }
        if (showCircles) {
            drawAllCircles();
        }
        if (showDelaunay) {
            drawAllDelaunay();
        }
        if (showVoronoi) {
            drawAllVoronoi();
        }
        g.setColor(temp);
    }

    /**
     * Draw all the Delaunay edges.
     */
    public void drawAllDelaunay() {
        // Loop through all the edges of the DT (each is done twice)
        for (final Simplex<Pnt> triangle : dt) {
            for (final Set<Pnt> edge : triangle.facets()) {
                final Pnt[] endpoint = edge.toArray(new Pnt[2]);
                draw(endpoint[0], endpoint[1]);
            }
        }
    }

    /**
     * Draw all the Voronoi edges.
     */
    public void drawAllVoronoi() {
        // Loop through all the edges of the DT (each is done twice)
        for (final Simplex<Pnt> triangle : dt) {
            for (final Simplex<Pnt> other : dt.neighbors(triangle)) {
                final Pnt p = Pnt.circumcenter(triangle.toArray(new Pnt[0]));
                final Pnt q = Pnt.circumcenter(other.toArray(new Pnt[0]));
                draw(p, q);
            }
        }
    }

    /**
     * Draw all the sites (i.e., the input points) of the DT.
     */
    public void drawAllSites() {
        // Loop through all sites of the DT
        // Each is done several times, about 6 times each on average; this
        // can be proved via Euler's formula.
        for (final Simplex<Pnt> triangle : dt) {
            for (final Pnt site : triangle) {
                draw(site);
            }
        }
    }

    /**
     * Draw all the empty circles (one for each triangle) of the DT.
     */
    public void drawAllCircles() {
// Loop through all triangles of the DT
loop:
        for (final Simplex<Pnt> triangle : dt) {
            for (final Pnt p : initialTriangle) { // Skip circles invoving initialTriangle
                if (triangle.contains(p)) {
                    continue loop;
                }
            }
            final Pnt c = Pnt.circumcenter(triangle.toArray(new Pnt[0]));
            final double radius = c.subtract(triangle.iterator().next()).magnitude();
            draw(c, radius, null);
        }
    }
}
