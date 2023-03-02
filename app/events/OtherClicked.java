package events;

import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Tile;
import structures.basic.TileState;
import structures.basic.Unit;
import structures.statemachine.GameStateMachine;
import utils.Constants;

/**
 * Indicates that the user has clicked an object on the game canvas, in this case
 * somewhere that is not on a card tile or the end-turn button.
 * 
 * { 
 *   messageType = "otherClicked"
 * }
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class OtherClicked implements EventProcessor{

	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message,  GameStateMachine gameStateMachine) {
		gameStateMachine.processInput(out, gameState, message, this);
	}
}
