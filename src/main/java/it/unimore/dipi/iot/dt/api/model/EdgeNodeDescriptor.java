package it.unimore.dipi.iot.dt.api.model;

/**
 * @author Marco Picone, Ph.D. - picone.m@gmail.com
 * @project conduits-zones-manager
 * @created 04/06/2021 - 14:25
 */
public class EdgeNodeDescriptor {

    public static final String EDGE_NODE_TYPE_DEFAULT = "edge_node_default";

    private String id;

    private String type = EDGE_NODE_TYPE_DEFAULT;

    private String ipAddress;

    private String dtManagerServiceBaseUrl;

    public EdgeNodeDescriptor() {
    }

    public EdgeNodeDescriptor(String id, String type, String ipAddress, String dtManagerServiceBaseUrl) {
        this.id = id;
        this.type = type;
        this.ipAddress = ipAddress;
        this.dtManagerServiceBaseUrl = dtManagerServiceBaseUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getDtManagerServiceBaseUrl() {
        return dtManagerServiceBaseUrl;
    }

    public void setDtManagerServiceBaseUrl(String dtManagerServiceBaseUrl) {
        this.dtManagerServiceBaseUrl = dtManagerServiceBaseUrl;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("EdgeNodeDescriptor{");
        sb.append("id='").append(id).append('\'');
        sb.append(", type='").append(type).append('\'');
        sb.append(", ipAddress='").append(ipAddress).append('\'');
        sb.append(", dtManagerServiceBaseUrl='").append(dtManagerServiceBaseUrl).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
