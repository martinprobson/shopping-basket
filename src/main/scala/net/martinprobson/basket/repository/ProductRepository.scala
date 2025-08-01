package net.martinprobson.basket.repository

import net.martinprobson.basket.domain.Product

trait ProductRepository {
  def get(name: String): Option[Product]
}

/**
 * Wire up an InMemoryProductRepository in the real world this would be backed by a DB
 */
object ProductRepository {
  def apply(): ProductRepository = InMemoryProductRepository
}