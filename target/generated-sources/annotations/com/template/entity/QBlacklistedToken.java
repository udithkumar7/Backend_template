package com.template.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QBlacklistedToken is a Querydsl query type for BlacklistedToken
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QBlacklistedToken extends EntityPathBase<BlacklistedToken> {

    private static final long serialVersionUID = -791600895L;

    public static final QBlacklistedToken blacklistedToken = new QBlacklistedToken("blacklistedToken");

    public final QBaseAuditEntity _super = new QBaseAuditEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final StringPath createdBy = _super.createdBy;

    public final DateTimePath<java.time.Instant> expiry = createDateTime("expiry", java.time.Instant.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath token = createString("token");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    //inherited
    public final StringPath updatedBy = _super.updatedBy;

    //inherited
    public final NumberPath<Long> version = _super.version;

    public QBlacklistedToken(String variable) {
        super(BlacklistedToken.class, forVariable(variable));
    }

    public QBlacklistedToken(Path<? extends BlacklistedToken> path) {
        super(path.getType(), path.getMetadata());
    }

    public QBlacklistedToken(PathMetadata metadata) {
        super(BlacklistedToken.class, metadata);
    }

}

