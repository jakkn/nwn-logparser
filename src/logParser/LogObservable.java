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

import java.nio.file.Path;
import java.nio.file.WatchEvent;

/**
 * 
 * @author Jakob Dagsland Knutsen (JDK)
 */
interface LogObservable {
    public void registerObserver(LogObserver o);
    public void removeObserver(LogObserver o);
    /**
     * Push notifications of file changes to all observers, calling
     * the corresponding LogObserver methods according to the event kind.
     * Pushing preferred over pulling to optimize the number of times the event
     * type is checked.
     */
    public void notifyLogObservers(WatchEvent.Kind<?> kind, Path path);
}
