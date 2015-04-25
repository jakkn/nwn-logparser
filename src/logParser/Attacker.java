/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package logParser;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 *
 * @author Jakob
 */
public class Attacker implements Comparable{

    private String name;
//    Damage dealt
    private int acidDamage = 0;
    private int coldDamage = 0;
    private int divineDamage = 0;
    private int electricalDamage = 0;
    private int fireDamage = 0;
    private int magicalDamage = 0;
    private int negativeDamage = 0;
    private int physicalDamage = 0;
    private int positiveDamage = 0;
    private int sonicDamage = 0;
    private int totalDamage = 0;
//    Damage taken
    private int acidDamageTaken = 0;
    private int coldDamageTaken = 0;
    private int divineDamageTaken = 0;
    private int electricalDamageTaken = 0;
    private int fireDamageTaken = 0;
    private int magicalDamageTaken = 0;
    private int negativeDamageTaken = 0;
    private int physicalDamageTaken = 0;
    private int positiveDamageTaken = 0;
    private int sonicDamageTaken = 0;
    private int totalDamageTaken = 0;
    private int damageResisted = 0;
    private int damageReduced = 0;
    private int damageImmunityAcid = 0;
    private int damageImmunityCold = 0;
    private int damageImmunityDivine = 0;
    private int damageImmunityElectrical = 0;
    private int damageImmunityFire = 0;
    private int damageImmunityMagical = 0;
    private int damageImmunityNegative = 0;
    private int damageImmunityPhysical = 0;
    private int damageImmunityPositive = 0;
    private int damageImmunitySonic = 0;
//    Misc
    private int kills = 0;
    private int deaths = 0;
    private int maxCriticalHit = 0;
    private int maxNormalHit = 0;
    private int maxDamageTaken = 0;
    private int xpGained = 0;
    private int hasHit = 0;
    private int wasHit = 0;
    private int hasMissed = 0;
    private int wasMissed = 0;
    private int criticalHit = 0;
    private int targetConcealed = 0;
    private int selfConcealed = 0;
    private int autoHit = 0;
    private int autoMiss = 0;
    private int numberOfOutgoingAttacks = 0;
    private int numberOfIncomingAttacks = 0;
    private int maxAB = -100;
    private int maxTotalAB = -100;
    private int lowestAB = 100;
    private int lowestTotalAB = 100;
    private int armorClass = 0;
    private ArrayList<Integer> ab;
    private int nofAB = 5;
//    Saves
    private int fortitudeSave = 0;
    private int reflexSave = 0;
    private int willSave = 0;
    private int numberOfSuccessfulSaves = 0;
    private int numberOfFailedSaves = 0;


    public Attacker(String inputName) {
//        if (!inputName.equals(null)) {
        if (inputName != null) {
            setName(inputName);
            ab = new ArrayList<Integer>();
            for (int i = 0; i < nofAB; i++) {
                ab.add(i, 0); //fill ab list with zeroes
            }
        }
    }

    public int getAcidDamage() {
        return acidDamage;
    }

    public int getColdDamage() {
        return coldDamage;
    }

    public int getDivineDamage() {
        return divineDamage;
    }

    public int getElectricalDamage() {
        return electricalDamage;
    }

    public int getFireDamage() {
        return fireDamage;
    }

    public int getMagicalDamage() {
        return magicalDamage;
    }

    public int getNegativeDamage() {
        return negativeDamage;
    }

    public int getPhysicalDamage() {
        return physicalDamage;
    }

    public int getPositiveDamage() {
        return positiveDamage;
    }

    public int getSonicDamage() {
        return sonicDamage;
    }

    public int getTotalDamage() {
        return totalDamage;
    }

    public int getAcidDamageTaken() {
        return acidDamageTaken;
    }

    public int getColdDamageTaken() {
        return coldDamageTaken;
    }

    public int getDivineDamageTaken() {
        return divineDamageTaken;
    }

