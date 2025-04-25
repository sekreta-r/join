package ru.hpclab.hl.module1.service;

import org.springframework.stereotype.Service;
import ru.hpclab.hl.module1.client.CourierClient;
import ru.hpclab.hl.module1.client.DeliveryClient;
import ru.hpclab.hl.module1.client.ParcelClient;
import ru.hpclab.hl.module1.dto.*;

import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;


@Service

public class CourierStatsService {

    private final CourierClient courierClient;
    private final DeliveryClient deliveryClient;
    private final ParcelClient parcelClient;

    public CourierStatsService(CourierClient courierClient, DeliveryClient deliveryClient, ParcelClient parcelClient) {
        this.courierClient = courierClient;
        this.deliveryClient = deliveryClient;
        this.parcelClient = parcelClient;
    }

    public List<CourierStatsDTO> getStatsForAllCouriers() {
        List<DeliveryDTO> deliveries = Optional.ofNullable(deliveryClient.getAllDeliveries())
                .map(Arrays::asList)
                .orElse(Collections.emptyList());

        List<CourierDTO> couriers = Optional.ofNullable(courierClient.getAllCouriers())
                .map(Arrays::asList)
                .orElse(Collections.emptyList());

        Map<Long, String> courierNamesById = couriers.stream()
                .collect(Collectors.toMap(CourierDTO::getId, CourierDTO::getFullName));

        Map<String, Map<Month, Double>> statsByCourier = new HashMap<>();

        for (DeliveryDTO delivery : deliveries) {
            if (!"DELIVERED".equalsIgnoreCase(delivery.getStatus())) continue;

            ParcelDTO parcel = parcelClient.getById(delivery.getParcelId());
            if (parcel == null) continue;

            String courierName = courierNamesById.get(delivery.getCourierId());
            if (courierName == null) continue;

            Month month = delivery.getDeliveryDate().getMonth();
            double weight = parcel.getWeight();

            statsByCourier
                    .computeIfAbsent(courierName, k -> new HashMap<>())
                    .merge(month, weight, Double::sum);
        }

        return statsByCourier.entrySet().stream()
                .map(e -> new CourierStatsDTO(e.getKey(), e.getValue()))
                .toList();
    }

}
