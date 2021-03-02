package com.zacharywarunek.kettering.cs461project.repositories.JPA;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;

@NoRepositoryBean
public interface IJPABaseRepo<T, ID extends Serializable> extends JpaRepository<T, Serializable> {
    public <S extends T> S save(S entity);
}
