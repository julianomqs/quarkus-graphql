package br.com.ibasi.constraint;

public enum ExistentTables {

  PRODUCT("product"), INVOICE("invoice"), INVOICE_ITEM("invoice_item");

  private String name;

  private ExistentTables(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }
}
