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
//<<<<<<< HEAD
	
    private void initiateMove(ActorRef out, GameState gameState ) {
     
        //Check the startTile has surrounding occupied tiles or not
        System.out.println("Start to move");
        if(!selectedUnit.isAi()&&gameState.currentTurn == Turn.PLAYER)
        {
        	unitMove(out, gameState);	
        }
        else if(selectedUnit.isAi())
        {
        	if(null==targetTile.getUnit())
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
    	if(1==Math.abs(targetX-startX)&&1==Math.abs(targetY-startY))
        {
        	Unit unit1=gameState.board.getTile(startX,targetY).getUnit();
        	Unit unit2=gameState.board.getTile(targetX,startY).getUnit();
        	if(null==unit1&&null!=unit2&&unit2.isAi())
        	{
 
        		BasicCommands.moveUnitToTile(out, selectedUnit, targetTile,false,gameState);
        	    selectedUnit.setMovement(false);
        	}
        	else if(null!=unit1&&null==unit2&&unit1.isAi())
        	{
            	//Depend on the unit is ai or not
                BasicCommands.moveUnitToTile(out, selectedUnit, targetTile,true,gameState);
                selectedUnit.setMovement(false);
        	}
        	else
        	{
        		//do not move
        		BasicCommands.moveUnitToTile(out, selectedUnit, startTile, gameState);
        		gameState.moved=false;
        		System.out.println("Do not move");
        	}
        }
        else
        {	
        	BasicCommands.moveUnitToTile(out, selectedUnit, targetTile, gameState);
            selectedUnit.setMovement(false);
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
        	if(null==unit1&&null!=unit2&&!unit2.isAi())
        	{
        		//Move horizontally first
        		 startTile.clearUnit();
        	     BasicCommands.moveUnitToTile(out, selectedUnit, targetTile,true,gameState);
                 selectedUnit.setMovement(false);
        	}
        	else if(null!=unit1&&null==unit2&&!unit1.isAi())
        	{
        		//Move vertically first
        		startTile.clearUnit();
        		BasicCommands.moveUnitToTile(out, selectedUnit, targetTile,false,gameState);
        	    selectedUnit.setMovement(false);
        	}
        	else if(null==unit1&&null==unit2)
        	{
                BasicCommands.moveUnitToTile(out, selectedUnit, targetTile, gameState);
                selectedUnit.setMovement(false);
        	}
        	else
        	{
        		//do not move
        		this.targetTile=startTile;
        		BasicCommands.moveUnitToTile(out, selectedUnit, startTile, gameState);
        		gameState.moved=false;
        		System.out.println("Do not move");
        	}
        }
        else
        {
        	
        	BasicCommands.moveUnitToTile(out, selectedUnit, targetTile,gameState);
            selectedUnit.setMovement(false);
        }
    }
    @Override
    public void handleInput(ActorRef out, GameState gameState, JsonNode message, EventProcessor event, GameStateMachine gameStateMachine) {
            if(event instanceof UnitStopped) {
            	//Check the unit is moved or not
				targetTile.setUnit(selectedUnit);
                selectedUnit.setPositionByTile(targetTile);
                if(gameState.currentTurn==Turn.PLAYER)
                	gameStateMachine.setState( nextState != null? nextState : new NoSelectionState(), out, gameState);
                else
                	gameStateMachine.setState( nextState != null? nextState : new EndTurnState());
            }
            else if(!selectedUnit.getMovement()&&gameState.currentTurn==Turn.PLAYER)
            {
            	gameStateMachine.setState(new NoSelectionState());
            }         	
            else if(gameState.currentTurn==Turn.AI)
            {
            	if(nextState!=null)
            		gameStateMachine.setState(nextState);
            	else
            		gameStateMachine.setState(new EndTurnState());
            }
            else
            {
            	gameStateMachine.setState(new EndTurnState());
            }
            	
    }



	@Override
	public void enter(ActorRef out, GameState gameState) {
		if(selectedUnit.getMovement())
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
