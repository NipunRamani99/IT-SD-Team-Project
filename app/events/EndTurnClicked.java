package events;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
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
			 gameState.endTurn = true;
			 BasicCommands.addPlayer1Notification(out, "endturn", 1);
		}		
	}

}
