package net.martinprobson.basket.client

import cats.effect.IO
import fs2.Stream

trait Source {
  def stream: Stream[IO, List[String]]
}