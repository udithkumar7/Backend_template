package com.template.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QUser is a Querydsl query type for User
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUser extends EntityPathBase<User> {

    private static final long serialVersionUID = -1812820497L;

    public static final QUser user = new QUser("user");

    public final QBaseAuditEntity _super = new QBaseAuditEntity(this);

    public final DateTimePath<java.time.LocalDateTime> accountExpiryDate = createDateTime("accountExpiryDate", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> accountLockedUntil = createDateTime("accountLockedUntil", java.time.LocalDateTime.class);

    public final BooleanPath accountNonLocked = createBoolean("accountNonLocked");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final StringPath createdBy = _super.createdBy;

    public final StringPath email = createString("email");

    public final BooleanPath enabled = createBoolean("enabled");

    public final NumberPath<Integer> failedLoginAttempts = createNumber("failedLoginAttempts", Integer.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath password = createString("password");

    public final StringPath password1 = createString("password1");

    public final StringPath password2 = createString("password2");

    public final StringPath password3 = createString("password3");

    public final StringPath password4 = createString("password4");

    public final StringPath password5 = createString("password5");

    public final SetPath<Role, QRole> roles = this.<Role, QRole>createSet("roles", Role.class, QRole.class, PathInits.DIRECT2);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    //inherited
    public final StringPath updatedBy = _super.updatedBy;

    public final StringPath username = createString("username");

    //inherited
    public final NumberPath<Long> version = _super.version;

    public QUser(String variable) {
        super(User.class, forVariable(variable));
    }

    public QUser(Path<? extends User> path) {
        super(path.getType(), path.getMetadata());
    }

    public QUser(PathMetadata metadata) {
        super(User.class, metadata);
    }

}

