package net.martinprobson.basket.pricing

import net.martinprobson.basket.domain.{Discount, Item, PriceBasketResult}
import net.martinprobson.basket.input.InputProcessor
import net.martinprobson.basket.output.OutputProcessor
import net.martinprobson.basket.repository.InMemoryProductRepository
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers.*
import org.scalatest.prop.TableDrivenPropertyChecks.*

val BREAD_DISCOUNT = "Buy 2 tins of soup and get a loaf of bread for half price"
val APPLE_DISCOUNT = "Apples 10% off"

val testCases = Table(
  ("TestCase", "Input", "Expected Result"),
  (1, "Apples",
    PriceBasketResult(BigDecimal(100), List(Discount(APPLE_DISCOUNT, BigDecimal(10))), BigDecimal(90))),
  (2, "Apples Apples",
    PriceBasketResult(BigDecimal(200), List(Discount(APPLE_DISCOUNT, BigDecimal(20))), BigDecimal(180))),
  (3, "Apples Apples Apples",
    PriceBasketResult(BigDecimal(300), List(Discount(APPLE_DISCOUNT, BigDecimal(30))), BigDecimal(270))),
  (4, "Bread",
    PriceBasketResult(BigDecimal(80), List(), BigDecimal(80))),
  (5, "Bread Bread",
    PriceBasketResult(BigDecimal(160), List(), BigDecimal(160))),
  (6, "Bread Bread Bread",
    PriceBasketResult(BigDecimal(240), List(), BigDecimal(240))),
  (7, "Soup",
      PriceBasketResult(BigDecimal(65), List(), BigDecimal(65))),
  (8, "Soup Soup",
    PriceBasketResult(BigDecimal(130), List(), BigDecimal(130))),
  (9, "Soup Soup Soup",
    PriceBasketResult(BigDecimal(195), List(), BigDecimal(195))),
  (10, "Milk",
    PriceBasketResult(BigDecimal(130), List(), BigDecimal(130))),
  (11, "Milk Milk",
    PriceBasketResult(BigDecimal(260), List(), BigDecimal(260))),
  (12, "Milk Milk Milk",
    PriceBasketResult(BigDecimal(390), List(), BigDecimal(390))),
  (13, "Apples Milk Bread",
    PriceBasketResult(BigDecimal(310), List(Discount(APPLE_DISCOUNT, BigDecimal(10))), BigDecimal(300))),
  (14, "Apples Milk Bread Soup",
    PriceBasketResult(BigDecimal(375), List(Discount(APPLE_DISCOUNT, BigDecimal(10))), BigDecimal(365))),
  (15, "Milk Bread Soup",
    PriceBasketResult(BigDecimal(275), List(), BigDecimal(275))),
  (16, "Milk Soup Bread Soup",
    PriceBasketResult(BigDecimal(340), List(Discount(BREAD_DISCOUNT, BigDecimal(40))), BigDecimal(300))),
  (17, "Milk Milk Milk Bread Bread Bread Bread Soup Soup Soup Soup Soup Soup Soup Soup Apples",
    PriceBasketResult(BigDecimal(1330), List(Discount(APPLE_DISCOUNT, BigDecimal(10)), Discount(BREAD_DISCOUNT, BigDecimal(40))), BigDecimal(1280))),
  (18, "Milk Soup Milk Bread Soup Soup Soup Soup Soup Milk Milk Soup Bread Bread Bread Bread",
    PriceBasketResult(BigDecimal(1375), List(Discount(BREAD_DISCOUNT, BigDecimal(40))), BigDecimal(1335))),
  (19, "Milk Soup Milk Bread Soup Soup Soup Soup Soup Apples Milk Milk Soup Bread Bread Bread Bread Apples Apples Apples Apples",
    PriceBasketResult(BigDecimal(1875), List(Discount(APPLE_DISCOUNT, BigDecimal(50)), Discount(BREAD_DISCOUNT, BigDecimal(40))), BigDecimal(1785)))
)

/**
 * These test cases are specific to the actual assignment.
 * <p>i.e. Bread, Milk, Apples and Soup with the two discounts.</p>
 */
class ShoppingBasketSpecTest extends AnyFunSuite {

  forEvery (testCases) { (testCase: Int, input: String, expected: PriceBasketResult) => {
    val inputProcessor: InputProcessor = InputProcessor()
    val pricingEngine: PricingEngine = PricingEngine()
    val outputProcessor: OutputProcessor = OutputProcessor()

    (for {
      items <- inputProcessor.process(input.split("\\s+").toList)
      result <- pricingEngine.priceBasket(items)
    } yield result) shouldBe Right(expected)
  }
  }
}
