package it.unimore.dipi.iot.dt.api.persistence;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.IdName;
import org.javalite.activejdbc.annotations.Table;

/**
 * @author Marco Picone, Ph.D. - picone.m@gmail.com
 * @project conduits-zones-manager
 * @created 04/06/2021 - 16:58
 */
@Table("edge_node")
@IdName("edge_node_id")
public class EdgeNodeModel extends Model {

    //Model Column Field Keys
    public static final String COLUMN_KEY_ID = "edge_node_id";
    public static final String COLUMN_KEY_TYPE = "type";
    public static final String COLUMN_KEY_IP_ADDRESS = "ip_address";
    public static final String COLUMN_KEY_BASE_URL = "dt_manager_base_url";

}
