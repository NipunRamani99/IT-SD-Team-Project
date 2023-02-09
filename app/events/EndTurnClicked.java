package events;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import structures.GameState;

/**
 * Indicates that the user has clicked an object on the game canvas, in this case
 * the end-turn button.
 * 
 * { 
 *   messageType = "endTurnClicked"
 * }
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class EndTurnClicked implements EventProcessor{

	private void checkWinner(GameState gameState) {
		//Check if player/Ai's avatar has 0 health.
		//Check if player/Ai's deck of cards is empty.
	}
	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {
		 checkWinner(gameState);
	}

}
