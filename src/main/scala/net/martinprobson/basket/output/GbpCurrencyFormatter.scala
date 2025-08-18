package net.martinprobson.basket.output

import cats.effect.IO

import scala.util.matching.Regex

object GbpCurrencyFormatter extends CurrencyFormatter {

  override def format(amount: BigDecimal): String =  
    val pounds = amount / BigDecimal(100)

    val hasDecimalPattern: Regex = ".*\\.\\d{1,2}".r

    pounds match {
      case p if p >= 1 && hasDecimalPattern.matches(p.toString) => f"£$p%.2f"
      case p if p >= 1 => f"£$p%.2f"
      case p => f"${(p * 100).toInt}p"
    }
    
  def apply(): IO[CurrencyFormatter] = IO(GbpCurrencyFormatter)
}

