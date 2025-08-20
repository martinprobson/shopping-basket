package net.martinprobson.basket.client

import cats.effect.{IO, IOApp}
import org.http4s.client.Client
import org.typelevel.log4cats.SelfAwareStructuredLogger
import org.typelevel.log4cats.slf4j.Slf4jLogger
import fs2.Stream
import net.martinprobson.basket.domain.PriceBasketResult
import org.http4s.circe.CirceEntityCodec.circeEntityEncoder
import org.http4s.circe.jsonOf
import org.http4s.implicits.uri
import org.http4s.{Method, Request}
import io.circe.generic.auto.*
import org.http4s.ember.client.EmberClientBuilder

object PriceBasketClient extends IOApp.Simple {

  private def log: SelfAwareStructuredLogger[IO] = Slf4jLogger.getLogger[IO]

  private def priceBasketClient(client: Client[IO], source: Stream[IO, List[String]]): IO[Unit] = {
    postBaskets(client, source)
      .evalMap{
        case Left((e, itms)) => log.error(s"Failed: $e for $itms")
        case Right(result) => log.info(s"Success: PriceBasketResult = $result")
      }
      .compile
      .drain
  }

  private def postBasket(items: List[String],
                         client: Client[IO]): IO[Either[(String, List[String]), PriceBasketResult]] = {
    def req(items: List[String]): Request[IO] = Request[IO](method = Method.POST, uri"http://localhost:8080/basket")
      .withEntity(items)
    log.info(s"call $items") >>
      client.expect(req(items))(jsonOf[IO, PriceBasketResult])
        .map(r => Right(r)).handleError(e => Left((e.toString, items)))
  }

  private def postBaskets(client: Client[IO],
                          source: Stream[IO, List[String]])
  : Stream[IO, Either[(String, List[String]), PriceBasketResult]] = for {
    c <- Stream(client)
    result <- source
      .parEvalMapUnorderedUnbounded(items => postBasket(items, c))
  } yield result

  def program(source: Stream[IO, List[String]]): IO[Unit] = {
    EmberClientBuilder
      .default[IO]
      .withLogger(log)
      .build
      .onFinalize(log.info("Shutdown of EmberClient"))
      .use(client => priceBasketClient(client, source))
  }

  override def run: IO[Unit] = program(RandomSource(10, 10).stream)
}
