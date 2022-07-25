package org.slf4j.impl;

import trace4cats.logging.Binder;

public class StaticLoggerBinder extends Binder {

  public static String REQUESTED_API_VERSION = "1.7";

  private static final StaticLoggerBinder _instance = new StaticLoggerBinder();

  public static StaticLoggerBinder getSingleton() {
    return _instance;
  }

}
