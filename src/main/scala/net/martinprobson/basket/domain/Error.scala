package net.martinprobson.basket.domain

import net.martinprobson.basket.domain.Error.FIRST_WORD

sealed trait Error {
  val msg: String
  
}

object Error {
  val FIRST_WORD = "PriceBasket"
}

case class EmptyInputError(msg: String = "Empty Input") extends Error
case class InvalidFirstEntry(msg: String = s"The first word in the list should be $FIRST_WORD")
  extends Error
case class InvalidProductError(msg: String) extends Error


