package com.ainetdinov.rest.service;

import com.ainetdinov.rest.model.Entity;
import lombok.Getter;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Getter
public abstract class EntityService<T extends Entity> {
    protected final ConcurrentMap<UUID, T> entities;
    protected final ValidatorService<T> validator;

    public EntityService(List<T> entities, ValidatorService<T> validator) {
        this.entities = initEntities(entities);
        this.validator = validator;
    }

    public boolean isUnique(T entity) {
        return !entities.containsValue(entity);
    }

    public List<T> getEntities(Predicate<T> filter) {
        return entities.values()
                .stream()
                .filter(filter)
                .collect(Collectors.toList());
    }

    public T getEntity(UUID uuid) {
        return entities.get(uuid);
    }

    public T getEntity(Predicate<T> filter) {
        return entities.values()
                .stream()
                .filter(filter)
                .findFirst()
                .orElse(null);
    }

    public String generateUUID() {
        UUID uuid;
        do {
            uuid = UUID.randomUUID();
        } while (entities.containsKey(uuid));
        return uuid.toString();
    }

    @SafeVarargs
    protected final boolean validateEntity(T entity, Predicate<T>... filters) {
        return Arrays.stream(filters).allMatch(filter -> filter.test(entity));
    }

    private ConcurrentMap<UUID, T> initEntities(List<T> entities) {
        return new ConcurrentHashMap<>(entities.stream()
                .collect(Collectors.toMap(e -> UUID.fromString(e.getUuid()), Function.identity())));
    }
}
