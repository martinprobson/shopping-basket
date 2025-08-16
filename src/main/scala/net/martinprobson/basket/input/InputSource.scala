package net.martinprobson.basket.input

import cats.effect.IO
import net.martinprobson.basket.domain.Error
import net.martinprobson.basket.input.CmdLineInputSource

/**
 * An input source will return a list of items to be validated.
 */
trait InputSource {
  def getInput: IO[Either[List[Error], List[String]]]
}

/**
 * Wire up our CmdLineInputSource
 */
object InputSource {
  def apply(args: Array[String]): InputSource = new CmdLineInputSource(args)
}
