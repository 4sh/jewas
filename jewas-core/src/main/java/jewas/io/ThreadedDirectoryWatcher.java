package jewas.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.*;
import java.util.concurrent.*;

/**
 * @author fcamblor
 *         Directory watcher which will launch a dedicated thread to watch for file changes and react
 *         on it
 */
public abstract class ThreadedDirectoryWatcher {

    private static final Logger LOG = LoggerFactory.getLogger(ThreadedDirectoryWatcher.class);

    private static final ThreadFactory DAEMON_THREAD_FACTORY = new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            Thread t = Executors.defaultThreadFactory().newThread(r);
            t.setDaemon(true);
            return t;
        }
    };

    private static final ConcurrentMap<String, ExecutorService> WATCHER_FAMILIES_EXECUTORS = new ConcurrentHashMap<>();

    String watcherFamily;
    Path watchingDirectory;
    WatchEvent.Kind[] events;
    WatchService watcher = null;

    public ThreadedDirectoryWatcher(String watcherFamily, Path watchingDirectory, WatchEvent.Kind... events) {
        if (!WATCHER_FAMILIES_EXECUTORS.containsKey(watcherFamily)) {
            throw new IllegalStateException("You didn't registered any ExecutorService for given watcher family : " + watcherFamily + ". " +
                    "See " + this.getClass().getName() + ".registerFamilyExecutorService().");
        }
        this.watchingDirectory = watchingDirectory;
        this.events = events;
        this.watcher = null;
        this.watcherFamily = watcherFamily;
    }

    public void startWatching() {
        getExecutorService().submit(new Runnable() {
            @Override
            public void run() {
                try {
                    _startWatching();
                } catch (Exception e) {
                    LOG.error("Exception thrown during file watcher of " + watchingDirectory + " in " + watcherFamily + " family !", e);
                } finally {
                    stopWatching();
                }
            }
        });
    }

    private void _startWatching() {
        try (WatchService watcher = FileSystems.getDefault().newWatchService()) {
            // This must be done this way because try-with-resource doesn't accept
            // variable in an outer scope than current try{} one
            this.watcher = watcher;

            watchingDirectory.register(watcher, events);

            onInit();
            while (!Thread.currentThread().isInterrupted()) {
                WatchKey key = null;
                try {
                    key = watcher.take();
                    handleWatchKey(key);
                } catch (ClosedWatchServiceException e) {
                    // We shouldn't throw anything here : it can be "normal" to have the watch service closed
                    // during stopWatching() call
                    LOG.debug("Closed watcher of " + watchingDirectory + " in " + watcherFamily + " family !");
                    Thread.currentThread().interrupt();
                } catch (InterruptedException e) {
                    LOG.error("Interrupted exception during watch of directory " + watchingDirectory, e);
                    this.watcher = null;
                    Thread.currentThread().interrupt();
                } catch (Exception e) {
                    LOG.error("Exception thrown during file watcher of " + watchingDirectory + " in " + watcherFamily + " family !", e);
                    throw e;
                } finally {
                    if (key != null) {
                        // Reset the key -- this step is critical if you want to
                        // receive further watch events.  If the key is no longer valid,
                        // the directory is inaccessible so exit the loop.
                        key.reset();
                    }
                }
            }
        } catch (IOException e) {
            LOG.error("Error while registering EEG Importer watcher in " + watchingDirectory, e);
            throw new RuntimeException(e);
        }
    }

    protected void handleWatchKey(WatchKey key) {
        for (WatchEvent<?> watchEvent : key.pollEvents()) {
            WatchEvent.Kind<?> kind = watchEvent.kind();

            // This key is registered only
            // for ENTRY_CREATE events,
            // but an OVERFLOW event can
            // occur regardless if events
            // are lost or discarded.
            if (kind == StandardWatchEventKinds.OVERFLOW) {
                continue;
            }

            // The filename is the context of the event
            WatchEvent<Path> ev = (WatchEvent<Path>) watchEvent;
            Path eventPath = ev.context();

            onFired(this.watchingDirectory.resolve(eventPath), kind);
        }
    }

    public void stopWatching() {
        try {
            if (this.watcher != null) {
                try {
                    this.watcher.close();
                } catch (IOException e) {
                    LOG.error("Error while closing file watcher", e);
                }
            }
        } finally {
            this.watcher = null;
        }
    }

    /**
     * Overridable
     * Should always be the same shared instance of executor service between a family of DirectoryWatchers
     */
    protected ExecutorService getExecutorService() {
        // Should never be null, see DirectoryWatchers' constructor
        return WATCHER_FAMILIES_EXECUTORS.get(watcherFamily);
    }

    /**
     * @see ThreadedDirectoryWatcher#registerFamilyExecutorService(String, ExecutorService)
     */
    public static void registerStandardFamilyExecutorService(String watcherFamily, int threadCount) {
        registerFamilyExecutorService(watcherFamily, Executors.newFixedThreadPool(threadCount, DAEMON_THREAD_FACTORY));
    }

    /**
     * This static method should be call prior to the first instanciation of ThreadedDirectoryWatcher(watcherFamily, ...)
     * Otherwise, an illegal state exception will be thrown during instanciation
     * It is intended to define the number of threads which will be allocated to the directory watcher family, to
     * watch for things in parallel
     * <p/>
     * If you know in advance you will only instantiate 1 DirectoryWatcher for current family, you should define an
     * executor service with only 1 thread (not more).
     * If you're working with more than 1 DirectoryWatcher per family, you should set the cursor accurately : the more you
     * will have threads, the more potential parallel onFired() you will have to support
     *
     * @see ThreadedDirectoryWatcher#registerStandardFamilyExecutorService(String, int) too
     */
    public static void registerFamilyExecutorService(String watcherFamily, ExecutorService executorService) {
        if (WATCHER_FAMILIES_EXECUTORS.putIfAbsent(watcherFamily, executorService) != null) {
            throw new IllegalArgumentException("Threaded directory watcher already set for family [" + watcherFamily + "] : a watcher of this family already started watching or you already registered this family before !");
        }
    }

    /**
     * Method called before entering events loop on watching directory
     */
    protected void onInit() {
    }

    /**
     * Method called when watching event is fired on current watching directory
     */
    protected abstract void onFired(Path eventPath, WatchEvent.Kind<?> kind);
}
