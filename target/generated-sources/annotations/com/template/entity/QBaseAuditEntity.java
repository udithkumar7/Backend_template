package com.template.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QBaseAuditEntity is a Querydsl query type for BaseAuditEntity
 */
@Generated("com.querydsl.codegen.DefaultSupertypeSerializer")
public class QBaseAuditEntity extends EntityPathBase<BaseAuditEntity> {

    private static final long serialVersionUID = 1880039273L;

    public static final QBaseAuditEntity baseAuditEntity = new QBaseAuditEntity("baseAuditEntity");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final StringPath createdBy = createString("createdBy");

    public final DateTimePath<java.time.LocalDateTime> updatedAt = createDateTime("updatedAt", java.time.LocalDateTime.class);

    public final StringPath updatedBy = createString("updatedBy");

    public final NumberPath<Long> version = createNumber("version", Long.class);

    public QBaseAuditEntity(String variable) {
        super(BaseAuditEntity.class, forVariable(variable));
    }

    public QBaseAuditEntity(Path<? extends BaseAuditEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QBaseAuditEntity(PathMetadata metadata) {
        super(BaseAuditEntity.class, metadata);
    }

}

