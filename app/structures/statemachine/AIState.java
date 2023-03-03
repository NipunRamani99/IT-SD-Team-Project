package structures.statemachine;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import events.EventProcessor;
import structures.GameState;

public class AIState implements State{
	
	public AIState(ActorRef out, GameState gameState, GameStateMachine gameStateMachine)
	{
		BasicCommands.addPlayer1Notification(out, "AI turn", 1);
		//BasicCommands.drawUnit(out, null, null);
	}

	@Override
	public void handleInput(ActorRef out, GameState gameState, JsonNode message, EventProcessor event,
			GameStateMachine gameStateMachine) {
		// TODO Auto-generated method stub
		gameStateMachine.setState(new EndTurnState(out, gameState,null, gameStateMachine), out, gameState);
		
	}

}
