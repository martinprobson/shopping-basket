package net.martinprobson.basket.input

import net.martinprobson.basket.domain.{EmptyInputError, InvalidFirstEntry}
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class CmdLineInputSourceSuite extends AnyFunSuite with Matchers {

  test("getInput should return EmptyInputError for null args") {
    val inputSource = new CmdLineInputSource(null)
    inputSource.getInput shouldBe Left(List(new EmptyInputError))
  }

  test("getInput should return EmptyInputError for empty args") {
    val inputSource = new CmdLineInputSource(Array.empty[String])
    inputSource.getInput shouldBe Left(List(new EmptyInputError))
  }

  test("getInput should return InvalidFirstEntry if first arg is not 'PriceBasket'") {
    val inputSource = new CmdLineInputSource(Array("Invalid", "Apples"))
    inputSource.getInput shouldBe Left(List(new InvalidFirstEntry))
  }

  test("getInput should return a list of items for valid input") {
    val inputSource = new CmdLineInputSource(Array("PriceBasket", "Apples", "Bread"))
    inputSource.getInput shouldBe Right(List("Apples", "Bread"))
  }

  test("getInput should return an empty list when only 'PriceBasket' is provided") {
    val inputSource = new CmdLineInputSource(Array("PriceBasket"))
    inputSource.getInput shouldBe Right(List.empty)
  }
}
