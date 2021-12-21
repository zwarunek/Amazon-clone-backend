package com.zacharywarunek.amazonclone.util.JPA.impl;

import com.zacharywarunek.amazonclone.util.JPA.IJPABaseRepo;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

import javax.persistence.EntityManager;
import java.io.Serializable;

public class JPABaseRepoImpl<T> extends SimpleJpaRepository<T, Serializable>
    implements IJPABaseRepo<T> {

  private final JpaEntityInformation<T, ?> entityInformation;
  private final EntityManager entityManager;

  public JPABaseRepoImpl(
      JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
    super(entityInformation, entityManager);
    this.entityInformation = entityInformation;
    this.entityManager = entityManager;
  }

  @Override
  public <S extends T> S save(S entity) {
    if (entityInformation.isNew(entity)) {
      entityManager.persist(entity);
      return entity;
    } else return entityManager.merge(entity);
  }
}
