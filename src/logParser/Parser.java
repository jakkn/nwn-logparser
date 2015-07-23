/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package logParser;

/**
 *
 * @author Jakob
 *
 * *critical hit resisted* in hit calculations?
 *
 * (DONE!) Total deaths and average hp.
 *
 * Damage per round:
 *      Set a timer to count damage on intervals
 *      Timer editable by user.
 *      List with recorded damage outputs.
 *
 * To-do list:
 * - (DONE!) Redo code with RegEx
 * - (DONE!) Set default input file location
 * - Choose what columns to show
 * - (DONE!) Make a uni box
 * - Add support for:
 *      (DONE!) Party member list
 *      (DONE!) Print information box
 *      (DONE!) Sort PartyList
 *      (DONE!) Damage taken table
 *      (DONE!) Miss/Hit/CriticalHit percentage (see above)
 *      (DONE!) AttackerObject AC
 *      (DONE!) Loot
 *      (DONE!) Reflex/Fortitude/Will saves
 *      (DONE!) Spell DC (show all saves made in one window)
 *      People logging in (friend list)
 *      Warning dispells (audio warning?)
 *      Shade every other table row gray
 *      Save GUI size settings
 *      Save table selection
 *      Add print lines
 *      Proper AB stats
 *
 *      Tam Glau: [Tell] 1) will, fort and reflex saves displayed
 *      Tam Glau: [Tell] 2) a "Search logfile" function, where you can filter out all lines with the proper line
 *      Tam Glau: [Tell] 3) there seems to be an error about the total attempts being larger than the sum of the actual hits+autohits+total misses, which shouldn't be
 *      Tam Glau: [Tell] 4) display spell DC
 *      Tam Glau: [Tell] 5) divide by number of times killed for average HP
 *
 * - Send input to server
 */
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.text.Document;

public class Parser {

    private ParserThread thread;
    private ArrayList<Attacker> attackerList = new ArrayList<Attacker>();
    private ArrayList<Attacker> partyList = new ArrayList<Attacker>();
    private AbstractParyListModel abstractParyListModel;
    private int unusableInputLength = 41;
    private ArrayList<String> codeInput;
    private Attacker attackerObject;
    private Attacker attackedObject;
    private int totalKills = 0;
    private int numberOfUniques = 0;
    private StringBuilder lootString = new StringBuilder();
    private StringBuilder serverInfoString = new StringBuilder();
    private final ArrayList<ParserListener> parserListeners = new ArrayList<ParserListener>();
    private volatile boolean shouldStop = false;
    private static UptimeStopwatch stopWatch = null;
    private String loginRegex1 = "\\[.*]\\s\\[.*]\\s";
    private String loginRegex2 = "\\shas\\sjoined\\sas\\sa\\splayer\\.+";

    public interface ParserListener {
        void dataUpdated(ArrayList<Attacker> attackers);
    }

    public Parser() {
        this(new AbstractParyListModel());
    }
    
    @SuppressWarnings("CallToThreadStartDuringObjectConstruction")
    public Parser(AbstractParyListModel partyListModel) {

//        Set up abstractPartyListModel
        abstractParyListModel = partyListModel;
        abstractParyListModel.setList(partyList);

        thread = new ParserThread();
    }
    
    /**
     * Must only be called once. See thread.start()
     */
    public void startParsing(){
        thread.start();
    }
    
    public void restart(){
        shouldStop = true;
        thread = new ParserThread();
        startParsing();
    }
    
    /**
     * For testing
     * @return 
     */
    protected Thread getParserThread(){
        return this.thread;
    }
    
    public void addParserListener(ParserListener listener){
        parserListeners.add(listener);
    }

    /**
     * For testing
     * @param listener
     * @return 
     */
    protected boolean isParserListenerRegistered(ParserListener listener){
        return parserListeners.contains(listener);
    }
    
    public void stopUpdate() {
        shouldStop = true;
        stopStopwatch();
    }

    private void fireDataChanged() {
        for (ParserListener listener : parserListeners) {
            listener.dataUpdated(getAttackerList());
        }
    }

    private class ParserThread extends Thread {

        private File nwClientLogFile;
        private String username;

        private ParserThread() {
            this.nwClientLogFile = ParserView.getCombatLog();
        }

