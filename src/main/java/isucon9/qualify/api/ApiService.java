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

    public ApiShipmentStatusResponse getShipmentStatus(String shipmentUrl, ApiShipmentStatusRequest request) {
        try {
            String endpoint = shipmentUrl + "/status";
            HttpHeaders headers = new HttpHeaders();
            headers.add("User-Agent", userAgent);
            headers.add("Content-Type", "application/json");
            headers.add("Authorization", isucariApiToken);
            HttpEntity<ApiShipmentStatusRequest> reqEntity = new HttpEntity<>(request, headers);
            ResponseEntity<ApiShipmentStatusResponse> resEntity = restTemplate.exchange(endpoint, HttpMethod.GET, reqEntity, ApiShipmentStatusResponse.class);
            return resEntity.getBody();    
        } catch (RestClientException e) {
            // TODO: log
            throw new ApiException("failed to request to shipment service", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
