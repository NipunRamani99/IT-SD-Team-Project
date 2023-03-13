package structures.statemachine;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import events.EventProcessor;
import events.Heartbeat;
import structures.GameState;
import ai.*;

/**
 *
 */
public class AIState extends State{

	public AIState()
	{

	}

	@Override
	public void handleInput(ActorRef out, GameState gameState, JsonNode message, EventProcessor event,
			GameStateMachine gameStateMachine) {
		// TODO Auto-generated method stub
		if(event instanceof Heartbeat) {
			System.out.println("Exiting AIState");
			gameStateMachine.setState(nextState != null ? nextState : new EndTurnState(), out, gameState);
		}
	}

	/**
	 *
	 * @param out
	 * @param gameState
	 */
	public void enter(ActorRef out, GameState gameState) {
		System.out.println("Entering AIState");
		boolean canPlay = gameState.ai.searchAction(gameState);
		State aiMove = gameState.ai.getNextAiMove();
		if(canPlay && aiMove != null) {
			aiMove.appendState(new AIState());
			nextState=aiMove;
		} else {
			nextState = new EndTurnState();
		}
	}
}
