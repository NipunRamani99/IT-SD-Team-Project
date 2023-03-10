package structures.statemachine;

import akka.actor.ActorRef;
import com.fasterxml.jackson.databind.JsonNode;
import commands.BasicCommands;
import events.EventProcessor;
import events.Heartbeat;
import org.checkerframework.checker.units.qual.C;
import structures.GameState;

public class DrawCardState extends State {

    boolean cardCasted = false;


    public DrawCardState(boolean cardCasted) {
        this.cardCasted = cardCasted;

    }

    @Override
    public void handleInput(ActorRef out, GameState gameState, JsonNode message, EventProcessor event,
                            GameStateMachine gameStateMachine) {
        if(cardCasted) {
            gameStateMachine.setState(nextState);
        }
        // Try to get the unit and attack
        if(event instanceof CardSelectedState) {
            State state = new CardSelectedState(out, message, gameState);
            state.setNextState(this);
            gameStateMachine.setState(state);
        }
    }

    @Override
    public void enter(ActorRef out, GameState gameState) {
        if(cardCasted) {

        } else {
            BasicCommands.addPlayer1Notification(out, "Draw a card", 1);
        }
    }

    @Override
    public void exit(ActorRef out, GameState gameState) {


    }

    public void setCardCasted(boolean cardCasted) {
        this.cardCasted = cardCasted;
    }
}
