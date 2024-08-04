package br.com.ibasi.invoice;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "invoice")
@Data
@NoArgsConstructor
@EqualsAndHashCode(of = { "id" })
public class Invoice {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;
  private LocalDateTime datetime;
  @OneToMany(cascade = { CascadeType.ALL }, mappedBy = "invoice", orphanRemoval = true)
  @Setter(value = AccessLevel.NONE)
  private Set<InvoiceItem> items = new LinkedHashSet<>();

  public Invoice(Integer id, LocalDateTime datetime, Set<InvoiceItem> items) {
    this.id = id;
    this.datetime = datetime;
    this.items = items;
  }

  public void addItem(InvoiceItem item) {
    item.setInvoice(this);
    items.add(item);
  }

  public void removeItem(InvoiceItem item) {
    item.setInvoice(null);
    items.remove(item);
  }
}
