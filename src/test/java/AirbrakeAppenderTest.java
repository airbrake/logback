package io.airbrake.logback;

import java.io.IOException;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import ch.qos.logback.classic.LoggerContext;

import org.junit.Before;
import org.junit.Test;
import org.junit.BeforeClass;
import static org.junit.Assert.*;

import io.airbrake.javabrake.NoticeError;
import io.airbrake.javabrake.Airbrake;
import io.airbrake.javabrake.NoticeStackFrame;
import io.airbrake.javabrake.Notifier;

public class AirbrakeAppenderTest {
  Notifier notifier = new Notifier(0, "");
  Throwable exc = new IOException("hello from Java");
  TestAsyncSender sender = new TestAsyncSender();

  @BeforeClass
  public static void beforeClass() {
    LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
    ch.qos.logback.classic.Logger logger = context.getLogger("io.airbrake.logback");

    AirbrakeAppender app = new AirbrakeAppender();
    app.setContext(context);
    app.start();
    logger.addAppender(app);
  }

  @Before
  public void before() {
    notifier.setAsyncSender(sender);
    Airbrake.setNotifier(notifier);
  }

  @Test
  public void testLogException() {
    Logger logger = LoggerFactory.getLogger("io.airbrake.logback");
    logger.error("hello from Java", exc);

    NoticeError err = sender.notice.errors.get(0);
    assertEquals("java.io.IOException", err.type);
    assertEquals("hello from Java", err.message);
  }

  @Test
  public void testLogMessage() {
    Logger logger = LoggerFactory.getLogger("io.airbrake.logback");
    logger.error("hello from Java");

    NoticeError err = sender.notice.errors.get(0);
    assertEquals("io.airbrake.logback", err.type);
    assertEquals("hello from Java", err.message);

    NoticeStackFrame frame = err.backtrace[0];
    assertEquals("testLogMessage", frame.function);
    assertEquals("test/io/airbrake/logback/AirbrakeAppenderTest.class", frame.file);
    assertEquals(54, frame.line);
  }
}
