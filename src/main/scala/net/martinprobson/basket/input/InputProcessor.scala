package net.martinprobson.basket.input

import cats.effect.IO
import net.martinprobson.basket.domain.{Error, Item}
import net.martinprobson.basket.repository.ProductRepository

trait InputProcessor {
  def process(input: List[String]): IO[Either[List[Error], Set[Item]]]

}

/**
 * Wire up our InputProcessor implementation.
 * This also needs a product repository to validate against
 */
object InputProcessor {
  def apply(): InputProcessor = new InputProcessorImpl(ProductRepository())
}
