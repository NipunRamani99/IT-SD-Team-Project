package structures.statemachine;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import events.EventProcessor;
import events.Heartbeat;
import structures.GameState;
import structures.Turn;
import structures.basic.Unit;
import structures.basic.UnitAnimationType;
import structures.basic.Tile;
import structures.basic.Tile.Occupied;
import structures.basic.TileState;
import utils.BasicObjectBuilders;

public class HumanAttackState extends State{

	private Unit selectedUnit = null;

	private Unit enemyUnit = null;

	HumanAttackState(Unit selectedUnit, Tile targetTile, boolean isPlayer)
	{
		this.selectedUnit = selectedUnit;
		if(isPlayer)
			this.enemyUnit = targetTile.getAiUnit();
		else
			this.enemyUnit = targetTile.getUnit();
	}

	HumanAttackState(Unit selectedUnit, Unit enemyUnit)
	{
		this.selectedUnit = selectedUnit;
		this.enemyUnit = enemyUnit;
	}

	public HumanAttackState(Unit selectedUnit, Tile targetTile, boolean reactAttack, boolean isPlayer)
	{
		this.selectedUnit = selectedUnit;

		this.enemyUnit = isPlayer ? targetTile.getAiUnit() : targetTile.getUnit();
		if(!reactAttack) {
			State reactState = new HumanAttackState(isPlayer ? targetTile.getAiUnit() : targetTile.getUnit(), selectedUnit);
			if (nextState != null) {
				reactState.setNextState(nextState);
			}
			this.nextState = reactState;
		}
	}
	
	@Override
	public void handleInput(ActorRef out, GameState gameState, JsonNode message, EventProcessor event,
			GameStateMachine gameStateMachine) {
		// Try to get the unit and attack
		if(event instanceof  Heartbeat)
			gameStateMachine.setState(nextState != null ? nextState : new NoSelectionState(), out, gameState);
	}

	@Override
	public void enter(ActorRef out, GameState gameState) {
		try {
			System.out.println("Unit attack");
			getUnitOnTileAttack(out, gameState);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void exit(ActorRef out, GameState gameState) {
		

	}


	private void getUnitOnTileAttack(ActorRef out, GameState gameState)throws InterruptedException
	{
		//Attack animation
		BasicCommands.playUnitAnimation(out, this.selectedUnit, UnitAnimationType.attack);
		try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
		
		int attackHealth = this.enemyUnit.getHealth() - this.selectedUnit.getAttack();
		unitAttack(out, enemyUnit, attackHealth, gameState);
	}
	

    
    /**
     * User unit Attack the Ai unit
     */

    private void unitAttack(ActorRef out, Unit enemyUnit, int health, GameState gameState)
    {
    	if(health <= 0)
		{
			BasicCommands.setUnitHealth(out, enemyUnit,0 );
			BasicCommands.playUnitAnimation(out, enemyUnit, UnitAnimationType.death);
			try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
			
			BasicCommands.deleteUnit(out, enemyUnit);
			if(enemyUnit.isAi())
			{
				gameState.board.getTile(enemyUnit.getPosition().getTilex(), enemyUnit.getPosition().getTiley()).clearAiUnit();	
			}
			else
			{
				gameState.board.getTile(enemyUnit.getPosition().getTilex(), enemyUnit.getPosition().getTiley()).clearUnit();
			}
			
			
		}
    	else
    	{
    		BasicCommands.setUnitHealth(out, enemyUnit,health );
    	}
    	
    	if(gameState.currentTurn==Turn.AI)
    	{
    		//nextState=nextState.getNextState();
    	}

    }
    
    private void setAvatarHealth(ActorRef out, Unit unit,int health)
    {
    	BasicCommands.setUnitHealth(out,unit,health );
		try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}

//			gameState.board.getTile(enemyUnit.getPosition().getTilex(), enemyUnit.getPosition().getTiley()).clearUnit();
//			nextState = nextState.getNextState();

    }
}
