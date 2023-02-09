package events;


import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import structures.GameState;

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

	//The id of the card that clicked
	private int handPosition;
	
	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {
		
		 handPosition = message.get("position").asInt();				
	}
	
	//Get the Id of the clicked card
	public int getHandPosition()
	{
		return handPosition;
	}
	

}
