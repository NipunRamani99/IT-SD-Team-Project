package structures.statemachine;

import akka.actor.ActorRef;
import com.fasterxml.jackson.databind.JsonNode;
import events.*;
import structures.GameState;
import structures.basic.Tile;

public class NoSelectionState extends State{

    public NoSelectionState() {

    }
    @Override
    public void handleInput(ActorRef out, GameState gameState, JsonNode message, EventProcessor event, GameStateMachine gameStateMachine) {

        if (event instanceof TileClicked) {
            System.out.println("NoSelectionState: Unit clicked");
            int tilex = message.get("tilex").asInt();
            int tiley = message.get("tiley").asInt();
            Tile tile = gameState.board.getTile(tilex, tiley);
            if(tile.getUnit() != null)
                gameStateMachine.setState(new UnitSelectedState(out, message, gameState));
        } else if (event instanceof CardClicked) {
            System.out.println("NoSelectionState: Card clicked");
            gameStateMachine.setState(new CardSelectedState(out, message, gameState));
        } else if (event instanceof Heartbeat) {
            System.out.println("Heartbeat");
        } else if (event instanceof EndTurnClicked) {
            gameStateMachine.setState(new EndTurnState(), out, gameState);
        }
        else {
            System.out.println("NoSelectionState: Invalid input");
        }
    }

    @Override
    public void enter(ActorRef out, GameState gameState) {

    }

    @Override
    public void exit(ActorRef out, GameState gameState) {

    }
}