    public int getElectricalDamageTaken() {
        return electricalDamageTaken;
    }

    public int getFireDamageTaken() {
        return fireDamageTaken;
    }

    public int getMagicalDamageTaken() {
        return magicalDamageTaken;
    }

    public int getNegativeDamageTaken() {
        return negativeDamageTaken;
    }

    public int getPhysicalDamageTaken() {
        return physicalDamageTaken;
    }

    public int getPositiveDamageTaken() {
        return positiveDamageTaken;
    }

    public int getSonicDamageTaken() {
        return sonicDamageTaken;
    }

    public int getTotalDamageTaken() {
        return totalDamageTaken;
    }

    public String getName() {
        return name;
    }

    public void setAcidDamage(int inputAcidDamage) {
        acidDamage += inputAcidDamage;
    }

    public void setColdDamage(int inputColdDamage) {
        coldDamage += inputColdDamage;
    }

    public void setDivineDamage(int inputDivineDamage) {
        divineDamage += inputDivineDamage;
    }

    public void setElectricalDamage(int inputElectricalDamage) {
        electricalDamage += inputElectricalDamage;
    }

    public void setFireDamage(int inputFireDamage) {
        fireDamage += inputFireDamage;
    }

    public void setMagicalDamage(int inputMagicalDamage) {
        magicalDamage += inputMagicalDamage;
    }

    public void setNegativeDamage(int inputNegativeDamage) {
        negativeDamage += inputNegativeDamage;
    }

    public void setPhysicalDamage(int inputPhysicalDamage) {
        physicalDamage += inputPhysicalDamage;
    }

    public void setPositiveDamage(int inputPositiveDamage) {
        positiveDamage += inputPositiveDamage;
    }

    public void setSonicDamage(int inputSonicDamage) {
        sonicDamage += inputSonicDamage;
    }

    public void setAcidDamageTaken(int inputAcidDamage) {
        acidDamageTaken += inputAcidDamage;
    }

    public void setColdDamageTaken(int inputColdDamage) {
        coldDamageTaken += inputColdDamage;
    }

    public void setDivineDamageTaken(int inputDivineDamage) {
        divineDamageTaken += inputDivineDamage;
    }

    public void setElectricalDamageTaken(int inputElectricalDamage) {
        electricalDamageTaken += inputElectricalDamage;
    }

    public void setFireDamageTaken(int inputFireDamage) {
        fireDamageTaken += inputFireDamage;
    }

    public void setMagicalDamageTaken(int inputMagicalDamage) {
        magicalDamageTaken += inputMagicalDamage;
    }

    public void setNegativeDamageTaken(int inputNegativeDamage) {
        negativeDamageTaken += inputNegativeDamage;
    }

    public void setPhysicalDamageTaken(int inputPhysicalDamage) {
        physicalDamageTaken += inputPhysicalDamage;
    }

    public void setPositiveDamageTaken(int inputPositiveDamage) {
        positiveDamageTaken += inputPositiveDamage;
    }

    public void setSonicDamageTaken(int inputSonicDamage) {
        sonicDamageTaken += inputSonicDamage;
    }

    public final void setName(String name) {
        this.name = name;
    }
    
    public int getDamageImmunityAcid() {
        return damageImmunityAcid;
    }

    public void setDamageImmunityAcid(int damageImmunityAcid) {
        this.damageImmunityAcid += damageImmunityAcid;
    }

    public int getDamageImmunityCold() {
        return damageImmunityCold;
    }

    public void setDamageImmunityCold(int damageImmunityCold) {
        this.damageImmunityCold += damageImmunityCold;
    }

    public int getDamageImmunityDivine() {
        return damageImmunityDivine;
    }

    public void setDamageImmunityDivine(int damageImmunityDivine) {
        this.damageImmunityDivine += damageImmunityDivine;
    }

    public int getDamageImmunityElectrical() {
        return damageImmunityElectrical;
    }

