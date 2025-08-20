package net.martinprobson.basket.domain

import io.circe.generic.auto.*
import cats.effect.IO
import org.http4s.{EntityDecoder, EntityEncoder}
import org.http4s.circe.{jsonEncoderOf, jsonOf}

case class PriceBasketResult(subTotal: BigDecimal,
                             discounts: List[Discount],
                             totalPrice: BigDecimal)

object PriceBasketResult {

  given EntityEncoder[IO, PriceBasketResult] = jsonEncoderOf[IO, PriceBasketResult]
  given EntityEncoder[IO, List[PriceBasketResult]] = jsonEncoderOf[IO, List[PriceBasketResult]]
  given EntityDecoder[IO, PriceBasketResult] = jsonOf[IO, PriceBasketResult]

}
