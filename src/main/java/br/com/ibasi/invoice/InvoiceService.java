package br.com.ibasi.invoice;

import static br.com.ibasi.jooq.Tables.INVOICE;
import static br.com.ibasi.jooq.Tables.INVOICE_ITEM;
import static org.jooq.impl.DSL.multiset;
import static org.jooq.impl.DSL.row;
import static org.jooq.impl.DSL.select;

import java.util.ArrayList;

import org.jooq.DSLContext;
import org.jooq.Records;

import br.com.ibasi.Page;
import br.com.ibasi.Schema.IDOperators;
import br.com.ibasi.invoice.InvoiceSchema.FindInvoiceFilter;
import br.com.ibasi.invoice.InvoiceSchema.FindInvoiceSort;
import br.com.ibasi.product.Product;
import br.com.ibasi.util.JooqUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.Builder;

@ApplicationScoped
public class InvoiceService {

  @Inject
  private EntityManager entityManager;
  @Inject
  private DSLContext db;

  @Builder
  public record FindOneInvoiceConfig(FindInvoiceFilter filter) {
  }

  @Builder
  public record FindManyInvoiceConfig(
      Integer offset,
      Integer limit,
      FindInvoiceFilter filter,
      FindInvoiceSort sort) {
  }

  @Transactional
  public Invoice save(Invoice invoice) {
    if (invoice.getId() == null) {
      entityManager.persist(invoice);
    } else {
      invoice = entityManager.merge(invoice);
    }

    entityManager.flush();

    return findOne(invoice.getId());
  }

  @Transactional
  public Invoice remove(Invoice invoice) {
    var mergedEntity = entityManager.merge(invoice);

    entityManager.remove(mergedEntity);

    entityManager.flush();

    return findOne(invoice.getId());
  }

  public Invoice findOne(Integer id) {
    return findOne(FindOneInvoiceConfig.builder()
        .filter(FindInvoiceFilter.builder()
            .id(IDOperators.builder()
                .eq(id)
                .build())
            .build())
        .build());
  }

  public Invoice findOne(FindOneInvoiceConfig config) {
    var where = JooqUtil.buildWhere(config.filter());

    return db.select(
        INVOICE.ID,
        INVOICE.DATETIME,
        multiset(
            select(
                INVOICE_ITEM.ID,
                INVOICE_ITEM.QUANTITY,
                INVOICE_ITEM.UNIT_VALUE,
                row(INVOICE_ITEM.product().ID, INVOICE_ITEM.product().NAME).mapping(Product.class, Product::new))
                    .from(INVOICE_ITEM)
                    .where(INVOICE_ITEM.INVOICE_ID.eq(INVOICE.ID)))
                        .as("items")
                        .convertFrom(r -> r.intoSet(Records.mapping(InvoiceItem::new))))
        .from(INVOICE)
        .where(where)
        .fetchOne(Records.mapping(Invoice::new));
  }

  public Page<Invoice> findMany(FindManyInvoiceConfig config) {
    var where = JooqUtil.buildWhere(config.filter());

    var orderBy = JooqUtil.buildOrderBy(config.sort());

    var fetch = JooqUtil.paginate(
        db,
        db.select(
            INVOICE.ID,
            INVOICE.DATETIME,
            multiset(
                select(
                    INVOICE_ITEM.ID,
                    INVOICE_ITEM.QUANTITY,
                    INVOICE_ITEM.UNIT_VALUE,
                    row(
                        INVOICE_ITEM.product().ID,
                        INVOICE_ITEM.product().NAME).mapping(Product.class, Product::new))
                            .from(INVOICE_ITEM)
                            .where(INVOICE_ITEM.INVOICE_ID.eq(INVOICE.ID)))
                                .as("items")
                                .convertFrom(r -> r.intoSet(Records.mapping(InvoiceItem::new))))
            .from(INVOICE)
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
        fetch.into(INVOICE.ID,
            INVOICE.DATETIME,
            multiset(
                select(
                    INVOICE_ITEM.ID,
                    INVOICE_ITEM.QUANTITY,
                    INVOICE_ITEM.UNIT_VALUE,
                    row(
                        INVOICE_ITEM.product().ID,
                        INVOICE_ITEM.product().NAME).mapping(Product.class, Product::new))
                            .from(INVOICE_ITEM)
                            .where(INVOICE_ITEM.INVOICE_ID.eq(INVOICE.ID)))
                                .as("items")
                                .convertFrom(r -> r.intoSet(Records.mapping(InvoiceItem::new))))
            .map(Records.mapping(Invoice::new)));
  }
}
