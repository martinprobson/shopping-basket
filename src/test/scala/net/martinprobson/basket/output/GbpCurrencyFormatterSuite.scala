package net.martinprobson.basket.output

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class GbpCurrencyFormatterSuite extends AnyFunSuite with Matchers {

  private val formatter = new GbpCurrencyFormatter

  test("Correctly format an amount less than £1") {
    formatter.format(BigDecimal(99)) shouldBe "99p"
  }

  test("Correctly format an amount of zero") {
    formatter.format(BigDecimal(0)) shouldBe "0p"
  }

  test("Correctly format a whole number amount greater than £1") {
    formatter.format(BigDecimal(100)) shouldBe "£1.00"
  }

  test("Correctly format a decimal amount greater than £1") {
    formatter.format(BigDecimal(123)) shouldBe "£1.23"
  }

  test("Correctly format a decimal amount and round up") {
    formatter.format(BigDecimal(199.9)) shouldBe "£2.00"
  }

  test("Correctly format a large amount") {
    formatter.format(BigDecimal(1234567)) shouldBe "£12345.67"
  }
}
