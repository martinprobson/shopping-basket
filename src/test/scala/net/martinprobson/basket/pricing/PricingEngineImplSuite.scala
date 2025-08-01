package net.martinprobson.basket.pricing

import net.martinprobson.basket.domain.{Discount, DiscountRule, Item, PriceBasketResult, Product}
import net.martinprobson.basket.repository.DiscountRuleRepository
import org.scalamock.scalatest.MockFactory
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class PricingEngineImplSuite extends AnyFunSuite with Matchers with MockFactory {

  private val discountRuleRepository: DiscountRuleRepository = mock[DiscountRuleRepository]
  private val pricingEngine = new PricingEngineImpl(discountRuleRepository)

  private val apples = Product("Apples", BigDecimal(1.00))
  private val bread = Product("Bread", BigDecimal(0.80))
  private val milk = Product("Milk", BigDecimal(1.30))

  test("priceBasket should return a correct result with no discounts") {
    (() => discountRuleRepository.getAll).expects().returning(List.empty)

    val items = Set(Item(apples, 2), Item(bread, 1))
    val expected = PriceBasketResult(BigDecimal(2.80), List.empty, BigDecimal(2.80))
    pricingEngine.priceBasket(items) shouldBe Right(expected)
  }

  test("priceBasket should return a correct result with one discount") {
    val discountRule = mock[DiscountRule]
    (discountRule.applyDiscount _).expects(*).returning(Some(Discount("Apples 10% off", BigDecimal(0.20))))
    (() => discountRuleRepository.getAll).expects().returning(List(discountRule))

    val items = Set(Item(apples, 2), Item(bread, 1))
    val expected = PriceBasketResult(BigDecimal(2.80), List(Discount("Apples 10% off", BigDecimal(0.20))), BigDecimal(2.60))
    pricingEngine.priceBasket(items) shouldBe Right(expected)
  }

  test("priceBasket should return a correct result with multiple discounts") {
    val discountRule1 = mock[DiscountRule]
    (discountRule1.applyDiscount _).expects(*).returning(Some(Discount("Apples 10% off", BigDecimal(0.20))))
    val discountRule2 = mock[DiscountRule]
    (discountRule2.applyDiscount _).expects(*).returning(Some(Discount("Bread 50% off", BigDecimal(0.40))))
    (() => discountRuleRepository.getAll).expects().returning(List(discountRule1, discountRule2))

    val items = Set(Item(apples, 2), Item(bread, 1))
    val expected = PriceBasketResult(BigDecimal(2.80), List(Discount("Apples 10% off", BigDecimal(0.20)), Discount("Bread 50% off", BigDecimal(0.40))), BigDecimal(2.20))
    pricingEngine.priceBasket(items) shouldBe Right(expected)
  }

  test("priceBasket should return a correct result with no items") {
    (() => discountRuleRepository.getAll).expects().returning(List.empty)

    val items = Set.empty[Item]
    val expected = PriceBasketResult(BigDecimal(0), List.empty, BigDecimal(0))
    pricingEngine.priceBasket(items) shouldBe Right(expected)
  }
}