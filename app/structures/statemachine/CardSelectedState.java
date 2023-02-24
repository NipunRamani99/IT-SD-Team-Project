package structures.statemachine;

import akka.actor.ActorRef;
import com.fasterxml.jackson.databind.JsonNode;
import commands.BasicCommands;
import events.CardClicked;
import events.EventProcessor;
import events.TileClicked;
import structures.GameState;
import structures.basic.Card;
import structures.basic.Tile;
import structures.basic.TileState;
import structures.basic.Unit;

import java.util.List;

public class CardSelectedState implements State{
    private int handPosition = 0;
    private Card cardSelcted = null;

    public CardSelectedState(ActorRef out, JsonNode message, GameState gameState) {
        gameState.resetCardSelection(out);
        handPosition = message.get("position").asInt();
        cardSelcted =gameState.board.getCard(handPosition);
        BasicCommands.drawCard(out, cardSelcted, handPosition, 1);
        highlightCardSelection(out, gameState);
    }
    @Override
    public void handleInput(ActorRef out, GameState gameState, JsonNode message, EventProcessor event, GameStateMachine gameStateMachine) {
        if(event instanceof TileClicked) {
            int tilex = message.get("tilex").asInt();
            int tiley = message.get("tiley").asInt();
            Tile tile = gameState.board.getTile(tilex, tiley);
            if(tile.getTileState() == TileState.None) {
                gameState.resetBoardSelection(out);
                gameState.resetCardSelection(out);
                System.out.println("CardSelectedState: None Tile Clicked");
                gameStateMachine.setState(new NoSelectionState());
            } else if (tile.getTileState() == TileState.Reachable) {
                gameState.resetBoardSelection(out);
                BasicCommands.drawUnit(out, null, tile); 
                System.out.println("CardSelectedState: Reachable Tile Clicked");
                gameStateMachine.setState(new NoSelectionState());
            }
        } else if(event instanceof CardClicked) {
            gameState.resetBoardSelection(out);
            System.out.println("CardSelectedState: Card Clicked");
            gameStateMachine.setState(new CardSelectedState(out, message, gameState));
        } else {
            System.out.println("CardSelectedState: Invalid Event");
        }
    }

    private void highlightCardSelection(ActorRef out, GameState gameState)
    {
        BasicCommands.addPlayer1Notification(out, "Card highlight ",1);
        List<Unit> unitList = gameState.board.getUnits();
        for(Unit unit : unitList) {
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    if(i == 0 && j == 0) continue;;
                    int x = unit.getPosition().getTilex() + i;
                    int y = unit.getPosition().getTiley() + j;
                    Tile surroundingTile = gameState.board.getTile(x, y);
                    if (surroundingTile != null) {
                        if (surroundingTile.getUnit() == null) {
                            surroundingTile.setTileState(TileState.Reachable);
                            BasicCommands.drawTile(out, surroundingTile, 1);
                            try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
                        }
                    }
                }
            }
        }
    }
    
    
    private void getUnitFromCard
}
