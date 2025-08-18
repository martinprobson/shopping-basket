package net.martinprobson.basket.repository

import cats.effect.IO
import net.martinprobson.basket.Logging
import net.martinprobson.basket.domain.*

object InMemoryProductRepository extends ProductRepository with Logging {

  private val products: Map[String, Product] = Map("Soup" -> Product("Soup", BigDecimal(65)),
  "Bread" -> Product("Bread", BigDecimal(80)),
  "Milk" -> Product("Milk", BigDecimal(130)),
  "Apples" -> Product("Apples", BigDecimal(100)))

  override def get(name: String): Option[Product] = products.get(name)
  
  def apply(): IO[ProductRepository] = IO(InMemoryProductRepository)
}
