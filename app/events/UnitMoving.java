package events;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Tile.Occupied;
import structures.basic.UnitAnimationType;

/**
 * Indicates that a unit instance has started a move. 
 * The event reports the unique id of the unit.
 * 
 * { 
 *   messageType = "unitMoving"
 *   id = unit id
 * }
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class UnitMoving implements EventProcessor, Runnable{

	private  GameState gameState;
	private ActorRef out;
	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {
		
		//int unitid = message.get("id").asInt();
		this.gameState=gameState;
		this.out=out;	
		//set the move status to false
		gameState.isMove=true;
	}

	@Override
	public void run() {
		
		//Check if is the user turn
		if(!gameState.endTurn)  //User turn
		{
			BasicCommands.addPlayer1Notification(out, "Moving ",1);
			//moving the unit
			BasicCommands.moveUnitToTile(out, gameState.unit, gameState.secondClickedTile);
			gameState.unit.setPositionByTile(gameState.secondClickedTile);

			try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
			
			//Clear the first tile status 
			gameState.firstClickedTile.clearUnit();
			
			//set the second tile status with unit
			gameState.secondClickedTile.setUnit(gameState.unit);
			
			//clear the unit and tiles in the gameState
			gameState.unit=null;
			gameState.firstClickedTile=null;
			gameState.secondClickedTile=null;
		}
		else //Ai turn
		{	
			BasicCommands.addPlayer1Notification(out, "AI Moving ",1);
			//moving the unit
			BasicCommands.moveUnitToTile(out, gameState.unit, gameState.secondClickedTile);
			gameState.unit.setPositionByTile(gameState.secondClickedTile);

			try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
			
			//Clear the first tile status 
			gameState.firstClickedTile.clearUnit();
			
			//set the second tile status with unit
			gameState.secondClickedTile.setAiUnit(gameState.unit);
			
			//clear the unit and tiles in the gameState
			gameState.unit=null;
			gameState.firstClickedTile=null;
			gameState.secondClickedTile=null;
		}
			
		
	}

}
