/*
 * Copyright (C) 2015 Jakob Dagsland Knutsen (JDK)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package logParser;

/**
 *
 * @author Jakob Dagsland Knutsen (JDK)
 */

import com.sun.nio.file.SensitivityWatchEventModifier;
import java.nio.file.*;
import java.io.*;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchService;
import java.util.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A PathWatcher monitors directory paths and notifies the registered
 * observers of changes that occur within the path. Observers register
 * through the observe method. Due to the fact that registering the
 * same path with a watch service more than once will return the
 * existing watch key and overwrite the events, a path watcher cannot
 * be used to register observers with different events. Thus, each path
 * watcher has its own watch service (instead of one static watch
 * service), and care must be taken not to use the same path watcher
 * for objects with conflicting path & event interests.
 */
public class PathWatcher implements Runnable {

    private final WatchService WATCH_SERVICE;
    private final Map<WatchKey, List<PathObserver>> WATCH_KEYS;

    PathWatcher() {
        this.WATCH_SERVICE = createWatchService();
        this.WATCH_KEYS = new ConcurrentHashMap<>();
    }

    private WatchService createWatchService(){
        try {
            return FileSystems.getDefault().newWatchService();
        } catch (IOException ex) {
            Logger.getLogger(PathWatcher.class.getName()).log(Level.SEVERE,
                    "Filesystem error.", ex);
        }
        // Bad practice but cannot think of alternative
        return null;
    }

    /**
     * Nonrecursive path observation
     *
     * @param path the path to observe. If the same path is registered twice, the events
     *             for this path will be overwritten for all observers registered to this path
     * @param observer the object to register for path observation
     * @param events events the observer wants to be notified of for this path
     * @throws IOException if an I/O error occurs
     * @throws ClosedWatchServiceException if the watch service is closed
     */
    public void observe(Path path, PathObserver observer, Kind<Path> ... events)
        throws IOException, ClosedWatchServiceException
    {
        WatchKey key = path.register(WATCH_SERVICE, events, SensitivityWatchEventModifier.HIGH);

        WATCH_KEYS.compute(key, (WatchKey k, List<PathObserver> list) -> {
            if(list != null) {
                list.add(observer);
                return list;
            } else {
                List<PathObserver> newList = new ArrayList<>(1);
                newList.add(observer); 
                return newList;
            }
        });
    }

    @Override
    public void run() {
        while (true) {
            
            WatchKey key;
            try {
                key = WATCH_SERVICE.take();
            } catch (InterruptedException e) {
                // Enables Thread.interrupt() to kill the thread
                return;
            }
            if (!pathIsValid(key)) {
                continue;
            }
            notifyPathObservers(key);
            resetKey(key);
            if (WATCH_KEYS.isEmpty()) {
                break;
            }
        }
    }

    private boolean pathIsValid(WatchKey key) {
        boolean validPath = (WATCH_KEYS.get(key) != null);
        if(!validPath) {
            Logger.getLogger(PathWatcher.class.getName()).log(Level.WARNING,
                    "Retreived a WatchKey linked to null path. "
                    + "Investigate key->path pair creation.");
        }
        return validPath;
    }

    private void notifyPathObservers(WatchKey key) {
        key.pollEvents().stream().forEach(watchEvent -> {
            WATCH_KEYS.get(key).stream().forEach(pathObserver -> {
                pathObserver.handleEvent(absolutePath(key, watchEvent), watchEvent);
            });
        });
    }

    private Path absolutePath(WatchKey key, WatchEvent<?> event){
        Path watchKeyPath = (Path) key.watchable();
        Path eventPath = (Path) event.context();
        return watchKeyPath.resolve(eventPath);
    }
    
    private void resetKey(WatchKey key) {
        boolean keyStillValid = key.reset();
        if (!keyStillValid) {
            WATCH_KEYS.remove(key);
        }
    }
}