package net.martinprobson.basket

import net.martinprobson.basket.input.{InputProcessor, InputSource}
import net.martinprobson.basket.output.OutputProcessor
import net.martinprobson.basket.pricing.PricingEngine

object Main extends Logging {

  def main(args: Array[String]): Unit = {
    // Wire up dependencies manually (via trait companion objects) - see readme.
    val source: InputSource = InputSource(args)
    val inputProcessor: InputProcessor = InputProcessor()
    val pricingEngine: PricingEngine = PricingEngine()
    val outputProcessor: OutputProcessor = OutputProcessor()


    // All calls below return an Either[List[Error], <validResult>] so we can
    // flatMap/Map over them using a for comprehension.
    (for {
      input <- source.getInput
      items <- inputProcessor.process(input)
      result <- pricingEngine.priceBasket(items)
      output <- outputProcessor.formatOutput(result)
    } yield output) match
      case Left(errs) => println(errs)
      case Right(output) => println(output)
  }
}

//TODO Finish all unit tests/ Craft README based on below/ submit to github



//TODO Describe Domain model in README


//TODO Scalability a) Size of shopping basket as described below
//TODO Number of products/discount - fast database implementation/caching
//TODO Number of requests to price shopping baskets - horizontal scale (running many instances behind an API

//TODO If the shopping basket is too large then switch to a streaming solution that aggregates immediately (I have
// used fs2 successfully for this in the past (outside of a cluster/spark process)


