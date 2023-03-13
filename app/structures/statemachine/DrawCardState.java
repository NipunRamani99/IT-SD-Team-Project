package structures.statemachine;

import akka.actor.ActorRef;
import com.fasterxml.jackson.databind.JsonNode;
import commands.BasicCommands;
import events.CardClicked;
import events.EventProcessor;
import events.Heartbeat;
import events.TileClicked;
import org.checkerframework.checker.units.qual.C;
import structures.GameState;
import structures.Turn;
import structures.basic.Card;
import structures.basic.UnitAbility;

import java.util.List;

public class DrawCardState extends State {

    UnitAbility unitAbility = UnitAbility.DRAW_CARD_ON_DEATH;
    public DrawCardState(UnitAbility unitAbility) {
        this.unitAbility = unitAbility;
    }

    @Override
    public void handleInput(ActorRef out, GameState gameState, JsonNode message, EventProcessor event,
                            GameStateMachine gameStateMachine) {
            if(event instanceof Heartbeat) {
                gameStateMachine.setState(nextState != null ? nextState : new NoSelectionState(), out, gameState);
            }
    }

    @Override
    public void enter(ActorRef out, GameState gameState) {
        if(unitAbility == UnitAbility.DRAW_CARD_ON_SUMMON) {
            System.out.println("Entering DrawCardState");
            BasicCommands.addPlayer1Notification(out, "Player Draw Card", 2);
            gameState.board.drawCard();
            List<Card> cards = gameState.board.getCards();
            for (int i = 0; i < cards.size(); i++) {
                BasicCommands.drawCard(out, cards.get(i), i + 1, 0);
            }
            try {
                Thread.sleep(500);
            } catch (Exception e) {
                System.out.println(e);
            }
            BasicCommands.addPlayer1Notification(out, "AI Draw A Card", 2);
            gameState.board.aiDrawCard();
            cards = gameState.board.getAiCards();
            for (int i = 0; i < cards.size(); i++) {
                BasicCommands.drawCard(out, cards.get(i), i + 1, 0);
            }
            try {
                Thread.sleep(500);
            } catch (Exception e) {
                System.out.println(e);
            }
        } else if(unitAbility == UnitAbility.DRAW_CARD_ON_DEATH) {
            BasicCommands.addPlayer1Notification(out, "AI Draw A Card", 2);
            gameState.board.aiDrawCard();
            List<Card> cards = gameState.board.getAiCards();
            for (int i = 0; i < cards.size(); i++) {
                BasicCommands.drawCard(out, cards.get(i), i + 1, 0);
            }
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                System.out.println(e);
            }
            if(gameState.currentTurn == Turn.PLAYER) {
                cards = gameState.board.getCards();
                for (int i = 0; i < cards.size(); i++) {
                    BasicCommands.drawCard(out, cards.get(i), i + 1, 0);
                }
            }
        }
    }

    @Override
    public void exit(ActorRef out, GameState gameState) {


    }

}
