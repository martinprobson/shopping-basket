package net.martinprobson.basket

import net.martinprobson.basket.input.{InputProcessor, InputSource}
import net.martinprobson.basket.output.OutputProcessor
import net.martinprobson.basket.pricing.PricingEngine

object Main extends Logging {

  def main(args: Array[String]): Unit = {
    //TODO Wire up dependencies manually in a real app would prefer Guice
    val source: InputSource = InputSource(args)
    val inputProcessor: InputProcessor = InputProcessor()
    val pricingEngine: PricingEngine = PricingEngine()
    val outputProcessor: OutputProcessor = OutputProcessor()

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


//TODO Test cases for CmdLineInputSource
//TODO Test case for InputProcessor
//TODO Test case for CurrencyFormatter
//TODO Test case for OutputFormatter
//TODO Test case for PricingEngine

//TODO Describe Domain model in README
//TODO Talk about dependency injection in README
//TODO Describe what is in each package

//TODO Instructions to run - Java >= 17 required, sbt, test etc
//TODO Assembly, then java -jar cmdline

//TODO Assuming the Discount rules are not cumulative i.e. 4 tins of soup in basket still means only one loaf at half-price
//TODO Assume the shopping basket never gets too big and can therefore be aggregated and processed in memory
//TODO Scalability a) Size of shopping basket as described below
//TODO Number of products/discount - fast database implementation/caching
//TODO Number of requests to price shopping baskets - horizontal scale (running many instances behind an API

//TODO If the shopping basket is too large then switch to a streaming solution that aggregates immediately (I have
// used fs2 successfully for this in the past (outside of a cluster/spark process)

//TODO I made the distinction beween DiscountRule and Discount - A Dicount is the result of applying a DiscountRule - I
// think this makes sense in terms of the design domain?

