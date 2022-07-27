package trace4cats.logging

import org.slf4j.{ILoggerFactory, Logger}

class LoggerFactory extends ILoggerFactory {
  def getLogger(name: String): Logger = new LoggerAdapter(name)
}
