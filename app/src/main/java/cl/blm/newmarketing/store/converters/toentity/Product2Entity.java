package cl.blm.newmarketing.store.converters.toentity;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import cl.blm.newmarketing.store.api.pojo.ProductPojo;
import cl.blm.newmarketing.store.jpa.entities.Product;
import cl.blm.newmarketing.store.jpa.entities.ProductType;

/**
 *
 * @author Benjamin La Madrid <bg.lamadrid at gmail.com>
 */
@Component
public class Product2Entity
    implements Converter<ProductPojo, Product> {

  @Override
  public Product convert(ProductPojo source) {
    Product target = new Product(source.getId());
    target.setName(source.getName());
    target.setBarcode(source.getBarcode());
    target.setPrice(source.getPrice());
    target.setProductType(new ProductType(source.getProductType().getId()));

    if (source.getCurrentStock() != null) {
      target.setStockCurrent(source.getCurrentStock());
    }

    if (source.getCriticalStock() != null) {
      target.setStockCritical(source.getCriticalStock());
    }
    return target;
  }
}