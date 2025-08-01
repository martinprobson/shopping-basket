package net.martinprobson.basket.input

import net.martinprobson.basket.domain.{EmptyInputError, Error, InvalidFirstEntry}
import net.martinprobson.basket.Logging

class CmdLineInputSource(val args: Array[String]) extends InputSource with Logging {

  override def getInput: Either[List[Error], List[String]] = {
    if (args == null || args.length == 0) {
      Left(List(new EmptyInputError))
    } else {
      args.toList match
        case "PriceBasket" :: tail  => Right(tail)
        case _ => Left(List(new InvalidFirstEntry))
    }
  }
}
