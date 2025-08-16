package net.martinprobson.basket.pricing

import net.martinprobson.basket.domain.{Error, Item, PriceBasketResult}
import net.martinprobson.basket.repository.DiscountRuleRepository
import cats.effect.IO

trait PricingEngine {
  def priceBasket(items: Set[Item]): IO[Either[List[Error], PriceBasketResult]]
}

object PricingEngine {
  def apply(): PricingEngine = PricingEngineImpl(DiscountRuleRepository())
}
