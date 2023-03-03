package commands;

import akka.actor.ActorRef;
import structures.basic.Units.Unit;

/**
 * This class is for the special abilities of different units and spells
 */
public class AbilityCommands {

    /**
     * Pureblade Enforce ability
     */
//    public static void enforceAbility(Unit unit){
//        unit.setHealth(unit.getHealth() + 1);
//        unit.setAttack(unit.getAttack() + 1);
//
//    }

    /**
     * Silverguard Knight ability
     */
    public static void knightAbility(){

    }

    /**
     * Fire Spitter ability
     */
    public static void fireSpitterAbility(){

    }

    /**
     * Truestrike spell
     */
    public static void truestrikeAbility(ActorRef out, Unit unit){
    	int health = unit.getHealth();
    	health = health - 2;
    	unit.setHealth(health);
    	BasicCommands.setUnitHealth(out, unit, health);
    }
    
    /**
     * Sundrop Elixir spell
     */
    public static void sundropElixir(ActorRef out, Unit unit){
    	int health = unit.getHealth();
    	health = health + 5;
    	int originalHealth = unit.gethpFromCard();
    	if(originalHealth < health) {
    		unit.setHealth(originalHealth);
    		BasicCommands.setUnitHealth(out, unit, originalHealth);
    	} else {
    		unit.setHealth(health);
    		BasicCommands.setUnitHealth(out, unit, health);
    	}
    }

    /**
     * Plannar Scout ability
     */
    public static void scoutAbility(){

    }

    /**
     * Pyromancer ability
     */
    public static void pyromancerAbility(){

    }
    /**
     * Blaze Hound ability
     */
    public static void houndAbility(){

    }
    /**
     * Staff of Y'Kir spell
     */
    public static void yKirAbility(ActorRef out, Unit unit){
    	int attack = unit.getAttack();
    	attack = attack + 2;
    	unit.setAttack(attack);
    	BasicCommands.setUnitAttack(out, unit, attack);
    }
    /**
     * Entropic Decay spell
     */
    public static void entropicDecay(ActorRef out, Unit unit) {
    	unit.setHealth(0);
    	BasicCommands.setUnitHealth(out, unit, 0);
    }
}
