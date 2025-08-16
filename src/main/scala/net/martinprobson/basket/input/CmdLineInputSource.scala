package net.martinprobson.basket.input

import cats.effect.IO
import net.martinprobson.basket.domain.{EmptyInputError, Error, InvalidFirstEntry}
import net.martinprobson.basket.Logging

class CmdLineInputSource(val args: Array[String]) extends InputSource with Logging {

  override def getInput: IO[Either[List[Error], List[String]]] = IO.pure{
    if (args == null || args.length == 0) {
      Left(List(new EmptyInputError))
    } else {
      args.toList match
        case "PriceBasket" :: tail  => Right(tail)
        case _ => Left(List(new InvalidFirstEntry))
    }
  }
}
