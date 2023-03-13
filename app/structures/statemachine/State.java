package structures.statemachine;

import akka.actor.ActorRef;
import com.fasterxml.jackson.databind.JsonNode;
import events.EventProcessor;
import structures.GameState;

/**
 * State base class which will be extended into specific game states.
 */
public abstract class State {

    protected State nextState  = null;

    public void handleInput(ActorRef out, GameState gameState, JsonNode message, EventProcessor event, GameStateMachine gameStateMachine) throws Exception {}

    public void enter(ActorRef out, GameState gameState) {}

    public void setNextState(State nextState) {
        this.nextState = nextState;
    }

    public State getNextState() { return nextState;}
    public void appendState(State s) {
    	if(s==null) return;
        if(nextState == null) {
            nextState=s;
        }
        else {
            nextState.appendState(s);
        }
    }
}
