package net.martinprobson.basket.client

import cats.effect.{IO, IOApp}
import cats.effect.implicits.*
import fs2.Stream
import net.martinprobson.basket.client.RandomSource.log
import org.typelevel.log4cats.SelfAwareStructuredLogger
import org.typelevel.log4cats.slf4j.Slf4jLogger

import scala.util.Random

case class RandomSource(size: Int, maxItems: Int) extends Source {

  val items = List("Bread", "Soup", "Apples", "Milk")

  private val randomItem: IO[String] = IO(items(Random.nextInt(items.length)))
  private val randomItems: IO[List[String]] = for {
    size <- IO(Random.nextInt(maxItems + 1))
    list <- randomItem.replicateA(size)
  } yield list

  override def stream: Stream[IO, List[String]] =
    Stream
      .repeatEval(randomItems)
      .evalTap(l => log.info(s"items = $l"))
      .take(size)
}

  object RandomSource extends IOApp.Simple {

  private def log: SelfAwareStructuredLogger[IO] = Slf4jLogger.getLogger[IO]

  override def run: IO[Unit] = RandomSource(5, 10).stream.compile.drain
}
