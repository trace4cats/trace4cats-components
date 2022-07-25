package trace4cats.logging

import org.slf4j.ILoggerFactory
import org.slf4j.spi.LoggerFactoryBinder

class Binder extends LoggerFactoryBinder {
  private val factoryClass = classOf[LoggerFactory].getName

  override def getLoggerFactory: ILoggerFactory = new LoggerFactory

  override def getLoggerFactoryClassStr: String = factoryClass
}
