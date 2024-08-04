package br.com.ibasi.constraint;

import static org.jooq.impl.DSL.count;
import static org.jooq.impl.DSL.field;

import org.jooq.DSLContext;

import jakarta.inject.Inject;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ExistentIdValidator implements ConstraintValidator<ExistentId, Integer> {

  @Inject
  private DSLContext db;

  private String table;

  @Override
  public void initialize(ExistentId constraintAnnotation) {
    this.table = constraintAnnotation.value().getName();
  }

  @Override
  public boolean isValid(Integer value, ConstraintValidatorContext context) {
    if (value == null) {
      return true;
    }

    var count = db.select(count())
        .from(table)
        .where(field("id").eq(value))
        .fetchOneInto(Integer.class);

    if (count == 0) {
      return false;
    }

    return true;
  }
}
