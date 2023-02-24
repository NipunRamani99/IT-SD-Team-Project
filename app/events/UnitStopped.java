package events;


import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import structures.GameState;
import structures.statemachine.GameStateMachine;

/**
 * Indicates that a unit instance has stopped moving. 
 * The event reports the unique id of the unit.
 * 
 * { 
 *   messageType = "unitStopped"
 *   id = unit id
 * }
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class UnitStopped implements EventProcessor{

	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message, GameStateMachine gameStateMachine) {
		
//		int unitid = message.get("id").asInt();
//		gameState.isMove= false;
//		//Clear the first tile status
//		if(null!=gameState.firstClickedTile&& null!=gameState.secondClickedTile)
//		{
//			gameState.firstClickedTile.clearUnit();
//
//			//set the second tile status with unit
//			gameState.secondClickedTile.setUnit(gameState.unit);
//
//			//clear the unit and tiles in the gameState
//			gameState.unit.setChosed(false);
//			gameState.firstClickedTile=null;
//			gameState.secondClickedTile=null;
//		}
//		gameState.unit=null;
		gameStateMachine.processInput(out, gameState, message, this);
	}

}
