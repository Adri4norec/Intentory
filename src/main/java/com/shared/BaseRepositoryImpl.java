package com.shared;

import org.springframework.data.jpa.repository.JpaRepository;

public abstract class BaseRepositoryImpl<T, Type> implements JpaRepository<T, Type>, BaseRepository {

}
