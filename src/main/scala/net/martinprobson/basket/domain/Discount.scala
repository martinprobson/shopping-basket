package net.martinprobson.basket.domain

/**
 * Represents a discount that has been applied to a [[PriceBasketResult]]
 * @param description The discount description
 * @param amount The amount of the discount
 */
case class Discount (description: String, amount: BigDecimal)
