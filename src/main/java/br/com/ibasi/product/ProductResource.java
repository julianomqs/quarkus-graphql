package br.com.ibasi.product;

import org.eclipse.microprofile.graphql.DefaultValue;
import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Mutation;
import org.eclipse.microprofile.graphql.Query;

import br.com.ibasi.product.ProductSchema.CreateProductInput;
import br.com.ibasi.product.ProductSchema.FindManyProductInput;
import br.com.ibasi.product.ProductSchema.FindManyProductPayload;
import br.com.ibasi.product.ProductSchema.FindOneProductInput;
import br.com.ibasi.product.ProductSchema.ProductPayload;
import br.com.ibasi.product.ProductSchema.RemoveProductInput;
import br.com.ibasi.product.ProductSchema.UpdateProductInput;
import br.com.ibasi.product.ProductService.FindManyProductConfig;
import br.com.ibasi.product.ProductService.FindOneProductConfig;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@GraphQLApi
public class ProductResource {

  @Inject
  private ProductService service;
  @Inject
  private ProductMapper mapper;

  @Mutation
  public ProductPayload createProduct(@NotNull @Valid CreateProductInput input) {
    var product = mapper.toProduct(input);

    var savedProduct = service.save(product);

    return mapper.toPayload(savedProduct);
  }

  @Mutation
  public ProductPayload updateProduct(@NotNull @Valid UpdateProductInput input) {
    var product = service.findOne(input.id());
    var updatedProduct = mapper.updateProduct(product, input);

    var savedProduct = service.save(updatedProduct);

    return mapper.toPayload(savedProduct);
  }

  @Mutation
  public ProductPayload removeProduct(@NotNull @Valid RemoveProductInput input) {
    var product = service.findOne(input.id());

    var removedProduct = service.remove(product);

    return mapper.toPayload(removedProduct);
  }

  @Query
  public ProductPayload findOneProduct(@NotNull @Valid FindOneProductInput input) {
    var config = new FindOneProductConfig(input.filter());

    var product = service.findOne(config);

    return mapper.toPayload(product);
  }

  @Query
  public FindManyProductPayload findManyProduct(@Valid @DefaultValue("{}") FindManyProductInput input) {
    var config = new FindManyProductConfig(
        input.offset(),
        input.limit(),
        input.filter(),
        input.sort());

    return mapper.toPayload(service.findMany(config));
  }
}
