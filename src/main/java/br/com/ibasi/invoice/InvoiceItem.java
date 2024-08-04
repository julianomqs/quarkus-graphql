package br.com.ibasi.invoice;

import java.math.BigDecimal;

import br.com.ibasi.product.Product;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "invoice_item")
@Data
@NoArgsConstructor
@EqualsAndHashCode(of = { "id" })
public class InvoiceItem {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;
  private BigDecimal quantity;
  @Column(name = "unit_value")
  private BigDecimal unitValue;
  @ManyToOne(fetch = FetchType.LAZY)
  private Invoice invoice;
  @ManyToOne(fetch = FetchType.LAZY)
  private Product product;

  public InvoiceItem(Integer id, BigDecimal quantity, BigDecimal unitValue, Product product) {
    this.id = id;
    this.quantity = quantity;
    this.unitValue = unitValue;
    this.product = product;
  }
}
