package events;


import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import structures.GameState;
import structures.statemachine.GameStateMachine;

/**
 * Indicates that the user has clicked an object on the game canvas, in this case a card.
 * The event returns the position in the player's hand the card resides within.
 * 
 * { 
 *   messageType = "cardClicked"
 *   position = hand index position [1-6]
 * }
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class CardClicked implements EventProcessor{

	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message, GameStateMachine gameStateMachine) {
    	gameState.resetBoardSelection(out);
		gameState.resetBoardState();
        gameState.resetCardSelection(out);
		gameStateMachine.processInput(out, gameState, message,this);
	}

	public CardClicked()
	{	
	};
}
