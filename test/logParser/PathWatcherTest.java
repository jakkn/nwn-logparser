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
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import junit.framework.Assert;
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
    
    private LogObserverTester logObserver;
    private File standardTestFile;
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
        PathWatcher pw = new PathWatcher(Paths.get(TestConstants.TESTDIR));
        logObserver = new LogObserverTester();
        pw.registerObserver(logObserver);
        
        pwThread = new Thread(pw);
        pwThread.start();
    }
    
    @After
    public void tearDown() {
        pwThread.interrupt();
    }

    @Test
    public void verifyFileWrites() {
        int nFileModifications = 100;
        
        writeTo(standardTestFile, nFileModifications);
        
        assertNumberOfWritesEquals(nFileModifications);
    }
    
    private static File createTestFile(String path) throws IOException {
        assert path != null : "file path should not be null";
        assert !path.equals("") : "file path should not be empty";
        
        Path filepath = Paths.get(TestConstants.TESTDIR).resolve(path);
        boolean success = filepath.toFile().createNewFile();
        assert success : "file creation failed: " + filepath.toString();
        return filepath.toFile();
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

    private void writeTo(File file, int nTimes) {
        assert file.canWrite() : "Cannot write to file";
        try {
            tryToWrite(file, nTimes);
        } catch (IOException ex) {
            Logger.getLogger(PathWatcherTest.class.getName()).log(Level.SEVERE, "Filewrite failed during test", ex);
        }
    }
    
//    private void tryToWrite(File file, int nTimes) throws IOException {
//        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
//              new FileOutputStream(file), "utf-8"))) {
//            for (int j = 0; j < nTimes; j++) {
//                writer.write( String.valueOf(j) );
//            }
//        }
//    }
    private void tryToWrite(File file, int nTimes) throws IOException {
        Charset utf8 = StandardCharsets.UTF_8;
        List<String> input = Arrays.asList("testinput");
        for (int j = 0; j < nTimes; j++) {
            Files.write(file.toPath(), input, utf8, StandardOpenOption.WRITE, StandardOpenOption.APPEND, StandardOpenOption.SYNC);
        }
    }

    private void assertNumberOfWritesEquals(int expectedFileModifications) {
        Assert.assertTrue(expectedFileModifications + " writes matches "
                + logObserver.modififiedNotifications + " notifications",
                logObserver.modififiedNotifications == expectedFileModifications);
    }

    private void assertNumberOfCreationsEquals(int expectedFileCreations) {
        Assert.assertTrue(expectedFileCreations + " creations matches "
                + logObserver.createdNotifications + " notifications",
                logObserver.createdNotifications == expectedFileCreations);
    }

    private static class LogObserverTester implements LogObserver {

        private Path lastCreatePath, lastDeletePath, lastModifyPath;
        private int createdNotifications, deletedNotifications, modififiedNotifications;
        
        public LogObserverTester() {
            lastCreatePath = lastDeletePath = lastModifyPath = null;
            createdNotifications = deletedNotifications = modififiedNotifications = 0;
        }

        @Override
        public void newFile(Path path) {
            ++createdNotifications;
            lastCreatePath = path;
        }

        @Override
        public void updatedFile(Path path) {
            ++modififiedNotifications;
            lastModifyPath = path;
        }

        @Override
        public void deletedFile(Path path) {
            ++deletedNotifications;
            lastDeletePath = path;
        }
    }

}
