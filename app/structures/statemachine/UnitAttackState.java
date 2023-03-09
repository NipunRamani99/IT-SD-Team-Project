package structures.statemachine;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import events.Attack;
import events.EventProcessor;
import events.Heartbeat;
import structures.GameState;
import structures.Turn;
import structures.basic.Unit;
import structures.basic.UnitAnimationType;
import structures.basic.Tile;

public class UnitAttackState extends State{

	private Unit selectedUnit = null;

	private Unit enemyUnit = null;

	public UnitAttackState(Unit selectedUnit, Tile targetTile, boolean isPlayer)
	{
		this.selectedUnit = selectedUnit;
		if(isPlayer)
			this.enemyUnit = targetTile.getAiUnit();
		else
			this.enemyUnit = targetTile.getUnit();
	}

	public UnitAttackState(Unit selectedUnit, Unit enemyUnit)
	{
		this.selectedUnit = selectedUnit;
		this.enemyUnit = enemyUnit;
		this.enemyUnit.setAttackBack(false);
		this.selectedUnit.setAttackBack(false);
	}

	public UnitAttackState(Unit selectedUnit, Tile targetTile, boolean reactAttack, boolean isPlayer)
	{

		this.selectedUnit = selectedUnit;	
		this.selectedUnit.setAttackBack(false);
		this.enemyUnit = isPlayer ? targetTile.getAiUnit() : targetTile.getUnit();
		this.enemyUnit.setAttackBack(false);
		if(!reactAttack && selectedUnit.canAttack()) {
			State reactState = new UnitAttackState(isPlayer ? targetTile.getAiUnit() : targetTile.getUnit(), selectedUnit);
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
		{
			if(gameState.currentTurn==Turn.PLAYER)
				gameStateMachine.setState(nextState != null ? nextState : new NoSelectionState(), out, gameState);
			else
				gameStateMachine.setState(nextState != null ? nextState : new EndTurnState());
		}	
	}

	@Override
	public void enter(ActorRef out, GameState gameState) {
		try {
				if(selectedUnit.canAttack()||selectedUnit.isAttackBack())
				{
					//make sure every unit can attack once
					selectedUnit.setCanAttack(false);
					System.out.println("Unit attack");
					getUnitOnTileAttack(out, gameState);
				}
				else
				{
					if(nextState==null)
						nextState=new NoSelectionState();
				}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void exit(ActorRef out, GameState gameState) {
		
		if(gameState.currentTurn==Turn.AI)
		{
			if(nextState==null)
			{
				nextState=new EndTurnState();
			}
		}
		
	}


	private void getUnitOnTileAttack(ActorRef out, GameState gameState)throws InterruptedException
	{
		//Attack animation
		BasicCommands.playUnitAnimation(out, this.selectedUnit, UnitAnimationType.attack);
		try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}

		BasicCommands.playUnitAnimation(out, this.selectedUnit, UnitAnimationType.idle);
		try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
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
          Attack.deleteEnemyUnit(out, enemyUnit, gameState);
          Attack.setPlayerHealth(out, health, enemyUnit, gameState);

		}
    	else
    	{
    	  enemyUnit.setAttackBack(true);
    	  BasicCommands.setUnitHealth(out, enemyUnit,health );
    	  try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
    	  Attack.setPlayerHealth(out, health, enemyUnit, gameState);
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
