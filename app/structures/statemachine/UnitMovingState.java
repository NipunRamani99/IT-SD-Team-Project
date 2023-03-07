package structures.statemachine;

import akka.actor.ActorRef;
import com.fasterxml.jackson.databind.JsonNode;
import commands.BasicCommands;
import events.EventProcessor;
import events.UnitMoving;
import events.UnitStopped;
import structures.GameState;
import structures.Turn;
import structures.basic.Tile;
import structures.basic.TileState;
import structures.basic.Unit;
import java.lang.Math;

public class UnitMovingState extends State {
    Tile targetTile = null;
    Tile startTile = null;
    Unit selectedUnit = null;

	boolean isAiAction = false;

	public UnitMovingState(Unit selectedUnit, Tile startTile, Tile targetTile) {
		this.selectedUnit = selectedUnit;
		this.startTile = startTile;
		this.targetTile = targetTile;
	}
<<<<<<< HEAD
	
    private void initiateMove(ActorRef out, GameState gameState ) {
     
        //Check the startTile has surrounding occupied tiles or not
        System.out.println("Start to move");
        if(!selectedUnit.isAi()&&gameState.currentTurn == Turn.PLAYER)
        {
        	unitMove(out, gameState);	
        }
        else if(selectedUnit.isAi())
        {
        	if(null==targetTile.getUnit()&&null==targetTile.getAiUnit())
        	{
        		aiUnitMove(out, gameState);
    		   // State attackState = new HumanAttackState(unit, startTile, false, true);
                //State moveState = new UnitMovingState(unit, tileClicked, adjacentTile);
    		  //  attackState.setNextState(attackState);
               // gameStateMachine.setState(attackState, out, gameState);
        	}
        	else if(null!=targetTile.getUnit())
        	{
//        		 Unit unit = targetTile.getUnit();
//        		 State attackState = new HumanAttackState(unit, startTile, false, true);
        	}
        	
        }
        else
        {
        	//do nothing
        }
    }
    
