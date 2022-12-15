package io.airbrake.logback;

import io.airbrake.javabrake.Notice;
import io.airbrake.javabrake.SyncSender;

class MockSyncSender implements SyncSender {
  public Notice notice;

  @Override
  public Notice send(Notice notice) {
    this.notice = notice;
    return notice;
  }

  @Override
  public void setErrorHost(String host) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void setAPMHost(String host) {
    // TODO Auto-generated method stub
    
  }


}
