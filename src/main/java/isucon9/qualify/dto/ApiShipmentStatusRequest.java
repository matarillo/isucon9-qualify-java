package isucon9.qualify.dto;

public class ApiShipmentStatusRequest {
    /*
     * ReserveID string `json:"reserve_id"`
     */

    private String reserveId;

    public String getReserveId() {
        return reserveId;
    }

    public void setReserveId(String reserveId) {
        this.reserveId = reserveId;
    }
}
