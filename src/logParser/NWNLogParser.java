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

import java.io.IOException;
import java.nio.file.ClosedWatchServiceException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Jakob Dagsland Knutsen (JDK)
 */
public class NWNLogParser {
    
    
    static void printUsage() {
        System.err.println("usage: java -jar " + Constants.APPLICATION_NAME + " " +
                Constants.APPLICATION_SWITCHES + " logdir");
        System.exit(-1);
    }
    
    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String args[]) throws IOException {
        // parse arguments
        if (args.length == 0 || args.length > 1){
            printUsage();
        }
 
        Path path = Paths.get(args[0]);
        PathWatcher pw = new PathWatcher();
        ParserModel model = new ParserModel();
        try {
            pw.observe(path, model);
        } catch (IOException | ClosedWatchServiceException ex) {
            Logger.getLogger(PathWatcher.class.getName()).log(Level.SEVERE,
                    "Failed to start observing log directory. "
                    + "Verify path: " + path, ex);
            return;
        }
        
        
        Thread thread = new Thread(pw);
        thread.run();

        ControllerInterface controller = new ParserController(model);
        java.awt.EventQueue.invokeLater(() -> {
            new ParserView(controller, model).setVisible(true);
        });
    }
}
