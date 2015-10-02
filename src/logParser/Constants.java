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
public interface Constants {
    public final static String DEFAULT_PROPERTIES = "default.properties";
    public final static String APPLICATION_PROPERTIES = "application.properties";
    public final static String PROPERTIES_HEADER = "#Hal's logparser properties file";
    public final static String LOG_FILENAME = "nwclientLog";
    public final static String INI_FILE_EXTENSION = ".ini";
    public final static String TEXT_FILE_EXTENSION = ".txt";
    public final static String INI_PLAYER_FILENAME = "nwnplayer";
    public final static String PROPERTY_INI_PLAYERNAME = "player"; //location: NWN/nwplayer.ini
    public final static String PROPERTY_LOG_FOLDER_LOCATION = "nwnlogsfolder";
    public final static String PROPERTY_NWN_INSTALL_PATH = "nwninstalldir";
    public final static String APPLICATION_NAME = "NWNLogParser.jar";
    public final static String APPLICATION_SWITCHES = "[no switches supported yet]";
}
