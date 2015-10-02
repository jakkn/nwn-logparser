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
import java.nio.file.Path;
import java.nio.file.Paths;

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
 
        // register directory and process its events
        Path path = Paths.get(args[0]);
        new PathWatcher(path).run();
        System.out.println("The above is a thread so this should never output, "
                + "unless said thread dies. If you see this, thread just went X.X");
        
//        ParserModel model = new ParserModel();
//        ControllerInterface controller = new ParserController(model);
//        java.awt.EventQueue.invokeLater(new Runnable() {
//            public void run() {
//                new ParserView(controller, model).setVisible(true);
//            }
//        });
    }
}
