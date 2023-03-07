package structures.statemachine;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import events.EventProcessor;
import structures.GameState;

public class AIState extends State{
	
	private GameStateMachine gameStateMachine;
	
	public AIState()
	{
		//BasicCommands.drawUnit(out, null, null);
	}

	@Override
	public void handleInput(ActorRef out, GameState gameState, JsonNode message, EventProcessor event,
			GameStateMachine gameStateMachine) {
		// TODO Auto-generated method stub
		gameStateMachine.setState(nextState != null ? nextState : new EndTurnState(), out, gameState);
		this.gameStateMachine=gameStateMachine;
	}
	
	public void enter(ActorRef out, GameState gameState) {
<<<<<<< HEAD
		boolean turnComplete = gameState.ai.searchAction(out ,gameState,gameStateMachine);
=======
		boolean canPlay = gameState.ai.searchAction(gameState);
>>>>>>> origin/dev/nipun
		State aiMove = gameState.ai.getNextAiMove();
		if(canPlay && aiMove != null) {
			aiMove.appendState(new AIState());
			nextState = aiMove;
		} else {
			nextState = new EndTurnState();
		}

	}
    public void exit(ActorRef out, GameState gameState){}
}
