package com.zacharywarunek.amazonclone.util.JPA;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;

@NoRepositoryBean
public interface IJPABaseRepo<T> extends JpaRepository<T, Serializable> {
    <S extends T> S save(S entity);
}
