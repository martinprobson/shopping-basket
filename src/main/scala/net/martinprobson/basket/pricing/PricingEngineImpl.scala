package net.martinprobson.basket.pricing

import net.martinprobson.basket.Logging
import net.martinprobson.basket.domain.{Discount, Error, Item, PriceBasketResult}
import net.martinprobson.basket.repository.DiscountRuleRepository

import scala.collection.mutable.ListBuffer

class PricingEngineImpl(discountRuleRepository: DiscountRuleRepository) extends PricingEngine with Logging {

  override def priceBasket(items: Set[Item]): Either[List[Error], PriceBasketResult] =

    var subTotal = BigDecimal(0)
    items.foreach(item => {
      subTotal = subTotal + (item.product.unitPrice * item.qty)
    })
    logger.info(s"subTotal is $subTotal")
    val discounts = discountRuleRepository.getAll.foldLeft(ListBuffer(): ListBuffer[Discount])( (acc, dr) => {
      val result = dr.applyDiscount(items)
      if (result.isDefined) {
        acc += result.get
      } else {
        acc
      }
    }).toList
    logger.info(s"Discounts applied $discounts")
    val discountTotal = discounts.map(_.amount).sum
    logger.info(s"Discount total is: $discountTotal")
    val totalPrice = subTotal - discountTotal
    logger.info(s"Total price is: $totalPrice")
    Right(PriceBasketResult(subTotal, discounts, totalPrice))
}
