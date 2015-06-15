/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
 * @author jakob
 */
public class ParserTest {
    
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
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of addParserListener method, of class Parser.
     */
    @Test
    public void testAddParserListener() {
        System.out.println("addParserListener");
        Parser.ParserListener listener = (ArrayList<Attacker> attackers) -> {throw new UnsupportedOperationException("Not supported yet.");};
        Parser instance = new Parser();
        instance.addParserListener(listener);
        assertTrue("Registering ParserLiseteners works", instance.isParserListenerRegistered(listener));
    }

    /**
     * Test of restart method, of class Parser.
     */
    @Test
    public void testRestart() {
        System.out.println("restart");
        Parser instance = new Parser();
        instance.restart();
        fail("The test case is a prototype.");
    }

    /**
     * Test of stopUpdate method, of class Parser.
     */
    @Test
    public void testStopUpdate() {
        System.out.println("stopUpdate");
        Parser instance = null;
        instance.stopUpdate();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of printToTextArea method, of class Parser.
     */
    @Test
    public void testPrintToTextArea() {
        System.out.println("printToTextArea");
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
        System.out.println("printToSpellTextArea");
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
        System.out.println("getAttackerList");
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
        System.out.println("getPartyList");
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
        System.out.println("displayTotalKills");
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
        System.out.println("displayUniques");
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
        System.out.println("startStopwatch");
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
        System.out.println("stopStopwatch");
        Parser.stopStopwatch();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of addToAttackerList method, of class Parser.
     */
    @Test
    public void testAddToAttackerList() {
        System.out.println("addToAttackerList");
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
        System.out.println("removeFromAttackerList");
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
        System.out.println("clear");
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
        System.out.println("clearParty");
        Parser instance = null;
        instance.clearParty();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
