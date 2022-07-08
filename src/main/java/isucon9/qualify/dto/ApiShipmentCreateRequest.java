package isucon9.qualify.dto;

public class ApiShipmentCreateRequest {
    /*
     * ToAddress string `json:"to_address"`
     * ToName string `json:"to_name"`
     * FromAddress string `json:"from_address"`
     * FromName string `json:"from_name"`
     */

    private String toAddress;
    private String toName;
    private String fromAddress;
    private String fromName;

    public String getToAddress() {
        return toAddress;
    }

    public void setToAddress(String toAddress) {
        this.toAddress = toAddress;
    }

    public String getToName() {
        return toName;
    }

    public void setToName(String toName) {
        this.toName = toName;
    }

    public String getFromAddress() {
        return fromAddress;
    }

    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
    }

    public String getFromName() {
        return fromName;
    }

    public void setFromName(String fromName) {
        this.fromName = fromName;
    }
}
