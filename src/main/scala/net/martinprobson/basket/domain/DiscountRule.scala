package net.martinprobson.basket.domain

import net.martinprobson.basket.Logging

/**
 * Represents a discount rule that can be applied to a shopping basket
 */
sealed trait DiscountRule {
  /**
   * Apply a discount rule to a shopping basket
   * @param items The shopping basket items - see [[Item]]
   * @return Some(Discount) If the discount has been applied, otherwise None if this discount rule is not applicable
   */
  def applyDiscount(items: Set[Item]): Option[Discount]

}

/**
 * Represents a percentage discount rule, 20% off product A for example
 * @param name The name of the discount rule
 * @param product The [[Product]] the discount rule applies to
 * @param percentage The percentage discount to apply
 */
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

/**
 * Represents a conditional discount. For example "20% off a product B if you buy 2 Product A's"
 * @param name The name of the discount rule
 * @param dependsOn The product the discount depends on
 * @param dependsOnQty The quantity of depends on product that must be in the basket for the discount to be triggered
 * @param targetProduct The target product to apply the discount to (if the dependsOn rules above are met)
 * @param targetProductQty The number of target products to discount
 * @param targetPercentage The percentage discount of the target product
 */
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
