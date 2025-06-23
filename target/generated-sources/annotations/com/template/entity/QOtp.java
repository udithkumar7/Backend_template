package com.template.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QOtp is a Querydsl query type for Otp
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QOtp extends EntityPathBase<Otp> {

    private static final long serialVersionUID = -889767801L;

    public static final QOtp otp = new QOtp("otp");

    public final QBaseAuditEntity _super = new QBaseAuditEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final StringPath createdBy = _super.createdBy;

    public final StringPath email = createString("email");

    public final DateTimePath<java.time.LocalDateTime> expiryTime = createDateTime("expiryTime", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath otpCode = createString("otpCode");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    //inherited
    public final StringPath updatedBy = _super.updatedBy;

    public final BooleanPath used = createBoolean("used");

    //inherited
    public final NumberPath<Long> version = _super.version;

    public QOtp(String variable) {
        super(Otp.class, forVariable(variable));
    }

    public QOtp(Path<? extends Otp> path) {
        super(path.getType(), path.getMetadata());
    }

    public QOtp(PathMetadata metadata) {
        super(Otp.class, metadata);
    }

}

