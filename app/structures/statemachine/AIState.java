package structures.statemachine;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import events.EventProcessor;
import structures.GameState;

public class AIState extends State{
	
	public AIState()
	{
		//BasicCommands.drawUnit(out, null, null);
	}

	@Override
	public void handleInput(ActorRef out, GameState gameState, JsonNode message, EventProcessor event,
			GameStateMachine gameStateMachine) {
		// TODO Auto-generated method stub
		gameStateMachine.setState(nextState != null ? nextState : new EndTurnState(), out, gameState);
	}
	
	public void enter(ActorRef out, GameState gameState) {
		boolean turnComplete = gameState.ai.searchAction(gameState);
		State aiMove = gameState.ai.getNextAiMove();
		if(!turnComplete) {
			appendState(aiMove, new AIState());
			nextState = aiMove;
		} else {
			nextState = new EndTurnState();
		}

	}
	public void appendState(State head, State state) {
		State currentState = head;
		while(currentState.nextState != null) {
			currentState = currentState.nextState;
		}
		currentState.setNextState(state);
	}
    public void exit(ActorRef out, GameState gameState){}
}
