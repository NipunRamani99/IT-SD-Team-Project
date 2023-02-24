package commands;

import akka.actor.ActorRef;
import structures.basic.Card;
import structures.basic.Unit;

/**
 * This class is for the special abilities of different units and spells
 */
public class AbilityCommands {

    /**
     * Pureblade Enforce ability
     */
    public static void enforceAbility(){

    }

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
     * Truestrike ability
     */
    public static void truestrikeAbility(ActorRef out, Unit unit){
    	int health = unit.getHealth();
    	health = health - 2;
    	BasicCommands.setUnitHealth(out, unit, health);
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
     * Staff of Y'Kir
     */
    public static void yKirAbility(){

    }
}
