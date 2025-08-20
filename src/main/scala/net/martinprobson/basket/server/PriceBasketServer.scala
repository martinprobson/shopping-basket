package net.martinprobson.basket.server

import cats.effect.{IO, IOApp}
import com.comcast.ip4s.{ipv4, port}
import net.martinprobson.basket.domain.{Error, PriceBasketResult}
import net.martinprobson.basket.input.InputProcessor
import net.martinprobson.basket.output.OutputProcessor
import net.martinprobson.basket.pricing.PricingEngine
import org.http4s.Method.POST
import org.http4s.circe.CirceEntityCodec.circeEntityDecoder
import org.http4s.dsl.io.*
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.implicits.*
import org.http4s.{HttpRoutes, Request}
import org.typelevel.log4cats.SelfAwareStructuredLogger
import org.typelevel.log4cats.slf4j.Slf4jLogger

import scala.concurrent.duration.*

object PriceBasketServer extends IOApp.Simple {

  private def log: SelfAwareStructuredLogger[IO] = Slf4jLogger.getLogger[IO]


  private def priceBasketService: HttpRoutes[IO] = HttpRoutes.of[IO] {
    case req @ POST -> Root / "basket" =>
      for {
        _ <- log.info("In basket endpoint")
        items <- req.as[List[String]]
        _ <- log.info(s"Items = $items")
        resp <- processBasket(items) match {
          case Left(errors) => BadRequest(errors)
          case Right(result) => Ok(result)
        }
      } yield resp
  }

  private val program: IO[Unit] = for {
    _ <- log.info("Program starting")
    server <- EmberServerBuilder
      .default[IO]
      .withHost(ipv4"0.0.0.0")
      .withPort(port"8080")
      .withHttpApp(priceBasketService.orNotFound)
      .withShutdownTimeout(3.seconds)
      .withLogger(log)
      .build
      .onFinalize(log.info("Shutdown of EmberServer"))
      .use(_ => IO.never)
      .start
    _ <- IO.println("Server started on port 8085")
    _ <- IO.println("Press enter to stop the server...")
    _ <- IO.consoleForIO.readLine
    _ <- server.cancel
  } yield ()

  private def processBasket(input: List[String]): Either[List[Error], PriceBasketResult] = {
    // Wire up dependencies manually (via trait companion objects) - see readme.
    val inputProcessor: InputProcessor = InputProcessor()
    val pricingEngine: PricingEngine = PricingEngine()
    val outputProcessor: OutputProcessor = OutputProcessor()
    inputProcessor.process(input).flatMap{ items => pricingEngine.priceBasket(items) }
  }

  override def run: IO[Unit] = program >> log.info("program exit")
}



