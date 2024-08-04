package br.com.ibasi.product;

import static br.com.ibasi.jooq.Tables.PRODUCT;

import java.util.ArrayList;

import org.jooq.DSLContext;
import org.jooq.Records;

import br.com.ibasi.Page;
import br.com.ibasi.Schema.IDOperators;
import br.com.ibasi.product.ProductSchema.FindProductFilter;
import br.com.ibasi.product.ProductSchema.FindProductSort;
import br.com.ibasi.util.JooqUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.Builder;

@ApplicationScoped
public class ProductService {

  @Inject
  private EntityManager entityManager;
  @Inject
  private DSLContext db;

  @Builder
  public record FindOneProductConfig(FindProductFilter filter) {
  }

  @Builder
  public record FindManyProductConfig(
      Integer offset,
      Integer limit,
      FindProductFilter filter,
      FindProductSort sort) {
  }

  @Transactional
  public Product save(Product product) {
    if (product.getId() == null) {
      entityManager.persist(product);
    } else {
      product = entityManager.merge(product);
    }

    return findOne(product.getId());
  }

  @Transactional
  public Product remove(Product product) {
    var mergedEntity = entityManager.merge(product);

    entityManager.remove(mergedEntity);

    return findOne(mergedEntity.getId());
  }

  public Product findOne(Integer id) {
    return findOne(FindOneProductConfig.builder()
        .filter(FindProductFilter.builder()
            .id(IDOperators.builder()
                .eq(id)
                .build())
            .build())
        .build());
  }

  public Product findOne(FindOneProductConfig config) {
    var where = JooqUtil.buildWhere(config.filter());

    return db.select(PRODUCT.ID, PRODUCT.NAME)
        .from(PRODUCT)
        .where(where)
        .fetchOne(Records.mapping(Product::new));
  }

  public Page<Product> findMany(FindManyProductConfig config) {
    var where = JooqUtil.buildWhere(config.filter());

    var orderBy = JooqUtil.buildOrderBy(config.sort());

    var fetch = JooqUtil.paginate(
        db,
        db.select(PRODUCT.ID, PRODUCT.NAME)
            .from(PRODUCT)
            .where(where),
        orderBy,
        config.limit(),
        config.offset())
        .fetch();

    if (fetch.size() == 0) {
      return new Page<>(0, new ArrayList<>());
    }

    return new Page<>(
        fetch.get(0).get("total_rows", Integer.class),
        fetch.into(PRODUCT.ID, PRODUCT.NAME).map(Records.mapping(Product::new)));
  }
}
