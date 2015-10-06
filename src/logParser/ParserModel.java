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
public class ParserModel implements ModelInterface, PathObserver {

    public ParserModel() {}
    
    /**
     * Method to check login name in nwnplayer.ini
     * @return Player username, or empty string if file not found
     */
    public String readUsernameFromFile() {
        //TODO: Below is extracted from old view for reference
//        File nwnplayer_ini = new File(pathManager.getNWNInstallPath() + Constants.INI_PLAYER_FILENAME + Constants.INI_FILE_EXTENSION);
//        try {
//            return tryToReadUsername(nwnplayer_ini);
//        } catch (IOException ex) {
//            Logger.getLogger(ParserView.class.getName()).log(Level.SEVERE, "Failed to read username. Verify path to nwnplayer.ini: " + nwnplayer_ini, ex);
//        }
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public void initialize() {
    }

    @Override
    public void run() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void pause() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void unpause() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void registerObserver(ParserObserver o) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void removeObserver(ParserObserver o) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void handleEvent(Path path, WatchEvent event) {
        //TODO: Below is extracted from execution in old View for reference.
//        if(isLogfile(path)) {
//            newFile = new File(pathManager.getNWNInstallPath().toString() + Constants.LOG_FILENAME + (logNumber + 1) + Constants.TEXT_FILE_EXTENSION);
//            if(newFile.exists() && (newFile.lastModified() > combatLog.lastModified())){ //29.06.2012 - changed from >= to > because it really shouldn't matter and > is a safer choice when avoiding duplicate file entries
//                setLogNumber(logNumber + 1);
//                combatLog = newFile;
//                return newFile;
//            }
//        }
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
