package org.sakaiproject.sdata.services.presence;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.EventListener;
import java.util.EventObject;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.db.cover.SqlService;
import org.sakaiproject.sdata.services.profile.ProfileBean;

public class TimerBean implements Runnable, Serializable {
	
	private static final Log log = LogFactory.getLog(TimerBean.class);
	
  public int getInterval() {
    return interval;
  }

  public void setInterval(int i) {
    interval = i;
  }

  public boolean isRunning() {
    return runner != null;
  }

  public void setRunning(boolean b) {
    if (b && runner == null) {
      runner = new Thread(this);
      runner.start();
    } else if (!b && runner != null) {
      runner.interrupt();
      runner = null;
    }
  }

  public synchronized void addTimerListener(TimerListener l) {
    timerListeners.addElement(l);
  }

  public synchronized void removeTimerListener(TimerListener l) {
    timerListeners.removeElement(l);
  }

  public void fireTimerEvent(TimerEvent evt) {
    
	  String s = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
	  long now = Long.parseLong(s) - 60;
	  
	  Object[] params = new Object[1];
	  params[0] = now;
	  SqlService.dbWrite("DELETE FROM sdata_presence WHERE lastseen < ?", params);
	  
	  String s2 = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
	  long now2 = Long.parseLong(s) - (60*60*24);
	  
	  Object[] params2 = new Object[1];
	  params2[0] = now2;
	  SqlService.dbWrite("DELETE FROM sdata_chat WHERE readwhen < ?", params2);
	  
	  log.info("Clean up operation performed");
	  
  }

  public void run() {
    if (interval <= 0)
      return;
    try {
      while (!Thread.interrupted()) {
        Thread.sleep(interval);
        fireTimerEvent(new TimerEvent(this));
      }
    } catch (InterruptedException e) {
    }
  }

  private int interval = 1000;

  private Vector timerListeners = new Vector();

  private Thread runner;
}

class TimerEvent extends EventObject {
  public TimerEvent(Object source) {
    super(source);
    now = new Date();
  }

  public Date getDate() {
    return now;
  }

  private Date now;
}

interface TimerListener extends EventListener {
  public void timeElapsed(TimerEvent evt);
}
