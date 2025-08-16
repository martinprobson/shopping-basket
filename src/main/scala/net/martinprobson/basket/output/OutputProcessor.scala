package net.martinprobson.basket.output

import net.martinprobson.basket.domain.{PriceBasketResult, Error}
import cats.effect.IO

trait OutputProcessor {
  def formatOutput(result: PriceBasketResult): IO[Either[List[Error], String]]
}

object OutputProcessor {
  def apply(): OutputProcessor = new OutputProcessorImpl(GbpCurrencyFormatter())
}
