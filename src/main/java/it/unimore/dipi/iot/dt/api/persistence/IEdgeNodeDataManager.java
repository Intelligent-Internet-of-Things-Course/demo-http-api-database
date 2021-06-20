package it.unimore.dipi.iot.dt.api.persistence;

import it.unimore.dipi.iot.dt.api.exception.EdgeNodeDataManagerConflict;
import it.unimore.dipi.iot.dt.api.exception.EdgeNodeDataManagerException;
import it.unimore.dipi.iot.dt.api.model.EdgeNodeDescriptor;

import java.util.List;
import java.util.Optional;

/**
 * @author Marco Picone, Ph.D. - picone.m@gmail.com
 * @project http-iot-api-demo
 * @created 05/10/2020 - 11:44
 */
public interface IEdgeNodeDataManager {

    public List<EdgeNodeDescriptor> getEdgeNodeList() throws EdgeNodeDataManagerException;

    public Optional<EdgeNodeDescriptor> getEdgeNodeById(String edgeNodeId) throws EdgeNodeDataManagerException;

    public EdgeNodeDescriptor createNewEdgeNode(EdgeNodeDescriptor edgeNodeDescriptor) throws EdgeNodeDataManagerException, EdgeNodeDataManagerConflict;

    public EdgeNodeDescriptor updateEdgeNode(EdgeNodeDescriptor edgeNodeDescriptor) throws EdgeNodeDataManagerException;

    public void deleteEdgeNode(String edgeNodeId) throws EdgeNodeDataManagerException;

}
