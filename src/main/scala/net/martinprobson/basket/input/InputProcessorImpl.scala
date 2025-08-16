package net.martinprobson.basket.input

import cats.effect.IO
import net.martinprobson.basket.domain.*
import net.martinprobson.basket.repository.ProductRepository

class InputProcessorImpl(val productRepository: ProductRepository) extends InputProcessor {

  override def process(input: List[String]): IO[Either[List[Error], Set[Item]]] = IO {
    // 1. Partition the input list into valid and invalid items.
    val validatedItems: List[Either[Error, Product]] = input.map { name =>
      productRepository.get(name) match {
        case Some(product) => Right(product)
        case None => Left(InvalidProductError(s"Invalid product: '$name'"))
      }
    }

    // 2. Separate the errors from the valid products.
    val (errors, products) = validatedItems.partition(_.isLeft)

    if (errors.nonEmpty) {
      // If there are any errors, collect them and return as Left.
      Left(errors.flatMap(_.left.toOption))
    } else {
      // If there are no errors, process the valid products.
      val validProducts = products.flatMap(_.toOption)

      // 3. Group the products by their name and count their occurrences.
      val groupedItems = validProducts
        .groupBy(_.name)
        .map { case (name, list) =>
          // Since all products in a group are the same, we can just take the head.
          val product = list.head
          val quantity = list.length
          Item(product, quantity)
        }
        .toSet

      // 4. Return the set of grouped items as Right.
      Right(groupedItems)
    }
  }
}