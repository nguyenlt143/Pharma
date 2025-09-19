package vn.edu.fpt.pharma.dto.store;

import vn.edu.fpt.pharma.entity.Store;

import java.util.Map;

public record StoreCsvOutput(
        Long id,
        String storeCode,
        String storeName,
        String address,
        String addressCode,
        String phoneNumber,
        Double latitude,
        Double longitude
) {
    public StoreCsvOutput(Store store) {
        this(
                store.getId(),
                store.getStoreCode(),
                store.getStoreName(),
                store.getAddress(),
                store.getAddressCode(),
                store.getPhoneNumber(),
                store.getLatitude(),
                store.getLongitude()
        );
    }
}
