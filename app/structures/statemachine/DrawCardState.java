package structures.statemachine;

import akka.actor.ActorRef;
import com.fasterxml.jackson.databind.JsonNode;
import commands.BasicCommands;
import events.CardClicked;
import events.EventProcessor;
import events.Heartbeat;
import org.checkerframework.checker.units.qual.C;
import structures.GameState;
import structures.Turn;
import structures.basic.Card;

import java.util.List;

public class DrawCardState extends State {

    boolean cardCasted = false;


    public DrawCardState(boolean cardCasted) {
        this.cardCasted = cardCasted;

    }

    @Override
    public void handleInput(ActorRef out, GameState gameState, JsonNode message, EventProcessor event,
                            GameStateMachine gameStateMachine) {
        if(cardCasted) {
            gameState.currentTurn = Turn.AI;
            AIState aiState = new AIState();
            aiState.drawCard(out, gameState);
            nextState = aiState;
            gameStateMachine.setState(nextState, out,gameState);
        } else if(event instanceof CardClicked) {
            gameState.currentTurn = Turn.PLAYER;
            State state = new CardSelectedState(out, message, gameState);
            state.setNextState(new DrawCardState(true));
            gameStateMachine.setState(state, out, gameState);
        }
    }

    @Override
    public void enter(ActorRef out, GameState gameState) {
        if(cardCasted) {
            BasicCommands.addPlayer1Notification(out, "AI Draw A Card", 1);
            List<Card> cards = gameState.board.getAiCards();
            for(int i = 0; i < cards.size(); i++) {
                BasicCommands.drawCard(out, cards.get(i),i+1,0);
            }
        } else {
            List<Card> cards = gameState.board.getCards();
            for(int i = 0; i < cards.size(); i++) {
                BasicCommands.drawCard(out, cards.get(i),i+1,0);
            }
            BasicCommands.addPlayer1Notification(out, "Player Draw A Card", 1);
        }
    }

    @Override
    public void exit(ActorRef out, GameState gameState) {


    }

    public void setCardCasted(boolean cardCasted) {
        this.cardCasted = cardCasted;
    }
}