    private void unitMove(ActorRef out,GameState gameState)
    {
    	//Get the target tile x, y position
=======

	public UnitMovingState(Unit selectedUnit, Tile startTile, Tile targetTile, boolean aiAction) {
		this.selectedUnit = selectedUnit;
		this.startTile = startTile;
		this.targetTile = targetTile;
		this.isAiAction = aiAction;
	}

    private void initiateMove(ActorRef out, GameState gameState) {
        //Get the target tile x, y position
>>>>>>> origin/dev/nipun
        int targetX = this.targetTile.getTilex();
        int targetY = this.targetTile.getTiley();
        //Get the start tile x, y position
        int startX = startTile.getTilex();
        int startY = startTile.getTiley();
<<<<<<< HEAD
    	if(1==Math.abs(targetX-startX)&&1==Math.abs(targetY-startY))
=======
        //Check the startTile has surrounding occupied tiles or not
        System.out.println("Start to move");
		selectedUnit.setMovement(false);
        if(1==Math.abs(targetX-startX)&&1==Math.abs(targetY-startY))
>>>>>>> origin/dev/nipun
        {
        	Unit unit1=gameState.board.getTile(startX,targetY).getAiUnit();
        	Unit unit2=gameState.board.getTile(targetX,startY).getAiUnit();
        	if(null==unit1&&null!=unit2)
        	{
<<<<<<< HEAD
            	//Depend on the unit is ai or not
            	if(selectedUnit.isAi())
            	{
            		startTile.clearAiUnit();
            	}
            	else
            	{
            		startTile.clearUnit();
            	}
            	//Move horizontally first
        	    BasicCommands.moveUnitToTile(out, selectedUnit, targetTile,true);
=======
        		//Move horizontally first
				if(gameState.currentTurn == Turn.PLAYER)
					startTile.setUnit(null);
				else
					startTile.setAiUnit(null);
        	     BasicCommands.moveUnitToTile(out, selectedUnit, targetTile,true);
>>>>>>> origin/dev/nipun

        	}
        	else if(null!=unit1&&null==unit2)
        	{
<<<<<<< HEAD
            	//Depend on the unit is ai or not
            	if(selectedUnit.isAi())
            	{
            		startTile.clearAiUnit();
            	}
            	else
            	{
            		startTile.clearUnit();
            	}
            	//Move vertically first
=======
        		//Move vertically first
				if(gameState.currentTurn == Turn.PLAYER)
					startTile.setUnit(null);
				else
					startTile.setAiUnit(null);
>>>>>>> origin/dev/nipun
        		BasicCommands.moveUnitToTile(out, selectedUnit, targetTile,false);
        	}
        	else if(null==unit1&&null==unit2)
        	{
<<<<<<< HEAD

            	//Depend on the unit is ai or not
            	if(selectedUnit.isAi())
            	{
            		startTile.clearAiUnit();
            	}
            	else
            	{
            		startTile.clearUnit();
            	}
            	
=======
				if(gameState.currentTurn == Turn.PLAYER)
					startTile.setUnit(null);
				else
					startTile.setAiUnit(null);
>>>>>>> origin/dev/nipun
                BasicCommands.moveUnitToTile(out, selectedUnit, targetTile);
        	}
        	else
        	{
        		//do not move
        		this.targetTile=startTile;
        		BasicCommands.moveUnitToTile(out, selectedUnit, startTile);
        		gameState.moved=false;
        		System.out.println("Do not move");
        	}
        }
        else
        {

        	//Depend on the unit is ai or not
        	if(selectedUnit.isAi())
        	{
        		startTile.clearAiUnit();
        	}
        	else
        	{
        		startTile.clearUnit();
        	}
        	
        	BasicCommands.moveUnitToTile(out, selectedUnit, targetTile);
        }
    }
    
    private void aiUnitMove(ActorRef out,GameState gameState)
    {
       	//Get the target tile x, y position
        int targetX = this.targetTile.getTilex();
        int targetY = this.targetTile.getTiley();
        //Get the start tile x, y position
        int startX = startTile.getTilex();
        int startY = startTile.getTiley();
    	if(1==Math.abs(targetX-startX)&&1==Math.abs(targetY-startY))
        {
        	Unit unit1=gameState.board.getTile(startX,targetY).getUnit();
        	Unit unit2=gameState.board.getTile(targetX,startY).getUnit();
        	if(null==unit1&&null!=unit2)
        	{
        		//Move horizontally first
        		 startTile.clearAiUnit();
        	     BasicCommands.moveUnitToTile(out, selectedUnit, targetTile,true);

        	}
        	else if(null!=unit1&&null==unit2)
        	{
        		//Move vertically first
        		startTile.clearAiUnit();
        		BasicCommands.moveUnitToTile(out, selectedUnit, targetTile,false);
        	}
        	else if(null==unit1&&null==unit2)
        	{
        		startTile.clearAiUnit();
                BasicCommands.moveUnitToTile(out, selectedUnit, targetTile);
        	}
        	else
        	{
        		//do not move
        		this.targetTile=startTile;
        		BasicCommands.moveUnitToTile(out, selectedUnit, startTile);
        		gameState.moved=false;
        		System.out.println("Do not move");
        	}
        }
        else
        {
        	startTile.clearAiUnit();
        	BasicCommands.moveUnitToTile(out, selectedUnit, targetTile);
        }
    }
    @Override
    public void handleInput(ActorRef out, GameState gameState, JsonNode message, EventProcessor event, GameStateMachine gameStateMachine) {
            if(event instanceof UnitStopped) {
            	//Check the unit is moved or not
				if(gameState.currentTurn == Turn.PLAYER) {
					targetTile.setUnit(selectedUnit);
				} else {
					targetTile.setAiUnit(selectedUnit);
				}
                selectedUnit.setPositionByTile(targetTile);
                gameStateMachine.setState( nextState != null? nextState : new NoSelectionState(), out, gameState);
            }
    }



	@Override
	public void enter(ActorRef out, GameState gameState) {
		initiateMove(out,gameState);
	}

	@Override
	public void exit(ActorRef out, GameState gameState) {

	}

}
