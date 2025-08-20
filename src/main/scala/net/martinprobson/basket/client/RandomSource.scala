package net.martinprobson.basket.client

import cats.effect.{IO, IOApp}
import fs2.Stream
import net.martinprobson.basket.client.RandomSource.log
import org.typelevel.log4cats.SelfAwareStructuredLogger
import org.typelevel.log4cats.slf4j.Slf4jLogger

case class RandomSource(size: Int, max: Int) extends Source {

  val items = List("Bread", "Soup", "Apples", "Milk")

  override def stream: Stream[IO, List[String]] =
    Stream
      .repeatEval(IO {
        val randomIndex = scala.util.Random.nextInt(items.length)
        items(randomIndex)
      })
      .chunkN(size)
      .map(_.toList)
      .evalTap(l => log.info(s"items = $l"))
      .take(max)
}

object RandomSource extends IOApp.Simple {

  private def log: SelfAwareStructuredLogger[IO] = Slf4jLogger.getLogger[IO]

  override def run: IO[Unit] = RandomSource(5, 1)
    .stream
    .compile
    .drain
}
