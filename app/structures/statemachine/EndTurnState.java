package structures.statemachine;

import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import events.EndTurnClicked;
import events.EventProcessor;
import structures.*;
import structures.basic.Card;

public class EndTurnState extends State{

	public EndTurnState() {

	}

	 public void endTurn(ActorRef out, GameState gameState)
	 {
		 //every turn, every unit can attack and move
		 gameState.board.setUnitAttackState();
		 gameState.board.setUnitMoveState();
		 
		 if(gameState.currentTurn == Turn.PLAYER)
		 {		 
			 gameState.currentTurn = Turn.AI;
             gameState.AiMarkEnemy=false;
			 BasicCommands.addPlayer1Notification(out, "AI turn", 1);
			 //Ai
			 //add mana+1 each turn
			 if(gameState.AiMana<9)
			 {
				 gameState.AiMana++;
				 gameState.AiPlayer.setMana( gameState.AiMana);
			 }
			 else
			 {
				 //Max mana is 9
				 gameState.AiMana=9;
			 }
			 gameState.board.getUnits().forEach(unit -> {unit.setCanAttack(true);});
			 gameState.board.getUnits().forEach(unit -> {unit.setMovement(true);});
			 BasicCommands.setPlayer2Mana(out, gameState.AiPlayer);
			 gameState.ai.beginTurn(gameState);
			 nextState = new AIState();
 			 //delete the player card
 			 List<Card> cards= gameState.board.getCards();
 			 for(int i=1;i<=6;i++)
        	 {
        	 	BasicCommands.deleteCard(out,i);
        	 }
			 //draw the ai card
 			 gameState.board.aiDrawCard();
	         List<Card> aiCards= gameState.board.getAiCards();	       
			 for(int i = 0; i < aiCards.size(); i++) {
				BasicCommands.drawCard(out, aiCards.get(i),i+1,0);
			 }
		 }
		 else
		 {   			 
			 gameState.currentTurn = Turn.PLAYER;
			 BasicCommands.addPlayer1Notification(out, "Player turn", 1);
			 //Human mana
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
 			 nextState = new NoSelectionState();
 			 
 			 
 			 //delete the ai card
 			 List<Card> aiCards= gameState.board.getAiCards();
 			 for(int i=1;i<=aiCards.size();i++)
        	 {
        	 	BasicCommands.deleteCard(out,i);
        	 }
 			 //draw the unit card
			gameState.board.drawCard();
			List<Card> cards = gameState.board.getCards();
			for(int i = 0; i < cards.size(); i++) {
				BasicCommands.drawCard(out, cards.get(i),i+1,0);
			}
		 }
	 }

	@Override
	public void handleInput(ActorRef out, GameState gameState, JsonNode message, EventProcessor event,
			GameStateMachine gameStateMachine) {
		// TODO Auto-generated method stub
		gameStateMachine.setState(nextState, out, gameState);
	}
	
	public void enter(ActorRef out, GameState gameState) {
		endTurn(out, gameState);
	}

    public void exit(ActorRef out, GameState gameState){}


}
