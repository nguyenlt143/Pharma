package vn.edu.fpt.pharma.dto.store;

import vn.edu.fpt.pharma.entity.Store;

import java.time.LocalDateTime;

public record StoreVM(
        Long id,
        String storeCode,
        String storeName,
        String address,
        String addressCode,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        String createdByName,
        String updatedByName
) {
    public StoreVM(Store store) {
        this(
                store.getId(),
                store.getStoreCode(),
                store.getStoreName(),
                store.getAddress(),
                store.getAddressCode(),
                store.getCreatedAt(),
                store.getUpdatedAt(),
                store.getCreatedByName(),
                store.getUpdatedByName()
        );
    }
}