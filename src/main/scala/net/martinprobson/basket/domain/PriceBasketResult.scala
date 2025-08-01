package net.martinprobson.basket.domain

case class PriceBasketResult(subTotal: BigDecimal,
                             discounts: List[Discount],
                             totalPrice: BigDecimal)
