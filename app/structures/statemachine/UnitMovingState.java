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

public class UnitMovingState extends State {
    Tile targetTile = null;
    Tile startTile = null;
    Unit selectedUnit = null;
	public UnitMovingState(Unit selectedUnit, Tile startTile, Tile targetTile) {
		this.selectedUnit = selectedUnit;
		this.startTile = startTile;
		this.targetTile = targetTile;
	}
    private void initiateMove(ActorRef out, GameState gameState) {
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
        	System.out.println(gameState.board.getTile(startX,targetY).getUnit());
        	System.out.println(gameState.board.getTile(startX,targetY).getUnit());
        	if(null!=gameState.board.getTile(startX,targetY).getUnit()
        		&&null==gameState.board.getTile(targetX, startY).getUnit())
        	{
        		//Move horizontally first
        		 startTile.clearUnit();        		 
        	     BasicCommands.moveUnitToTile(out, selectedUnit, targetTile,false);
        	}
        	else if(null==gameState.board.getTile(startX,targetY).getUnit()
            		&&null!=gameState.board.getTile(targetX, startY).getUnit())
        	{
        		//Move vertically first
        		startTile.clearUnit();;
        		BasicCommands.moveUnitToTile(out, selectedUnit, targetTile,true);
        	}
        	else if(null==gameState.board.getTile(startX,targetY).getUnit()
            		&&null==gameState.board.getTile(targetX, startY).getUnit())
        	{
        		startTile.clearUnit();;
                BasicCommands.moveUnitToTile(out, selectedUnit, targetTile);
        	}
        	else
        	{
        		//do not move
        		this.targetTile=startTile;
        		BasicCommands.moveUnitToTile(out, selectedUnit, startTile);
        		System.out.println("Do not move");
        	}
        }
        else
        {
        	startTile.clearUnit();;  
        	BasicCommands.moveUnitToTile(out, selectedUnit, targetTile);
        }
//        startTile.setUnit(null);
//        BasicCommands.moveUnitToTile(out, selectedUnit, targetTile);
    }
    @Override
    public void handleInput(ActorRef out, GameState gameState, JsonNode message, EventProcessor event, GameStateMachine gameStateMachine) {
            if(event instanceof UnitStopped) {
            	//Check the unit is moved or not
             	targetTile.setUnit(selectedUnit);
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
