package structures.statemachine;

import akka.actor.ActorRef;
import com.fasterxml.jackson.databind.JsonNode;
import events.EventProcessor;
import structures.GameState;

public class GameStateMachine {
    private State currentState;

    public GameStateMachine() {
        currentState = new NoSelectionState();
    }

    public void setState(State newState) {
        currentState = newState;
    }

    public void processInput(ActorRef out, GameState gameState, JsonNode message, EventProcessor eventProcessor) {
        currentState.handleInput(out, gameState, message, eventProcessor,this);
    }
    
    public State getCurrState()
    {
    	return this.currentState;
    }
}
