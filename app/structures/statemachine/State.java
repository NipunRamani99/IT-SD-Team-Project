package structures.statemachine;

import akka.actor.ActorRef;
import com.fasterxml.jackson.databind.JsonNode;
import events.EventProcessor;
import structures.GameState;

public abstract class State {

    protected State nextState  = null;

    public void handleInput(ActorRef out, GameState gameState, JsonNode message, EventProcessor event, GameStateMachine gameStateMachine) {}

    public void enter(ActorRef out, GameState gameState) {}

    public void exit(ActorRef out, GameState gameState){}


    public void setNextState(State nextState) {
        this.nextState = nextState;
    }

    public State getNextState() { return nextState; }
}
