package com.shared;

import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BaseService<T, Type> {
    T save(T request);

    List<T> findAll(Pageable pageable);

    T findById(Type id);

    void delete(Type id);
    T Update(Type id, T request);
}
