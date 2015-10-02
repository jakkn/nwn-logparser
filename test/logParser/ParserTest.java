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

import java.util.ArrayList;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Jakob Dagsland Knutsen (JDK)
 */
public class ParserTest {
    
    Parser instance;
    
    public ParserTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        instance = new Parser();
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of addParserListener method, of class Parser.
     */
    @Test
    public void testAddParserListener() {
        Parser.ParserListener listener = (ArrayList<Attacker> attackers) -> {throw new UnsupportedOperationException("Not supported yet.");};
        instance.addParserListener(listener);
        
        assertTrue("Registering ParserLiseteners works", instance.isParserListenerRegistered(listener));
    }

    /**
     * Test of restart method, of class Parser.
     */
    @Test
    public void testRestart() {
        instance.startParsing();
        Thread thread = instance.getParserThread();
        instance.restart();
        Thread thread2 = instance.getParserThread();
        
        assertFalse("Restarting parser kills old thread", thread.isAlive());
        assertTrue("Restarting parser creates new thread", thread != thread2);
        assertTrue("Restarting parser starts new thread", thread2.isAlive());
    }

    /**
     * Test of stopUpdate method, of class Parser.
     */
    @Test
    public void testStopUpdate() {
        Parser instance = new Parser();
        instance.startParsing();
        Thread thread = instance.getParserThread();
        instance.stopUpdate();
        assertTrue("Stopping parser kills thread", !thread.isAlive());
    }

    /**
     * Test of printToTextArea method, of class Parser.
     */
    @Test
    public void testPrintToTextArea() {
        Parser instance = null;
        instance.printToTextArea();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of printToSpellTextArea method, of class Parser.
     */
    @Test
    public void testPrintToSpellTextArea() {
        String text = "";
        Parser instance = null;
        instance.printToSpellTextArea(text);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getAttackerList method, of class Parser.
     */
    @Test
    public void testGetAttackerList() {
        Parser instance = null;
        ArrayList<Attacker> expResult = null;
        ArrayList<Attacker> result = instance.getAttackerList();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getPartyList method, of class Parser.
     */
    @Test
    public void testGetPartyList() {
        Parser instance = null;
        ArrayList<Attacker> expResult = null;
        ArrayList<Attacker> result = instance.getPartyList();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of displayTotalKills method, of class Parser.
     */
    @Test
    public void testDisplayTotalKills() {
        Parser instance = null;
        String expResult = "";
        String result = instance.displayTotalKills();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of displayUniques method, of class Parser.
     */
    @Test
    public void testDisplayUniques() {
        Parser instance = null;
        String expResult = "";
        String result = instance.displayUniques();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of startStopwatch method, of class Parser.
     */
    @Test
    public void testStartStopwatch() {
        String stopwatchPattern = "";
        Parser.startStopwatch(stopwatchPattern);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of stopStopwatch method, of class Parser.
     */
    @Test
    public void testStopStopwatch() {
        Parser.stopStopwatch();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of addToAttackerList method, of class Parser.
     */
    @Test
    public void testAddToAttackerList() {
        String name = "";
        Parser instance = null;
        instance.addToAttackerList(name);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of removeFromAttackerList method, of class Parser.
     */
    @Test
    public void testRemoveFromAttackerList() {
        String name = "";
        Parser instance = null;
        instance.removeFromAttackerList(name);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of clear method, of class Parser.
     */
    @Test
    public void testClear() {
        Parser instance = null;
        instance.clear();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of clearParty method, of class Parser.
     */
    @Test
    public void testClearParty() {
        Parser instance = null;
        instance.clearParty();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
