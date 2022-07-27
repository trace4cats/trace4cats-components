package trace4cats.logging

import cats.syntax.partialOrder._
import io.odin.Level
import io.odin.formatter.Formatter
import org.slf4j.Logger
import org.slf4j.helpers.{FormattingTuple, MarkerIgnoringBase, MessageFormatter}

class LoggerAdapter(loggerName: String) extends MarkerIgnoringBase with Logger {
  private val translator = OdinTranslator.unsafeConsole(Level.Info, Formatter.default)

  override def getName: String = loggerName

  private def run(level: Level, msg: String, t: Option[Throwable] = None): Unit =
    translator.run(loggerName, level, msg, t)

  private[logging] def runFormatted(level: Level, tuple: FormattingTuple): Unit =
    run(level, tuple.getMessage, Option(tuple.getThrowable))

  def isTraceEnabled: Boolean = translator.minLevel <= Level.Trace

  def trace(msg: String): Unit = run(Level.Trace, msg)

  def trace(format: String, arg: Any): Unit = runFormatted(Level.Trace, MessageFormatter.format(format, arg))

  def trace(format: String, arg1: Any, arg2: Any): Unit =
    runFormatted(Level.Trace, MessageFormatter.format(format, arg1, arg2))

  def trace(msg: String, t: Throwable): Unit =
    run(Level.Trace, msg, Option(t))

  def isDebugEnabled: Boolean = translator.minLevel <= Level.Debug

  def debug(msg: String): Unit = run(Level.Debug, msg)

  def debug(format: String, arg: Any): Unit = runFormatted(Level.Debug, MessageFormatter.format(format, arg))

  def debug(format: String, arg1: Any, arg2: Any): Unit =
    runFormatted(Level.Debug, MessageFormatter.format(format, arg1, arg2))

  def debug(msg: String, t: Throwable): Unit =
    run(Level.Debug, msg, Option(t))

  def isInfoEnabled: Boolean = translator.minLevel <= Level.Info

  def info(msg: String): Unit = run(Level.Info, msg)

  def info(format: String, arg: Any): Unit = runFormatted(Level.Info, MessageFormatter.format(format, arg))

  def info(format: String, arg1: Any, arg2: Any): Unit =
    runFormatted(Level.Info, MessageFormatter.format(format, arg1, arg2))

  def info(msg: String, t: Throwable): Unit =
    run(Level.Info, msg, Option(t))

  def isWarnEnabled: Boolean = translator.minLevel <= Level.Warn

  def warn(msg: String): Unit = run(Level.Warn, msg)

  def warn(format: String, arg: Any): Unit = runFormatted(Level.Warn, MessageFormatter.format(format, arg))

  def warn(format: String, arg1: Any, arg2: Any): Unit =
    runFormatted(Level.Warn, MessageFormatter.format(format, arg1, arg2))

  def warn(msg: String, t: Throwable): Unit =
    run(Level.Warn, msg, Option(t))

  def isErrorEnabled: Boolean = translator.minLevel <= Level.Error

  def error(msg: String): Unit = run(Level.Error, msg)

  def error(format: String, arg: Any): Unit = runFormatted(Level.Error, MessageFormatter.format(format, arg))

  def error(format: String, arg1: Any, arg2: Any): Unit =
    runFormatted(Level.Error, MessageFormatter.format(format, arg1, arg2))

  def error(msg: String, t: Throwable): Unit =
    run(Level.Error, msg, Option(t))

  def trace(format: String, arguments: AnyRef*): Unit =
    runFormatted(Level.Trace, MessageFormatter.arrayFormat(format, arguments.toArray))

  def debug(format: String, arguments: AnyRef*): Unit =
    runFormatted(Level.Debug, MessageFormatter.arrayFormat(format, arguments.toArray))

  def info(format: String, arguments: AnyRef*): Unit =
    runFormatted(Level.Info, MessageFormatter.arrayFormat(format, arguments.toArray))

  def warn(format: String, arguments: AnyRef*): Unit =
    runFormatted(Level.Warn, MessageFormatter.arrayFormat(format, arguments.toArray))

  def error(format: String, arguments: AnyRef*): Unit =
    runFormatted(Level.Error, MessageFormatter.arrayFormat(format, arguments.toArray))

}
