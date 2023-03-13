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
		 gameState.board.getUnits().forEach(unit -> {unit.setCanAttack(true);});
		 gameState.board.getUnits().forEach(unit -> {unit.setMovement(true);});
		 gameState.board.getUnits().forEach(unit -> {unit.setAttackBack(true);});
		 gameState.board.getUnits().forEach(unit -> {unit.setAttackTimes(0);});
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
			 }
			 else
			 {
				 //Max mana is 9
				 gameState.AiMana=9;
			 }
			 gameState.AiPlayer.setMana( gameState.AiMana);
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
			 //check AI run out the cards or not
			 if(aiCards.size()==0)
					gameState.humanRunOut=true;
		 }
		 else
		 {   			 
			 gameState.currentTurn = Turn.PLAYER;
			 BasicCommands.addPlayer1Notification(out, "Player turn", 1);
			 //Human mana
			 if(gameState.humanMana<9)
			 {
				 gameState.humanMana++;
				
			 }
			 else
			 {
				 gameState.humanMana=9;
			 }
			 gameState.humanPlayer.setMana( gameState.humanMana);
			 BasicCommands.setPlayer1Mana(out, gameState.humanPlayer);
 			 nextState = new NoSelectionState();
 			 
 			 
 			 //delete the ai card
 			 List<Card> aiCards= gameState.board.getAiCards();
 			 for(int i=1;i<=6;i++)
        	 {
        	 	BasicCommands.deleteCard(out,i);
        	 }
 			 //draw the unit card
			gameState.board.drawCard();
			List<Card> cards = gameState.board.getCards();
			for(int i = 0; i < cards.size(); i++) {
				BasicCommands.drawCard(out, cards.get(i),i+1,0);
			}
			
			//check human run out the cards or not
			if(cards.size()==0)
				gameState.humanRunOut=true;
		 }
	 }

	@Override
	public void handleInput(ActorRef out, GameState gameState, JsonNode message, EventProcessor event,
			GameStateMachine gameStateMachine) {
		// TODO Auto-generated method stub
		if(nextState!=null) {
			System.out.println("Exiting EndTurnState");
			gameStateMachine.setState(nextState, out, gameState);
		}
	}
	
	public void enter(ActorRef out, GameState gameState) {
		System.out.println("Entering EndTurnState");
		endTurn(out, gameState);
	}

    public void exit(ActorRef out, GameState gameState){}


}
