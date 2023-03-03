package structures.statemachine;

import akka.actor.ActorRef;
import com.fasterxml.jackson.databind.JsonNode;
import events.EventProcessor;
import structures.GameState;

import java.util.*;

public class GameStateMachine {
    private State currentState;
    private Queue<State> stateQueue;
    public GameStateMachine() {
        stateQueue = new LinkedList<>();
        currentState = new NoSelectionState();
    }

    public void setState(State newState) {
        currentState = newState;
    }

    public void setState(State newState, ActorRef out, GameState gameState) {
        newState.enter(out, gameState);
        currentState = newState;
    }

    public void queueState(State state) {
        stateQueue.add(state);
    }

    public void processInput(ActorRef out, GameState gameState, JsonNode message, EventProcessor eventProcessor) {
        currentState.handleInput(out, gameState, message, eventProcessor,this);

    }
    
    public State getCurrState()
    {
    	return currentState;
    }
}
