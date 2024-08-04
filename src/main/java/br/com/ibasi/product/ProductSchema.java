package br.com.ibasi.product;

import java.util.List;

import org.eclipse.microprofile.graphql.DefaultValue;
import org.eclipse.microprofile.graphql.Id;
import org.eclipse.microprofile.graphql.Input;
import org.eclipse.microprofile.graphql.Type;

import br.com.ibasi.Schema.IDOperators;
import br.com.ibasi.Schema.SortOrder;
import br.com.ibasi.Schema.StringOperators;
import br.com.ibasi.constraint.ExistentId;
import br.com.ibasi.constraint.ExistentTables;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

public class ProductSchema {

  @Input("CreateProductInput")
  public record CreateProductInput(@NotBlank String name) {
  }

  @Input("UpdateProductInput")
  public record UpdateProductInput(@NotNull @ExistentId(ExistentTables.PRODUCT) @Id Integer id, String name) {
  }

  @Input("RemoveProductInput")
  public record RemoveProductInput(@NotNull @ExistentId(ExistentTables.PRODUCT) @Id Integer id) {
  }

  @Input("FindOneProductInput")
  public record FindOneProductInput(@NotNull FindProductFilter filter) {
  }

  @Type("Product")
  public record ProductPayload(@NotNull @Id Integer id, @NotBlank String name) {
  }

  @Input("FindManyProductInput")
  public record FindManyProductInput(
      @Min(0) @DefaultValue("0") Integer offset,
      @Min(0) @Max(100) @DefaultValue("20") Integer limit,
      FindProductFilter filter,
      FindProductSort sort) {
  }

  @Type("FindManyProductPayload")
  public record FindManyProductPayload(@NotNull Integer total, @NotNull List<@NotNull ProductPayload> results) {
  }

  @Builder
  @Input("FindProductFilter")
  public record FindProductFilter(
      IDOperators id,
      StringOperators name,
      List<FindProductFilter2> or,
      List<FindProductFilter2> and,
      List<FindProductFilter2> not) {
  }

  @Builder
  @Input("FindProductFilter2")
  public record FindProductFilter2(IDOperators id, StringOperators name) {
  }

  @Builder
  @Input("FindProductSort")
  public record FindProductSort(SortOrder id, SortOrder name) {
  }
}
