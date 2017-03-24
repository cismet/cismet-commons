/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.remote.test;

import de.cismet.remote.AbstractRESTRemoteControlMethod;
import de.cismet.remote.RESTRemoteControlMethod;
import de.cismet.remote.RESTRemoteControlStarter;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.log4j.Logger;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author pd
 */
@Path("/jsonRemoteMethod")
@Produces({ MediaType.APPLICATION_JSON })
@ServiceProvider(service = RESTRemoteControlMethod.class)
public class JsonRemoteMethod extends AbstractRESTRemoteControlMethod implements RESTRemoteControlMethod {

    private static final Logger LOG = Logger.getLogger(RESTRemoteControlStarter.class
    );

    public JsonRemoteMethod() {
        super(31338, "/jsonRemoteMethod");
        LOG.info("jsonRemoteMethod created");
    }

    @GET
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces({ MediaType.APPLICATION_JSON })
    public Response show() {
        LOG.debug("show() method called");
        return Response.ok(new RemoteMethodBean()).build();
    }
}
