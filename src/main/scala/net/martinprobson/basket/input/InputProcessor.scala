package net.martinprobson.basket.input

import cats.effect.IO
import net.martinprobson.basket.domain.{Error, Item}
import net.martinprobson.basket.repository.{InMemoryProductRepository, ProductRepository}

trait InputProcessor {
  def process(input: List[String]): IO[Either[List[Error], Set[Item]]]

}