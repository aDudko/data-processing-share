package com.data.processing.analytics.service.dataaccess.repository.impl;

import com.data.processing.analytics.service.dataaccess.entity.BaseEntity;
import com.data.processing.analytics.service.dataaccess.repository.AnalyticsCustomRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.util.Collection;

@Slf4j
@Repository
public class AnalyticsRepositoryImpl<T extends BaseEntity<PK>, PK> implements AnalyticsCustomRepository<T, PK> {


    @PersistenceContext
    protected EntityManager entityManager;

    @Value("${spring.jpa.properties.hibernate.jdbc.batch_size:50}")
    protected int batchSize;


    @Override
    @Transactional
    public <S extends T> PK persist(S entity) {
        this.entityManager.persist(entity);
        return entity.getId();
    }

    @Override
    @Transactional
    public <S extends T> void batchPersist(Collection<S> entities) {
        if (entities.isEmpty()) {
            log.info("No entity found to insert!");
            return;
        }
        int batchCnt = 0;
        for (S entity : entities) {
            log.trace("Persisting entity with id: {}", entity.getId());
            this.entityManager.persist(entity);
            batchCnt++;
            if (batchCnt % batchSize == 0) {
                this.entityManager.flush();
                this.entityManager.clear();
            }
        }
        if (batchCnt % batchSize != 0) {
            this.entityManager.flush();
            this.entityManager.clear();
        }
    }

    @Override
    @Transactional
    public <S extends T> S merge(S entity) {
        return this.entityManager.merge(entity);
    }

    @Override
    @Transactional
    public <S extends T> void batchMerge(Collection<S> entities) {
        if (entities.isEmpty()) {
            log.info("No entity found to insert!");
            return;
        }
        int batchCnt = 0;
        for (S entity : entities) {
            log.trace("Merging entity with id: {}", entity.getId());
            this.entityManager.merge(entity);
            batchCnt++;
            if (batchCnt % batchSize == 0) {
                this.entityManager.flush();
                this.entityManager.clear();
            }
        }
        if (batchCnt % batchSize != 0) {
            this.entityManager.flush();
            this.entityManager.clear();
        }
    }

    @Override
    public void clear() {
    }

}