    public void setDamageImmunityElectrical(int damageImmunityElectrical) {
        this.damageImmunityElectrical += damageImmunityElectrical;
    }

    public int getDamageImmunityFire() {
        return damageImmunityFire;
    }

    public void setDamageImmunityFire(int damageImmunityFire) {
        this.damageImmunityFire += damageImmunityFire;
    }

    public int getDamageImmunityMagical() {
        return damageImmunityMagical;
    }

    public void setDamageImmunityMagical(int damageImmunityMagical) {
        this.damageImmunityMagical += damageImmunityMagical;
    }

    public int getDamageImmunityNegative() {
        return damageImmunityNegative;
    }

    public void setDamageImmunityNegative(int damageImmunityNegative) {
        this.damageImmunityNegative += damageImmunityNegative;
    }

    public int getDamageImmunityPhysical() {
        return damageImmunityPhysical;
    }

    public void setDamageImmunityPhysical(int damageImmunityPhysical) {
        this.damageImmunityPhysical += damageImmunityPhysical;
    }

    public int getDamageImmunityPositive() {
        return damageImmunityPositive;
    }

    public void setDamageImmunityPositive(int damageImmunityPositive) {
        this.damageImmunityPositive += damageImmunityPositive;
    }

    public int getDamageImmunitySonic() {
        return damageImmunitySonic;
    }

    public void setDamageImmunitySonic(int damageImmunitySonic) {
        this.damageImmunitySonic += damageImmunitySonic;
    }

    public int getDamageReduced() {
        return damageReduced;
    }

    public void setDamageReduced(int damageReduced) {
        this.damageReduced += damageReduced;
    }

    public int getDamageResisted() {
        return damageResisted;
    }

    public void setDamageResisted(int damageResisted) {
        this.damageResisted += damageResisted;
    }
    
    public void setTotalDamage() {
        totalDamage = acidDamage + coldDamage + divineDamage + electricalDamage + fireDamage + magicalDamage + negativeDamage + physicalDamage + positiveDamage + sonicDamage;
    }

    public void setTotalDamageTaken() {
        totalDamageTaken = acidDamageTaken + coldDamageTaken + divineDamageTaken + electricalDamageTaken + fireDamageTaken + magicalDamageTaken + negativeDamageTaken + physicalDamageTaken + positiveDamageTaken + sonicDamageTaken;
    }

    public void setKills(int kills) {
        this.kills = kills;
    }

    public void incrementKills() {
        this.kills++;
    }

    public int getKills() {
        return kills;
    }

    public void setXpGained(int xpGained) {
        this.xpGained += xpGained;
    }

    public int getXpGained() {
        return xpGained;
    }

    public int getMaxCriticalHit() {
        return maxCriticalHit;
    }

    public void setMaxCriticalHit(int maxCriticalHit) {
        this.maxCriticalHit = maxCriticalHit;
    }

    public int getMaxNormalHit() {
        return maxNormalHit;
    }

    public void setMaxNormalHit(int maxNormalHit) {
        if(getMaxNormalHit() < maxNormalHit) this.maxNormalHit = maxNormalHit;
    }

    public void setHasHit(){
        this.hasHit += 1;
    }

    /**
     * HasHit holds the number of successfull attacks landed <i>by</i> this object.
     * @return hasHit counter
     */
    public int getHasHit(){
        return this.hasHit;
    }

    public void setWasHit(){
        this.wasHit += 1;
    }

    /**
     * WasHit holds the number of successfull attacks landed <i>on</i> this object.
     * @return 
     */
    public int getWasHit(){
        return this.wasHit;
    }

    public void setCriticalHit(){
        this.criticalHit += 1;
    }

    /**
     * CriticalHit holds the number of critical hits landed <i>by</i> this object.
     * @return criticalHit counter
     */
    public int getCriticalHit(){
        return this.criticalHit;
    }

    public void setHasMissed(){
        this.hasMissed += 1;
    }

