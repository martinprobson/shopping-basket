package net.martinprobson.basket.repository

import net.martinprobson.basket.domain.Product

trait ProductRepository {
  def get(name: String): Option[Product]
}