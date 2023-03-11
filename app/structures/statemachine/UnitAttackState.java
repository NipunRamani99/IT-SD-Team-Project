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
	
	private boolean counterAttack=true;

	public UnitAttackState(Unit selectedUnit, Tile targetTile, boolean isPlayer)
	{
		this.selectedUnit = selectedUnit;
		this.enemyUnit = targetTile.getUnit();
	}

	public UnitAttackState(Unit selectedUnit, Unit enemyUnit)
	{
		this.selectedUnit = selectedUnit;
		this.enemyUnit = enemyUnit;

	}

	public UnitAttackState(Unit selectedUnit, Tile targetTile, boolean reactAttack, boolean isPlayer)
	{

		this.selectedUnit = selectedUnit;	
		this.enemyUnit =targetTile.getUnit();
		if(!reactAttack && selectedUnit.canAttack()) {
			this.counterAttack=true;
		}
	}
	
	@Override
	public void handleInput(ActorRef out, GameState gameState, JsonNode message, EventProcessor event,
			GameStateMachine gameStateMachine) {
		// Try to get the unit and attack
		if(event instanceof Heartbeat)
		{
			if(this.selectedUnit!=null&& this.enemyUnit!=null)
			{
				BasicCommands.playUnitAnimation(out, this.selectedUnit, UnitAnimationType.idle);
				BasicCommands.playUnitAnimation(out, this.enemyUnit, UnitAnimationType.idle);
			}
			else 
			{
				if(this.selectedUnit!=null)
					BasicCommands.playUnitAnimation(out, this.selectedUnit, UnitAnimationType.idle);
				if(this.enemyUnit!=null)
					BasicCommands.playUnitAnimation(out, this.enemyUnit, UnitAnimationType.idle);
				gameStateMachine.setState(new EndTurnState(), out, gameState);
			}
			if(gameState.currentTurn==Turn.PLAYER)
				gameStateMachine.setState(nextState != null ? nextState : new NoSelectionState(), out, gameState);
			else
			{
				gameStateMachine.setState(nextState != null ? nextState : new EndTurnState(), out, gameState);
				if(nextState!=null) {}
					//enter(out,gameState);
			}
		}
	}

	@Override
	public void enter(ActorRef out, GameState gameState) {
	
		if(selectedUnit.canAttack()&&null!=enemyUnit)
		{
			//make sure every unit can attack once
			System.out.println("Unit attack");
			getUnitOnTileAttack(out, gameState);
		}
		if(null==enemyUnit)
		{
			nextState= new EndTurnState();
		}
		else
		{
			if(gameState.currentTurn==Turn.PLAYER)
			{
				if(nextState==null)
					nextState=new NoSelectionState();
			}
			else
			{
				if(nextState==null)
					nextState=new EndTurnState();
			}
			
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


	private void getUnitOnTileAttack(ActorRef out, GameState gameState)
	{
		//Attack animation
		try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}
		BasicCommands.playUnitAnimation(out, this.selectedUnit, UnitAnimationType.attack);
		try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
		BasicCommands.playUnitAnimation(out, selectedUnit, UnitAnimationType.idle);
		this.selectedUnit.setCanAttack(false);
 		int attackHealth = this.enemyUnit.getHealth() - this.selectedUnit.getAttack();
		unitAttack(out, this.enemyUnit, attackHealth, gameState);		
		if(this.enemyUnit.isAttackBack())
  		  attackBack(out, this.enemyUnit,this.selectedUnit, gameState);
	}
	

    
    /**
     * User unit Attack the Ai unit
     */

    private void unitAttack(ActorRef out, Unit enemyUnit, int health, GameState gameState)
    {
    	//make sure can unit can attack back once 
    	
    	if(health <= 0)
		{
    	  enemyUnit.setAttackBack(false);
          Attack.deleteEnemyUnit(out, enemyUnit, gameState);
          Attack.setPlayerHealth(out, health, enemyUnit, gameState);

		}
    	else
    	{	
    	  BasicCommands.setUnitHealth(out, enemyUnit,health );
    	  try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
    	  Attack.setPlayerHealth(out, health, enemyUnit, gameState);
    	}

    }
    
    /**
     * The unit attack back
     * @param out
     * @param selectedUnit
     * @param enemyUnit
     * @param gameState
     */
    private void attackBack(ActorRef out, Unit selectedUnit, Unit enemyUnit, GameState gameState)
    {
    	try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
    	BasicCommands.playUnitAnimation(out, selectedUnit, UnitAnimationType.attack);
    	try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
    	BasicCommands.playUnitAnimation(out, selectedUnit, UnitAnimationType.idle);
	    selectedUnit.setAttackBack(false);
 		int attackHealth = enemyUnit.getHealth() - selectedUnit.getAttack();
		unitAttack(out, enemyUnit, attackHealth, gameState);
    }
    

}