    /**
     * HasMissed holds the number of failed attacks attempted <i>by</i> this object.
     * @return hasMissed counter
     */
    public int getHasMissed(){
        return this.hasMissed;
    }

    public void setWasMissed(){
        this.wasMissed += 1;
    }

    /**
     * WasMissed holds the number of failed attacks attempted <i>on</i> this object.
     * @return wasMissed counter
     */
    public int getWasMissed(){
        return this.wasMissed;
    }

    public void setConcealedOutgoingAttacks(){
        this.targetConcealed += 1;
    }

    public int getConcealedOutgoingAttacks(){
        return this.targetConcealed;
    }

    public void setConcealedIncomingAttacks(){
        this.selfConcealed += 1;
    }

    public int getConcealedIncomingAttacks(){
        return this.selfConcealed;
    }

    public void setAutoHit(){
        this.autoHit++;
    }

    public int getAutoHit(){
        return this.autoHit;
    }

    public void setAutoMiss(){
        this.autoMiss++;
    }

    public int getAutoMiss(){
        return this.autoMiss;
    }
    
    public void setNumberOfOutgoingAttacks(){
        this.numberOfOutgoingAttacks += 1;
    }

    public int getNumberOfOutgoingAttacks(){
        return this.numberOfOutgoingAttacks;
    }
    
    public void setNumberOfIncomingAttacks(){
        this.numberOfIncomingAttacks += 1;
    }

    public int getNumberOfIncomingAttacks(){
        return this.numberOfIncomingAttacks;
    }

    public void setMaxDamageTaken(int i){
        if(getMaxDamageTaken() < i) maxDamageTaken = i;
    }

    public int getMaxDamageTaken(){
        return this.maxDamageTaken;
    }
    
    public int getTotalMissedHits(){
        return getHasMissed() + getConcealedOutgoingAttacks();
    }
    
    public int getTotalHitsLanded(){
        return getHasHit() + getCriticalHit();
    }

    /**
     * This method takes in two integers and returns the percentage, formatted by DecimalFormat.
     * @param statToDivide integer to divide
     * @param statToDivideOn integer to divide on
     * @return divided number formatted as percentage
     */
    private String calculatePercentage(int statToDivide, int statToDivideOn){
        BigDecimal percentage;
        DecimalFormat formatter = new DecimalFormat("#0.0");
        formatter.applyPattern("0.0%");

        if(statToDivideOn > 0){
            percentage = new BigDecimal(statToDivide);
            percentage = percentage.divide(new BigDecimal(statToDivideOn), 3, RoundingMode.HALF_UP);
            return formatter.format(percentage);
        }
        else return formatter.format(0);
    }
    
    /**
     * Calculates the hit percentage from the number of landed hits (hasHit + criticalHit) divided by the number of total attacks.
     * @return divided number formatted as percentage
     */
    public String percentageHit(){
        return calculatePercentage(getTotalHitsLanded(), getNumberOfOutgoingAttacks());
    }

    /**
     * Calculates the miss percentage from the total number of missed hits (miss + concealed hits), divided by the number of total attacks.
     * @return divided number formatted as percentage
     */
    public String percentageMissOfTotal(){
        return calculatePercentage(getTotalMissedHits(), getNumberOfOutgoingAttacks());
    }

    /**
     * Calculates the critical hit percentage from the number of critical hits divided by the number of total attacks.
     * @return divided number formatted as percentage
     */
    public String percentageCriticalHitOfTotal(){
        return calculatePercentage(getCriticalHit(), getNumberOfOutgoingAttacks());
    }

    /**
     * Calculates the critical hit percentage from the number of critical hits divided by the number of hits.
     * @return "NN.D%" (N=Number, D=Decimal)
     */
    public String percentageCriticalHitOfHits(){
        return calculatePercentage(getCriticalHit(), getTotalHitsLanded());
    }

