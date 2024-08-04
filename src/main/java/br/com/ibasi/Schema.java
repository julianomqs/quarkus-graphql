package br.com.ibasi;

import java.time.LocalDateTime;
import java.util.List;

import org.eclipse.microprofile.graphql.Id;
import org.eclipse.microprofile.graphql.Input;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

public class Schema {

  @org.eclipse.microprofile.graphql.Enum("SortOrder")
  public enum SortOrder {
    ASC, DESC
  }

  @Builder
  @Input("IDOperators")
  public record IDOperators(
      @Id Integer eq,
      @Id Integer ne,
      @Id List<Integer> in,
      @Id List<Integer> notIn) {
  }

  @Builder
  @Input("StringOperators")
  public record StringOperators(
      String eq,
      String ne,
      String startsWith,
      String endsWith,
      String notStartsWith,
      String notEndsWith,
      String contains,
      String notContains,
      List<String> in,
      List<String> notIn) {
  }

  @Builder
  @Input("DateTimeRange")
  public record DateTimeRange(
      @NotNull LocalDateTime start,
      @NotNull LocalDateTime end) {
  }

  @Builder
  @Input("DateTimeOperators")
  public record DateTimeOperators(
      LocalDateTime eq,
      LocalDateTime ne,
      DateTimeRange between,
      DateTimeRange notBetween,
      DateTimeRange gt,
      DateTimeRange ge,
      DateTimeRange lt,
      DateTimeRange le,
      List<LocalDateTime> in,
      List<LocalDateTime> notIn) {
  }
}
