package br.com.ibasi.util;

import static org.jooq.impl.DSL.and;
import static org.jooq.impl.DSL.count;
import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.inline;
import static org.jooq.impl.DSL.max;
import static org.jooq.impl.DSL.noCondition;
import static org.jooq.impl.DSL.not;
import static org.jooq.impl.DSL.or;
import static org.jooq.impl.DSL.rowNumber;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Select;
import org.jooq.SortField;
import org.jooq.Table;

import br.com.ibasi.Schema.IDOperators;
import br.com.ibasi.Schema.SortOrder;
import br.com.ibasi.Schema.StringOperators;

public class JooqUtil {

  private JooqUtil() {
  }

  public static Field<?>[] buildSelect(Set<String> fields) {
    if (fields == null) {
      return new Field<?>[] {};
    }

    return fields
        .stream()
        .map(f -> field(f))
        .toArray(Field<?>[]::new);
  }

  public static Condition buildWhere(Record record) {
    if (record == null) {
      return noCondition();
    }

    var conditions = new ArrayList<Condition>();
    var components = record.getClass().getRecordComponents();

    for (var component : components) {
      try {
        var value = component.getAccessor().invoke(record);

        if (value != null) {
          if (value instanceof IDOperators || value instanceof StringOperators) {
            conditions.add(buildCondition(component.getName(), (Record) value));
          } else if (value.getClass().isArray()) {
            conditions.add(buildCondition(component.getName(), (Object[]) value));
          }
        }
      } catch (Exception ex) {
        throw new RuntimeException(ex);
      }
    }

    return and(conditions);
  }

  public static List<SortField<?>> buildOrderBy(Record record) {
    if (record == null) {
      return new ArrayList<>();
    }

    var orderBys = new ArrayList<SortField<?>>();
    var components = record.getClass().getRecordComponents();

    for (var component : components) {
      try {
        var value = component.getAccessor().invoke(record);

        if (value != null) {
          SortOrder sortOrder = (SortOrder) value;

          if (sortOrder == SortOrder.ASC) {
            orderBys.add(field(component.getName()).asc());
          } else if (sortOrder == SortOrder.DESC) {
            orderBys.add(field(component.getName()).desc());
          }

        }
      } catch (Exception ex) {
        throw new RuntimeException(ex);
      }
    }

    return orderBys;
  }

  public static Select<?> paginate(
      DSLContext ctx,
      Select<?> original,
      List<SortField<?>> sort,
      int limit,
      int offset) {
    Table<?> u = original.asTable("u");
    Field<Integer> totalRows = count().over().as("total_rows");
    Field<Integer> row = rowNumber().over().orderBy(u.fields(sort.toArray(Field<?>[]::new)))
        .as("row");

    Table<?> t = ctx
        .select(u.asterisk())
        .select(totalRows, row)
        .from(u)
        .orderBy(u.fields(sort.toArray(Field<?>[]::new)))
        .limit(limit)
        .offset(offset)
        .asTable("t");

    Select<?> result = ctx
        .select(t.fields(original.getSelect().toArray(Field<?>[]::new)))
        .select(
            count().over().as("actual_page_size"),
            field(max(t.field(row)).over().eq(t.field(totalRows)))
                .as("last_page"),
            t.field(totalRows),
            t.field(row),
            t.field(row).minus(inline(1)).div(limit).plus(inline(1))
                .as("current_page"))
        .from(t)
        .orderBy(t.fields(sort.toArray(Field<?>[]::new)));

    return result;
  }

  private static Condition buildCondition(String name, Record record) throws Exception {
    var components = record.getClass().getRecordComponents();

    for (var component : components) {
      var value = component.getAccessor().invoke(record);

      if (value != null) {
        switch (component.getName()) {
          case "eq":
            return field(name).eq(value);

          case "ne":
            return field(name).ne(value);

          case "in":
            return field(name).in(value);

          case "notIn":
            return field(name).notIn(value);

          case "startsWith":
            return field(name).startsWith(value);

          case "notStartsWith":
            return not(field(name).startsWith(value));

          case "endsWith":
            return field(name).endsWith(value);

          case "notEndsWith":
            return not(field(name).endsWith(value));

          case "contains":
            return field(name).contains(value);

          case "notContains":
            return field(name).notContains(value);

          default:
            throw new IllegalArgumentException("Invalid operator");
        }
      }
    }

    return noCondition();
  }

  private static Condition buildCondition(String name, Object[] array) throws Exception {
    var conditions = new ArrayList<Condition>();

    for (var item : array) {
      var components = item.getClass().getRecordComponents();

      for (var component : components) {
        var value = component.getAccessor().invoke(item);

        if (value != null) {
          conditions.add(buildCondition(component.getName(), (Record) value));
        }
      }
    }

    switch (name) {
      case "or":
        return or(conditions);

      case "and":
        return and(conditions);

      case "not":
        return not(and(conditions));

      default:
        throw new IllegalArgumentException("Invalid operator");
    }
  }
}
