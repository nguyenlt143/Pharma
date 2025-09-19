package vn.edu.fpt.pharma.dto.store;

import vn.edu.fpt.pharma.entity.Store;

public record StoreCsvInput(
        String storeCode,
        String storeName,
        String address,
        String addressCode,
        String phoneNumber,
        Double latitude,
        Double longitude
) {
    public Store toEntity() {
        return Store.builder()
                .storeCode(storeCode)
                .storeName(storeName)
                .address(address)
                .addressCode(addressCode)
                .phoneNumber(phoneNumber)
                .latitude(latitude)
                .longitude(longitude)
                .build();
    }
}
