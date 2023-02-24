package structures.statemachine;

import akka.actor.Actor;
import akka.actor.ActorRef;
import com.fasterxml.jackson.databind.JsonNode;
import commands.BasicCommands;
import events.CardClicked;
import events.EventProcessor;
import events.TileClicked;
import structures.GameState;
import structures.basic.Tile;
import structures.basic.TileState;
import structures.basic.Unit;
import utils.Constants;

public class UnitSelectedState implements State{
    private Unit unitClicked = null;
    private Tile tileClicked = null;
    public UnitSelectedState(ActorRef out, JsonNode message, GameState gameState) {
        int tilex = message.get("tilex").asInt();
        int tiley = message.get("tiley").asInt();
        Tile tile = gameState.board.getTile(tilex, tiley);
        this.tileClicked = tile;
        this.unitClicked = tile.getUnit();
        highlightSurroundingTiles(out, tilex, tiley, tileClicked, gameState);
    }
    @Override
    public void handleInput(ActorRef out, GameState gameState, JsonNode message, EventProcessor event, GameStateMachine gameStateMachine) {
        if(event instanceof TileClicked) {
            int tilex = message.get("tilex").asInt();
            int tiley = message.get("tiley").asInt();
            Tile tile = gameState.board.getTile(tilex, tiley);
            if(tile.getTileState() == TileState.None) {
                gameState.resetBoardSelection(out);
                System.out.println("UnitSelectedState: None Tile Clicked");
                gameStateMachine.setState(new NoSelectionState());
            } else if (tile.getTileState() == TileState.Reachable) {
                gameState.resetBoardSelection(out);
                System.out.println("UnitSelectedState: Reachable Tile Clicked");
                gameStateMachine.setState(new UnitMovingState(out, unitClicked, tileClicked, tile, gameState));
            }
        } else if(event instanceof CardClicked) {
            gameState.resetBoardSelection(out);
            System.out.println("UnitSelectedState: Card Clicked");
            gameStateMachine.setState(new CardSelectedState(out, message, gameState));
        } else {
            System.out.println("UnitSelectedState: Invalid Event");
        }
    }

    public void highlightSurroundingTiles(ActorRef out, int tilex, int tiley, Tile tileClicked, GameState gameState) {
        for(int i = -1; i <=1; i++ ) {
            for(int j = -1; j <= 1; j++) {
                int x = tilex + i;
                if(x < 0) continue;
                if(x >= Constants.BOARD_WIDTH) continue;
                int y = tiley + j;
                if(y < 0) continue;
                if(y >= Constants.BOARD_HEIGHT) continue;
                Tile surroundingTile = gameState.board.getTile(x, y);
                if(surroundingTile == tileClicked)
                    continue;
                if (surroundingTile != null) {
                    if(surroundingTile.getUnit() == null && surroundingTile.getAiUnit()==null) {
                        surroundingTile.setTileState(TileState.Reachable);
                        BasicCommands.drawTile(out, surroundingTile, 1);
                    }
                    else {
                        surroundingTile.setTileState(TileState.Occupied);
                        BasicCommands.drawTile(out, surroundingTile, 2);
                    }
                }
            }
        }
    }
}
