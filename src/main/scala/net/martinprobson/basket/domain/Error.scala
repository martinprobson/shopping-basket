package net.martinprobson.basket.domain

import cats.effect.IO
import net.martinprobson.basket.domain.Error.FIRST_WORD
import org.http4s.circe.{jsonEncoderOf, jsonOf}
import org.http4s.{EntityDecoder, EntityEncoder}
import io.circe.generic.auto.*
import cats.effect.IO
import org.http4s.{EntityDecoder, EntityEncoder}
import org.http4s.circe.{jsonEncoderOf, jsonOf}

sealed trait Error {
  val msg: String
  
}

object Error {
  val FIRST_WORD = "PriceBasket"
  
  given EntityEncoder[IO, Error] = jsonEncoderOf[IO, Error]
  given EntityEncoder[IO, List[Error]] = jsonEncoderOf[IO, List[Error]]
  given EntityDecoder[IO, Error] = jsonOf[IO, Error]
}

case class EmptyInputError(msg: String = "Empty Input") extends Error
case class InvalidFirstEntry(msg: String = s"The first word in the list should be $FIRST_WORD")
  extends Error
case class InvalidProductError(msg: String) extends Error


