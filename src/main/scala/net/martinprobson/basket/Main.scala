package net.martinprobson.basket

import cats.data.EitherT
import cats.effect.{ExitCode, IO, IOApp}
import net.martinprobson.basket.domain.Error
import net.martinprobson.basket.input.{CmdLineInputSource, InputProcessorImpl}
import net.martinprobson.basket.output.{GbpCurrencyFormatter, OutputProcessorImpl}
import net.martinprobson.basket.pricing.PricingEngineImpl
import net.martinprobson.basket.repository.{InMemoryDiscountRuleRepository, InMemoryProductRepository}

object Main extends IOApp with Logging {
  override def run(args: List[String]): IO[ExitCode] =
    //  prog(args.toArray).parReplicateA(100000).as(ExitCode.Success)
    (prog(args.toArray),prog(args.toArray), prog(args.toArray)).parTupled.as(ExitCode.Success)

  private def prog(args: Array[String]): IO[Unit] = {
    (for {
      _ <- EitherT.liftF(IO(logger.info("Start")))
      // Wire up dependencies....
      productRepository <- EitherT.liftF(InMemoryProductRepository())
      discountRuleRepository <- EitherT.liftF(InMemoryDiscountRuleRepository(productRepository))
      source <- EitherT.liftF(CmdLineInputSource(args))
      inputProcessor <- EitherT.liftF(InputProcessorImpl(productRepository))
      pricingEngine <- EitherT.liftF(PricingEngineImpl(discountRuleRepository))
      currencyFormatter <- EitherT.liftF(GbpCurrencyFormatter())
      outputProcessor <- EitherT.liftF(OutputProcessorImpl(currencyFormatter))
      // Start processing....
      input <- EitherT(source.getInput)
      items <- EitherT(inputProcessor.process(input))
      result <- EitherT(pricingEngine.priceBasket(items))
      output <- EitherT(outputProcessor.formatOutput(result))
      _ <- EitherT.liftF(IO(logger.info("End")))
    } yield output).value.flatMap {
      case Left(errs) => IO.println(errs) >> IO.raiseError(new RuntimeException(errs.toString))
      case Right(result) => IO.println(result)
    }
  }
}
