package isucon9.qualify.dto;

public class PostShipResponse {
    /*
     * Path string `json:"path"`
     * ReserveID string `json:"reserve_id"`
     */

    private String path;
    private String reserveId;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getReserveId() {
        return reserveId;
    }

    public void setReserveId(String reserveId) {
        this.reserveId = reserveId;
    }
}
