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

import java.nio.file.*;
import java.io.*;
import java.util.*;

public class PathWatcher implements Runnable, LogObservable {

    private final WatchService watchService;
    private final Map<WatchKey,Path> watchKeys;
    private final List<LogObserver> logObservers;

    PathWatcher(Path path) throws IOException {
        this.watchService = FileSystems.getDefault().newWatchService();
        this.watchKeys = new HashMap<>();
        this.logObservers = new ArrayList<>();
        
        registerPathWithWatchService(path);
    }

    private void registerPathWithWatchService(Path path) throws IOException {
        WatchKey key = path.register(watchService,
                StandardWatchEventKinds.ENTRY_CREATE,
                StandardWatchEventKinds.ENTRY_DELETE,
                StandardWatchEventKinds.ENTRY_MODIFY);
        watchKeys.put(key, path);
    }

    @Override
    public void run() {
        while(true) {
            
            WatchKey key;
            try {
                key = watchService.take();
            } catch (InterruptedException e) {
                return; // Enables Thread.interrupt() to kill the thread
            }
            if (!pathIsValid(key)) {
                continue;
            }
            processPendingEvents(key);
            resetKey(key);
            if (watchKeys.isEmpty()) {
                break;
            }
        }
    }

    private boolean pathIsValid(WatchKey key) {
        boolean validPath = watchKeys.get(key) != null;
        if(!validPath) {
            System.err.println("Retreived a WatchKey linked to null path.");
        }
        return validPath;
    }

    private void processPendingEvents(WatchKey key) {
        key.pollEvents().stream().forEach((event) -> {
            handleOverflow(event);
//            System.out.format("%s: %s\n", event.kind().name(), absolutePath(key, event));
            notifyLogObservers(event.kind(), absolutePath(key, event));
        });
    }
    
    private void resetKey(WatchKey key) {
        boolean keyStillValid = key.reset();
        if (!keyStillValid) {
            watchKeys.remove(key);
        }
    }

    private void handleOverflow(WatchEvent<?> event) {
        if (event.kind() == StandardWatchEventKinds.OVERFLOW){
                System.err.println("OVERFLOW WatchEvent encountered. Log "
                        + "information may have been lost.");
        }
    }

    private Path absolutePath(WatchKey key, WatchEvent<?> event){
        Path watchkeyPath = watchKeys.get(key);
        Path eventPath = (Path) event.context();
        return watchkeyPath.resolve(eventPath);
    }

    @Override
    public void registerObserver(LogObserver o) {
        logObservers.add(o);
    }
    
    @Override
    public void removeObserver(LogObserver o) {
        logObservers.remove(o);
    }
    
    @Override
    public void notifyLogObservers(WatchEvent.Kind<?> kind, Path path) {
        logObservers.stream().forEach((observer) -> {
            if      (kind == StandardWatchEventKinds.ENTRY_CREATE) observer.newFile(path);
            else if (kind == StandardWatchEventKinds.ENTRY_MODIFY) observer.updatedFile(path);
            else if (kind == StandardWatchEventKinds.ENTRY_DELETE) observer.deletedFile(path);
        });
    }

}