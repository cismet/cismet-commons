/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.tools;

import org.apache.log4j.Logger;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.Transferable;

/**
 * DOCUMENT ME!
 *
 * @version  $Revision$, $Date$
 */
public class SystemClipboardListener implements ClipboardOwner {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(SystemClipboardListener.class);

    //~ Constructors -----------------------------------------------------------

    /**
     * final Clipboard systemClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
     */
    public SystemClipboardListener() {
        gainOwnership(getContents());
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private Transferable getContents() {
        final int max = 10;
        final int sleepMs = 5;
        RuntimeException lastException = null;
        for (int i = 0; i < max; i++) {
            try {
                return getSystemClipboard().getContents(this);
            } catch (final RuntimeException ex) {
                LOG.info(String.format("could not get clipboard contents. trying again %d/%d", i, max), ex);
                lastException = ex;
            }
            try {
                Thread.sleep(sleepMs);
            } catch (final InterruptedException ex) {
                LOG.warn("sleep failed", ex);
            }
        }
        throw lastException;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private static Clipboard getSystemClipboard() {
        return Toolkit.getDefaultToolkit().getSystemClipboard();
    }

    @Override
    public void lostOwnership(final Clipboard clipboard, final Transferable transferable) {
        final Transferable contents = getContents();
        processContents(contents);
        gainOwnership(contents);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  contents  DOCUMENT ME!
     */
    protected final void gainOwnership(final Transferable contents) {
        getSystemClipboard().setContents(contents, this);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  contents  DOCUMENT ME!
     */
    protected void processContents(final Transferable contents) {
    }
}
