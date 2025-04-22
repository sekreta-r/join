package ru.hpclab.hl.module1.client;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.hpclab.hl.module1.dto.CourierDTO;
import org.springframework.beans.factory.annotation.Value;


@Component
public class CourierClient {
    @Value("${core.service.host}")
    private String coreServiceHost;

    @Value("${core.service.port}")
    private String coreServicePort;

    private final RestTemplate restTemplate;

    public CourierClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public CourierDTO[] getAllCouriers() {
        return restTemplate.getForObject("http://" + coreServiceHost + ":" + coreServicePort + "/couriers", CourierDTO[].class);
    }
}
