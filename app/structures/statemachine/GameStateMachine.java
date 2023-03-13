package structures.statemachine;

import akka.actor.ActorRef;
import com.fasterxml.jackson.databind.JsonNode;
import events.EventProcessor;
import structures.GameState;

import java.util.LinkedList;
import java.util.Queue;

/**
 * GameStateMachine provides encapsulates the current state of the game and provides method to switch between states.
 */
public class GameStateMachine {
    private State currentState;
    public GameStateMachine() {
        currentState = new NoSelectionState();
    }

    public void setState(State newState, ActorRef out, GameState gameState) {
    	try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
    	if(gameState.AiPlayer.getHealth()==0||gameState.humanPlayer.getHealth()==0)
    		newState= new NoSelectionState();
        newState.enter(out, gameState);
        currentState = newState;
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
