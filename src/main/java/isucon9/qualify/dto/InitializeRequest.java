package isucon9.qualify.dto;

public class InitializeRequest {
    /*
     * PaymentServiceURL string `json:"payment_service_url"`
     * ShipmentServiceURL string `json:"shipment_service_url"`
     */

    private String paymentServiceURL;
    private String shipmentServiceURL;

    public String getPaymentServiceURL() {
        return paymentServiceURL;
    }

    public void setPaymentServiceURL(String paymentServiceURL) {
        this.paymentServiceURL = paymentServiceURL;
    }

    public String getShipmentServiceURL() {
        return shipmentServiceURL;
    }

    public void setShipmentServiceURL(String shipmentServiceURL) {
        this.shipmentServiceURL = shipmentServiceURL;
    }
}
