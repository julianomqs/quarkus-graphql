package br.com.ibasi.invoice;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.eclipse.microprofile.graphql.DefaultValue;
import org.eclipse.microprofile.graphql.Id;
import org.eclipse.microprofile.graphql.Input;
import org.eclipse.microprofile.graphql.Type;

import br.com.ibasi.Schema.DateTimeOperators;
import br.com.ibasi.Schema.IDOperators;
import br.com.ibasi.Schema.SortOrder;
import br.com.ibasi.constraint.ExistentId;
import br.com.ibasi.constraint.ExistentTables;
import br.com.ibasi.product.ProductSchema.ProductPayload;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

public class InvoiceSchema {

  @Input("CreateInvoiceInput")
  public record CreateInvoiceInput(
      @NotNull LocalDateTime datetime,
      @NotNull List<@NotNull CreateInvoiceItemInput> items) {
  }

  @Input("CreateInvoiceItemInput")
  public record CreateInvoiceItemInput(
      @NotNull @ExistentId(ExistentTables.PRODUCT) @Id Integer product,
      @NotNull @Min(0) BigDecimal quantity,
      @NotNull @Min(0) BigDecimal unitValue) {
  }

  @Input("UpdateInvoiceInput")
  public record UpdateInvoiceInput(
      @NotNull @ExistentId(ExistentTables.INVOICE) @Id Integer id,
      LocalDateTime datetime,
      @DefaultValue("{}") ModifyInvoiceItemInput items) {
  }

  @Input("UpdateInvoiceItemInput")
  public record UpdateInvoiceItemInput(
      @NotNull @ExistentId(ExistentTables.INVOICE_ITEM) @Id Integer id,
      @ExistentId(ExistentTables.PRODUCT) @Id Integer product,
      @Min(0) BigDecimal quantity,
      @Min(0) BigDecimal unitValue) {
  }

  @Input("ModifyInvoiceItemInput")
  public record ModifyInvoiceItemInput(
      @DefaultValue("[]") List<CreateInvoiceItemInput> create,
      @DefaultValue("[]") List<UpdateInvoiceItemInput> update,
      @DefaultValue("[]") @Id List<@ExistentId(ExistentTables.INVOICE_ITEM) Integer> remove) {
  }

  @Input("RemoveInvoiceInput")
  public record RemoveInvoiceInput(@NotNull @ExistentId(ExistentTables.INVOICE) @Id Integer id) {
  }

  @Input("FindOneInvoiceInput")
  public record FindOneInvoiceInput(@NotNull FindInvoiceFilter filter) {
  }

  @Type("Invoice")
  public record InvoicePayload(
      @NotNull @Id Integer id,
      @NotNull LocalDateTime datetime,
      @NotNull List<@NotNull InvoiceItemPayload> items) {
  }

  @Type("InvoiceItem")
  public record InvoiceItemPayload(
      @NotNull @Id Integer id,
      @NotNull ProductPayload product,
      @NotNull BigDecimal quantity,
      @NotNull BigDecimal unitValue) {
  }

  @Input("FindManyInvoiceInput")
  public record FindManyInvoiceInput(
      @Min(0) @DefaultValue("0") Integer offset,
      @Min(0) @Max(100) @DefaultValue("20") Integer limit,
      FindInvoiceFilter filter,
      FindInvoiceSort sort) {
  }

  @Type("FindManyInvoicePayload")
  public record FindManyInvoicePayload(
      @NotNull Integer total,
      @NotNull List<@NotNull InvoicePayload> results) {
  }

  @Builder
  @Input("FindInvoiceFilter")
  public record FindInvoiceFilter(
      IDOperators id,
      DateTimeOperators datetime,
      List<FindInvoiceFilter2> or,
      List<FindInvoiceFilter2> and,
      List<FindInvoiceFilter2> not) {
  }

  @Builder
  @Input("FindInvoiceFilter2")
  public record FindInvoiceFilter2(
      IDOperators id,
      DateTimeOperators datetime) {
  }

  @Builder
  @Input("FindInvoiceSort")
  public record FindInvoiceSort(
      SortOrder id,
      SortOrder dateTime) {
  }
}
