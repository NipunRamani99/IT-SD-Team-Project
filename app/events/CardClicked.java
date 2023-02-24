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

	//The id of the card that clicked
	private int handPosition;
	
	//The card name
	private String cardName="";
	
	//The card that clicked
	private Card card;
	
	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message, GameStateMachine gameStateMachine) {
		gameStateMachine.processInput(out, gameState, message,this);
//		//Highlight the card when click
//		if(!gameState.isMove)
//		{
//			if(gameState.cardIsClicked) {
//				resetCardSelection(out, gameState);
//				resetBoardSelection(out, gameState);
//				gameState.cardIsClicked=false;
//			}
//			handPosition = message.get("position").asInt();
//			card=gameState.board.getCard(handPosition);
//			gameState.cardIsClicked = true;
//			gameState.card = card;
//			gameState.handPosition = handPosition;
//			BasicCommands.drawCard(out, card, handPosition, 1);
//			highlightCardSelection(out,gameState);
//
//		}
	}
	
	//Highlight the tiles when selecting the cards
	private void highlightCardSelection(ActorRef out, GameState gameState)
	{
		BasicCommands.addPlayer1Notification(out, "Card hightlight ",1);
		List<Unit> unitList = gameState.board.getUnits();
		for(Unit unit : unitList) {
			for (int i = -1; i <= 1; i++) {
				for (int j = -1; j <= 1; j++) {
					if(i == 0 && j == 0) continue;;
					int x = unit.getPosition().getTilex() + i;
					int y = unit.getPosition().getTiley() + j;
					Tile surroundingTile = gameState.board.getTile(x, y);
					if (surroundingTile != null) {
						if (surroundingTile.getUnit() == null) {
							surroundingTile.setTileState(TileState.Reachable);
							BasicCommands.drawTile(out, surroundingTile, 1);
							try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
						}
					}
				}
			}
		}
	}
	//reset the card selection
	private void resetCardSelection(ActorRef out, GameState gameState) {
		BasicCommands.drawCard(out, gameState.card, gameState.handPosition, 0);
		gameState.card = null;
		gameState.handPosition = -1;
		gameState.cardIsClicked = false;
	}
	
	//Reset the board selection
	private void resetBoardSelection(ActorRef out, GameState gameState) {
		for(int i = 0; i < Constants.BOARD_WIDTH; i++ ) {
			for(int j = 0; j < Constants.BOARD_HEIGHT; j++) {
				Tile tile = gameState.board.getTile(i, j);
				BasicCommands.drawTile(out, tile, 0);
				try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
				tile.setTileState(TileState.None);
			}
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
