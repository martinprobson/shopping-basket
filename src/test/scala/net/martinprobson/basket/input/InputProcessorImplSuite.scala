package net.martinprobson.basket.input

import net.martinprobson.basket.domain.{InvalidProductError, Item, Product}
import net.martinprobson.basket.repository.ProductRepository
import org.scalamock.scalatest.MockFactory
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class InputProcessorImplSuite extends AnyFunSuite with Matchers with MockFactory {

  private val productRepository: ProductRepository = mock[ProductRepository]
  private val inputProcessor = new InputProcessorImpl(productRepository)

  private val apples = Product("Apples", BigDecimal(1.00))
  private val bread = Product("Bread", BigDecimal(0.80))

  test("process should return a set of items for valid input") {
    (productRepository.get _).expects("Apples").returning(Some(apples)).twice()
    (productRepository.get _).expects("Bread").returning(Some(bread)).once()

    val input = List("Apples", "Bread", "Apples")
    val expected = Set(Item(apples, 2), Item(bread, 1))
    inputProcessor.process(input) shouldBe Right(expected)
  }

  test("process should return an empty set for empty input") {
    val input = List.empty[String]
    inputProcessor.process(input) shouldBe Right(Set.empty)
  }

  test("process should return InvalidProductError for invalid products") {
    (productRepository.get _).expects("Apples").returning(Some(apples))
    (productRepository.get _).expects("Cheese").returning(None)

    val input = List("Apples", "Cheese")
    val expected = Left(List(InvalidProductError("Invalid product: 'Cheese'")))
    inputProcessor.process(input) shouldBe expected
  }

  test("process should return multiple InvalidProductErrors for multiple invalid products") {
    (productRepository.get _).expects("Apples").returning(Some(apples))
    (productRepository.get _).expects("Cheese").returning(None)
    (productRepository.get _).expects("Milk").returning(None)

    val input = List("Apples", "Cheese", "Milk")
    val expected = Left(List(InvalidProductError("Invalid product: 'Cheese'"),
      InvalidProductError("Invalid product: 'Milk'")))
    inputProcessor.process(input) shouldBe expected
  }
}
