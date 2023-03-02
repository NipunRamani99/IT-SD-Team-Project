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
                gameStateMachine.setState(new UnitMovingState(out, unitClicked, tileClicked, tile, gameState,gameStateMachine));
            }
            else if(tile.getTileState()==TileState.Occupied)
            {
            	if(null!=tile.getAiUnit())
            	{
            		gameState.resetBoardSelection(out);
            		System.out.println("Get the Ai unit");
            		gameState.targetTile=tile;
            		gameStateMachine.setState(new HumanAttackState(out, unitClicked, tileClicked, tile, gameState, gameStateMachine));
            	}
            	  
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
        
        //highlight the Ai occupied tiles
        for(int i=0;i<Constants.BOARD_WIDTH;i++)
        {
        	for(int j=0;j<Constants.BOARD_HEIGHT;j++)
        	{
        		Tile aiTile=gameState.board.getTile(i, j);
        		if(null!=aiTile.getAiUnit())
        		{
        			   aiTile.setTileState(TileState.Occupied);;
                       BasicCommands.drawTile(out, aiTile, 2);
        		}
        	}

//        int x = tilex - 2;
//        if(x >= 0) {
//            boolean occupied = gameState.board.getTile(x + 1, tiley).getUnit() != null;
//            if(!occupied) {
//                TileState tileState = TileState.Reachable;
//                if(gameState.board.getTile(x, tiley).getUnit() != null)
//                    tileState = TileState.Occupied;
//                gameState.board.getTile(x, tiley).setTileState(tileState);
//                BasicCommands.drawTile(out, gameState.board.getTile(x,tiley), tileState.ordinal());
//            }
//        }
//        x = tilex + 2;
//        if(x < Constants.BOARD_WIDTH) {
//            boolean occupied = gameState.board.getTile(x - 1, tiley).getUnit() != null;
//            if(!occupied) {
//                TileState tileState = TileState.Reachable;
//                if(gameState.board.getTile(x, tiley).getUnit() != null)
//                    tileState = TileState.Occupied;
//                gameState.board.getTile(x, tiley).setTileState(tileState);
//                BasicCommands.drawTile(out, gameState.board.getTile(x,tiley),tileState.ordinal());
//            }
//        }
//        int y = tiley - 2;
//        if(y >= 0) {
//            boolean occupied = gameState.board.getTile(tilex, y + 1).getUnit() != null;
//            if(!occupied) {
//                TileState tileState = TileState.Reachable;
//                if(gameState.board.getTile(tilex, y).getUnit() != null)
//                    tileState = TileState.Occupied;
//                gameState.board.getTile(tilex, y).setTileState(tileState);
//                BasicCommands.drawTile(out, gameState.board.getTile(tilex, y), tileState.ordinal());
//            }
//        }
//        y = tiley + 2;
//        if(y < Constants.BOARD_HEIGHT) {
//            boolean occupied = gameState.board.getTile(tilex, y - 1).getUnit() != null;
//            if(!occupied) {
//                TileState tileState = TileState.Reachable;
//                if(gameState.board.getTile(tilex, y).getUnit() != null)
//                    tileState = TileState.Occupied;
//                gameState.board.getTile(tilex, y).setTileState(tileState);
//                BasicCommands.drawTile(out, gameState.board.getTile(tilex, y), tileState.ordinal());
//            }

        }
    }
}
