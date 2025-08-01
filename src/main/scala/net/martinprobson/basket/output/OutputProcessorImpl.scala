package net.martinprobson.basket.output

import net.martinprobson.basket.domain.PriceBasketResult

class OutputProcessorImpl(val currencyFormatter: CurrencyFormatter) extends OutputProcessor {

  //FIXME Dummy return for now
  override def formatOutput(result: PriceBasketResult): Either[List[Error], String] =
    val sb = StringBuilder("Subtotal: ")
    sb.append(currencyFormatter.format(result.subTotal))
    sb.append("\n")
    if (result.discounts.isEmpty) {
      sb.append("(No offers available)").append("\n")
    } else {
      result.discounts.foreach( d => {
        sb.append(d.description).append(": ").append(currencyFormatter.format(d.amount)).append("\n")
      })
    }
    sb.append("Total price: ").append(currencyFormatter.format(result.totalPrice)).append("\n")
    Right(sb.toString())
}
