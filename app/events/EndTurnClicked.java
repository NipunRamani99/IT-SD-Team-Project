package events;

import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Card;
import structures.statemachine.GameStateMachine;

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
	public void processEvent(ActorRef out, GameState gameState, JsonNode message, GameStateMachine gameStateMachine) {
		if(!gameState.isMove)
		{
			 checkWinner(gameState);


			//draw a card
			gameState.board.drawCard();
			List<Card> cards = gameState.board.getCards();
			for(int i = 0; i < cards.size(); i++) {
				BasicCommands.drawCard(out, cards.get(i),i+1,0);
			}

			 //Ai
			 //add mana+1 each turn
			 if(gameState.AiMana<9)
			 {
				 gameState.AiMana++;
				 gameState.AiPlayer.setMana( gameState.AiMana);
			 }
			 else
			 {
				 gameState.AiMana=9;
			 }

			 BasicCommands.setPlayer2Mana(out, gameState.AiPlayer);
			 try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}

			 //Human
			 if(gameState.humanMana<9)
			 {
				 gameState.humanMana++;
				 gameState.humanPlayer.setMana( gameState.humanMana);
			 }
			 else
			 {
				 gameState.humanMana=9;
			 }

			 BasicCommands.setPlayer1Mana(out, gameState.humanPlayer);
			 try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
			 
			 BasicCommands.addPlayer1Notification(out, "endturn", 1);
			// gameStateMachine.setState(new EndTurnState(out, gameState,this, gameStateMachine));
		}
		gameStateMachine.processInput(out, gameState, message, this);
	}

}
