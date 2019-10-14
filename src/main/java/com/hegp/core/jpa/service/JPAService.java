package com.hegp.core.jpa.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

import javax.persistence.EntityManager;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface JPAService<T, ID> {
    // 保存所有的jpa实体类与EntityManager的对应关系
    Map<Class, EntityManager> entityManagerMap = new HashMap<>();
    // 保存所有的jpa实体类与SimpleJpaRepository的对应关系
    Map<Class, SimpleJpaRepository> simpleJpaRepositoryMap = new HashMap<>();

    T find(ID id);

    T save(T entity);

    List<T> save(T[] entities);

    List<T> save(List<T> entities);

    void delete(T entity);

    void delete(T[] entities);

    void delete(List<T> entities);

    void deleteById(ID id);

    void deleteById(ID[] ids);

    void deleteById(List<ID> ids);

    Page<T> page(Pageable pageable);

    EntityManager getEntityManager();

    EntityManager getEntityManager(Class clazz);

    SimpleJpaRepository<T, ID> getRepository();

    SimpleJpaRepository<T, ID> getRepository(Class clazz);
}
