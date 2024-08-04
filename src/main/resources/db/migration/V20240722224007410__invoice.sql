CREATE TABLE `invoice` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `datetime` DATETIME(3) NOT NULL,
  PRIMARY KEY (`id`)
);

CREATE TABLE `invoice_item` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `quantity` DECIMAL(19, 4) NOT NULL,
  `unit_value` DECIMAL(19, 4) NOT NULL,
  `invoice_id` INT UNSIGNED NOT NULL,
  `product_id` INT UNSIGNED NOT NULL,
  PRIMARY KEY (`id`)
);

ALTER TABLE `invoice_item`
ADD CONSTRAINT `fk_invoice_item__invoice` FOREIGN KEY (`invoice_id`) REFERENCES `invoice`(`id`) ON DELETE NO ACTION ON UPDATE NO ACTION;

ALTER TABLE `invoice_item`
ADD CONSTRAINT `fk_invoice_item__product` FOREIGN KEY (`product_id`) REFERENCES `product`(`id`) ON DELETE NO ACTION ON UPDATE NO ACTION;