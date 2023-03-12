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
        int targetX = this.targetTile.getTilex();
        int targetY = this.targetTile.getTiley();
        //Get the start tile x, y position
        int startX = startTile.getTilex();
        int startY = startTile.getTiley();
        //Check the startTile has surrounding occupied tiles or not
        System.out.println("Start to move");
        //change the TileState
        startTile.setTileState(TileState.None);
        targetTile.setTileState(TileState.Occupied);
        if(1==Math.abs(targetX-startX)&&1==Math.abs(targetY-startY))
        {
        	Unit unit1=gameState.board.getTile(startX,targetY).getAiUnit();
        	Unit unit2=gameState.board.getTile(targetX,startY).getAiUnit();
        	if(null==unit1&&null!=unit2)
        	{
        		//Move horizontally first
        		 startTile.clearUnit();        		 
        	     BasicCommands.moveUnitToTile(out, selectedUnit, targetTile,false);
            	//Move vertically first
            	startTile.clearUnit();
        		BasicCommands.moveUnitToTile(out, selectedUnit, targetTile,false);
        	    selectedUnit.setCanMove(false);
        	}
        	else if(null==unit1&&null==unit2)
        	{
        		//Move vertically first
        		startTile.clearUnit();;
        		BasicCommands.moveUnitToTile(out, selectedUnit, targetTile,true);
        	}
        	else if(null==gameState.board.getTile(startX,targetY).getUnit()
            		&&null==gameState.board.getTile(targetX, startY).getUnit())
        	{
        		startTile.clearUnit();;
            	//Depend on the unit is ai or not
            	startTile.clearUnit();
                BasicCommands.moveUnitToTile(out, selectedUnit, targetTile);
                selectedUnit.setCanMove(false);
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
        	startTile.clearUnit();;  
        	startTile.clearUnit();	
        	BasicCommands.moveUnitToTile(out, selectedUnit, targetTile);
            selectedUnit.setCanMove(false);
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
                 selectedUnit.setCanMove(false);
        	}
        	else if(null!=unit1&&null==unit2)
        	{
        		//Move vertically first
        		startTile.clearAiUnit();
        		BasicCommands.moveUnitToTile(out, selectedUnit, targetTile,false);
        	    selectedUnit.setCanMove(false);
        	}
        	else if(null==unit1&&null==unit2)
        	{
        		startTile.clearAiUnit();
                BasicCommands.moveUnitToTile(out, selectedUnit, targetTile);
                selectedUnit.setCanMove(false);
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
            selectedUnit.setCanMove(false);
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
                if(gameState.currentTurn==Turn.PLAYER)
                	gameStateMachine.setState( nextState != null? nextState : new NoSelectionState(), out, gameState);
                else
                	gameStateMachine.setState( nextState != null? nextState : new EndTurnState());
            }
            else if(!selectedUnit.canMove()&&gameState.currentTurn==Turn.PLAYER)
            {
            	gameStateMachine.setState(new NoSelectionState());
            }         	
            else if(gameState.currentTurn==Turn.AI)
            {
            	gameStateMachine.setState(nextState);
            }
            else
            {
            	gameStateMachine.setState(new EndTurnState());
            }
            	
    }



	@Override
	public void enter(ActorRef out, GameState gameState) {
		if(selectedUnit.canMove())
			initiateMove(out,gameState);
			
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

}
