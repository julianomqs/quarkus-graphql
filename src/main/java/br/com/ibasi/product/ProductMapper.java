package br.com.ibasi.product;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import br.com.ibasi.Page;
import br.com.ibasi.QuarkusMappingConfig;
import br.com.ibasi.product.ProductSchema.CreateProductInput;
import br.com.ibasi.product.ProductSchema.FindManyProductPayload;
import br.com.ibasi.product.ProductSchema.ProductPayload;
import br.com.ibasi.product.ProductSchema.UpdateProductInput;

@Mapper(config = QuarkusMappingConfig.class)
public interface ProductMapper {

  Product toProduct(CreateProductInput input);

  Product updateProduct(@MappingTarget Product product, UpdateProductInput input);

  ProductPayload toPayload(Product product);

  FindManyProductPayload toPayload(Page<Product> page);
}
