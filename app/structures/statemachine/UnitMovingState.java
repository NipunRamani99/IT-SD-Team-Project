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
import utils.Constants;

import java.lang.Math;

public class UnitMovingState implements State {
    Tile targetTile = null;
    Tile startTile = null;
    Unit selectedUnit = null;

    public UnitMovingState(ActorRef out, Unit selectedUnit, Tile startTile, Tile targetTile, GameState gameState,GameStateMachine gameStateMachine) {
        this.selectedUnit = selectedUnit;
        this.targetTile = targetTile;
        this.startTile=startTile;
    	//If the target tile has no Ai unit, just move
    	moveToTile(out, gameState, startTile, targetTile, gameStateMachine);
          
      
//        startTile.setUnit(null);
//        BasicCommands.moveUnitToTile(out, selectedUnit, targetTile);
    }
    @Override
    public void handleInput(ActorRef out, GameState gameState, JsonNode message, EventProcessor event, GameStateMachine gameStateMachine) {
            if(event instanceof UnitStopped) {
            	//Check the unit is moved or not
            	if(targetTile!=startTile) targetTile.setUnit(selectedUnit);
            
                selectedUnit.setPositionByTile(targetTile);
                gameState.moved=false;
                gameStateMachine.setState(new NoSelectionState());
                
            }
    }
    
    //Move to tile state
    private boolean moveToTile(ActorRef out,GameState gameState, Tile startTile, Tile targetTile, GameStateMachine gameStateMachine)
    {
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
        		 startTile.clearUnit();
        	     BasicCommands.moveUnitToTile(out, selectedUnit, targetTile,false);
        	     gameState.moved=true;
        	}
        	else if(null==gameState.board.getTile(startX,targetY).getUnit()
            		&&null!=gameState.board.getTile(targetX, startY).getUnit())
        	{
        		//Move vertically first
        		startTile.clearUnit();
        		BasicCommands.moveUnitToTile(out, selectedUnit, targetTile,true); 
        		gameState.moved=true;	
        	}
        	else if(null==gameState.board.getTile(startX,targetY).getUnit()
            		&&null==gameState.board.getTile(targetX, startY).getUnit())
        	{
        		startTile.clearUnit();
                BasicCommands.moveUnitToTile(out, selectedUnit, targetTile);
                gameState.moved=true;
        	}
        	else
        	{
        		//do not move
        		this.targetTile=startTile;
        		BasicCommands.moveUnitToTile(out, selectedUnit, startTile);
        		//set move to false
        		gameState.moved=false;
        		System.out.println("Do not move");
        	}
        }
        else
        {
        	startTile.clearUnit();  
        	BasicCommands.moveUnitToTile(out, selectedUnit, targetTile);
        	gameState.moved=true;
        	
         
        }
        //If it is move and attack function, set the state machine to move and attack
        if(true==gameState.isAttacking)
        {
        	gameStateMachine.setState(new HumanAttackState(out, selectedUnit, startTile, targetTile, gameState, gameStateMachine));
        }
        return gameState.moved;
    }
    
//    //The is for the unit to move and attack
//    private void moveAndAttack(ActorRef out, GameState gameState, Tile startTile, Tile targetTile, GameStateMachine gameStateMachine)
//    {
//  	    //Get the target tile x, y position
//        int targetX= targetTile.getTilex();
//        int targetY= targetTile.getTiley();
//        //Get the start tile x, y position
//        int startX = startTile.getTilex();
//        int startY = startTile.getTiley();
//        
//        int newTargetX=0;
//        int newTargetY=0;
//        Tile newTargetTile=null;
//        newTargetTile=gameState.board.getTile(newTargetX, newTargetY);
//        for(int i = -1; i <=1; i++ ) {
//            for(int j = -1; j <= 1; j++) {
//                int x = targetX + i;
//                if(x < 0) continue;
//                if(x >= Constants.BOARD_WIDTH) continue;
//                int y = targetY + j;
//                if(y < 0) continue;
//                if(y >= Constants.BOARD_HEIGHT) continue;
//                Tile surroundingTile = gameState.board.getTile(x, y);
//                if(surroundingTile == targetTile)
//                    continue;
//                if (surroundingTile != null) {
//                    if(surroundingTile.getUnit() == null && surroundingTile.getAiUnit()==null) {
//                        surroundingTile.setTileState(TileState.Reachable);
//                        BasicCommands.drawTile(out, surroundingTile, 1);
//                    }
//                    else {
//                        surroundingTile.setTileState(TileState.Occupied);
//                        BasicCommands.drawTile(out, surroundingTile, 2);
//                    }
//                }
//            }
//        }
//        
//        
//    }
}
