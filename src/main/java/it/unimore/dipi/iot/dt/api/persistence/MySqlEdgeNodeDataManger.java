package it.unimore.dipi.iot.dt.api.persistence;

import it.unimore.dipi.iot.dt.api.exception.EdgeNodeDataManagerConflict;
import it.unimore.dipi.iot.dt.api.exception.EdgeNodeDataManagerException;
import it.unimore.dipi.iot.dt.api.model.EdgeNodeDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.*;
import java.util.stream.Collectors;

/**
 *
 * Demo IoT Inventory Data Manager handling all data in a local cache implemented through Maps and Lists
 *
 * @author Marco Picone, Ph.D. - picone.m@gmail.com
 * @project http-iot-api-demo
 * @created 05/10/2020 - 11:48
 */
public class MySqlEdgeNodeDataManger implements IEdgeNodeDataManager {

    final private Logger logger = LoggerFactory.getLogger(MySqlEdgeNodeDataManger.class);

    private Map<String, EdgeNodeDescriptor> edgeNodeMap;

    private JdbcDataAccess jdbcDataAccess;

    public MySqlEdgeNodeDataManger(JdbcDataAccess jdbcDataAccess) {
        this.jdbcDataAccess = jdbcDataAccess;
        this.edgeNodeMap = new HashMap<>();
    }

    @Override
    public List<EdgeNodeDescriptor> getEdgeNodeList() throws EdgeNodeDataManagerException {

        return this.jdbcDataAccess.onDBConnection(()->{
            List<EdgeNodeModel> modelList = EdgeNodeModel.findAll();

            return modelList.stream()
                    .map(m -> new EdgeNodeDescriptor(m.getString(EdgeNodeModel.COLUMN_KEY_ID),
                            m.getString(EdgeNodeModel.COLUMN_KEY_TYPE),
                            m.getString(EdgeNodeModel.COLUMN_KEY_IP_ADDRESS),
                            m.getString(EdgeNodeModel.COLUMN_KEY_BASE_URL)))
                    .collect(Collectors.toList());
        });
    }

