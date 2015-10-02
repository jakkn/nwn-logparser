/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package logParser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 *
 * @author Jakob Dagsland Knutsen (JDK)
 */
public class TestClass {

//    private int unusableInputLength = 41;
//    private ArrayList<String> codeInput;
//    private ArrayList<Attacker> attackerList = new ArrayList<Attacker>();
//    private Attacker attackerObject;
//    private int totalKills = 0;
//    private int numberOfUnis = 0;
    private ArrayList<Attacker> attackerList = new ArrayList<Attacker>();
    private ArrayList<Attacker> partyList = new ArrayList<Attacker>();
    private int unusableInputLength = 41;
    private ArrayList<String> codeInput;
    private Attacker attackerObject;
    private final int lengthOfAattackOfOpportunity = 23;
    private final int lengthOfSneakAattack = 15;
    private final int lengthOfPowerAttack = 14;
    private final int lengthOfImprovedPowerAttack = 23;
    private final int lengthOfFlurryOfBlows = 17;
    private final int lengthOfRapidShot = 12;
    private final int lengthOfSmiteEvil = 12;
    private final int lengthOfSmiteGood = 12;
    private final int lengthOfKiDamage = 11;
    private int totalKills = 0;
    private int numberOfUniques = 0;

    public TestClass(File inputFile) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(inputFile));
        while (bufferedReader.ready()) {
            String lineString = bufferedReader.readLine().trim();
            if (lineString.contains("[Party]") || lineString.contains("[Tell]") || lineString.contains("[Whisper]") || lineString.contains("[Talk]") || lineString.contains("Exp") || lineString.contains("exp")) {
                continue;
            }
            else if (lineString.isEmpty()) {
                continue;
            }
//            else if (lineString.matches(".*\\].*\\]\\s+.*:\\s\\[Party\\].*")){
//                if(ParserJFrame.jCheckParty.isSelected()){
//                    setPartyMember(lineString);
//                }
//                else continue;
//            }
            else if (lineString.contains("BONUS XP:")) {
                numberOfUniques++;
            }
            else if (!lineString.contains("attacks") && !lineString.contains("damages") && !lineString.contains("kill") && !lineString.contains("xp")) {
                continue;
            }
            else if (lineString.contains("target concealed")) {
                System.out.println(lineString);
//                attackParser(lineString);
            }
            else if (lineString.contains("damages")) {
//                damageParser(lineString);
            }
            else if (lineString.contains("killed")) {
//                killParser(lineString);
            }
            else if (lineString.contains("xp)")) {
//                xpParser(lineString);
            }
        }
        System.out.print("--==Hits==-- ");
        for (Attacker attacker : attackerList) {
//            if (attacker.getName().matches("Bregg Wakenk")) System.out.print(attacker.getName() + ": " + attacker.hitPercentage() + " ");
//            else if (attacker.getName().matches("Grrr'ark Bloodpaw")) System.out.print(attacker.getName() + ": " + attacker.hitPercentage() + " ");
//            else if (attacker.getName().matches("Nathan Reynolds")) System.out.print(attacker.getName() + ": " + attacker.hitPercentage() + " ");
//            else if (attacker.getName().matches("Xin'thah Moragh")) System.out.print(attacker.getName() + ": " + attacker.hitPercentage() + " ");
//            else if (attacker.getName().matches("Smith Smurf")) System.out.print(attacker.getName() + ": " + attacker.hitPercentage() + " ");
//            else if (attacker.getName().matches("Durga")) System.out.print(attacker.getName() + ": " + attacker.hitPercentage() + " ");
//            else if (attacker.getName().matches("Mathan Kelter")) System.out.print(attacker.getName() + ": " + attacker.hitPercentage() + " ");
        }
        System.out.println("");
        System.out.print("--==Damage==-- ");
        for (Attacker attacker : attackerList) {
            if (attacker.getName().matches("Bregg Wakenk")) System.out.print(attacker.getName() + ": " + attacker.getTotalDamage() + " dmg. ");
            else if (attacker.getName().matches("Kilena")) System.out.print(attacker.getName() + ": " + attacker.getTotalDamage() + " dmg. ");
            else if (attacker.getName().matches("Muric Tucker")) System.out.print(attacker.getName() + ": " + attacker.getTotalDamage() + " dmg. ");
            else if (attacker.getName().matches("Mera Valermo")) System.out.print(attacker.getName() + ": " + attacker.getTotalDamage() + " dmg. ");
            else if (attacker.getName().matches("Maura Silverhand")) System.out.print(attacker.getName() + ": " + attacker.getTotalDamage() + " dmg. ");
            else if (attacker.getName().matches("Grrr'ark Bloodpaw")) System.out.print(attacker.getName() + ": " + attacker.getTotalDamage() + " dmg. ");
            else if (attacker.getName().matches("~Drekana Darkstorm~")) System.out.print(attacker.getName() + ": " + attacker.getTotalDamage() + " dmg. ");
            else if (attacker.getName().matches("Thord Son o' Thord")) System.out.print(attacker.getName() + ": " + attacker.getTotalDamage() + " dmg. ");
            else if (attacker.getName().matches("Uther Greenman")) System.out.print(attacker.getName() + ": " + attacker.getTotalDamage() + " dmg. ");
            else if (attacker.getName().matches("~Huor~")) System.out.print(attacker.getName() + ": " + attacker.getTotalDamage() + " dmg. ");
            else if (attacker.getName().matches("Nathan Reynolds")) System.out.print(attacker.getName() + ": " + attacker.getTotalDamage() + " dmg. ");
        }
        System.out.println("");
        System.out.print("--==Max Hit==-- ");
        for (Attacker attacker : attackerList) {
            if (attacker.getName().matches("Bregg Wakenk")) System.out.print(attacker.getName() + ": "+ attacker.getMaxNormalHit() + " max hit. ");
            else if (attacker.getName().matches("Kilena")) System.out.print(attacker.getName() + ": "+ attacker.getMaxNormalHit() + " max hit. ");
            else if (attacker.getName().matches("Muric Tucker")) System.out.print(attacker.getName() + ": "+ attacker.getMaxNormalHit() + " max hit. ");
            else if (attacker.getName().matches("Mera Valermo")) System.out.print(attacker.getName() + ": "+ attacker.getMaxNormalHit() + " max hit. ");
            else if (attacker.getName().matches("Maura Silverhand")) System.out.print(attacker.getName() + ": "+ attacker.getMaxNormalHit() + " max hit. ");
            else if (attacker.getName().matches("Grrr'ark Bloodpaw")) System.out.print(attacker.getName() + ": "+ attacker.getMaxNormalHit() + " max hit. ");
            else if (attacker.getName().matches("~Drekana Darkstorm~")) System.out.print(attacker.getName() + ": "+ attacker.getMaxNormalHit() + " max hit. ");
            else if (attacker.getName().matches("Thord Son o' Thord")) System.out.print(attacker.getName() + ": "+ attacker.getMaxNormalHit() + " max hit. ");
            else if (attacker.getName().matches("Uther Greenman")) System.out.print(attacker.getName() + ": "+ attacker.getMaxNormalHit() + " max hit. ");
            else if (attacker.getName().matches("~Huor~")) System.out.print(attacker.getName() + ": "+ attacker.getMaxNormalHit() + " max hit. ");
            else if (attacker.getName().matches("Nathan Reynolds")) System.out.print(attacker.getName() + ": "+ attacker.getMaxNormalHit() + " max hit. ");
        }
        System.out.println("");
        System.out.print("--==Kills==-- ");
        for (Attacker attacker : attackerList) {
                if (attacker.getKills() != 0) {
                        System.out.print(attacker.getName() + " has killed " + attacker.getKills() + ". ");
                }
        }
        System.out.println("");
        System.out.print("--==XP==-- ");
        for (Attacker attacker : attackerList) {
                if (attacker.getXpGained() != 0) {
                        System.out.print(attacker.getName() + ": " + attacker.getXpGained() + " xp. ");
                }
        }
        System.out.println("");
        for (Attacker attacker : attackerList) {
                if (attacker.getKills() != 0) {
                        totalKills += attacker.getKills();
                }
        }
        System.out.println("Total kills: " + totalKills);
        System.out.println("Uniques killed: " + numberOfUniques);
        System.out.println("");
    }

    private int getNumberOfUniques(){
        return numberOfUniques;
    }

    private void clearNumberOfUniques(){
        numberOfUniques=0;
    }

    private int getTotalKills(){
        return totalKills;
    }

    private void increaseTotalKills(){
        totalKills++;
    }

    private void clearTotalKills(){
        totalKills=0;
    }

    public ArrayList<Attacker> getAttackerList() {
        return attackerList;
    }

    private void deleteUnusableCharacters(StringBuilder lineInput, String charToDelete) {
        while (lineInput.indexOf(charToDelete) > 0) {
            lineInput.deleteCharAt(lineInput.indexOf(charToDelete));
        }
    }

    private void replaceUnusableCharacters(String lineInput, String charToDelete, String charToReplace) {
        while (lineInput.indexOf(charToDelete) > 0) {
            lineInput.replaceAll(charToDelete, charToReplace);
        }
    }

    private void deleteUnusableInput(StringBuilder stringBuilder) {
        stringBuilder.delete(0, unusableInputLength);
    }

    private void splitAndFillArray(StringBuilder stringBuilder) {
        String str = stringBuilder.toString();
        String[] input = str.split("\\ ");
        for (int i = 0; i < input.length; i++) {
            codeInput.add(input[i]);
        }
    }

    private void assignDamage(Attacker object, String inputType, int inputNumber) {
        if (inputType.matches("Physical")) object.setPhysicalDamage(inputNumber);
        else if (inputType.matches("Fire")) object.setFireDamage(inputNumber);
        else if (inputType.matches("Magical")) object.setMagicalDamage(inputNumber);
        else if (inputType.matches("Divine")) object.setDivineDamage(inputNumber);
        else if (inputType.matches("Acid")) object.setAcidDamage(inputNumber);
        else if (inputType.matches("Cold")) object.setColdDamage(inputNumber);
        else if (inputType.matches("Sonic")) object.setSonicDamage(inputNumber);
        else if (inputType.matches("Electrical")) object.setElectricalDamage(inputNumber);
        else if (inputType.matches("Positive")) object.setPositiveDamage(inputNumber);
        else if (inputType.matches("Negative")) object.setNegativeDamage(inputNumber);
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
            attackerList.add(new Attacker(name));
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

    @SuppressWarnings("unchecked")
    private void swapTwoLastIndexes(ArrayList list, int intToSwap, int intToSwapWith) {
        list.add(intToSwap, list.get(intToSwapWith));
        list.remove(list.size() - 1);
    }

    private void deleteFeatInput(StringBuilder stringBuilder){
        if(stringBuilder.toString().contains("Attack Of Opportunity")) stringBuilder.delete(0, lengthOfAattackOfOpportunity);
        if(stringBuilder.toString().contains("Sneak Attack")) stringBuilder.delete(0, lengthOfSneakAattack);
        if(stringBuilder.toString().contains("Improved Power Attack")) stringBuilder.delete(0, lengthOfImprovedPowerAttack);
        else if(stringBuilder.toString().contains("Power Attack")) stringBuilder.delete(0, lengthOfPowerAttack);
        else if(stringBuilder.toString().contains("Flurry Of Blows")) stringBuilder.delete(0, lengthOfFlurryOfBlows);
        else if(stringBuilder.toString().contains("Rapid Shot")) stringBuilder.delete(0, lengthOfRapidShot);
        else if(stringBuilder.toString().contains("Smite Evil")) stringBuilder.delete(0, lengthOfSmiteEvil);
        else if(stringBuilder.toString().contains("Smite Good")) stringBuilder.delete(0, lengthOfSmiteGood);
        else if(stringBuilder.toString().contains("Ki Damage")) stringBuilder.delete(0, lengthOfKiDamage);
    }

    private void checkAndSetHit(String string) {
        attackerObject.setNumberOfOutgoingAttacks();
        if(string.contains("critical hit")) attackerObject.setHasHit();
        else if(string.contains("miss")) attackerObject.setHasMissed();
        else if(string.contains("hit")) attackerObject.setCriticalHit();
        else if(string.contains("target concealed")) attackerObject.setConcealedOutgoingAttacks();
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
            partyList.add(attackerObject);
        }

//        String input = lineString;
//        String regex = ".*\\].*\\]\\s+(.*):\\s\\[Party\\].*";  //
//        String name = input.replaceAll(regex, "$0");
//        findAttackerObject(name);
//        partyList.add(attackerObject);

    }

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
                if(this.attackerObject.getMaxNormalHit() < tempHit){
                    this.attackerObject.setMaxNormalHit(tempHit);
                }
                break;
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

    private void attackParser(String inputLines) {
//        have to check for sneak/death attacks or anything else first before method is initiated.
//        Sneak Attack, Death Attack, Flurry of Blows, Rapid Shot, Knockdown, Disarm, Power Attack, Called Shot, Expertise
//        Ki Damage,

        if(inputLines.contains("Attack :")) sneakAttackParser(inputLines);
        else if(inputLines.contains("Blows :")) specialAttackParser(inputLines);
        else if(inputLines.contains("Shot :")) specialAttackParser(inputLines);
        else normalAttackParser(inputLines);
    }

    private void normalAttackParser(String inputLines){
//        Pattern ATTACK_LINE_PATTERN = Pattern.compile(".*\\].*\\]\\s+(.*)attacks");
        Pattern ATTACK_LINE_PATTERN = Pattern.compile(".*\\].*\\]\\s+(.*)attacks(.*):\\s\\*(.*)\\*.*");
        Matcher m = ATTACK_LINE_PATTERN.matcher(inputLines);
        String name = m.replaceAll("$1");
        name = name.trim();
        findAttackerObject(name);
        checkAndSetHit(m.replaceAll("$3"));
    }

    private void sneakAttackParser(String inputLines){

    }

    private void specialAttackParser(String inputLines){

    }

    public static void main(String[] args) throws IOException {
        String url = "C:/Games/NeverwinterNights/NWN/logs/nwclientLog1.txt";
//         String url = TestClass.class.getResource("/logParser/test3.txt").getFile();
//         String url = TestClass.class.getResource("/logParser/nwclientlog1.txt").getFile();
        url = url.replaceAll("%20", " ");
        new TestClass(new File(url));
    }
}
