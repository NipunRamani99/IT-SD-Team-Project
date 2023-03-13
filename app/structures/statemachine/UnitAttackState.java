package structures.statemachine;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.AbilityCommands;
import commands.BasicCommands;
import events.Attack;
import events.EventProcessor;
import events.Heartbeat;
import structures.GameState;
import structures.Turn;
import structures.basic.*;
import utils.BasicObjectBuilders;
import utils.StaticConfFiles;

public class UnitAttackState extends State{

	private Unit selectedUnit = null;

	private Unit enemyUnit = null;

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
	}
	
	@Override
	public void handleInput(ActorRef out, GameState gameState, JsonNode message, EventProcessor event,
			GameStateMachine gameStateMachine) {
		// Try to get the unit and attack
		if(event instanceof Heartbeat)
		{
			if(null!=this.selectedUnit)
				BasicCommands.playUnitAnimation(out, this.selectedUnit, UnitAnimationType.idle);
			if(null!=this.enemyUnit)
				BasicCommands.playUnitAnimation(out, this.enemyUnit, UnitAnimationType.idle);
			System.out.println("Exiting UnitAttackState");
			if(gameState.currentTurn==Turn.PLAYER)
				gameStateMachine.setState(nextState != null ? nextState : new NoSelectionState(), out, gameState);
			else
			{
				gameStateMachine.setState(nextState != null ? nextState : new EndTurnState(), out, gameState);
			}
		}
	}

	@Override
	public void enter(ActorRef out, GameState gameState) {
		System.out.println("Entering UnitAttackState");

		AbilityCommands.checkIsProvoked(selectedUnit, gameState);
        if(!selectedUnit.isIsProvoked()||enemyUnit.isHasProvoke()) {
			if(selectedUnit.getAttackTimes()<1&&null!=enemyUnit)
			{
				//make sure every unit can attack once			
				//The attack time will add
				this.selectedUnit.setAttackTimes(this.selectedUnit.getAttackTimes()+1);
				this.selectedUnit.setCanAttack(false);
				System.out.println("Unit attack");
				getUnitOnTileAttack(out, gameState);
			}
			else if(selectedUnit.getAttackTimes()<2&&null!=enemyUnit&&(gameState.unitAbilityTable.getUnitAbilities(selectedUnit.getName()).contains(UnitAbility.ATTACK_TWICE)))
			{
				//unit can attack twice		
				//The attack time will add
				this.selectedUnit.setAttackTimes(this.selectedUnit.getAttackTimes()+1);
				if(selectedUnit.getAttackTimes()>1)
					this.selectedUnit.setCanAttack(false);
				System.out.println("Unit attack twice");
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
			gameState.resetBoardSelection(out);
			gameState.resetBoardSelection(out);
			
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
		if(this.selectedUnit.isHasRanged())
		{
			EffectAnimation projectile = BasicObjectBuilders.loadEffect(StaticConfFiles.f1_projectiles);
			BasicCommands.playUnitAnimation(out, this.selectedUnit, UnitAnimationType.attack);
		}
		else 	
			BasicCommands.playUnitAnimation(out, this.selectedUnit, UnitAnimationType.attack);
		try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}

		BasicCommands.playUnitAnimation(out, selectedUnit, UnitAnimationType.idle);
		
 		int attackHealth = this.enemyUnit.getHealth() - this.selectedUnit.getAttack();
 		
 		unitAttack(out, this.enemyUnit, attackHealth, gameState);
 		
		//BUFF_UNIT_ON_AVATAR_DAMAGE
		if(!selectedUnit.isAi() && selectedUnit.isAvatar()) {
			AbilityCommands.BUFF_UNIT_ON_AVATAR_DAMAGE(out, gameState);
		}
 		//for attack back
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
    	if(enemyUnit.withinDistance(selectedUnit))
    	{
    		try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
        	BasicCommands.playUnitAnimation(out, selectedUnit, UnitAnimationType.attack);
        	try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
        	BasicCommands.playUnitAnimation(out, selectedUnit, UnitAnimationType.idle);
    	    selectedUnit.setAttackBack(false);
    	    
    		//BUFF_UNIT_ON_AVATAR_DAMAGE
    		if(!selectedUnit.isAi() && selectedUnit.isAvatar()) {
    			AbilityCommands.BUFF_UNIT_ON_AVATAR_DAMAGE(out, gameState);
    		}
    		
     		int attackHealth = enemyUnit.getHealth() - selectedUnit.getAttack();
    		unitAttack(out, enemyUnit, attackHealth, gameState);
    	}
    	
    }
    

}
