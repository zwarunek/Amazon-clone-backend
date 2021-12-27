package com.zacharywarunek.amazonclone.util.JPA;

import java.io.Serializable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface IJPABaseRepo<T> extends JpaRepository<T, Serializable> {
  <S extends T> S save(S entity);
}
