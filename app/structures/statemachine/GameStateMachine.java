package structures.statemachine;

import akka.actor.ActorRef;
import com.fasterxml.jackson.databind.JsonNode;
import events.EventProcessor;
import structures.GameState;
import structures.Turn;

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
    	try {Thread.sleep(10);} catch (InterruptedException e) {e.printStackTrace();}
    	if(gameState.AiPlayer.getHealth()==0||gameState.humanPlayer.getHealth()==0)
    		newState= new NoSelectionState();
        newState.enter(out, gameState);
        currentState = newState;
    }

    public void queueState(State state) {
        stateQueue.add(state);
    }

    public void processInput(ActorRef out, GameState gameState, JsonNode message, EventProcessor eventProcessor) {
        try {
			currentState.handleInput(out, gameState, message, eventProcessor,this);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    }
    
    public State getCurrState()
    {
    	return currentState;
    }
}
