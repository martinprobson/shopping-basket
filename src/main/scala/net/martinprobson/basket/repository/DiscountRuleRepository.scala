package net.martinprobson.basket.repository

import net.martinprobson.basket.domain.DiscountRule

trait DiscountRuleRepository {
  def getAll: List[DiscountRule]
}