package structures.statemachine;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import events.EndTurnClicked;
import events.EventProcessor;
import structures.*;

public class EndTurnState implements State{
	 public EndTurnState(ActorRef out, GameState gameState,EventProcessor event, GameStateMachine gameStateMachine)
	 {
		 if(event instanceof EndTurnClicked )
		 {
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

			 BasicCommands.setPlayer2Mana(out, gameState.AiPlayer);
			 try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
			 gameStateMachine.setState(new AIState(out, gameState, gameStateMachine));
		 }
		 else
		 {
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
			 gameStateMachine.setState(new NoSelectionState());
		 }
	 }

	@Override
	public void handleInput(ActorRef out, GameState gameState, JsonNode message, EventProcessor event,
			GameStateMachine gameStateMachine) {
		// TODO Auto-generated method stub
		
	}

}
