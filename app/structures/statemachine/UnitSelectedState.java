package structures.statemachine;

import akka.actor.Actor;
import akka.actor.ActorRef;
import com.fasterxml.jackson.databind.JsonNode;
import commands.BasicCommands;
import events.CardClicked;
import events.EventProcessor;
import events.Heartbeat;
import events.TileClicked;
import scala.concurrent.impl.FutureConvertersImpl;
import structures.GameState;
import structures.basic.Tile;
import structures.basic.TileState;
import structures.basic.Unit;
import utils.Constants;

public class UnitSelectedState extends State{
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
                gameStateMachine.setState(new UnitMovingState(unitClicked, tileClicked, tile), out, gameState);
            }
            else if(tile.getTileState()==TileState.Occupied)
            {
            	if(null!=tile.getAiUnit())
            	{
                    int distance = Tile.distance(tile, tileClicked);
                    if(distance == 2) {
                    	//X axis moving
                        if(Math.abs(tile.getTilex() - tileClicked.getTilex()) == 2) {
                            int diffX = tile.getTilex() - tileClicked.getTilex();
                            diffX = diffX/2;
                            Tile adjacentTile = gameState.board.getTile(tileClicked.getTilex() + diffX, tileClicked.getTiley());
                            State attackState = new HumanAttackState(unitClicked, tile, false, true);
                            State moveState = new UnitMovingState(unitClicked, tileClicked, adjacentTile);
                            moveState.setNextState(attackState);
                            gameStateMachine.setState(moveState, out, gameState);
                        }
                        //Y axis moving
                        else if(Math.abs(tile.getTiley() - tileClicked.getTiley()) == 2)
                        {
                        	int diffY = tile.getTiley() - tileClicked.getTiley();
                            diffY = diffY/2;
                            Tile adjacentTile = gameState.board.getTile(tileClicked.getTilex() , tileClicked.getTiley()+ diffY);
                            State attackState = new HumanAttackState(unitClicked, tile, false, true);
                            State moveState = new UnitMovingState(unitClicked, tileClicked, adjacentTile);
                            moveState.setNextState(attackState);
                            gameStateMachine.setState(moveState, out, gameState);
                        }
                        else
                        {
                        	//Only attack without moving
                        	State attackState = new HumanAttackState(unitClicked, tile, false, true);
                            gameStateMachine.setState(attackState, out, gameState);
                        }                      
                    } 
                    else if(distance == 3)
                    {
                    	//X axis moving
                        if(Math.abs(tile.getTilex() - tileClicked.getTilex()) == 3) {
                            int diffX = tile.getTilex() - tileClicked.getTilex();
                            diffX = diffX/2+1;
                            Tile adjacentTile = gameState.board.getTile(tileClicked.getTilex() + diffX, tileClicked.getTiley());
                            State attackState = new HumanAttackState(unitClicked, tile, false, true);
                            State moveState = new UnitMovingState(unitClicked, tileClicked, adjacentTile);
                            moveState.setNextState(attackState);
                            gameStateMachine.setState(moveState, out, gameState);
                        }
                        //Y axis moving
                        else if(Math.abs(tile.getTiley() - tileClicked.getTiley()) == 3)
                        {
                        	int diffY = tile.getTiley() - tileClicked.getTiley();
                            diffY = diffY/2+1;
                            Tile adjacentTile = gameState.board.getTile(tileClicked.getTilex() , tileClicked.getTiley()+ diffY);
                            State attackState = new HumanAttackState(unitClicked, tile, false, true);
                            State moveState = new UnitMovingState(unitClicked, tileClicked, adjacentTile);
                            moveState.setNextState(attackState);
                            gameStateMachine.setState(moveState, out, gameState);
                        }
                        else if(Math.abs(tile.getTilex() - tileClicked.getTilex()) == 2&&Math.abs(tile.getTiley() - tileClicked.getTiley()) == 1)
                        {
                        	int diffX = tile.getTilex() - tileClicked.getTilex();
                            diffX = diffX/2;
                            Tile adjacentTile = gameState.board.getTile(tileClicked.getTilex()+ diffX , tileClicked.getTiley());
                            State attackState = new HumanAttackState(unitClicked, tile, false, true);
                            State moveState = new UnitMovingState(unitClicked, tileClicked, adjacentTile);
                            moveState.setNextState(attackState);
                            gameStateMachine.setState(moveState, out, gameState);
                        }
                        else if(Math.abs(tile.getTilex() - tileClicked.getTilex()) == 1&&Math.abs(tile.getTiley() - tileClicked.getTiley()) == 2)
                        {
                        	int diffY = tile.getTiley() - tileClicked.getTiley();
                            diffY = diffY/2;
                            Tile adjacentTile = gameState.board.getTile(tileClicked.getTilex() , tileClicked.getTiley()+ diffY);
                            State attackState = new HumanAttackState(unitClicked, tile, false, true);
                            State moveState = new UnitMovingState(unitClicked, tileClicked, adjacentTile);
                            moveState.setNextState(attackState);
                            gameStateMachine.setState(moveState, out, gameState);
                        }
                        else
                        {
                        	//do nothing
                        	State noSelectionState = new NoSelectionState();
                        	gameStateMachine.setState(noSelectionState, out, gameState);
                        }
                    }
                    
                    else if(distance==4)
                    {
                     	//X axis moving
                        if(Math.abs(tile.getTilex() - tileClicked.getTilex()) == 3) {
                            int diffX = tile.getTilex() - tileClicked.getTilex();
                            diffX = diffX/2+1;
                            Tile adjacentTile = gameState.board.getTile(tileClicked.getTilex() + diffX, tileClicked.getTiley());
                            State attackState = new HumanAttackState(unitClicked, tile, false, true);
                            State moveState = new UnitMovingState(unitClicked, tileClicked, adjacentTile);
                            moveState.setNextState(attackState);
                            gameStateMachine.setState(moveState, out, gameState);
                        }
                        //Y axis moving
                        else if(Math.abs(tile.getTiley() - tileClicked.getTiley()) == 3)
                        {
                        	int diffY = tile.getTiley() - tileClicked.getTiley();
                            diffY = diffY/2+1;
                            Tile adjacentTile = gameState.board.getTile(tileClicked.getTilex() , tileClicked.getTiley()+ diffY);
                            State attackState = new HumanAttackState(unitClicked, tile, false, true);
                            State moveState = new UnitMovingState(unitClicked, tileClicked, adjacentTile);
                            moveState.setNextState(attackState);
                            gameStateMachine.setState(moveState, out, gameState);
                        }
                        else
                        {
                        	//do nothing
                        	State noSelectionState = new NoSelectionState();
                        	gameStateMachine.setState(noSelectionState, out, gameState);
                        }
                    }
                    else if(distance>4)
                    {
                    	//do nothing
                    	State noSelectionState = new NoSelectionState();
                    	gameStateMachine.setState(noSelectionState, out, gameState);
                    }
                    else {
                        System.out.println("Get the Ai unit");
                        gameStateMachine.setState(new HumanAttackState(unitClicked, tile, false, true), out, gameState);
                    }
                    gameState.resetBoardSelection(out);
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

    @Override
    public void enter(ActorRef out, GameState gameState) {

    }

    @Override
    public void exit(ActorRef out, GameState gameState) {

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
//<<<<<<< HEAD
        
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

//=======
        int x = tilex - 2;
        if(x >= 0) {
            boolean occupied = gameState.board.getTile(x + 1, tiley).getAiUnit() != null;
            if(!occupied) {
                TileState tileState = TileState.Reachable;
                if(gameState.board.getTile(x, tiley).getAiUnit() != null)
                    tileState = TileState.Occupied;
                gameState.board.getTile(x, tiley).setTileState(tileState);
                BasicCommands.drawTile(out, gameState.board.getTile(x,tiley), tileState.ordinal());
            }
        }
        x = tilex + 2;
        if(x < Constants.BOARD_WIDTH) {
            boolean occupied = gameState.board.getTile(x - 1, tiley).getAiUnit() != null;
            if(!occupied) {
                TileState tileState = TileState.Reachable;
                if(gameState.board.getTile(x, tiley).getAiUnit() != null)
                    tileState = TileState.Occupied;
                gameState.board.getTile(x, tiley).setTileState(tileState);
                BasicCommands.drawTile(out, gameState.board.getTile(x,tiley),tileState.ordinal());
            }
        }
        int y = tiley - 2;
        if(y >= 0) {
            boolean occupied = gameState.board.getTile(tilex, y + 1).getAiUnit() != null;
            if(!occupied) {
                TileState tileState = TileState.Reachable;
                if(gameState.board.getTile(tilex, y).getAiUnit() != null)
                    tileState = TileState.Occupied;
                gameState.board.getTile(tilex, y).setTileState(tileState);
                BasicCommands.drawTile(out, gameState.board.getTile(tilex, y), tileState.ordinal());
            }
        }
        y = tiley + 2;
        if(y < Constants.BOARD_HEIGHT) {
            boolean occupied = gameState.board.getTile(tilex, y - 1).getAiUnit() != null;
            if(!occupied) {
                TileState tileState = TileState.Reachable;
                if(gameState.board.getTile(tilex, y).getAiUnit() != null)
                    tileState = TileState.Occupied;
                gameState.board.getTile(tilex, y).setTileState(tileState);
                BasicCommands.drawTile(out, gameState.board.getTile(tilex, y), tileState.ordinal());
            }
//>>>>>>> origin/dev/nipun
        }
    }
  }
}  
