package net.martinprobson.basket

import cats.data.EitherT
import cats.effect.{ExitCode, IO, IOApp}
import net.martinprobson.basket.domain.{Item, PriceBasketResult, Error}
import net.martinprobson.basket.input.{InputProcessor, InputSource}
import net.martinprobson.basket.output.OutputProcessor
import net.martinprobson.basket.pricing.PricingEngine

object Main extends IOApp with Logging {
  override def run(args: List[String]): IO[ExitCode] =
    IO(logger.info("Start")) *>
    (prog(args.toArray),prog(args.toArray)).parTupled.as(ExitCode.Success)

  def prog(args: Array[String]): IO[Unit] = {
    // Wire up dependencies manually (via trait companion objects) - see readme.
    val source: InputSource = InputSource(args)
    val inputProcessor: InputProcessor = InputProcessor()
    val pricingEngine: PricingEngine = PricingEngine()
    val outputProcessor: OutputProcessor = OutputProcessor()

    (for {
      input <- EitherT(source.getInput)
      items <- EitherT(inputProcessor.process(input))
      result <- EitherT(pricingEngine.priceBasket(items))
      output <- EitherT(outputProcessor.formatOutput(result))
    } yield output).value.flatMap {
      case Left(errs) => IO.println(errs)
      case Right(result) => IO.println(result)
    }
  }
}
