package events;


import structures.basic.Card;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
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
	
	//The card name
	private String cardName="";
	
	//The card that clicked
	private Card card;
	
	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {
		
		if(!gameState.isMove)
		{
			 handPosition = message.get("position").asInt();
			 card=gameState.board.getCard(handPosition);
			 gameState.cardIsClicked=true;
			//Highlight the card when click		
			 BasicCommands.drawCard(out, card, handPosition, 1);
		}		 
	}
	
	//Get the Id of the clicked card
	public int getHandPosition()
	{
		return handPosition;
	}
	public CardClicked()
	{	
	};
	
	//Get the string information of the card
	public Card getCard()
	{
		return this.card;
	}
	
	//return the card clicked object
	public CardClicked getCardClicked()
	{
		return this;
	}
	

}
