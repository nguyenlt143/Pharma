package vn.edu.fpt.pharma.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.edu.fpt.pharma.base.BaseEntity;
import vn.edu.fpt.pharma.entity.User;
import vn.edu.fpt.pharma.repository.UserRepository;
import vn.edu.fpt.pharma.service.AuditService;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuditServiceImpl implements AuditService {

    private final UserRepository userRepository;

    @Override
    public <T extends BaseEntity<?>> List<T> addAuditInfo(List<T> entities) {
        Set<Long> userIds = entities.stream()
                .mapMulti((T entity, Consumer<Long> consumer) -> {
                    if (entity.getCreatedBy() != null) consumer.accept(entity.getCreatedBy());
                    if (entity.getUpdatedBy() != null) consumer.accept(entity.getUpdatedBy());
                })
                .collect(Collectors.toSet());

        Map<Long, String> userNames = getUserNames(new ArrayList<>(userIds));

        entities.forEach(entity -> {
            if (entity.getCreatedBy() != null) entity.setCreatedByName(userNames.get(entity.getCreatedBy()));
            if (entity.getUpdatedBy() != null) entity.setUpdatedByName(userNames.get(entity.getUpdatedBy()));
        });

        return entities;
    }

    private Map<Long, String> getUserNames(List<Long> userIds) {
        return userRepository.findAllById(userIds).stream()
                .collect(Collectors.toMap(
                        BaseEntity::getId,
                        User::getFullName
                ));
    }
}
