package net.martinprobson.basket.repository

import cats.effect.IO
import net.martinprobson.basket.domain.*

case class InMemoryDiscountRuleRepository(productRepository: ProductRepository)
  extends DiscountRuleRepository {

  private val discounts: List[DiscountRule] =
    List(PercentageDiscount("Apples 10% off", productRepository.get("Apples").get, 10),
         ConditionalDiscount("Buy 2 tins of soup and get a loaf of bread for half price",
                  productRepository.get("Soup").get, 2,
                  productRepository.get("Bread").get, 1, 50))


  override def getAll: List[DiscountRule] = discounts
}

object InMemoryDiscountRuleRepository {
  def apply(productRepository: ProductRepository): IO[DiscountRuleRepository] =
    IO(new InMemoryDiscountRuleRepository(productRepository))
  
}
