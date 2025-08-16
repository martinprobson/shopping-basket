package net.martinprobson.basket

import cats.data.EitherT
import cats.effect.{ExitCode, IO, IOApp}
import net.martinprobson.basket.domain.{Item, PriceBasketResult, Error}
import net.martinprobson.basket.input.{InputProcessor, InputSource}
import net.martinprobson.basket.output.OutputProcessor
import net.martinprobson.basket.pricing.PricingEngine

object Main extends IOApp {
  // override def run(args: List[String]): IO[ExitCode] = IO.println("Hello").as(ExitCode.Success)
  override def run(args: List[String]): IO[ExitCode] = prog(args.toArray).flatMap {
    case Left(errs)    => IO.println(errs).as(ExitCode.Error)
    case Right(result) => IO.println(result).as(ExitCode.Success)
  }

  def prog(args: Array[String]): IO[Either[List[domain.Error], String]] = {
    // Wire up dependencies manually (via trait companion objects) - see readme.
    val source: InputSource = InputSource(args)
    val inputProcessor: InputProcessor = InputProcessor()
    val pricingEngine: PricingEngine = PricingEngine()
    val outputProcessor: OutputProcessor = OutputProcessor()

    // All calls below return an Either[List[Error], <validResult>] so we can
    // flatMap/Map over them using a for comprehension.
    // (for {
    //  input <- source.getInput
    //  items <- inputProcessor.process(input)
    //  result <- pricingEngine.priceBasket(items)
    //  output <- outputProcessor.formatOutput(result)
    // } yield output) match
    //  case Left(errs) => println(errs)
    //  case Right(output) => println(output)
    (for {
      input <- EitherT(source.getInput)
      items <- EitherT(inputProcessor.process(input))
      result <- EitherT(pricingEngine.priceBasket(items))
      output <- EitherT(outputProcessor.formatOutput(result))
    } yield output).value
  }
}
