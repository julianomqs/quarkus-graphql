package br.com.ibasi.invoice;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import br.com.ibasi.Page;
import br.com.ibasi.QuarkusMappingConfig;
import br.com.ibasi.invoice.InvoiceSchema.CreateInvoiceInput;
import br.com.ibasi.invoice.InvoiceSchema.CreateInvoiceItemInput;
import br.com.ibasi.invoice.InvoiceSchema.FindManyInvoicePayload;
import br.com.ibasi.invoice.InvoiceSchema.InvoicePayload;
import br.com.ibasi.invoice.InvoiceSchema.UpdateInvoiceInput;
import br.com.ibasi.invoice.InvoiceSchema.UpdateInvoiceItemInput;
import br.com.ibasi.product.Product;

@Mapper(config = QuarkusMappingConfig.class)
public interface InvoiceMapper {

  default Invoice toInvoice(CreateInvoiceInput input) {
    var invoice = new Invoice();

    invoice.setDatetime(input.datetime());

    for (var item : input.items()) {
      invoice.addItem(toInvoiceItem(item));
    }

    return invoice;
  }

  InvoicePayload toPayload(Invoice invoice);

  FindManyInvoicePayload toPayload(Page<Invoice> page);

  InvoiceItem toInvoiceItem(CreateInvoiceItemInput input);

  InvoiceItem updateInvoiceItem(@MappingTarget InvoiceItem item, UpdateInvoiceItemInput input);

  default Invoice updateInvoice(Invoice invoice, UpdateInvoiceInput input) {
    // Remove
    var itemsToRemove = invoice.getItems()
        .stream()
        .filter(i -> input.items().remove().stream().allMatch(ii -> ii != i.getId()))
        .toList();

    for (var itemToRemove : itemsToRemove) {
      invoice.removeItem(itemToRemove);
    }

    // Update
    invoice.getItems()
        .stream()
        .forEach(i -> {
          var foundItem = input.items()
              .update()
              .stream()
              .filter(ii -> ii.id() == i.getId())
              .findFirst();

          if (foundItem.isPresent()) {
            updateInvoiceItem(i, foundItem.get());
          }
        });

    // Create
    for (var item : input.items().create()) {
      invoice.addItem(toInvoiceItem(item));
    }

    invoice.setId(input.id());
    invoice.setDatetime(input.datetime());

    return invoice;
  }

  default Product toProduct(Integer id) {
    var product = new Product();
    product.setId(id);

    return product;
  }
}
