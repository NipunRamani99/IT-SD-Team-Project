package structures.statemachine;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import events.EventProcessor;
import structures.GameState;
import structures.basic.Unit;
import structures.basic.UnitAnimationType;
import structures.basic.Tile;
import structures.basic.Tile.Occupied;

public class HumanAttackState implements State{

	
	HumanAttackState(ActorRef out, Unit selectedUnit, Tile startTile, Tile targetTile, GameState gameState,GameStateMachine gameStateMachine)
	{
		try {
			System.out.println("Unit attack"); 
			getUnitOnTileAttack(out, startTile,targetTile);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	@Override
	public void handleInput(ActorRef out, GameState gameState, JsonNode message, EventProcessor event,
			GameStateMachine gameStateMachine) {
		// Try to get the unit and attack
		gameStateMachine.setState(new NoSelectionState());
		
	}

	
	private void getUnitOnTileAttack(ActorRef out,Tile startTile,Tile targetTile)throws InterruptedException
	{
		//get the unit from the first tile
		Unit unit =startTile.getUnit();
		//get ai unit from the second tile
		Unit enemyUnit= targetTile.getAiUnit();
		//Attack animation
		BasicCommands.playUnitAnimation(out, unit, UnitAnimationType.attack);
		try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
		int attackHealth=enemyUnit.getHealth()-unit.getAttack();
		unitAttack(out, startTile, targetTile,unit, enemyUnit, attackHealth);		
	}
	
    
    /**
     * User unit Attack the Ai unit
     */
    private void unitAttack(ActorRef out,Tile startTile,Tile targetTile, Unit unit,Unit enemyUnit, int health)
    {
    	
    	if(health>0)
		{
			BasicCommands.setUnitHealth(out, enemyUnit,health );
			BasicCommands.playUnitAnimation(out, enemyUnit, UnitAnimationType.attack);
			//The human unit will be attacked
			int attackBackHealth=unit.getHealth()-enemyUnit.getAttack();
			if(attackBackHealth>0)
			{
				BasicCommands.setUnitHealth(out,unit,attackBackHealth );
				try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
			}
			else //the human unit dead
			{
				BasicCommands.setUnitHealth(out,unit,0 );
				BasicCommands.deleteUnit(out, enemyUnit);
				startTile.clearUnit();
				try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
			}
		
		}
		else
		{
			BasicCommands.setUnitHealth(out, enemyUnit,0 );
			BasicCommands.playUnitAnimation(out, enemyUnit, UnitAnimationType.death);
			BasicCommands.deleteUnit(out, enemyUnit);
			targetTile.clearUnit();
			try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
		}	   	
    	
    }
}
