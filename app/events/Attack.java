package events;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.*;
import structures.statemachine.GameStateMachine;

/**
 * This class will define the action of the unit attack, player attack, and attack between unit and player
 */
public class Attack {
    /**
     * This unit is for the friendly unit that will launch an attack
     */
    private Unit myUnit;
    /**
     * This unit is for the unit that will be attacked
     */
    private Unit enemyUnit;

    /**
     * This player is for the player 1 information
     */
    private Player player1;

    /**
     * This player is for the player 2 information
     */
    private Player player2;
    
    /**
     * The actor reference
     */
    private ActorRef out;
    
    /**
     * The gameState
     */
    private GameState gameState;

    /**
     * This constructor is for the attack between two units
     * @param myUnit the unit refers to the attacking unit
     * @param enemyUnit the unit refer to the attacked unit
     */
   
    /**
     * delete the enemy unit
     * @param out
     * @param enemyUnit
     * @param gameState
     */
    public static void deleteEnemyUnit(ActorRef out,Unit enemyUnit, GameState gameState)
    {
		BasicCommands.setUnitHealth(out, enemyUnit,0 );
		try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
		BasicCommands.playUnitAnimation(out, enemyUnit, UnitAnimationType.death);
		try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
		
		BasicCommands.deleteUnit(out, enemyUnit);		
		gameState.board.getTile(enemyUnit.getPosition()).clearUnit();
		gameState.board.deleteUnit(enemyUnit);
    }
    
    
    public static void setPlayerHealth(ActorRef out,int health,Unit targetUnit, GameState gameState)
    {
    	//for the human player
    	if(0==targetUnit.getId())
    	{
    		if(health<=0)
    			gameState.humanPlayer.setHealth(0);
    		else
    			gameState.humanPlayer.setHealth(health);
    		BasicCommands.setPlayer1Health(out, gameState.humanPlayer);
    		try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
    	}
    	//for the AI player
    	else if(1==targetUnit.getId())
    	{
    		if(health<=0)
    			gameState.AiPlayer.setHealth(0);
    		else
    			gameState.AiPlayer.setHealth(health);
    		BasicCommands.setPlayer2Health(out, gameState.AiPlayer);
    		try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
    	}
    	else
    	{
    		//do nothing
    	}
    }
}
