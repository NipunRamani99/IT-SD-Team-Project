package events;

import java.util.Collections;
import java.util.List;
import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.Turn;
import structures.basic.*;
import structures.statemachine.GameStateMachine;


/**
 * Indicates that the user has clicked an object on the game canvas, in this case a tile.
 * The event returns the x (horizontal) and y (vertical) indices of the tile that was
 * clicked. Tile indices start at 1.
 * 
 * { 
 *   messageType = "tileClicked"
 *   tilex = x index of the tile
 *   tiley = y index of the tile
 * }
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class TileClicked implements EventProcessor{

	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message, GameStateMachine gameStateMachine) {
		
		if(gameState.currentTurn==Turn.PLAYER)
			gameState.resetCardSelection(out);
		else
			gameState.resetAiCardSelection(out);
		
		BasicCommands.addPlayer1Notification(out, "Tile Clicked ",1);
		int tilex = message.get("tilex").asInt();
		int tiley = message.get("tiley").asInt();
		gameState.position = new Position(tilex, tiley);
		gameStateMachine.processInput(out, gameState, message,this);

	}
}
