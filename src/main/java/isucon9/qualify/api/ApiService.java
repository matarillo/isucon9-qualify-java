package isucon9.qualify.api;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import isucon9.qualify.ApiException;
import isucon9.qualify.dto.ApiPaymentServiceTokenRequest;
import isucon9.qualify.dto.ApiPaymentServiceTokenResponse;
import isucon9.qualify.dto.ApiShipmentCreateRequest;
import isucon9.qualify.dto.ApiShipmentCreateResponse;
import isucon9.qualify.dto.ApiShipmentRequest;
import isucon9.qualify.dto.ApiShipmentStatusRequest;
import isucon9.qualify.dto.ApiShipmentStatusResponse;

@Service
public class ApiService {
    private static final String isucariApiToken = "Bearer 75ugk2m37a750fwir5xr-22l6h4wmue1bwrubzwd0";
    private static final String userAgent = "isucon9-qualify-webapp";

    private final RestTemplate restTemplate;

    public ApiService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    public ApiPaymentServiceTokenResponse getPaymentToken(String paymentUrl, ApiPaymentServiceTokenRequest request) {
        try {
            String endpoint = paymentUrl + "/token";
            HttpHeaders headers = new HttpHeaders();
            headers.add("User-Agent", userAgent);
            headers.add("Content-Type", "application/json");

            HttpEntity<ApiPaymentServiceTokenRequest> reqEntity = new HttpEntity<>(request, headers);
            ResponseEntity<ApiPaymentServiceTokenResponse> resEntity = restTemplate.exchange(endpoint, HttpMethod.POST,
                    reqEntity, ApiPaymentServiceTokenResponse.class);
            return resEntity.getBody();
        } catch (RestClientException e) {
            throw new ApiException("failed to request to payment service", HttpStatus.INTERNAL_SERVER_ERROR, e);
        }
    }

    public ApiShipmentCreateResponse createShipment(String shipmentUrl, ApiShipmentCreateRequest request) {
        try {
            String endpoint = shipmentUrl + "/create";
            HttpHeaders headers = new HttpHeaders();
            headers.add("User-Agent", userAgent);
            headers.add("Content-Type", "application/json");
            headers.add("Authorization", isucariApiToken);
            HttpEntity<ApiShipmentCreateRequest> reqEntity = new HttpEntity<>(request, headers);
            ResponseEntity<ApiShipmentCreateResponse> resEntity = restTemplate.exchange(endpoint, HttpMethod.POST,
                    reqEntity, ApiShipmentCreateResponse.class);
            return resEntity.getBody();
        } catch (RestClientException e) {
            throw new ApiException("failed to request to shipment service", HttpStatus.INTERNAL_SERVER_ERROR, e);
        }
    }

    public byte[] requestShipment(String shipmentUrl, ApiShipmentRequest request) {
        try {
            String endpoint = shipmentUrl + "/status";
            HttpHeaders headers = new HttpHeaders();
            headers.add("User-Agent", userAgent);
            headers.add("Content-Type", "application/json");
            headers.add("Authorization", isucariApiToken);
            HttpEntity<ApiShipmentRequest> reqEntity = new HttpEntity<>(request, headers);
            ResponseEntity<byte[]> resEntity = restTemplate.exchange(endpoint, HttpMethod.POST,
                    reqEntity, byte[].class);
            return resEntity.getBody();
        } catch (RestClientException e) {
            throw new ApiException("failed to request to shipment service", HttpStatus.INTERNAL_SERVER_ERROR, e);
        }
    }

    public ApiShipmentStatusResponse getShipmentStatus(String shipmentUrl, ApiShipmentStatusRequest request) {
        try {
            String endpoint = shipmentUrl + "/status";
            HttpHeaders headers = new HttpHeaders();
            headers.add("User-Agent", userAgent);
            headers.add("Content-Type", "application/json");
            headers.add("Authorization", isucariApiToken);
            HttpEntity<ApiShipmentStatusRequest> reqEntity = new HttpEntity<>(request, headers);
            ResponseEntity<ApiShipmentStatusResponse> resEntity = restTemplate.exchange(endpoint, HttpMethod.GET,
                    reqEntity, ApiShipmentStatusResponse.class);
            return resEntity.getBody();
        } catch (RestClientException e) {
            throw new ApiException("failed to request to shipment service", HttpStatus.INTERNAL_SERVER_ERROR, e);
        }
    }
}
