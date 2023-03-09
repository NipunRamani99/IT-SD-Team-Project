package commands;

import java.util.List;

import akka.actor.ActorRef;
import events.Attack;
import structures.GameState;
import structures.Turn;
import structures.basic.Card;
import structures.basic.EffectAnimation;
import structures.basic.Tile;
import structures.basic.Unit;
import structures.basic.UnitAbility;
import structures.basic.UnitAbilityTable;
import structures.basic.UnitAnimationType;
import utils.BasicObjectBuilders;
import utils.StaticConfFiles;

/**
 * This class is for the special abilities of different units and spells
 */
public class AbilityCommands {

    /**
     * Provoke ability
     */
    public static void Provoke(ActorRef out, Unit unit, GameState gameState){
    }

    /**
     * Ranged ability
     */
    public static void Ranged(){
    }

    /**
     * HEAL_AVATAR_ON_SUMMON ability
     */
    public static void Heal_Avatar_On_Summon(ActorRef out, GameState gameState){
    	for(int i=0;i<9;i++) {
    		for(int j=0;j<5;j++) {
     			Tile tile = gameState.board.getTile(i, j);
    			Unit unit = tile.getUnit();
    			boolean isAvatar = false;
    			if(unit != null) {
    			isAvatar = unit.isAvatar();
    			}
    			if(isAvatar) {
    				int health = unit.getHealth();
    				if(health<17) {
    					health = health+3;
    				}else {
    					health = 20;
    				}
    				EffectAnimation ef = BasicObjectBuilders.loadEffect(StaticConfFiles.f1_buff);
    				BasicCommands.playEffectAnimation(out, ef, tile);
    				unit.setHealth(health);
    				BasicCommands.setUnitHealth(out, unit, health);
    				Attack.setPlayerHealth(out, health, unit, gameState);
    				break;
    			}
    		}
    	}
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
    
    public static void useAbility(ActorRef out, Unit unit, GameState gameState) {
    	UnitAbilityTable unitAbilityTable = new UnitAbilityTable();
    	List<UnitAbility> unitAbilityList = unitAbilityTable.getUnitAbilities(unit.getName());
    	for(UnitAbility unitAbility:unitAbilityList) {
    		if(unitAbility == UnitAbility.ATTACK_TWICE) {
    			
    		}else if(unitAbility == UnitAbility.BUFF_UNIT_ON_AVATAR_DAMAGE) {
    			
    		}else if(unitAbility == UnitAbility.BUFF_UNIT_ON_ENEMY_SPELL) {
    			
    		}else if(unitAbility == UnitAbility.DRAW_CARD_ON_DEATH) {
    			
    		}else if(unitAbility == UnitAbility.DRAW_CARD_ON_SUMMON) {
    			
    		}else if(unitAbility == UnitAbility.FLYING) {
    			
    		}else if(unitAbility == UnitAbility.HEAL_AVATAR_ON_SUMMON) {
    			Heal_Avatar_On_Summon(out, gameState);
    		}else if(unitAbility == UnitAbility.PROVOKE) {
    			
    		}else if(unitAbility == UnitAbility.RANGED) {
    			
    		}else if(unitAbility == UnitAbility.SUMMON_ANYWHERE) {
    			
    		}
    	}
    }
}
