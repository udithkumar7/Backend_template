package com.template.jquery.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QProduct is a Querydsl query type for Product
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QProduct extends EntityPathBase<Product> {

    private static final long serialVersionUID = 1122151085L;

    public static final QProduct product = new QProduct("product");

    public final com.template.entity.QBaseAuditEntity _super = new com.template.entity.QBaseAuditEntity(this);

    public final BooleanPath active = createBoolean("active");

    public final StringPath brand = createString("brand");

    public final StringPath category = createString("category");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final StringPath createdBy = _super.createdBy;

    public final StringPath description = createString("description");

    public final DateTimePath<java.time.LocalDateTime> discontinueDate = createDateTime("discontinueDate", java.time.LocalDateTime.class);

    public final BooleanPath featured = createBoolean("featured");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final DateTimePath<java.time.LocalDateTime> launchDate = createDateTime("launchDate", java.time.LocalDateTime.class);

    public final StringPath name = createString("name");

    public final NumberPath<java.math.BigDecimal> price = createNumber("price", java.math.BigDecimal.class);

    public final NumberPath<java.math.BigDecimal> rating = createNumber("rating", java.math.BigDecimal.class);

    public final NumberPath<Integer> reviewCount = createNumber("reviewCount", Integer.class);

    public final StringPath sku = createString("sku");

    public final NumberPath<Integer> stockQuantity = createNumber("stockQuantity", Integer.class);

    public final StringPath subCategory = createString("subCategory");

    public final StringPath tags = createString("tags");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    //inherited
    public final StringPath updatedBy = _super.updatedBy;

    //inherited
    public final NumberPath<Long> version = _super.version;

    public final NumberPath<java.math.BigDecimal> weightKg = createNumber("weightKg", java.math.BigDecimal.class);

    public QProduct(String variable) {
        super(Product.class, forVariable(variable));
    }

    public QProduct(Path<? extends Product> path) {
        super(path.getType(), path.getMetadata());
    }

    public QProduct(PathMetadata metadata) {
        super(Product.class, metadata);
    }

}

