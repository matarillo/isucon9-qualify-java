package isucon9.qualify.dto;

public class ApiShipmentStatusResponse {
    /*
     * Status string `json:"status"`
     * ReserveTime int64 `json:"reserve_time"`
     */

    private String status;

    private long reserveTime;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getReserveTime() {
        return reserveTime;
    }

    public void setReserveTime(long reserveTime) {
        this.reserveTime = reserveTime;
    }
}
