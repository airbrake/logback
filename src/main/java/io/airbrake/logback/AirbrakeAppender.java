package io.airbrake.logback;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.StackTraceElementProxy;
import ch.qos.logback.core.AppenderBase;
import io.airbrake.javabrake.Notifier;
import io.airbrake.javabrake.Airbrake;
import io.airbrake.javabrake.Config;
import io.airbrake.javabrake.Notice;
import io.airbrake.javabrake.NoticeError;

public class AirbrakeAppender extends AppenderBase<ILoggingEvent> {

  Notifier notifier;
  int projectId;
  String projectKey;String env;

  public void setProjectId(int projectId) {
    this.projectId = projectId;
    this.initNotifier();
  }

  public void setProjectKey(String projectKey) {
    this.projectKey = projectKey;
    this.initNotifier();
  }

  public void setEnv(String env) {
    this.env = env;
  }

  public void initNotifier() {
    if (projectId == 0 || projectKey == null) {
      return;
    }
    Config config = new Config();
    config.projectId = projectId;
    config.projectKey = projectKey;
    this.notifier = new Notifier(config);
  }

  @Override
  protected void append(ILoggingEvent event) {
    NoticeError err = newNoticeError(event);
    if (err == null) {
      return;
    }

    List<NoticeError> errors = new ArrayList<>();
    errors.add(err);

    Notice notice = new Notice(errors);
    if (this.env  != null) {
      notice.setContext("environment", this.env);
    }
    notice.setContext("severity", formatLevel(event.getLevel()));
    notice.setParam("threadName", event.getThreadName());
    Map<String, String> mdc = event.getMDCPropertyMap();
    if (mdc !=null && mdc.size() > 0) {
      notice.setParam("mdc", mdc);
    }
    Map<String, String> ctx = event.getLoggerContextVO().getPropertyMap();
    if (ctx!= null && ctx.size() > 0) {
      notice.setParam("contextV0", ctx);
    }
    if (event.getMarker() != null ) {
      notice.setParam("marker", event.getMarker().getName());
    }
    this.send(notice);
  }

  static NoticeError newNoticeError(ILoggingEvent event) {
    IThrowableProxy throwableProxy = event.getThrowableProxy();
    if (throwableProxy != null) {
      String type = throwableProxy.getClassName();
      String message = throwableProxy.getMessage();
      StackTraceElement[] stackTrace =
          toStackTrace(throwableProxy.getStackTraceElementProxyArray());
      return new NoticeError(type, message, stackTrace);
    }

    String type = event.getLoggerName();
    String message = event.getFormattedMessage();
    return new NoticeError(type, message, event.getCallerData());
  }

  static StackTraceElement[] toStackTrace(StackTraceElementProxy[] proxyElements) {
    StackTraceElement[] stackTrace = new StackTraceElement[proxyElements.length];
    for (int i = 0; i < proxyElements.length; i++) {
      stackTrace[i] = proxyElements[i].getStackTraceElement();
    }
    return stackTrace;
  }

  static String formatLevel(Level level) {
    if (level.isGreaterOrEqual(Level.ERROR)) {
      return "error";
    }
    if (level.isGreaterOrEqual(Level.WARN)) {
      return "warn";
    }
    if (level.isGreaterOrEqual(Level.INFO)) {
      return "info";
    }
    if (level.isGreaterOrEqual(Level.DEBUG)) {
      return "debug";
    }
    return "trace";
  }

  Notice send(Notice notice) {
    if (this.notifier != null) {
      return this.notifier.sendSync(notice);
    }
    return Airbrake.sendSync(notice);
  }
}
