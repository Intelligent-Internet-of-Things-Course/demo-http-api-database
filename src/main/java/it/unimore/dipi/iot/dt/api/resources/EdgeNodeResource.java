package it.unimore.dipi.iot.dt.api.resources;

import com.codahale.metrics.annotation.Timed;
import io.dropwizard.jersey.errors.ErrorMessage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import it.unimore.dipi.iot.dt.api.dto.EdgeNodeCreationRequest;
import it.unimore.dipi.iot.dt.api.dto.EdgeNodeUpdateRequest;
import it.unimore.dipi.iot.dt.api.exception.EdgeNodeDataManagerConflict;
import it.unimore.dipi.iot.dt.api.model.EdgeNodeDescriptor;
import it.unimore.dipi.iot.dt.api.services.AppConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.List;
import java.util.Optional;

@Path("/api/edge_node")
@Api("Edge Node Inventory Endpoint")
public class EdgeNodeResource {

    final protected Logger logger = LoggerFactory.getLogger(EdgeNodeResource.class);

    public static class MissingKeyException extends Exception{}
    final AppConfig conf;

    public EdgeNodeResource(AppConfig conf) {
        this.conf = conf;
    }

    @GET
    @Path("/")
    @Timed
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value="Get all edge nodes")
    public Response getEdgeNodes(@Context ContainerRequestContext req) {

        try {

            logger.info("Loading all stored edge nodes ...");

            List<EdgeNodeDescriptor> edgeNodeList = this.conf.getEdgeNodeDataManager().getEdgeNodeList();

            if(edgeNodeList == null)
                return Response.status(Response.Status.NOT_FOUND).type(MediaType.APPLICATION_JSON_TYPE).entity(new ErrorMessage(Response.Status.NOT_FOUND.getStatusCode(),"Devices Not Found !")).build();

            return Response.ok(edgeNodeList).build();

        } catch (Exception e){
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).type(MediaType.APPLICATION_JSON_TYPE).entity(new ErrorMessage(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),"Internal Server Error !")).build();
        }
    }

    @GET
    @Path("/{edge_node_id}")
    @Timed
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value="Get Digital Twin by Id")
    public Response getDigitalTwinById(@Context ContainerRequestContext req,
                                @PathParam("edge_node_id") String edgeNodeId) {

        try {

            logger.info("Loading Edge Node for id: {}", edgeNodeId);

            //Check the request
            if(edgeNodeId == null)
                return Response.status(Response.Status.BAD_REQUEST).type(MediaType.APPLICATION_JSON_TYPE).entity(new ErrorMessage(Response.Status.BAD_REQUEST.getStatusCode(),"Invalid DigitalTwin Id Provided !")).build();

            Optional<EdgeNodeDescriptor> optionalEdgeNodeDescriptor = this.conf.getEdgeNodeDataManager().getEdgeNodeById(edgeNodeId);

            if(!optionalEdgeNodeDescriptor.isPresent())
                return Response.status(Response.Status.NOT_FOUND).type(MediaType.APPLICATION_JSON_TYPE).entity(new ErrorMessage(Response.Status.NOT_FOUND.getStatusCode(),"Resource Not Found !")).build();

            return Response.ok(optionalEdgeNodeDescriptor.get()).build();

        } catch (Exception e){
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).type(MediaType.APPLICATION_JSON_TYPE).entity(new ErrorMessage(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),"Internal Server Error !")).build();
        }
    }

    @POST
    @Path("/")
    @Timed
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value="Create a new Edge Node")
    public Response createDigitalTwin(@Context ContainerRequestContext req,
                               @Context UriInfo uriInfo,
                               EdgeNodeCreationRequest edgeNodeCreationRequest) {

        try {

            logger.info("Incoming User Creation Request: {}", edgeNodeCreationRequest);

            //Check the request
            if(edgeNodeCreationRequest == null)
                return Response.status(Response.Status.BAD_REQUEST).type(MediaType.APPLICATION_JSON_TYPE).entity(new ErrorMessage(Response.Status.BAD_REQUEST.getStatusCode(),"Invalid request payload")).build();

            EdgeNodeDescriptor edgeNodeDescriptor = this.conf.getEdgeNodeDataManager().createNewEdgeNode(edgeNodeCreationRequest);

            return Response.created(new URI(String.format("%s/%s",uriInfo.getAbsolutePath(),edgeNodeDescriptor.getId()))).build();

        } catch(EdgeNodeDataManagerConflict e1){
            return Response.status(Response.Status.CONFLICT).type(MediaType.APPLICATION_JSON_TYPE).entity(new ErrorMessage(Response.Status.CONFLICT.getStatusCode(),"Resource with the same Id already registered !")).build();
        }
        catch (Exception e2){
            e2.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).type(MediaType.APPLICATION_JSON_TYPE).entity(new ErrorMessage(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),"Internal Server Error !")).build();
        }
    }

    @PUT
    @Path("/{edge_node_id}")
    @Timed
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value="Update an existing Edge Node")
    public Response updateDigitalTwin(@Context ContainerRequestContext req,
                                   @Context UriInfo uriInfo,
                                   @PathParam("edge_node_id") String edgeNodeId,
                                   EdgeNodeUpdateRequest edgeNodeUpdateRequest) {

        try {

            logger.info("Incoming Edge Node ({}) Update Request: {}", edgeNodeId, edgeNodeUpdateRequest);

            //Check if the request is valid and Path Id match resource id
            if(edgeNodeUpdateRequest == null || !edgeNodeUpdateRequest.getId().equals(edgeNodeId))
                return Response.status(Response.Status.BAD_REQUEST).type(MediaType.APPLICATION_JSON_TYPE).entity(new ErrorMessage(Response.Status.BAD_REQUEST.getStatusCode(),"Invalid request ! Check DigitalTwin Id")).build();

            //Check if the device is available and correctly registered otherwise a 404 response will be sent to the client
            if(!this.conf.getEdgeNodeDataManager().getEdgeNodeById(edgeNodeId).isPresent())
                return Response.status(Response.Status.NOT_FOUND).type(MediaType.APPLICATION_JSON_TYPE).entity(new ErrorMessage(Response.Status.NOT_FOUND.getStatusCode(),"Resource not found !")).build();

            this.conf.getEdgeNodeDataManager().updateEdgeNode(edgeNodeUpdateRequest);

            return Response.noContent().build();

        } catch (Exception e){
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).type(MediaType.APPLICATION_JSON_TYPE).entity(new ErrorMessage(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),"Internal Server Error !")).build();
        }
    }

    @DELETE
    @Path("/{edge_node_id}")
    @Timed
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value="Delete a Single Edge Node")
    public Response deleteDigitalTwin(@Context ContainerRequestContext req,
                                 @PathParam("edge_node_id") String edgeNodeId) {

        try {

            logger.info("Deleting EdgeNode with id: {}", edgeNodeId);

            //Check the request
            if(edgeNodeId == null)
                return Response.status(Response.Status.BAD_REQUEST).type(MediaType.APPLICATION_JSON_TYPE).entity(new ErrorMessage(Response.Status.BAD_REQUEST.getStatusCode(),"Invalid DigitalTwin Id Provided !")).build();

            //Check if the device is available or not
            if(!this.conf.getEdgeNodeDataManager().getEdgeNodeById(edgeNodeId).isPresent())
                return Response.status(Response.Status.NOT_FOUND).type(MediaType.APPLICATION_JSON_TYPE).entity(new ErrorMessage(Response.Status.NOT_FOUND.getStatusCode(),"Resource Not Found !")).build();

            //Delete the location
            this.conf.getEdgeNodeDataManager().deleteEdgeNode(edgeNodeId);

            return Response.noContent().build();

        } catch (Exception e){
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).type(MediaType.APPLICATION_JSON_TYPE).entity(new ErrorMessage(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),"Internal Server Error !")).build();
        }

    }

}
