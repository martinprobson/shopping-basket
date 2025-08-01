package net.martinprobson.basket.output

import net.martinprobson.basket.domain.{Discount, PriceBasketResult}
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class OutputProcessorImplSuite extends AnyFunSuite with Matchers {

  private val currencyFormatter = new GbpCurrencyFormatter
  private val outputProcessor = new OutputProcessorImpl(currencyFormatter)

  test("Correctly format output with no discounts") {
    val result = PriceBasketResult(subTotal = BigDecimal(100), discounts = List.empty, totalPrice = BigDecimal(100))
    val expectedOutput = "Subtotal: £1.00\n(No offers available)\nTotal price: £1.00\n"
    outputProcessor.formatOutput(result) shouldBe Right(expectedOutput)
  }

  test("Correctly format output with one discount") {
    val discounts = List(Discount("Apples 10% off", BigDecimal(10)))
    val result = PriceBasketResult(subTotal = BigDecimal(100), discounts = discounts, totalPrice = BigDecimal(90))
    val expectedOutput = "Subtotal: £1.00\nApples 10% off: 10p\nTotal price: 90p\n"
    outputProcessor.formatOutput(result) shouldBe Right(expectedOutput)
  }

  test("Correctly format output with multiple discounts") {
    val discounts = List(
      Discount("Apples 10% off", BigDecimal(10)),
      Discount("Bread 50% off", BigDecimal(50))
    )
    val result = PriceBasketResult(subTotal = BigDecimal(200), discounts = discounts, totalPrice = BigDecimal(140))
    val expectedOutput = "Subtotal: £2.00\nApples 10% off: 10p\nBread 50% off: 50p\nTotal price: £1.40\n"
    outputProcessor.formatOutput(result) shouldBe Right(expectedOutput)
  }

  test("Correctly format output with zero total") {
    val result = PriceBasketResult(subTotal = BigDecimal(0), discounts = List.empty, totalPrice = BigDecimal(0))
    val expectedOutput = "Subtotal: 0p\n(No offers available)\nTotal price: 0p\n"
    outputProcessor.formatOutput(result) shouldBe Right(expectedOutput)
  }
}