/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.remote;

import org.apache.log4j.Logger;

import org.openide.util.lookup.ServiceProvider;

import java.util.Arrays;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * DOCUMENT ME!
 *
 * @author   pd
 * @version  $Revision$, $Date$
 */
@Path("/simpleJsonRemoteMethod")
@Produces({ MediaType.APPLICATION_JSON })
@ServiceProvider(service = RESTRemoteControlMethod.class)
public class SimpleJsonRemoteMethod extends AbstractRESTRemoteControlMethod implements RESTRemoteControlMethod {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(RESTRemoteControlStarter.class);

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new SimpleJsonRemoteMethod object.
     */
    public SimpleJsonRemoteMethod() {
        super(-1, "/simpleJsonRemoteMethod");
        LOG.info("simpleJsonRemoteMethod created");
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @GET
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces({ MediaType.APPLICATION_JSON })
    public Response show() {
        if (LOG.isDebugEnabled()) {
            LOG.debug("show() method called");
        }
        return Response.ok(Arrays.asList(new Integer[] { 1, 2, 3, 4, 5, 6, 7, 8, 9 })).build();
    }
}