        @Override
        @SuppressWarnings("SleepWhileHoldingLock")
        public void run() {
            try {
                long pos = 0;
                long oldLength = 0;
                username = ParserView.readUsernameFromFile();

                RandomAccessFile fileIn;
                while (!shouldStop) {
                    if (oldLength == nwClientLogFile.length()) {
                        File newNwClientLogFile = ParserView.searchForNewInputFile();
                        if (this.nwClientLogFile != newNwClientLogFile) {
                            nwClientLogFile = newNwClientLogFile;
                            pos = 0;
                            oldLength = 0;
                        }
                    }
                    if (oldLength < nwClientLogFile.length()) {
                        oldLength = nwClientLogFile.length();
                        fileIn = new RandomAccessFile(nwClientLogFile, "r");
                        fileIn.seek(pos);
                        String lineString;
                        while ((lineString = fileIn.readLine()) != null && !shouldStop) {
                            lineString = lineString.trim();
//                            Since the stopwatch is triggered on the first server message, this must be here.
//                            This means that the clock will start and continue to run before the trueLogin check.
//                            To stop the watch a stop method has been added just before the return statement.
                            if(lineString.matches(loginRegex1 + username + loginRegex2)) startStopwatch(lineString);
                            
                            if (lineString.contains("[Tell]") || lineString.contains("[Whisper]") || lineString.contains("[Talk]")) {
                                continue;
                            } else if (lineString.isEmpty()) {
                                continue;
                            } else if (lineString.matches(".*\\].*\\]\\s+.*:\\s\\[Party\\].*")) {
                                if (ParserView.jCheckBoxAutoCheckParty.isSelected()) {
//                                        System.out.println(lineString);
                                    setPartyMember(lineString);
                                } else {
                                    continue;
                                }
                            } else if (lineString.contains("BONUS XP:")) {
                                numberOfUniques++;
                            } else if (lineString.matches(".*\\].*\\]\\s+(.*)\\s+has\\sjust\\slooted\\s+(.*)")) {
//                                    System.out.println(lineString);
                                lootParser(lineString);
                            } else if (lineString.matches(".*\\].*\\]\\s!!\\sLFP\\s!!")) {
                                serverInfoLFPNotificationParser(lineString);
                            } else if (lineString.matches(".*\\].*\\]\\s\\(LFP\\)\\s+.*\\s+\\(\\d+\\)\\sis\\slooking\\sfor\\sa\\sparty\\.\\s+Party\\sType\\:.*")) {
                                serverInfoLFPRequestParser(lineString);
                            } //                                Bottleneck to avoid getting chat input into parsers. Might be unnecessary for attacks, damages, killed and attempts.
                            else if (!lineString.contains("attacks") && !lineString.contains("damage") && !lineString.contains("Damage") && !lineString.contains("kill") && !lineString.contains("xp") && !lineString.contains("attempts") && !lineString.contains("Save") && !lineString.contains("casts")) {
//                                    System.out.println(lineString);
                                continue;
                            } else if (lineString.matches(".*\\].*\\]\\s(.*)\\s+attacks\\s(.*)\\*(.*)\\*(.*)")) {
//                                    System.out.println(lineString);
                                attackParser(lineString);
                            } else if (lineString.matches(".*\\].*\\]\\s.*\\s+damages\\s.*:\\s\\d+\\s\\(.*\\)")) {
//                                    damageParser2 uses regex and sets damage taken. damageParser is the orignal crappy version.
                                damageParser2(lineString);
//                                    System.out.println(lineString);
                            } else if (lineString.matches(".*\\].*\\]\\s+.*\\s+\\:\\sDamage\\sResistance\\sabsorbs\\s\\d+\\sdamage")) {
                                dmgResistanceParser(lineString);
                            } else if (lineString.matches(".*\\].*\\]\\s+.*\\s+\\:\\sDamage\\sReduction\\sabsorbs\\s\\d+\\sdamage")) {
                                dmgReductionParser(lineString);
                            } else if (lineString.matches(".*\\].*\\]\\s+.*\\s+\\:\\sDamage\\sImmunity\\sabsorbs\\s\\d+\\spoint\\(s\\)\\sof\\s.*")) {
                                dmgImmunityParser(lineString);
                            } else if (lineString.matches(".*\\].*\\]\\s.*\\s+killed\\s+.*")) {
//                                    killParser2 uses regex and sets both kill and death. killParser is the original crappy version.
                                killParser2(lineString);
                            } else if (lineString.matches(".*\\].*\\]\\s.*\\s:\\s(Reflex|Will|Fortitude)\\sSave.*:\\s\\*(success|failure).*")) {
                                savingThrowParser(lineString);
//                                    System.out.println(lineString);
                            } else if (lineString.contains("casts")) {
//                                    System.out.println(lineString);
                                printToSpellTextArea(lineString);
                            } else if (lineString.contains("xp)")) {
                                xpParser(lineString);
//                                    System.out.println(lineString);
                            } else if (lineString.matches(".*\\].*\\]\\s(.*)\\s+attempts\\s(.*)\\*(.*)\\*(.*)") && !lineString.contains("resist spell")) {
//                                else if(lineString.contains("attempts") && !lineString.contains("resist spell")) {
//                                    System.out.println(lineString);
                                attemptParser(lineString);
                            }
                        }
                        fireDataChanged();
                        pos = fileIn.getFilePointer();
                        fileIn.close();
                    }
                    Thread.sleep(1000);
                }
            } catch (Exception ex) {
                Logger.getLogger(Parser.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    private Comparator<Attacker> DamageComparator         = (Attacker attacker, Attacker anotherAttacker) -> anotherAttacker.getTotalDamage() - attacker.getTotalDamage();
    private Comparator<Attacker> DamagePerHitComparator   = (Attacker attacker, Attacker anotherAttacker) -> anotherAttacker.getAverageDamage() - attacker.getAverageDamage();
    private Comparator<Attacker> MaxHitComparator         = (Attacker attacker, Attacker anotherAttacker) -> anotherAttacker.getMaxNormalHit() - attacker.getMaxNormalHit();
    private Comparator<Attacker> KillsComparator          = (Attacker attacker, Attacker anotherAttacker) -> anotherAttacker.getKills() - attacker.getKills();
    private Comparator<Attacker> XPComparator             = (Attacker attacker, Attacker anotherAttacker) -> anotherAttacker.getXpGained() - attacker.getXpGained();
    private Comparator<Attacker> DamageTakenComparator    = (Attacker attacker, Attacker anotherAttacker) -> anotherAttacker.getTotalDamageTaken() - attacker.getTotalDamageTaken();
    private Comparator<Attacker> MaxDamageTakenComparator = (Attacker attacker, Attacker anotherAttacker) -> anotherAttacker.getMaxDamageTaken() - attacker.getMaxDamageTaken();
    private Comparator<Attacker> MaxAbComparator          = (Attacker attacker, Attacker anotherAttacker) -> anotherAttacker.getMaxAB() - attacker.getMaxAB();
    private Comparator<Attacker> AbComparator             = (Attacker attacker, Attacker anotherAttacker) -> anotherAttacker.getABCompare() - attacker.getABCompare();
    private Comparator<Attacker> AcComparator             = (Attacker attacker, Attacker anotherAttacker) -> anotherAttacker.getArmorClass() - attacker.getArmorClass();
    
    private Comparator<Attacker> PercentageHitComparator = (Attacker attacker, Attacker anotherAttacker) -> {
        String s1 = anotherAttacker.percentageHit();
        String s2 = attacker.percentageHit();
        return (int) (Double.parseDouble(s1.replaceAll(",", ".").substring(0, s1.length()-1)) - Double.parseDouble(s2.replaceAll(",", ".").substring(0, s2.length()-1)));
    };
    private Comparator<Attacker> PercentageCriticalHitOfHitsComparator = (Attacker attacker, Attacker anotherAttacker) -> {
        String s1 = anotherAttacker.percentageCriticalHitOfHits();
        String s2 = attacker.percentageCriticalHitOfHits();
        return (int) (Double.parseDouble(s1.replace(",", ".").substring(0, s1.length()-1)) - Double.parseDouble(s2.replace(",", ".").substring(0, s2.length()-1)));
    };
    private Comparator<Attacker> PercentageMissComparator = (Attacker attacker, Attacker anotherAttacker) -> {
        String s1 = anotherAttacker.percentageMissOfTotal();
        String s2 = attacker.percentageMissOfTotal();
        return (int) (Double.parseDouble(s1.replace(",", ".").substring(0, s1.length()-1)) - Double.parseDouble(s2.replace(",", ".").substring(0, s2.length()-1)));
    };
    private Comparator<Attacker> PercentageConcealComparator = (Attacker attacker, Attacker anotherAttacker) -> {
        String s1 = anotherAttacker.percentageConceal();
        String s2 = attacker.percentageConceal();
        return (int) (Double.parseDouble(s1.replace(",", ".").substring(0, s1.length()-1)) - Double.parseDouble(s2.replace(",", ".").substring(0, s2.length()-1)));
    };

    public void printToTextArea() {
        StringBuilder outputString = new StringBuilder();
        Collections.sort(partyList, DamageComparator);
        outputString.append("--==DAMAGE==-- ");
        for (Attacker attacker : partyList) {
            outputString.append(attacker.getName()).append(": ").append(attacker.getTotalDamage()).append(" | ");
        }
        outputString.delete(outputString.length() - 3, outputString.length() - 1);
        Collections.sort(partyList, DamagePerHitComparator);
        outputString.append("\n");
        outputString.append("--==DAMAGE PER HIT==-- ");
        for (Attacker attacker : partyList) {
            outputString.append(attacker.getName()).append(": ").append(attacker.getAverageDamage()).append(" | ");
        }
        outputString.delete(outputString.length() - 3, outputString.length() - 1);
        Collections.sort(partyList, MaxHitComparator);
        outputString.append("\n");
        outputString.append("--==MAX HIT==-- ");
        for (Attacker attacker : partyList) {
            outputString.append(attacker.getName()).append(": ").append(attacker.getMaxNormalHit()).append(" | ");
        }
        outputString.delete(outputString.length() - 3, outputString.length() - 1);
        Collections.sort(partyList, KillsComparator);
        outputString.append("\n");
        outputString.append("--==KILLS==-- ");
        for (Attacker attacker : partyList) {
            outputString.append(attacker.getName()).append(": ").append(attacker.getKills()).append(" | ");
        }
        outputString.delete(outputString.length() - 3, outputString.length() - 1);
        Collections.sort(partyList, XPComparator);
        outputString.append("\n");
        outputString.append("--==XP==-- ");
        for (Attacker attacker : partyList) {
            outputString.append(attacker.getName()).append(": ").append(attacker.getXpGained()).append(" | ");
        }
        outputString.delete(outputString.length() - 3, outputString.length() - 1);
        Collections.sort(partyList, DamageTakenComparator);
        outputString.append("\n");
        outputString.append("--==DAMAGE TAKEN==-- ");
        for (Attacker attacker : partyList) {
            outputString.append(attacker.getName()).append(": ").append(attacker.getTotalDamageTaken()).append(" | ");
        }
        outputString.delete(outputString.length() - 3, outputString.length() - 1);
        Collections.sort(partyList, MaxDamageTakenComparator);
        outputString.append("\n");
        outputString.append("--==MAX HIT TAKEN==-- ");
        for (Attacker attacker : partyList) {
            outputString.append(attacker.getName()).append(": ").append(attacker.getMaxDamageTaken()).append(" | ");
        }
        outputString.delete(outputString.length() - 3, outputString.length() - 1);
        Collections.sort(partyList, AbComparator);
        outputString.append("\n");
        outputString.append("--==AB==-- ");
        for (Attacker attacker : partyList) {
            outputString.append(attacker.getName()).append(": ").append(attacker.getAB()).append(" | ");
        }
        outputString.delete(outputString.length() - 3, outputString.length() - 1);
        Collections.sort(partyList, MaxAbComparator);
        outputString.append("\n");
        outputString.append("--==MAX AB==-- ");
        for (Attacker attacker : partyList) {
            outputString.append(attacker.getName()).append(": ").append(attacker.getMaxAB()).append(" | ");
        }
        outputString.delete(outputString.length() - 3, outputString.length() - 1);
        Collections.sort(partyList, AcComparator);
        outputString.append("\n");
        outputString.append("--==MOST RECENT AC==-- ");
        for (Attacker attacker : partyList) {
            outputString.append(attacker.getName()).append(": ").append(attacker.getArmorClass()).append(" | ");
        }
        outputString.delete(outputString.length() - 3, outputString.length() - 1);
        Collections.sort(partyList, PercentageHitComparator);
        outputString.append("\n");
        outputString.append("--==HIT PERCENTAGE==-- ");
        for (Attacker attacker : partyList) {
            outputString.append(attacker.getName()).append(": ").append(attacker.percentageHit()).append(" | ");
        }
        outputString.delete(outputString.length() - 3, outputString.length() - 1);
        Collections.sort(partyList, PercentageCriticalHitOfHitsComparator);
        outputString.append("\n");
        outputString.append("--==CRITICAL HIT PERCENTAGE==-- ");
        for (Attacker attacker : partyList) {
            outputString.append(attacker.getName()).append(": ").append(attacker.percentageCriticalHitOfHits()).append(" | ");
        }
        outputString.delete(outputString.length() - 3, outputString.length() - 1);
        Collections.sort(partyList, PercentageMissComparator);
        outputString.append("\n");
        outputString.append("--==MISS PERCENTAGE==-- ");
        for (Attacker attacker : partyList) {
            outputString.append(attacker.getName()).append(": ").append(attacker.percentageMissOfTotal()).append(" | ");
        }
        outputString.delete(outputString.length() - 3, outputString.length() - 1);
        Collections.sort(partyList, PercentageConcealComparator);
        outputString.append("\n");
        outputString.append("--==CONCEALMENT PERCENTAGE==-- ");
        for (Attacker attacker : partyList) {
            outputString.append(attacker.getName()).append(": ").append(attacker.percentageConceal()).append(" | ");
        }
        outputString.delete(outputString.length() - 3, outputString.length() - 1);
        ParserView.jTextAreaPrintInfo.setText(outputString.toString());
    }

    /**
     * Method to print spell casting info and saving throws. Modifies the timestamp.
     * @param text
     */
    public void printToSpellTextArea(String text) {
        Pattern LINE_PATTERN = Pattern.compile(".*\\](.*\\].*)");
        Matcher m = LINE_PATTERN.matcher(text);
        String outputText = m.replaceAll("$1");
        outputText = outputText.trim();
        ParserView.jTextAreaSpellInfo.append(outputText + "\n");

//        Scroll to end
        Document doc = ParserView.jTextAreaSpellInfo.getDocument();
        ParserView.jTextAreaSpellInfo.setCaretPosition(doc.getLength());
    }

    /**
     * Method to format loot info properly with HTML tags and call print.
     * @param text
     */
    private void lootParser(String text) {
//        [CHAT WINDOW TEXT] [Sat Apr 03 11:53:45] Mathan Kelter has just looted  a gem.

        Pattern LINE_PATTERN = Pattern.compile(".*\\](.*\\])(.*)\\s+has\\sjust\\slooted\\s+(.*)");
        Matcher m = LINE_PATTERN.matcher(text);
        lootString.append("<font color=gray>").append(m.replaceAll("$1")).append("</font><font color=blue>").append(m.replaceAll("$2")).
                append("</font> has just looted <font color=green>").append(m.replaceAll("$3")).append("</font><br></br>");
        appendTextToJTextPaneLoot(); //call print
    }

    /**
     * Method to format LFP requests properly with HTML tags and call print.
     * @param text
     */
    private void serverInfoLFPRequestParser(String text) {
//        [CHAT WINDOW TEXT] [Sun Jun 05 11:29:59] (LFP) Roman de Renart (23) is looking for a party.  Party Type: Any

        Pattern LINE_PATTERN = Pattern.compile(".*\\](.*\\])(\\s.*\\)\\s+)(.*)(\\s+\\(\\d+\\))(.*:\\s)(\\w+)");
        Matcher m = LINE_PATTERN.matcher(text);
        serverInfoString.append("<font color=gray>").append(m.replaceAll("$1")).append("</font>").append(m.replaceAll("$2")).
                append("<font color=blue>").append(m.replaceAll("$3")).append("</font><font color=green>").append(m.replaceAll("$4")).
                append("</font>").append(m.replaceAll("$5")).append("<font color=red>").append(m.replaceAll("$6")).append("</font><br></br>");
        appendTextToJTextPaneServerInfo(); //call print
    }

    /**
     * Method to format LFP notification properly with HTML tags and call print.
     * @param text
     */
    private void serverInfoLFPNotificationParser(String text) {
//        [CHAT WINDOW TEXT] [Sun Jun 05 11:29:59] !! LFP !!

        Pattern LINE_PATTERN = Pattern.compile(".*\\](.*\\])(.*)");
        Matcher m = LINE_PATTERN.matcher(text);
        serverInfoString.append("<font color=gray>").append(m.replaceAll("$1")).append("</font>").append(m.replaceAll("$2")).append("<br></br>");
        appendTextToJTextPaneServerInfo(); //call print
    }

    /**
     * Custom method that replace the whole window content.
     * Is only set to work on jTextPaneLoot.
     */
    private void appendTextToJTextPaneLoot() {
        Document doc = ParserView.jTextPaneLoot.getDocument();
//        JFrameParser.jTextPaneLoot.replaceSelection(textToAppend + "\n");
        ParserView.jTextPaneLoot.setText(lootString.toString());
        ParserView.jTextPaneLoot.setCaretPosition(doc.getLength());
    }

    /**
     * Custom method that replace the whole window content.
     * Is only set to work on jTextPaneServerInfo.
     */
    private void appendTextToJTextPaneServerInfo() {
        Document doc = ParserView.jTextPaneServerInfo.getDocument();
//        JFrameParser.jTextPaneLoot.replaceSelection(textToAppend + "\n");
        ParserView.jTextPaneServerInfo.setText(serverInfoString.toString());
        ParserView.jTextPaneServerInfo.setCaretPosition(doc.getLength());
    }

    private int getNumberOfUniques() {
        return numberOfUniques;
    }

    private void clearNumberOfUniques() {
        numberOfUniques = 0;
    }

    private int getTotalKills() {
        return totalKills;
    }

    private void increaseTotalKills() {
        totalKills++;
    }

    private void clearTotalKills() {
        totalKills = 0;
    }

    private void clearTextAreas() {
        ParserView.jTextAreaPrintInfo.setText("");
        ParserView.jTextAreaSpellInfo.setText("");
        ParserView.jTextPaneLoot.setText("");
        ParserView.jTextPaneServerInfo.setText("");
    }

    public ArrayList<Attacker> getAttackerList() {
        return attackerList;
    }

    public ArrayList<Attacker> getPartyList() {
        return partyList;
    }

    public String displayTotalKills() {
        if (getTotalKills() != 0) {
            return "Total Kills: " + String.valueOf(getTotalKills());
        } else {
            return "Total Kills: 0";
        }
    }

    public String displayUniques() {
        if (getNumberOfUniques() != 0) {
            return "Uniques: " + String.valueOf(getNumberOfUniques());
        } else {
            return "Uniques: 0";
        }
    }

    public static void startStopwatch(String stopwatchPattern){
        if(stopWatch == null) stopWatch = new UptimeStopwatch(stopwatchPattern);
        else stopWatch.restart(stopwatchPattern);
    }
    
    public static void stopStopwatch(){
        stopWatch.stop();
    }
    
    /**
     * @deprecated  As of release 1.09, no longer used.
     */
    private void deleteUnusableCharacters(StringBuilder lineInput, String charToDelete) {
        while (lineInput.indexOf(charToDelete) > 0) {
            lineInput.deleteCharAt(lineInput.indexOf(charToDelete));
        }
    }

    /**
     * @deprecated  As of release 1.09, no longer used.
     */
    private void replaceUnusableCharacters(String lineInput, String charToDelete, String charToReplace) {
        while (lineInput.indexOf(charToDelete) > 0) {
            lineInput.replaceAll(charToDelete, charToReplace);
        }
    }

    /**
     * 
     * @deprecated  As of release 1.09, no longer used.
     */
    private void deleteUnusableInput(StringBuilder stringBuilder) {
        stringBuilder.delete(0, unusableInputLength);
    }

    private void splitAndFillArray(StringBuilder stringBuilder) {
        String str = stringBuilder.toString();
        String[] input = str.split("\\ ");
        codeInput.addAll(Arrays.asList(input));
//        for (int i = 0; i < input.length; i++) {
//            codeInput.add(input[i]);
//        }
    }

    /**
     * Only sets damage dealt.
     * @deprecated  As of release 1.09, replaced by assignDamage2
     * @param attackerObject
     * @param inputType (damage type)
     * @param inputNumber (amount of damage)
     */
    private void assignDamage(Attacker object, String inputType, int inputNumber) {
        if (inputType.matches("Physical")) {
            object.setPhysicalDamage(inputNumber);
        } else if (inputType.matches("Fire")) {
            object.setFireDamage(inputNumber);
        } else if (inputType.matches("Magical")) {
            object.setMagicalDamage(inputNumber);
        } else if (inputType.matches("Divine")) {
            object.setDivineDamage(inputNumber);
        } else if (inputType.matches("Acid")) {
            object.setAcidDamage(inputNumber);
        } else if (inputType.matches("Cold")) {
            object.setColdDamage(inputNumber);
        } else if (inputType.matches("Sonic")) {
            object.setSonicDamage(inputNumber);
        } else if (inputType.matches("Electrical")) {
            object.setElectricalDamage(inputNumber);
        } else if (inputType.matches("Positive")) {
            object.setPositiveDamage(inputNumber);
        } else if (inputType.matches("Negative")) {
            object.setNegativeDamage(inputNumber);
        }
    }

    private String findName(String action, String name, int numberOfNames) {
        numberOfNames = codeInput.indexOf(action);
        for (int i = 0; i < numberOfNames; i++) {
            name += codeInput.get(i) + " ";
        }
        name = name.trim();
        return name;
    }

    private void findAttackerObject(String name) {
        if (attackerList.isEmpty()) {
            Attacker attacker = new Attacker(name);
            attackerList.add(attacker);
            this.attackerObject = attacker;
            return;
        }
        boolean exists = false;
        for (Attacker attacker : attackerList) {
            if (attacker.getName().equals(name)) {
                exists = true;
                this.attackerObject = attacker;
                return;
            }
        }
        if (!exists) {
            attackerList.add(new Attacker(name));
            this.attackerObject = attackerList.get(attackerList.size() - 1);
        }
    }

    private void findAttackedObject(String name) {
        if (attackerList.isEmpty()) {
            Attacker attacker = new Attacker(name);
            attackerList.add(attacker);
            this.attackedObject = attacker;
            return;
        }
        boolean exists = false;
        for (Attacker attacker : attackerList) {
            if (attacker.getName().equals(name)) {
                exists = true;
                this.attackedObject = attacker;
                return;
            }
        }
        if (!exists) {
            attackerList.add(new Attacker(name));
            this.attackedObject = attackerList.get(attackerList.size() - 1);
        }
    }

    private void compareAttackerAndPartyList(String name) {
        if (attackerList.isEmpty()) {
            Attacker attacker = new Attacker(name);
            attackerList.add(attacker);
            abstractParyListModel.add(attacker);
            return;
        }
        boolean exists = false;
        for (Attacker attacker : attackerList) {
            if (attacker.getName().equals(name)) {
                abstractParyListModel.add(attacker);
                return;
            }
        }
        if (!exists) {
            Attacker attacker = new Attacker(name);
            attackerList.add(attacker);
            abstractParyListModel.add(attacker);
        }
    }

    public void addToAttackerList(String name) {
        if (partyList.isEmpty()) {
            compareAttackerAndPartyList(name);
        }
        boolean exists = false;
        for (Attacker attacker : partyList) {
            if (attacker.getName().equals(name)) {
                exists = true;
                break;
            }
        }
        if (!exists) {
            compareAttackerAndPartyList(name);
        }
        fireDataChanged();
    }

    public void removeFromAttackerList(String name) {
        if (!partyList.isEmpty()) {
            for (Attacker attacker : partyList) {
                if (attacker.getName().equals(name)) {
                    abstractParyListModel.remove(attacker);
                    break;
                }
            }
        }
        fireDataChanged();
    }

    public void clear() {
        if (attackerList != null) {
            attackerList.clear();
        }
        if (partyList != null) {
            abstractParyListModel.clear();
        }
        if (codeInput != null) {
            codeInput.clear();
        }
        clearNumberOfUniques();
        clearTotalKills();
        clearTextAreas();
        stopStopwatch();
        fireDataChanged();
    }

    public void clearParty() {
        if (partyList != null) {
            abstractParyListModel.clear();
        }
        fireDataChanged();
    }

    @SuppressWarnings("unchecked")
    private void swapTwoLastIndexes(ArrayList list, int intToSwap, int intToSwapWith) {
        list.add(intToSwap, list.get(intToSwapWith));
        list.remove(list.size() - 1);
    }

    private void checkAttackRoll(int i) {
        if (i == 20) {
            attackerObject.setAutoHit();
        } else if (i == 1) {
            attackerObject.setAutoMiss();
        }
    }

    private void checkAttackBonus(int i) {
        attackerObject.registerAttackBonus(i);
    }

    private void checkTotalAB(int totalAB) {
        attackerObject.registerTotalAB(totalAB);
    }

    private void registerArmorClass(int totalAB, int attackRoll, String hitValue) {
        if (attackRoll > 1 && attackRoll < 20) {
            attackedObject.registerArmorClass(totalAB, hitValue);
        }
    }

    /**
     * Registers type of hit to both attacker and attacked. The possibilities are:
     * <ul>
     * <li>hit</li>
     * <li>miss</li>
     * <li>critical hit</li>
     * <li>target concealed</li>
     * <li>attacker miss chance</li>
     * <li>failed</li>
     * <li>resisted</li>
     * </ul>
     * The checks have been ordered according to the most frequent ones in ascending order for optimized efficiency.
     * @param string
     * @return concealment boolean
     */
    private boolean checkAndRegisterHit(String string) {
        boolean concealed = false;
        if (string.matches("hit")) {                          //should be most frequent
            attackerObject.setHasHit();
            attackedObject.setWasHit();
        } else if (string.matches("miss")) {                  //should be second most frequent
            attackerObject.setHasMissed();
            attackedObject.setWasMissed();
        } else if (string.matches("critical hit")) {          //should be thrid most frequent
            attackerObject.setCriticalHit();
            attackedObject.setWasHit();
        } else if (string.contains("target concealed")) {     //should be fourth most frequent
            attackerObject.setConcealedOutgoingAttacks();
            attackedObject.setConcealedIncomingAttacks();
            concealed = true;
        } else if (string.contains("attacker miss chance")) { //should be fifth most frequent
            attackerObject.setConcealedOutgoingAttacks();
            attackedObject.setConcealedIncomingAttacks();
            concealed = true;
        } else if (string.matches("failed")) {                //should be less frequent than any of the above
            attackerObject.setHasHit();
            attackedObject.setWasHit();
        } else if (string.matches("resisted")) {              //should be less frequent than any of the above
            attackerObject.setHasHit();
            attackedObject.setWasHit();
        }
        attackerObject.setNumberOfOutgoingAttacks();          //must be run everytime after the above to increment total number of attacks
        attackedObject.setNumberOfIncomingAttacks();          //must be run everytime after the above to increment total number of attacks
        return concealed;
    }

    private void setPartyMember(String lineString) {
        Pattern PARTY_CHAT_PATTERN = Pattern.compile(".*\\].*\\]\\s+(.*):\\s\\[Party\\].*");
        Matcher m = PARTY_CHAT_PATTERN.matcher(lineString);
        String name = m.replaceAll("$1");
        name = name.trim();
        findAttackerObject(name);
        boolean existsInPartyList = false;
        for (Attacker attacker : partyList) {
            if (attacker.getName().equals(name)) {
                existsInPartyList = true;
                break;
            }
        }
        if (!existsInPartyList) {
            abstractParyListModel.add(attackerObject);
        }

//        String input = lineString;
//        String regex = ".*\\].*\\]\\s+(.*):\\s\\[Party\\].*";  //
//        String name = input.replaceAll(regex, "$0");
//        findAttackerObject(name);
//        abstractParyListModel.add(attackerObject);

    }

    private String findNameInSpecialAttacks(String stringLine) {
        stringLine = stringLine.trim();
        String name = "";
        if (stringLine.contains(":")) {
            Pattern PARTY_CHAT_PATTERN = Pattern.compile(".*:\\s+(.*)");
            Matcher m = PARTY_CHAT_PATTERN.matcher(stringLine);
            return name = m.replaceAll("$1");
        } else {
            return name = stringLine;
        }
//        return name;
    }

    /**
     * @deprecated  As of release 1.09, replaced by damageParser2
     */
    private void damageParser(String inputLines) throws IOException {
        codeInput = new ArrayList<String>();
        StringBuilder lines = new StringBuilder();
        lines.append(inputLines);
        int numberOfNames = 0;
        String name = "";

//	This discards of all unusable characters defined below
        deleteUnusableInput(lines);
        deleteUnusableCharacters(lines, "(");
        deleteUnusableCharacters(lines, ")");
        deleteUnusableCharacters(lines, ":");

//	This splits lines into a usable String array and fills the codeInput ArrayList with the array
        splitAndFillArray(lines);

//	This finds the name of attacker and the number of names altogether
        name = findName("damages", name, numberOfNames);

//	This searchers for the current attackerObject in attackerList. If not present, a new attacker is created.
        findAttackerObject(name);

//      Record the amount of damage and compare/set it as a max hit
        for (int i = numberOfNames; i < codeInput.size(); i++) {
            if (codeInput.get(i).toString().matches("[\\d]{1,4}")) {
                int tempHit = Integer.parseInt(codeInput.get(i));
                this.attackerObject.setMaxNormalHit(tempHit);
                return;
            }
        }

//	This sets damage input of the correct type to the attackerObject
        for (int i = numberOfNames; i < codeInput.size(); i++) {
            if (codeInput.get(i).toString().matches("[\\d]{1,4}")) {
                int inputNumber = Integer.parseInt(codeInput.get(i));
                String inputType = codeInput.get(i + 1);
                assignDamage(this.attackerObject, inputType, inputNumber);
            }
        }
        this.attackerObject.setTotalDamage();
    }

    private void killParser2(String inputLines) {
        Pattern ATTACK_LINE_PATTERN = Pattern.compile(".*\\].*\\]\\s+(.*)\\s+killed\\s+(.*)");
        Matcher m = ATTACK_LINE_PATTERN.matcher(inputLines);
//        Find killer
        String killerName = m.replaceAll("$1");
        killerName = killerName.trim();
        findAttackerObject(killerName);

        increaseTotalKills();
        this.attackerObject.incrementKills();

//        Find object that is killed
        String killedName = m.replaceAll("$2");
        killedName = killedName.trim();
        findAttackedObject(killedName);

        this.attackedObject.incrementDeaths();
    }

    private void savingThrowParser(String inputLines) {
        printToSpellTextArea(inputLines);
        Pattern ATTACK_LINE_PATTERN;
        /**
         * If-sentence to guard against Evasion and Improved Evasion.
         * Used to create objects named something like "Elf Mercenary : Improved Evasion" before this was implemented.
         * Anyone with Evasion in their name will mess up the parser at this point, but hopefully this will not be an issue.
         */
        if(inputLines.contains("Evasion")) ATTACK_LINE_PATTERN = Pattern.compile(".*\\].*\\]\\s+(.*)\\s+:.*:\\s(Reflex|Will|Fortitude)\\sSave.*:\\s\\*(success|failure)\\*\\s:\\s\\(\\d+\\s[+-]\\s(\\d+)\\s=\\s\\d+\\svs\\.\\sDC:\\s(\\d+)\\)");
        else ATTACK_LINE_PATTERN = Pattern.compile(".*\\].*\\]\\s+(.*)\\s+:\\s(Reflex|Will|Fortitude)\\sSave.*:\\s\\*(success|failure)\\*\\s:\\s\\(\\d+\\s[+-]\\s(\\d+)\\s=\\s\\d+\\svs\\.\\sDC:\\s(\\d+)\\)");
        Matcher m = ATTACK_LINE_PATTERN.matcher(inputLines);
//        Find object
        String objectName = m.replaceAll("$1");
        objectName = objectName.trim();
        findAttackerObject(objectName);

//        Type of save and value
        String saveType = m.replaceAll("$2");
        String outcome = m.replaceAll("$3");
        int save = Integer.parseInt(m.replaceAll("$4"));

        if (saveType.contains("Fortitude")) {
            this.attackerObject.setFortitudeSave(save);
        } else if (saveType.contains("Reflex")) {
            this.attackerObject.setReflexSave(save);
        } else if (saveType.contains("Will")) {
            this.attackerObject.setWillSave(save);
        }

        if (outcome.contains("success")) {
            this.attackerObject.incrementSuccessSave();
        } else if (outcome.contains("failure")) {
            this.attackerObject.incrementFailureSave();
        }
    }

    /**
     * @deprecated  As of release 1.09, replaced by killParser2
     * @param inputLines 
     */
    private void killParser(String inputLines) {
        codeInput = new ArrayList<String>();
        StringBuilder lines = new StringBuilder();
        lines.append(inputLines);
        int numberOfNames = 0;
        String name = "";

//	This discards of all unusable characters defined below
        deleteUnusableInput(lines);

//	This splits lines into a usable String array and fills the codeInput ArrayList with the array
        splitAndFillArray(lines);

//	This finds the name of attacker and the number of names altogether
        name = findName("killed", name, numberOfNames);

//	This searchers for the current attackerObject in attackerList. If not present, a new attacker is created.
        findAttackerObject(name);

        increaseTotalKills();
        this.attackerObject.incrementKills();
    }

    private void xpParser(String inputLines) {
        codeInput = new ArrayList<String>();
        StringBuilder lines = new StringBuilder();
        lines.append(inputLines);
        int numberOfNames = 0;
        String name = "";
        String temporaryString = "";
        int xp = 0;

//		This discards of all unusable characters defined below and adds space where necessary
        deleteUnusableInput(lines);
        deleteUnusableCharacters(lines, ":");
        deleteUnusableCharacters(lines, "(");
        temporaryString = lines.toString().replaceAll("\\)", " ");
        lines.delete(0, lines.length());
        lines.append(temporaryString);
        for (int i = 0; i < lines.length(); i++) {
            if (("" + lines.charAt(i)).toString().matches("[\\d]") && ("" + lines.charAt(i + 1)).toString().matches("[\\D]")) {
                lines.insert(i + 1, ' ');
                lines.delete(i + 4, lines.length());
                break;
            }
        }

//		This splits lines into a usable String array and fills the codeInput ArrayList with the array
        splitAndFillArray(lines);

//		This swaps the two last indexes to make the array readable by the following methods
        swapTwoLastIndexes(codeInput, codeInput.size() - 2, codeInput.size() - 1);

//		This finds the name of attacker and the number of names altogether
        name = findName("xp", name, numberOfNames);

//		This searchers for the current attackerObject in attackerList. If not present, a new attacker is created
        findAttackerObject(name);

//		This sets xp gained to the current attacker object
        xp = Integer.parseInt(codeInput.get(codeInput.size() - 1).toString());
        this.attackerObject.setXpGained(xp);
    }

    private void parseDamageType(String input) {
//        Split input on "space" and fill it in inputArray
        ArrayList<String> damageInput = new ArrayList<String>();
        damageInput.addAll(Arrays.asList(input.split("\\ ")));

//        Locate number and corresponding damage type and send it to be assigned
        for (int i = 0; i < damageInput.size(); i++) {
            if (damageInput.get(i).toString().matches("[\\d]+")) {
                int inputNumber = Integer.parseInt(damageInput.get(i));
                String inputType = damageInput.get(i + 1);
                assignDamage2(inputNumber, inputType);
            }
        }
    }

    /**
     * Assigns damage dealt to attackerObject and damage taken to attackedObject
     * 
     * @param inputNumber (amount of damage)
     * @param inputType (type of damage)
     */
    private void assignDamage2(int inputNumber, String inputType) {
//        Assign damage dealt to attackerObject and damage taken to attackedObject
        if (inputType.matches("Physical")) {
            attackerObject.setPhysicalDamage(inputNumber);
            attackedObject.setPhysicalDamageTaken(inputNumber);
        } else if (inputType.matches("Fire")) {
            attackerObject.setFireDamage(inputNumber);
            attackedObject.setFireDamageTaken(inputNumber);
        } else if (inputType.matches("Magical")) {
            attackerObject.setMagicalDamage(inputNumber);
            attackedObject.setMagicalDamageTaken(inputNumber);
        } else if (inputType.matches("Divine")) {
            attackerObject.setDivineDamage(inputNumber);
            attackedObject.setDivineDamageTaken(inputNumber);
        } else if (inputType.matches("Acid")) {
            attackerObject.setAcidDamage(inputNumber);
            attackedObject.setAcidDamageTaken(inputNumber);
        } else if (inputType.matches("Cold")) {
            attackerObject.setColdDamage(inputNumber);
            attackedObject.setColdDamageTaken(inputNumber);
        } else if (inputType.matches("Sonic")) {
            attackerObject.setSonicDamage(inputNumber);
            attackedObject.setSonicDamageTaken(inputNumber);
        } else if (inputType.matches("Electrical")) {
            attackerObject.setElectricalDamage(inputNumber);
            attackedObject.setElectricalDamageTaken(inputNumber);
        } else if (inputType.matches("Positive")) {
            attackerObject.setPositiveDamage(inputNumber);
            attackedObject.setPositiveDamageTaken(inputNumber);
        } else if (inputType.matches("Negative")) {
            attackerObject.setNegativeDamage(inputNumber);
            attackedObject.setNegativeDamageTaken(inputNumber);
        }
    }

    private void damageParser2(String inputLines) {
        Pattern ATTACK_LINE_PATTERN = Pattern.compile(".*\\].*\\]\\s+(.*)\\s+damages\\s+(.*):\\s(\\d+)\\s\\((.*)\\)");
        Matcher m = ATTACK_LINE_PATTERN.matcher(inputLines);
//        Find attacker
        String attackerName = m.replaceAll("$1");
        attackerName = attackerName.trim();
        findAttackerObject(attackerName);
//        Find object that is being attacked
        String attackedName = m.replaceAll("$2");
        attackedName = attackedName.trim();
        findAttackedObject(attackedName);

//      Record the amount of damage and send it to method that deals with it appropriately
        int i = Integer.parseInt(m.replaceAll("$3"));
        attackerObject.setMaxNormalHit(i);
        attackedObject.setMaxDamageTaken(i);

//	Set damage input of the correct type to the attackerObject
        parseDamageType(m.replaceAll("$4"));

        attackerObject.setTotalDamage();
        attackedObject.setTotalDamageTaken();
    }

    private void attackParser(String inputLines) {
        Pattern ATTACK_LINE_PATTERN = Pattern.compile(".*\\].*\\]\\s+(.*)\\s+attacks\\s+(.*)\\:\\s\\*(.*)\\*\\s:\\s\\((\\d+)\\s[+-]\\s(\\d+)\\s\\=\\s-?(\\d+)[\\s:ThreatRoll\\d\\+\\-\\=\\)]+");
        Matcher m = ATTACK_LINE_PATTERN.matcher(inputLines);
        String attackerName = findNameInSpecialAttacks(m.replaceAll("$1"));
        String attackedName = findNameInSpecialAttacks(m.replaceAll("$2"));
        attackerName = attackerName.trim();
        attackedName = attackedName.trim();
        findAttackerObject(attackerName);
        findAttackedObject(attackedName);

        String hitValue = m.replaceAll("$3");
        boolean concealed = checkAndRegisterHit(hitValue);    //set hit/miss/crit/concealed and capture concealed boolean
        int attackRoll = Integer.parseInt(m.replaceAll("$4"));
        if (!concealed) {
            checkAttackRoll(attackRoll);
        }
        checkAttackBonus(Integer.parseInt(m.replaceAll("$5")));
        int totalAB = Integer.parseInt(m.replaceAll("$6"));
        checkTotalAB(totalAB);
        registerArmorClass(totalAB, attackRoll, hitValue);
    }

    private void attemptParser(String inputLines) {
//        [CHAT WINDOW TEXT] [Wed Jun 08 11:05:46] Half-Orc Bandit attempts Knockdown on Tyrmons Minion : *hit* : (13 + 35 = 48)
        Pattern ATTACK_LINE_PATTERN = Pattern.compile(".*\\].*\\]\\s+(.*)\\s+attempts\\s.*\\son(.*)\\s+:\\s\\*(.*)\\*\\s:\\s\\((\\d+)\\s[+-]\\s(\\d+)\\s\\=\\s-?(\\d+)[\\s:ThreatRoll\\d\\+\\-\\=\\)]+");
        Matcher m = ATTACK_LINE_PATTERN.matcher(inputLines);

        String attackerName = findNameInSpecialAttacks(m.replaceAll("$1"));
        String attackedName = findNameInSpecialAttacks(m.replaceAll("$2"));
        attackerName = attackerName.trim();
        attackedName = attackedName.trim();
        findAttackerObject(attackerName);
        findAttackedObject(attackedName);

        String hitValue = m.replaceAll("$3");
        boolean concealed = checkAndRegisterHit(hitValue);    //set hit/miss/crit/concealed and capture concealed boolean
        int attackRoll = Integer.parseInt(m.replaceAll("$4"));
        if (!concealed) {
            checkAttackRoll(attackRoll);
        }
        checkAttackBonus(Integer.parseInt(m.replaceAll("$5")));
        int totalAB = Integer.parseInt(m.replaceAll("$6"));
        checkTotalAB(totalAB);
        registerArmorClass(totalAB, attackRoll, hitValue);
    }

    private void dmgResistanceParser(String inputLines){
        //.*\].*\]\s+(.*)\s+\:\sDamage\sResistance\sabsorbs\s(\d+)\sdamage
        //.*\\].*\\]\\s+(.*)\\s+\\:\\sDamage\\sResistance\\sabsorbs\\s(\\d+)\\sdamage
        Pattern DAMAGE_RESISTANCE_PATTERN = Pattern.compile(".*\\].*\\]\\s+(.*)\\s+\\:\\sDamage\\sResistance\\sabsorbs\\s(\\d+)\\sdamage");
        Matcher m = DAMAGE_RESISTANCE_PATTERN.matcher(inputLines);
//        Find object
        String attackerName = m.replaceAll("$1");
        attackerName = attackerName.trim();
        findAttackerObject(attackerName);

//      Record the amount of damage resisted and send it to method that deals with it appropriately
        int i = Integer.parseInt(m.replaceAll("$2"));
        attackerObject.setDamageResisted(i);
    }

    private void dmgReductionParser(String inputLines){
        //.*\].*\]\s+(.*)\s+\:\sDamage\sReduction\sabsorbs\s(\d+)\sdamage
        //.*\\].*\\]\\s+(.*)\\s+\\:\\sDamage\\sReduction\\sabsorbs\\s(\\d+)\\sdamage
        Pattern DAMAGE_REDUCTION_PATTERN = Pattern.compile(".*\\].*\\]\\s+(.*)\\s+\\:\\sDamage\\sReduction\\sabsorbs\\s(\\d+)\\sdamage");
        Matcher m = DAMAGE_REDUCTION_PATTERN.matcher(inputLines);
//        Find object
        String attackerName = m.replaceAll("$1");
        attackerName = attackerName.trim();
        findAttackerObject(attackerName);

//      Record the amount of damage resisted and send it to method that deals with it appropriately
        int i = Integer.parseInt(m.replaceAll("$2"));
        attackerObject.setDamageReduced(i);
    }

    private void dmgImmunityParser(String inputLines){
        //.*\].*\]\s+(.*)\s+\:\sDamage\sImmunity\sabsorbs\s(\d+)\spoint\(s\)\sof\s(.*)
        //.*\\].*\\]\\s+(.*)\\s+\\:\\sDamage\\sImmunity\\sabsorbs\\s(\\d+)\\spoint\\(s\\)\\sof\\s(.*)
        Pattern DAMAGE_RESISTANCE_PATTERN = Pattern.compile(".*\\].*\\]\\s+(.*)\\s+\\:\\sDamage\\sImmunity\\sabsorbs\\s(\\d+)\\spoint\\(s\\)\\sof\\s(.*)");
        Matcher m = DAMAGE_RESISTANCE_PATTERN.matcher(inputLines);
//        Find object
        String attackerName = m.replaceAll("$1");
        attackerName = attackerName.trim();
        findAttackerObject(attackerName);

//      Record the amount of damage resisted and send it to method that deals with it appropriately
        int i = Integer.parseInt(m.replaceAll("$2"));
        String dmgType = m.replaceAll("$3");
        checkDamageImmunity(i, dmgType);
    }
    
    /*
     * Calls the appropriate immunity setter of the attacker object. Call priority
     * set by frequency of dmg type. Where unclear, alphabetical order rules.
     */
    private void checkDamageImmunity(int i, String dmgType){
        if("Physical".equals(dmgType)) attackerObject.setDamageImmunityPhysical(i);
        else if("Acid".equals(dmgType)) attackerObject.setDamageImmunityAcid(i);
        else if("Cold".equals(dmgType)) attackerObject.setDamageImmunityCold(i);
        else if("Electrical".equals(dmgType)) attackerObject.setDamageImmunityElectrical(i);
        else if("Fire".equals(dmgType)) attackerObject.setDamageImmunityFire(i);
        else if("Sonic".equals(dmgType)) attackerObject.setDamageImmunitySonic(i);
        else if("Divine".equals(dmgType)) attackerObject.setDamageImmunityDivine(i);
        else if("Magical".equals(dmgType)) attackerObject.setDamageImmunityMagical(i);
        else if("Negative".equals(dmgType)) attackerObject.setDamageImmunityNegative(i);
        else if("Positive".equals(dmgType)) attackerObject.setDamageImmunityPositive(i);
    }
    
//    public static void main(String[] args) throws IOException {
//        String url = "C:/Users/Jakob/Documents/My Dropbox/NetBeans/NetBeansProjects/NeverwinterNights/src/logParser/test";
//        String url = "C:/Games/NeverwinterNights/NWN/logs/nwclientlog1.txt";
//        File file = new File(url);
//        Parser parser = new Parser(file);
//    }
}
