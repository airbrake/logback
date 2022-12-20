package io.airbrake.logback;

import java.io.IOException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.airbrake.javabrake.NoticeError;
import io.airbrake.javabrake.Airbrake;
import io.airbrake.javabrake.Config;
import io.airbrake.javabrake.NoticeStackFrame;
import io.airbrake.javabrake.Notifier;

public class AirbrakeAppenderTest {
  static Notifier notifier;
  Throwable exc = new IOException("hello from Java");
  MockSyncSender senderSync = new MockSyncSender();

  @BeforeAll
  public static void beforeClass() {
    Config config = new Config();
    config.projectId = 0;
    config.projectKey = "";
    notifier = new Notifier(config);

    LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
    ch.qos.logback.classic.Logger logger = context.getLogger("io.airbrake.logback");

    AirbrakeAppender app = new AirbrakeAppender();
    app.setContext(context);
    app.start();
    logger.addAppender(app);
  }

  @BeforeEach
  public void before() {
    notifier.setSyncSender(senderSync);
    Airbrake.setNotifier(notifier);
  }

  @Test
  public void testLogException() {
    Logger logger = LoggerFactory.getLogger("io.airbrake.logback");
    logger.error("hello from Java", exc);

    NoticeError err = senderSync.notice.errors.get(0);
    assertEquals("java.io.IOException", err.type);
    assertEquals("hello from Java", err.message);
  }

  @Test
  public void testLogMessage() {
    Logger logger = LoggerFactory.getLogger("io.airbrake.logback");
    logger.error("hello from Java");

    NoticeError err = senderSync.notice.errors.get(0);
    assertEquals("io.airbrake.logback", err.type);
    assertEquals("hello from Java", err.message);

    NoticeStackFrame frame = err.backtrace[0];
    assertEquals("testLogMessage", frame.function);
    assertTrue(frame.file.contains("test/io/airbrake/logback/AirbrakeAppenderTest.class"));
    assertEquals(65, frame.line);
  }
}
