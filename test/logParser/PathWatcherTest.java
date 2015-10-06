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

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Assert;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Jakob Dagsland Knutsen (JDK)
 */
public class PathWatcherTest {
    
    private pathObserverTester pathObserver;
    private Path standardTestFile;
    private Thread pwThread;
    
    public PathWatcherTest() {}
    
    @BeforeClass
    public static void setUpClass() {
        createDirectory(TestConstants.TESTDIR);
    }
    
    @AfterClass
    public static void tearDownClass() {
        deleteAll(new File(TestConstants.TESTDIR));
    }
    
    @Before
    public void setUp() {
        try {
            tryToInitTestEnvironment();
        } catch (IOException ex) {
            Logger.getLogger(PathWatcherTest.class.getName()).log(Level.SEVERE,
                    "Failed to initialize test environment. Check file "
                    + "creations.", ex);
        }
    }
    
    private void tryToInitTestEnvironment() throws IOException {
        standardTestFile = createTestFile(TestConstants.STANDARD_TESTFILE);

        PathWatcher pw = new PathWatcher();
        pathObserver = new pathObserverTester();
        pw.observe(Paths.get(TestConstants.TESTDIR), pathObserver,
                StandardWatchEventKinds.ENTRY_CREATE,
                StandardWatchEventKinds.ENTRY_MODIFY,
                StandardWatchEventKinds.ENTRY_DELETE);

        pwThread = new Thread(pw);
        pwThread.start();
    }
    
    @After
    public void tearDown() {
        pwThread.interrupt();
    }

    @Test
    public void verifyFileWrites() {
        int nFileModifications = 10;
        
        int nWrites = writeTo(standardTestFile, nFileModifications);
        
        assertWriteNotificationsEquals(nWrites);
    }
    
    private static Path createTestFile(String path) throws IOException {
        assert path != null : "file path should not be null";
        assert !path.equals("") : "file path should not be empty";
        
        Path filepath = Paths.get(TestConstants.TESTDIR).resolve(path);
        boolean success = filepath.toFile().createNewFile();
        assert success : "file creation failed: " + filepath.toString();
        return filepath;
    }

    private static void createDirectory(String path) {
        assert path != null : "dirpath should not be null";
        assert !path.equals("") : "dirpath should not be empty";
        
        Path dirpath = Paths.get(path);
        boolean success = dirpath.toFile().mkdir();
        assert success : "directory creation failed: " + dirpath.toString();
    }

    private static void deleteAll(File file) {
        boolean success = file.delete();
        if (!success) {
            for (File child : file.listFiles()) {
                deleteAll(child);
            }
            success = file.delete();
        }
        assert success : "File delete failed: " + file.getPath();
    }

    private int writeTo(Path file, int nTimes) {
        assert file.toFile().canWrite() : "Cannot write to file";
        
        List<String> input = Arrays.asList("testinput");
        Charset utf8 = StandardCharsets.UTF_8;
        
        for (int i = 0; i < nTimes; i++) {
            try {
                tryToWrite(file, utf8, input);
            } catch (IOException ex) {
                Logger.getLogger(PathWatcherTest.class.getName()).log(
                        Level.SEVERE, "Filewrite failed during test", ex);
                return i;
            }
        }
        return nTimes;
    }
    
    private void tryToWrite(Path file, Charset charset, List<String> input)
        throws IOException
    {
        Files.write(file,
                    input,
                    charset,
                    StandardOpenOption.WRITE,
                    StandardOpenOption.APPEND,
                    StandardOpenOption.SYNC);
    }

    private void assertWriteNotificationsEquals(int expectedFileModifications) {
        Assert.assertTrue(expectedFileModifications + " writes matches "
                + pathObserver.modififiedNotifications + " notifications",
                pathObserver.modififiedNotifications == expectedFileModifications);
    }

    private void assertCreateNotificationsEquals(int expectedFileCreations) {
        Assert.assertTrue(expectedFileCreations + " creations matches "
                + pathObserver.createdNotifications + " notifications",
                pathObserver.createdNotifications == expectedFileCreations);
    }

    private static class pathObserverTester implements PathObserver {

        private Path lastCreatePath, lastDeletePath, lastModifyPath;
        private int createdNotifications, deletedNotifications, modififiedNotifications;
        
        public pathObserverTester() {
            lastCreatePath = lastDeletePath = lastModifyPath = null;
            createdNotifications = deletedNotifications = modififiedNotifications = 0;
        }

        @Override
        public void handleEvent(Path path, WatchEvent event) {
            if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
                createdNotifications += event.count();
                lastCreatePath = (Path) event.context();
            } else if (event.kind() == StandardWatchEventKinds.ENTRY_MODIFY) {
                modififiedNotifications += event.count();
                lastModifyPath = (Path) event.context();
            } else if (event.kind() == StandardWatchEventKinds.ENTRY_DELETE) {
                deletedNotifications += event.count();
                lastDeletePath = (Path) event.context();
            }
        }
    }
}
