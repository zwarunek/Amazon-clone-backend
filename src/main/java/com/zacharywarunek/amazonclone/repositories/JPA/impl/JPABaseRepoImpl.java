package com.zacharywarunek.amazonclone.repositories.JPA.impl;

import com.zacharywarunek.amazonclone.repositories.JPA.IJPABaseRepo;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

import javax.persistence.EntityManager;
import java.io.Serializable;

public class JPABaseRepoImpl <T, ID extends Serializable> extends SimpleJpaRepository<T, Serializable> implements IJPABaseRepo<T, Serializable> {

    private JpaEntityInformation<T, ?> entityInformation;
    private EntityManager entityManager;

    public JPABaseRepoImpl(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager){
        super(entityInformation, entityManager);
        this.entityInformation = entityInformation;
        this.entityManager = entityManager;
    }

    @Override
    public <S extends T> S save(S entity) {
        if(entityInformation.isNew(entity)){
            entityManager.persist(entity);
            return entity;
        }
        else
            return entityManager.merge(entity);
    }
}
