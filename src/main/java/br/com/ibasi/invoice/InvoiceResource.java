package br.com.ibasi.invoice;

import org.eclipse.microprofile.graphql.DefaultValue;
import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Mutation;
import org.eclipse.microprofile.graphql.Query;

import br.com.ibasi.invoice.InvoiceSchema.CreateInvoiceInput;
import br.com.ibasi.invoice.InvoiceSchema.FindManyInvoiceInput;
import br.com.ibasi.invoice.InvoiceSchema.FindManyInvoicePayload;
import br.com.ibasi.invoice.InvoiceSchema.FindOneInvoiceInput;
import br.com.ibasi.invoice.InvoiceSchema.InvoicePayload;
import br.com.ibasi.invoice.InvoiceSchema.RemoveInvoiceInput;
import br.com.ibasi.invoice.InvoiceSchema.UpdateInvoiceInput;
import br.com.ibasi.invoice.InvoiceService.FindManyInvoiceConfig;
import br.com.ibasi.invoice.InvoiceService.FindOneInvoiceConfig;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@GraphQLApi
public class InvoiceResource {

  @Inject
  private InvoiceService service;
  @Inject
  private InvoiceMapper mapper;

  /**
   * Ao incluir/excluir novos campos ajustar o método {@link InvoiceMapper#toInvoice(CreateInvoiceInput)}.
   */
  @Mutation
  public InvoicePayload createInvoice(@NotNull @Valid CreateInvoiceInput input) {
    var invoice = mapper.toInvoice(input);
    var savedInvoice = service.save(invoice);

    return mapper.toPayload(savedInvoice);
  }

  /**
   * Ao incluir/excluir novos campos ajustar o método {@link InvoiceMapper#updateInvoice(Invoice, UpdateInvoiceInput)}.
   */
  @Mutation
  public InvoicePayload updateInvoice(@NotNull @Valid UpdateInvoiceInput input) {
    var invoice = service.findOne(input.id());
    var updatedInvoice = mapper.updateInvoice(invoice, input);

    var savedInvoice = service.save(updatedInvoice);

    return mapper.toPayload(savedInvoice);
  }

  @Mutation
  public InvoicePayload removeInvoice(@NotNull @Valid RemoveInvoiceInput input) {
    var invoice = service.findOne(input.id());

    var removedInvoice = service.remove(invoice);

    return mapper.toPayload(removedInvoice);
  }

  @Query
  public InvoicePayload findOneInvoice(@NotNull @Valid FindOneInvoiceInput input) {
    var config = new FindOneInvoiceConfig(input.filter());

    var invoice = service.findOne(config);

    return mapper.toPayload(invoice);
  }

  @Query
  public FindManyInvoicePayload findManyInvoice(@Valid @DefaultValue("{}") FindManyInvoiceInput input) {
    var config = new FindManyInvoiceConfig(
        input.offset(),
        input.limit(),
        input.filter(),
        input.sort());

    return mapper.toPayload(service.findMany(config));
  }
}
