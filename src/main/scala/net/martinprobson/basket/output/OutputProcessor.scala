package net.martinprobson.basket.output

import net.martinprobson.basket.domain.PriceBasketResult

trait OutputProcessor {
  def formatOutput(result: PriceBasketResult): Either[List[Error], String]
}

object OutputProcessor {
  def apply(): OutputProcessor = new OutputProcessorImpl(GbpCurrencyFormatter())
}
