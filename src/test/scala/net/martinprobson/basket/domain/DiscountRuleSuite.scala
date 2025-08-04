package net.martinprobson.basket.domain

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class DiscountRuleSuite extends AnyFunSuite with Matchers {

  private val apples = Product("Apples", BigDecimal(1.00))
  private val bread = Product("Bread", BigDecimal(0.80))
  private val milk = Product("Milk", BigDecimal(1.30))
  private val soup = Product("Soup", BigDecimal(0.65))

  // PercentageDiscount tests
  test("PercentageDiscount should apply correctly when product is present") {
    val discount = PercentageDiscount("Apples 10% off", apples, 10)
    val items = Set(Item(apples, 1), Item(bread, 1))
    val expectedDiscount = Some(Discount("Apples 10% off", BigDecimal(0.10)))
    discount.applyDiscount(items) shouldBe expectedDiscount
  }

  test("PercentageDiscount should not apply when product is not present") {
    val discount = PercentageDiscount("Apples 10% off", apples, 10)
    val items = Set(Item(bread, 1), Item(milk, 1))
    discount.applyDiscount(items) shouldBe None
  }

  test("PercentageDiscount should apply correctly for multiple quantities") {
    val discount = PercentageDiscount("Apples 10% off", apples, 10)
    val items = Set(Item(apples, 3), Item(bread, 1))
    val expectedDiscount = Some(Discount("Apples 10% off", BigDecimal(0.30)))
    discount.applyDiscount(items) shouldBe expectedDiscount
  }

  // ConditionalDiscount tests
  test("ConditionalDiscount should apply when conditions are met") {
    val discount = ConditionalDiscount("Bread 50% off if you buy 2 tins of soup", soup, 2, bread, 1, 50)
    val items = Set(Item(soup, 2), Item(bread, 1))
    val expectedDiscount = Some(Discount("Bread 50% off if you buy 2 tins of soup", BigDecimal(0.40)))
    discount.applyDiscount(items) shouldBe expectedDiscount
  }

  test("ConditionalDiscount should not apply when dependsOn quantity is too low") {
    val discount = ConditionalDiscount("Bread 50% off if you buy 2 tins of soup", soup, 2, bread, 1, 50)
    val items = Set(Item(soup, 1), Item(bread, 1))
    discount.applyDiscount(items) shouldBe None
  }

  test("ConditionalDiscount should not apply when target product is not present") {
    val discount = ConditionalDiscount("Bread 50% off if you buy 2 tins of soup", soup, 2, bread, 1, 50)
    val items = Set(Item(soup, 2), Item(apples, 1))
    discount.applyDiscount(items) shouldBe None
  }

  test("ConditionalDiscount should not apply when dependsOn product is not present") {
    val discount = ConditionalDiscount("Bread 50% off if you buy 2 tins of soup", soup, 2, bread, 1, 50)
    val items = Set(Item(apples, 2), Item(bread, 1))
    discount.applyDiscount(items) shouldBe None
  }

  test("ConditionalDiscount should apply to the correct number of target items (less than targetProductQty)") {
    val discount = ConditionalDiscount("Bread 50% off if you buy 2 tins of soup", soup, 2, bread, 2, 50)
    val items = Set(Item(soup, 2), Item(bread, 1))
    val expectedDiscount = Some(Discount("Bread 50% off if you buy 2 tins of soup", BigDecimal(0.40)))
    discount.applyDiscount(items) shouldBe expectedDiscount
  }

  test("ConditionalDiscount should apply to the correct number of target items (more than targetProductQty)") {
    val discount = ConditionalDiscount("Bread 50% off if you buy 2 tins of soup", soup, 2, bread, 1, 50)
    val items = Set(Item(soup, 2), Item(bread, 2))
    val expectedDiscount = Some(Discount("Bread 50% off if you buy 2 tins of soup", BigDecimal(0.40)))
    discount.applyDiscount(items) shouldBe expectedDiscount
  }
}
