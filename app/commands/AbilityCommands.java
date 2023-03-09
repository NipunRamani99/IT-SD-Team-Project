package commands;

import akka.actor.ActorRef;
import events.Attack;
import structures.GameState;
import structures.Turn;
import structures.basic.Card;
import structures.basic.Tile;
import structures.basic.Unit;
import structures.basic.UnitAnimationType;

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
     * Truestrike spell
     */
    public static void truestrikeAbility(ActorRef out, Unit unit, GameState gameState){
    	int health = unit.getHealth();
    	health = health - 2;
    	if(health<=0)
    	{
    		Attack.deleteEnemyUnit(out, unit, gameState);
    	}
    	unit.setHealth(health);
    	BasicCommands.setUnitHealth(out, unit, health);
    	Attack.setPlayerHealth(out, health, unit, gameState);
    }
    
    /**
     * Sundrop Elixir spell
     */
    public static void sundropElixir(ActorRef out, Unit unit, GameState gameState){
    	int health = unit.getHealth();
    	health = health + 5;
    	int originalHealth = unit.gethpFromCard();
    	if(originalHealth < health) {
    		unit.setHealth(originalHealth);
    		BasicCommands.setUnitHealth(out, unit, originalHealth);
    	}else {
    		unit.setHealth(health);
    		BasicCommands.setUnitHealth(out, unit, health);
    	}
    	Attack.setPlayerHealth(out, health, unit, gameState);
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
    public static void entropicDecay(ActorRef out, Unit unit, GameState gameState) {
    	unit.setHealth(0);
    	BasicCommands.setUnitHealth(out, unit, 0);
    	BasicCommands.playUnitAnimation(out, unit,UnitAnimationType.death);
    	try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
    	BasicCommands.deleteUnit(out, unit);
    	Tile tile= gameState.board.getTile(unit.getPosition());
    	if(unit.isAi())
    		tile.clearAiUnit();
    	else tile.clearUnit();
    }
}