    @Override
    public Optional<EdgeNodeDescriptor> getEdgeNodeById(String edgeNodeId) throws EdgeNodeDataManagerException {

        logger.info("Retrieving EdgeNode with Id: {}", edgeNodeId);

        if(edgeNodeId == null || edgeNodeId.length() == 0)
            throw new EdgeNodeDataManagerException("Wrong or missing edgeNodeId !");

        try{
            return this.jdbcDataAccess.onDBConnection(()->{
                try{
                    List<EdgeNodeModel> modelList = EdgeNodeModel.where("edge_node_id = ?", edgeNodeId);

                    if(modelList == null || modelList.size() == 0)
                        return Optional.empty();

                    return modelList.stream()
                            .map(m -> new EdgeNodeDescriptor(m.getString(EdgeNodeModel.COLUMN_KEY_ID),
                                    m.getString(EdgeNodeModel.COLUMN_KEY_TYPE),
                                    m.getString(EdgeNodeModel.COLUMN_KEY_IP_ADDRESS),
                                    m.getString(EdgeNodeModel.COLUMN_KEY_BASE_URL)))
                            .findFirst();
                }catch (Exception e){
                    logger.error("Error loading EdgeNodeById ! Msg: {}", e.getMessage());
                    return Optional.empty();
                }
            });
        }catch (Exception e){
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public EdgeNodeDescriptor createNewEdgeNode(EdgeNodeDescriptor edgeNodeDescriptor) throws EdgeNodeDataManagerException, EdgeNodeDataManagerConflict {

        //Check Resource Conflict
        if(edgeNodeDescriptor != null && edgeNodeDescriptor.getId() != null && this.getEdgeNodeById(edgeNodeDescriptor.getId()).isPresent())
            throw new EdgeNodeDataManagerConflict("Edge Node with the same id already available!");

        //Validate Input
        //if(edgeNodeDescriptor.getId() == null || edgeNodeDescriptor.getId().length() == 0)
            //throw new ConduitsZonesDataManagerException("Missing or Wrong Parameters ! Check the incoming resource object !");

        if(edgeNodeDescriptor.getId() == null || edgeNodeDescriptor.getId().length() == 0)
            edgeNodeDescriptor.setId(UUID.randomUUID().toString());

        boolean insertResult = false;

        try{
            insertResult = this.jdbcDataAccess.onDBConnection(()->{
                try{

                    EdgeNodeModel model = new EdgeNodeModel();
                    model.setString(EdgeNodeModel.COLUMN_KEY_ID, edgeNodeDescriptor.getId());
                    model.setString(EdgeNodeModel.COLUMN_KEY_TYPE, edgeNodeDescriptor.getType());
                    model.setString(EdgeNodeModel.COLUMN_KEY_IP_ADDRESS, edgeNodeDescriptor.getIpAddress());
                    model.setString(EdgeNodeModel.COLUMN_KEY_BASE_URL, edgeNodeDescriptor.getDtManagerServiceBaseUrl());
                    return model.insert();

                }catch (Exception e){
                    logger.error("ActiveJdbc Exception ! Msg: {}", e.getMessage());
                    throw new EdgeNodeDataManagerException(e.getMessage());
                }
            });
        }catch (Exception e){
            throw new EdgeNodeDataManagerException(e.getMessage());
        }

        if(!insertResult)
            throw new EdgeNodeDataManagerException("Error creating the new resource !");
        else
            return edgeNodeDescriptor;
    }

    @Override
    public EdgeNodeDescriptor updateEdgeNode(EdgeNodeDescriptor edgeNodeDescriptor) throws EdgeNodeDataManagerException {

        //Validate Input
        if(edgeNodeDescriptor == null || edgeNodeDescriptor.getId() == null || edgeNodeDescriptor.getId().length() == 0)
            throw new EdgeNodeDataManagerException("Missing or Wrong Parameters ! Check the incoming resource object !");

        int resultCount = -1;

        try{
            resultCount = this.jdbcDataAccess.onDBConnection(()->{
                try{

                    return EdgeNodeModel.update(
                            String.format("%s = ?, " +
                                    "%s = ?, " +
                                    "%s = ?",
                            EdgeNodeModel.COLUMN_KEY_TYPE,
                            EdgeNodeModel.COLUMN_KEY_IP_ADDRESS,
                            EdgeNodeModel.COLUMN_KEY_BASE_URL),
                            String.format("%s = ?", EdgeNodeModel.COLUMN_KEY_ID),
                            edgeNodeDescriptor.getType(),
                            edgeNodeDescriptor.getIpAddress(),
                            edgeNodeDescriptor.getDtManagerServiceBaseUrl(),
                            edgeNodeDescriptor.getId());

                }catch (Exception e){
                    logger.error("ActiveJdbc Exception ! Msg: {}", e.getMessage());
                    throw new EdgeNodeDataManagerException(e.getMessage());
                }
            });
        }catch (Exception e){
            throw new EdgeNodeDataManagerException(e.getMessage());
        }

        if(resultCount <= 0)
            throw new EdgeNodeDataManagerException("Error updating the new resource !");
        else
            return edgeNodeDescriptor;

    }

    @Override
    public void deleteEdgeNode(String edgeNodeId) throws EdgeNodeDataManagerException {

        //Validate Input
        if(edgeNodeId == null || edgeNodeId.length() == 0)
            throw new EdgeNodeDataManagerException(String.format("Missing or Wrong Parameters ! Provided ResourceId: %s", edgeNodeId));

        int resultCount = -1;

        try{
            resultCount = this.jdbcDataAccess.onDBConnection(()->{
                try{

                    return EdgeNodeModel.delete(
                            String.format("%s = ?", EdgeNodeModel.COLUMN_KEY_ID),
                            edgeNodeId);
                }catch (Exception e){
                    logger.error("ActiveJdbc Exception ! Msg: {}", e.getMessage());
                    throw new EdgeNodeDataManagerException(e.getMessage());
                }
            });
        }catch (Exception e){
            throw new EdgeNodeDataManagerException(e.getMessage());
        }

        if(resultCount <= 0)
            throw new EdgeNodeDataManagerException("Error deleting the resource !");
    }
}
