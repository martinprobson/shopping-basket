package net.martinprobson.basket.domain

import net.martinprobson.basket.Logging

sealed trait DiscountRule {
  def applyDiscount(items: Set[Item]): Option[Discount]

}

case class PercentageDiscount(name: String, product: Product, percentage: Int)
  extends DiscountRule with Logging {
  override def applyDiscount(items: Set[Item]): Option[Discount] = {
    val productCount = items.filter(_.product == product).map(_.qty).sum
    if (productCount > 0) {
      val totalDiscount = BigDecimal(percentage) / 100 * productCount * product.unitPrice
      logger
        .info(s"Found $productCount ${product.name} applying " +
          s"$percentage discount - totalDiscount = $totalDiscount")
      Some(Discount(name, totalDiscount))
    } else {
      None
    }
  }
}

case class ConditionalDiscount(name: String,
                               dependsOn: Product,
                               dependsOnQty: Int,
                               targetProduct: Product,
                               targetProductQty: Int,
                               targetPercentage: Int) extends DiscountRule with Logging {
  override def applyDiscount(items: Set[Item]): Option[Discount] = {
    val dependsOnProductsInBasket = items.filter(_.product == dependsOn).map(_.qty).sum
    val targetProductsInBasket = items.filter(_.product == targetProduct).map(_.qty).sum
    if (dependsOnProductsInBasket == 0 || targetProductsInBasket == 0) {
      None
    } else {
      logger.info(s"Found $dependsOnProductsInBasket depends on products in basket")
      logger.info(s"Found $targetProductsInBasket target products in basket")
      if (dependsOnProductsInBasket >= dependsOnQty) {
        val discountItemCount = Math.min(targetProductQty, targetProductsInBasket)
        logger.info(s"Condition met - applying discount " +
          s"on $discountItemCount ${targetProduct.name}")
        val totalDiscount =
          BigDecimal(targetPercentage) / 100 * discountItemCount * targetProduct.unitPrice
        logger.info(s"Total Discount applied = $totalDiscount")
        Some(Discount(name, totalDiscount))
      } else {
        None
      }
    }
  }
}
