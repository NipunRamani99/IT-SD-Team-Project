package events;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Tile.Occupied;
import structures.basic.UnitAnimationType;
import structures.statemachine.GameStateMachine;

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
public class UnitMoving implements EventProcessor {

	private  GameState gameState;
	private ActorRef out;
	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message, GameStateMachine gameStateMachine) {
		gameState.resetBoardSelection(out);
		gameState.resetBoardState();
		gameStateMachine.processInput(out, gameState, message, this);
	}
}
