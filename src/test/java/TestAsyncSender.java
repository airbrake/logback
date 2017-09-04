package io.airbrake.logback;

import java.util.concurrent.CompletableFuture;

import io.airbrake.javabrake.Notice;
import io.airbrake.javabrake.AsyncSender;

class TestAsyncSender implements AsyncSender {
  public Notice notice;

  @Override
  public CompletableFuture<Notice> send(Notice notice) {
    this.notice = notice;
    return new CompletableFuture<Notice>();
  }

  @Override
  public void setHost(String host) {}
}
