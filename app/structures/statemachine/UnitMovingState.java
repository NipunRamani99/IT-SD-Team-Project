package structures.statemachine;

import akka.actor.ActorRef;
import com.fasterxml.jackson.databind.JsonNode;
import commands.BasicCommands;
import events.EventProcessor;
import events.UnitMoving;
import events.UnitStopped;
import structures.GameState;
import structures.basic.Tile;
import structures.basic.TileState;
import structures.basic.Unit;
import java.lang.Math;

public class UnitMovingState implements State {
    Tile targetTile = null;
    Tile startTile = null;
    Unit selectedUnit = null;

    public UnitMovingState(ActorRef out, Unit selectedUnit, Tile startTile, Tile targetTile, GameState gameState,GameStateMachine gameStateMachine) {
        this.selectedUnit = selectedUnit;
        this.targetTile = targetTile;
        this.startTile=startTile;
        //Get the target tile x, y position
        int targetX= this.targetTile.getTilex();
        int targetY= this.targetTile.getTiley();
        //Get the start tile x, y position
        int startX = startTile.getTilex();
        int startY = startTile.getTiley();
        //Check the startTile has surrounding occupied tiles or not
        System.out.println("Start to move");
        if(1==Math.abs(targetX-startX)&&1==Math.abs(targetY-startY)) 
        {
        	System.out.println(gameState.board.getTile(startX,targetY).getUnit());
        	System.out.println(gameState.board.getTile(startX,targetY).getUnit());
        	if(null!=gameState.board.getTile(startX,targetY).getUnit()
        		&&null==gameState.board.getTile(targetX, startY).getUnit())
        	{
        		//Move horizontally first
        		 startTile.setUnit(null);
        	     BasicCommands.moveUnitToTile(out, selectedUnit, targetTile,false);  		
        	}
        	else if(null==gameState.board.getTile(startX,targetY).getUnit()
            		&&null!=gameState.board.getTile(targetX, startY).getUnit())
        	{
        		//Move vertically first
        		startTile.setUnit(null);
        		BasicCommands.moveUnitToTile(out, selectedUnit, targetTile,true);  	
        	}
        	else if(null==gameState.board.getTile(startX,targetY).getUnit()
            		&&null==gameState.board.getTile(targetX, startY).getUnit())
        	{
        		startTile.setUnit(null);
                BasicCommands.moveUnitToTile(out, selectedUnit, targetTile);
        	}
        	else
        	{
        		//do not move
        		this.targetTile=startTile;

//        		startTile.setUnit(null);
        		BasicCommands.moveUnitToTile(out, selectedUnit, startTile);  
        		System.out.println("Do not move");
        	}
        }
        else
        {
        	startTile.setUnit(null);
            BasicCommands.moveUnitToTile(out, selectedUnit, targetTile);
        }
//        startTile.setUnit(null);
//        BasicCommands.moveUnitToTile(out, selectedUnit, targetTile);
    }
    @Override
    public void handleInput(ActorRef out, GameState gameState, JsonNode message, EventProcessor event, GameStateMachine gameStateMachine) {
            if(event instanceof UnitStopped) {
            	//Check the unit is moved or not
            	if(targetTile!=startTile) targetTile.setUnit(selectedUnit);
            
                selectedUnit.setPositionByTile(targetTile);
                gameStateMachine.setState(new NoSelectionState());
            }
    }
}
