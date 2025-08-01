package net.martinprobson.basket.output

trait CurrencyFormatter {
  def format(amount: BigDecimal): String
}
