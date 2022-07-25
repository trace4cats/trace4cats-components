package trace4cats.logging

import java.io.PrintStream

import cats.Eval
import io.odin.formatter.Formatter
import io.odin.meta.Position
import io.odin.{Level, LoggerMessage}
import cats.syntax.partialOrder._

private[logging] trait OdinTranslator {
  def minLevel: Level
  def run(name: String, level: Level, msg: String, t: Option[Throwable] = None): Unit
}

object OdinTranslator {
  private[logging] def unsafeConsole(level: Level, formatter: Formatter): OdinTranslator = new OdinTranslator {
    override def minLevel: Level = level

    private def println(out: PrintStream, msg: LoggerMessage): Unit =
      out.println(formatter.format(msg))

    override def run(loggerName: String, level: Level, msg: String, t: Option[Throwable]): Unit =
      if (level >= minLevel) {
        val message = makeMessage(loggerName, level, msg, t, System.currentTimeMillis())

        if (level < Level.Warn) {
          println(System.out, message)
        } else {
          println(System.err, message)
        }
      } else {
        ()
      }
  }

  private def makeMessage(loggerName: String, level: Level, msg: String, t: Option[Throwable], timestamp: Long) =
    LoggerMessage(
      level = level,
      message = Eval.now(msg),
      context = Map.empty,
      exception = t,
      position = Position(fileName = loggerName, enclosureName = loggerName, packageName = loggerName, line = -1),
      threadName = Thread.currentThread().getName,
      timestamp = timestamp
    )
}
