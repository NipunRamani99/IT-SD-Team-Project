package structures.statemachine;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import events.EndTurnClicked;
import events.EventProcessor;
import structures.*;

public class EndTurnState extends State{

	public EndTurnState() {

	}

	 public void endTurn(ActorRef out, GameState gameState)
	 {
		 if(gameState.currentTurn == Turn.PLAYER)
		 {
			 gameState.currentTurn = Turn.AI;

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
		 }
		 else
		 {
			 gameState.currentTurn = Turn.PLAYER;
			 BasicCommands.addPlayer1Notification(out, "Player turn", 1);
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
 			 nextState = new NoSelectionState();
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
