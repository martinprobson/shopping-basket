package net.martinprobson.basket.output

import net.martinprobson.basket.domain.{PriceBasketResult, Error}
import cats.effect.IO

class OutputProcessorImpl(val currencyFormatter: CurrencyFormatter) extends OutputProcessor {

  override def formatOutput(result: PriceBasketResult): IO[Either[List[Error], String]] = IO {
    val sb = StringBuilder("Subtotal: ")
    sb.append(currencyFormatter.format(result.subTotal))
    sb.append("\n")
    if (result.discounts.isEmpty) {
      sb.append("(No offers available)").append("\n")
    } else {
      result.discounts.foreach(d => {
        sb.append(d.description).append(": ").append(currencyFormatter.format(d.amount)).append("\n")
      })
    }
    sb.append("Total price: ").append(currencyFormatter.format(result.totalPrice)).append("\n")
    Right(sb.toString())
  }
}

object OutputProcessorImpl {
  def apply(currencyFormatter: CurrencyFormatter): IO[OutputProcessor] =
    IO(new OutputProcessorImpl(currencyFormatter))
}