    /**
     * Calculates the concealment percentage of this object from the number of concealed incoming attacks divided by
     * the number of total incoming attacks.
     * @return divided number formatted as percentage
     */
    public String percentageConceal(){
        return calculatePercentage(getConcealedIncomingAttacks(), getNumberOfIncomingAttacks());
    }

    public void registerAttackBonus(int i){
        if(i > this.maxAB) this.maxAB = i;
        else if(i < this.lowestAB) this.lowestAB = i;
        ab.add(0, i);
        ab.remove(nofAB);
    }

    public void registerArmorClass(int i, String s){
        if (s.contains("miss")){
            if (armorClass == 0) armorClass = i+1;
            else if(i >= armorClass) armorClass = i+1;
        } else if (s.contains("hit")){
            if (armorClass == 0) armorClass = i;
            else if(i < armorClass) armorClass = i;
        }
    }

    public int getArmorClass(){
        return armorClass;
    }

    public void registerTotalAB(int i){
        if(i > this.maxTotalAB) this.maxTotalAB = i;
//        else if(!lowestTotalABModified) this.lowestTotalAB = i;
        else if(i < this.lowestTotalAB) this.lowestTotalAB = i;
    }

    public int getMaxAB(){
        return maxAB;
    }

    public String getAB() {
        StringBuilder attackBonus = new StringBuilder();
        for (int i = nofAB-1; i >= 0; --i) {
            attackBonus.append("+").append(this.ab.get(i)).append("/");
        }
        return attackBonus.substring(0, attackBonus.length()-1).toString();
    }
    
    /*
     * Getter for comparator "AbComparator" found in Parser.java
     */
    public int getABCompare() {
        int max = 0;
        for (int i = 0; i < nofAB; i++) {
            if(ab.get(i) > max) max = ab.get(0);
        }
        return max;
    }
    
    public int getHighestTotalAB(){
        return maxTotalAB;
    }

    public int getLowestAB(){
        return lowestAB;
    }

    public int getLowestTotalAB(){
        return lowestTotalAB;
    }

    public int compareTo(Object anotherAttacker) throws ClassCastException {
        if (!(anotherAttacker instanceof Attacker)) {
            throw new ClassCastException("A Attacker object expected.");
        }
        int anotherAttackerDamage = ((Attacker) anotherAttacker).getTotalDamage();
        return anotherAttackerDamage - this.getTotalDamage();
    }

    public int getDeaths() {
        return deaths;
    }

    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }

    public void incrementDeaths() {
        this.deaths++;
    }

    public int averageHitPoints(){
        if(this.getTotalDamageTaken() != 0 && this.getDeaths() != 0){
            return this.getTotalDamageTaken() / this.getDeaths();
        }
        else return 0;
    }

    public int getAverageDamage(){
        if(this.getTotalDamage() != 0 && this.getHasHit() != 0){
            return this.getTotalDamage() / (this.getHasHit() + this.getCriticalHit());
        }
        else return 0;
    }

    public int getFortitudeSave() {
        return fortitudeSave;
    }

    public void setFortitudeSave(int fortitudeSave) {
        if(fortitudeSave != this.fortitudeSave) this.fortitudeSave = fortitudeSave;
    }

    public int getReflexSave() {
        return reflexSave;
    }

    public void setReflexSave(int reflexSave) {
        if(reflexSave != this.reflexSave) this.reflexSave = reflexSave;
    }

    public int getWillSave() {
        return willSave;
    }

    public void setWillSave(int willSave) {
        if(willSave != this.willSave) this.willSave = willSave;
    }

    public void incrementSuccessSave() {
        numberOfSuccessfulSaves++;
    }

    public void incrementFailureSave() {
        numberOfFailedSaves++;
    }

    public int getNumberOfSuccessfulSaves() {
        return numberOfSuccessfulSaves;
    }

    public int getNumberOfFailedSaves() {
        return numberOfFailedSaves;
    }
    
    @Override
    /**
     * Used for JList to represent Attacker objects.
     */
    public String toString(){
        return getName();
    }
}