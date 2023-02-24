package events;


import structures.basic.Card;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Tile;
import structures.basic.TileState;
import structures.basic.Unit;
import structures.statemachine.GameStateMachine;
import utils.Constants;

import java.util.List;

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
		gameStateMachine.processInput(out, gameState, message,this);
	}

	public CardClicked()
	{	
	};
}
