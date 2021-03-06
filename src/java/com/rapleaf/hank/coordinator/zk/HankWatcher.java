package com.rapleaf.hank.coordinator.zk;

import org.apache.log4j.Logger;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

/**
 * Abstract Watcher implementation that adds a few features.
 * 
 * The watch is immediately reset upon notification, *before* the subclass is
 * notified, guaranteeing that you won't miss notifications. Though, when you do
 * your get() call, you might get get data for events that happened after the
 * original notification, and then be notified *again* for the same data.
 * 
 * The process method is synchronized so that we don't have to worry about
 * concurrent notifications
 * 
 * Calling cancel() on the watcher sets a flag that prevents further
 * notifications and watch-setting.
 */
abstract class HankWatcher implements Watcher {
  private static final Logger LOG = Logger.getLogger(HankWatcher.class);

  private boolean cancelled = false;

  protected HankWatcher() throws KeeperException, InterruptedException {
    setWatch();
  }

  public abstract void setWatch() throws KeeperException, InterruptedException;
  protected abstract void realProcess(WatchedEvent event);

  @Override
  public final void process(WatchedEvent event) {
    synchronized (this) {
      if (cancelled) {
        return;
      }

      try {
        setWatch();
      } catch (KeeperException e) {
        LOG.error("Failed to reset watch!", e);
      } catch (InterruptedException e) {
        // TODO: support retrying here?
        LOG.error("Failed to reset watch!", e);
      }

      realProcess(event);
    }
  }

  public final void cancel() {
    cancelled = true;
  }
}
