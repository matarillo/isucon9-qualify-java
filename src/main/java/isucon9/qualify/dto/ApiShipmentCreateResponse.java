package isucon9.qualify.dto;

public class ApiShipmentCreateResponse {
    /*
     * ReserveID string `json:"reserve_id"`
     * ReserveTime int64 `json:"reserve_time"`
     */

    private String reserveId;
    private long reserveTime;

    public String getReserveId() {
        return reserveId;
    }

    public void setReserveId(String reserveId) {
        this.reserveId = reserveId;
    }

    public long getReserveTime() {
        return reserveTime;
    }

    public void setReserveTime(long reserveTime) {
        this.reserveTime = reserveTime;
    }
}
