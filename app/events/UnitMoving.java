package events;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
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
	}

	@Override
	public void run() {
		//Clear the first tile status 
		gameState.firstClickedTile.clearUnit();
		//set the move status to false
		gameState.isMove=true;
		BasicCommands.addPlayer1Notification(out, "Moving ",1);
		//moving the unit
		BasicCommands.moveUnitToTile(out, gameState.unit, gameState.secondClickedTile);
		gameState.unit.setPositionByTile(gameState.secondClickedTile);
		//clear the first tile status
		gameState.firstClickedTile.clearUnit();
		try {Thread.sleep(4000);} catch (InterruptedException e) {e.printStackTrace();}
		
		//set the second tile status with unit
		gameState.secondClickedTile.setUnit(gameState.unit);
		
		//clear the unit and tiles in the gameState
		gameState.unit=null;
		gameState.firstClickedTile=null;
		gameState.secondClickedTile=null;
		//Set the status to false after moving
		gameState.isMove=false;
		
	}

}
