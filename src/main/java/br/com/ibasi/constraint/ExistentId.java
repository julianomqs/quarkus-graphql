package br.com.ibasi.constraint;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import br.com.ibasi.constraint.ExistentId.List;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target({ FIELD, PARAMETER, TYPE_USE })
@Retention(RUNTIME)
@Constraint(validatedBy = ExistentIdValidator.class)
@Documented
@Repeatable(List.class)
public @interface ExistentId {

  String message() default "Registro inexistente";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

  ExistentTables value();

  @Target({ FIELD, PARAMETER, TYPE_USE })
  @Retention(RUNTIME)
  @Documented
  @interface List {
    ExistentId[] value();
  }
}
