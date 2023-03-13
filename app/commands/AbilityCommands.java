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
import utils.Constants;
import utils.StaticConfFiles;

/**
 * This class is for the special abilities of different units and spells
 */
public class AbilityCommands {

    /**
     * Provoke ability
     */

    public static void checkIsProvoked(Unit unit,GameState gameState){
    	unit.setIsProvoked(false);
    	if (!unit.isAi()) {
    		for (int i = -1; i <= 1; i++){
				for (int j = -1; j <= 1; j++){
					if (i == 0 && j == 0) continue;
					int x = unit.getPosition().getTilex() + i;
					int y = unit.getPosition().getTiley() + j;
					Tile surroundingTile = gameState.board.getTile(x, y);
					if (surroundingTile!=null) {
					if (surroundingTile.getUnit() != null && surroundingTile.getUnit().isHasProvoke()&&
							surroundingTile.getUnit().isAi()) {
						unit.setIsProvoked(true);
						break;
					}}
				}
			}
    	}else if (unit.isAi()) {
    		for (int i = -1; i <= 1; i++){
				for (int j = -1; j <= 1; j++){
					if (i == 0 && j == 0) continue;
					int x = unit.getPosition().getTilex() + i;
					int y = unit.getPosition().getTiley() + j;
					Tile surroundingTile = gameState.board.getTile(x, y);
					if (surroundingTile!=null) {
					if (surroundingTile.getUnit() != null && surroundingTile.getUnit().isHasProvoke()) {
						unit.setIsProvoked(true);
						break;
					}}
				}
			}
    	}

    }
   

    /**
     * Ranged ability
     */
    public static boolean checkSUMMON_ANYWHERE(Card card){
    	boolean hasAirDrop = false;
    	 UnitAbilityTable unitAbilityTable = new UnitAbilityTable();
         List<UnitAbility> unitAbilityList = unitAbilityTable.getUnitAbilities(card.getCardname());
         for(UnitAbility unitAbility:unitAbilityList) {
        	 if(unitAbility==UnitAbility.SUMMON_ANYWHERE) {
        		 hasAirDrop = true;
        	 }
         }
         return hasAirDrop;
    }


    /**
     * HEAL_AVATAR_ON_SUMMON ability
     */
    public static void Heal_Avatar_On_Summon(ActorRef out, GameState gameState){
    	for(int i=0;i<Constants.BOARD_WIDTH;i++) {
    		for(int j=0;j<Constants.BOARD_HEIGHT;j++) {
     			Tile tile = gameState.board.getTile(i, j);
    			Unit unit = tile.getUnit();
    			boolean isAvatar = false;
    			boolean isAi=false;
    			if(unit != null) {
    			isAvatar = unit.isAvatar();
    			isAi=unit.isAi();
    			}
    			if(isAvatar&&!isAi) {
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
    	} else {
    		unit.setHealth(health);
    		BasicCommands.setUnitHealth(out, unit, health);
    	}
    	Attack.setPlayerHealth(out, health, unit, gameState);
    }

    /**
     * BUFF_UNIT_ON_AVATAR_DAMAGE ability
     */
    public static void BUFF_UNIT_ON_AVATAR_DAMAGE(ActorRef out, GameState gameState){
    	UnitAbilityTable unitAbilityTable = new UnitAbilityTable();
    	for(int i=0;i<Constants.BOARD_WIDTH;i++) {
    		for(int j=0;j<Constants.BOARD_HEIGHT;j++) {
     			Tile tile = gameState.board.getTile(i, j);
    			Unit unit = tile.getUnit();
    			if(unit != null) {
    				if(!unit.isAvatar()) {
    					List<UnitAbility> unitAbilityList = unitAbilityTable.getUnitAbilities(unit.getName());
    					for(UnitAbility unitAbility:unitAbilityList) {
    						if(unitAbility == UnitAbility.BUFF_UNIT_ON_AVATAR_DAMAGE) {
    							int attack = unit.getAttack();
    							attack = attack+2;
    							EffectAnimation ef = BasicObjectBuilders.loadEffect(StaticConfFiles.f1_buff);
    							BasicCommands.playEffectAnimation(out, ef, tile);
    							unit.setAttack(attack);
    							BasicCommands.setUnitAttack(out, unit, attack);
    							break;
    						}
    					}
    				}
    			}   			
    		}
    	}
    }

    /**
     * BUFF_UNIT_ON_ENEMY_SPELL ability
     */
    public static void BUFF_UNIT_ON_ENEMY_SPELL(ActorRef out, GameState gameState){
    	UnitAbilityTable unitAbilityTable = new UnitAbilityTable();
    	for(int i=0;i<Constants.BOARD_WIDTH;i++) {
    		for(int j=0;j<Constants.BOARD_HEIGHT;j++) {
     			Tile tile = gameState.board.getTile(i, j);
    			Unit unit = tile.getUnit();
    			if(unit != null) {
    				if(!unit.isAvatar()) {
    					List<UnitAbility> unitAbilityList = unitAbilityTable.getUnitAbilities(unit.getName());
    					for(UnitAbility unitAbility:unitAbilityList) {
    						if(unitAbility == UnitAbility.BUFF_UNIT_ON_ENEMY_SPELL) {
    							int attack = unit.getAttack();
    							int health = unit.getHealth();
    							attack = attack+1;
    							health = health+1;
    							EffectAnimation ef = BasicObjectBuilders.loadEffect(StaticConfFiles.f1_buff);
    							BasicCommands.playEffectAnimation(out, ef, tile);
    							unit.setAttack(attack);
    							unit.setHealth(health);
    							BasicCommands.setUnitAttack(out, unit, attack);
    							BasicCommands.setUnitHealth(out, unit, health);
    							break;
    						}
    					}
    				}
    			}   			
    		}
    	}
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
    	gameState.board.getTile(unit.getPosition()).clearUnit();
    	gameState.board.deleteUnit(unit);
    	BasicCommands.setUnitHealth(out, unit, 0);
    	BasicCommands.playUnitAnimation(out, unit,UnitAnimationType.death);
    	try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
    	BasicCommands.deleteUnit(out, unit);
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
    			if(unit!=null) {
    				unit.setHasFlying(true);
    			}
    		}else if(unitAbility == UnitAbility.HEAL_AVATAR_ON_SUMMON) {
    			Heal_Avatar_On_Summon(out, gameState);
    		}else if(unitAbility == UnitAbility.PROVOKE) {
    			if(unit!=null) {
    				unit.setHasProvoke(true);
    			}
    		}else if(unitAbility == UnitAbility.RANGED) {
    			if(unit!=null) {
    				unit.setHasRanged(true);
    			}
    		}else if(unitAbility == UnitAbility.SUMMON_ANYWHERE) {
    			if(unit!=null) {
    				unit.setHasAirDrop(true);
    			}
    		}
    	}
    }
}
