package structures.statemachine;

import akka.actor.ActorRef;
import com.fasterxml.jackson.databind.JsonNode;

import commands.AbilityCommands;
import commands.BasicCommands;
import events.CardClicked;
import events.EndTurnClicked;
import events.EventProcessor;
import events.OtherClicked;
import events.TileClicked;
import structures.GameState;
import structures.Turn;
import structures.basic.Tile;
import structures.basic.TileState;
import structures.basic.Unit;
import structures.basic.UnitAbility;
import utils.Constants;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class UnitSelectedState extends State{
    private Unit unitClicked = null;
    private Tile tileClicked = null;
    private boolean skip = false;
    public UnitSelectedState(ActorRef out, JsonNode message, GameState gameState) {
        int tilex = message.get("tilex").asInt();
        int tiley = message.get("tiley").asInt();
        Tile tile = gameState.board.getTile(tilex, tiley);
        this.tileClicked = tile;
        this.unitClicked = tile.getUnit();

    }
    
    public UnitSelectedState(Unit unit,Tile tile)
    {
    	this.tileClicked = tile;
    	this.unitClicked = unit;
    }
    
    public UnitSelectedState(Unit unit,Unit enemyUnit,GameState gameState)
    {
    	this.tileClicked = gameState.board.getTile(enemyUnit.getPosition());
    	this.unitClicked = unit;
    }
    @Override
    public void handleInput(ActorRef out, GameState gameState, JsonNode message, EventProcessor event, GameStateMachine gameStateMachine) {
        if(skip) {
            System.out.println("Exiting UnitSelectedState 48");
            gameStateMachine.setState(new NoSelectionState(), out, gameState);
        } else {
            if (event instanceof TileClicked) {
                int tilex = message.get("tilex").asInt();
                int tiley = message.get("tiley").asInt();
                Tile tile = gameState.board.getTile(tilex, tiley);
                if (tile.getTileState() == TileState.None) {
                    gameState.resetBoardSelection(out);
                    System.out.println("UnitSelectedState: None Tile Clicked");
                    System.out.println("Exiting UnitSelectedState 49");
                    gameStateMachine.setState(new NoSelectionState(), out, gameState);
                } else if (tile.getTileState() == TileState.Reachable) {
                    gameState.resetBoardSelection(out);
                    System.out.println("UnitSelectedState: Reachable Tile Clicked");
                    System.out.println("Exiting UnitSelectedState 54");
                    AbilityCommands.checkIsProvoked(unitClicked, gameState);
                    if(!unitClicked.isIsProvoked()) {
                    	gameStateMachine.setState(new UnitMovingState(unitClicked, tileClicked, tile), out, gameState);
                    }
                } else if (tile.getTileState() == TileState.Occupied) {
                    if (unitClicked.withinDistance(tile.getUnit())||unitClicked.isHasRanged()) {
                    	AbilityCommands.checkIsProvoked(unitClicked, gameState);
                        if(!unitClicked.isIsProvoked()) {
                        	gameStateMachine.setState(new UnitAttackState(unitClicked, tile, false, true), out, gameState);
                        }
                        gameStateMachine.setState(new UnitAttackState(unitClicked, tile, false, true), out, gameState);
                    } else {
                        List<Tile> reachableTiles = gameState.board.getTiles().stream().filter((t) -> {
                            return t.getTileState() == TileState.Reachable;
                        }).collect(Collectors.toList());
                        List<Tile> adjacentTiles = reachableTiles.stream().filter(t -> t.withinDistance(tile.getUnit())).collect(Collectors.toList());
                        adjacentTiles.sort(Comparator.comparingInt(t -> t.distanceToUnit(unitClicked)));
                        Tile adjacentTile=null;
                        for(int i =0; i<adjacentTiles.size();i++)
                        {
                        	adjacentTile=adjacentTiles.get(i);
                        	if(adjacentTile!=null)
                        		break;
                        }
                        AbilityCommands.checkIsProvoked(unitClicked, gameState);
                        if(!unitClicked.isIsProvoked()) {
                        	 State moveState = new UnitMovingState(unitClicked, gameState.board.getTile(unitClicked.getPosition()), adjacentTile);
                             State attackState = new UnitAttackState(unitClicked, tile, false, true);
                             moveState.setNextState(attackState);
                             gameStateMachine.setState(moveState, out, gameState);
                        }
                       
                    }

                }
            } else if (event instanceof CardClicked) {
                gameState.resetBoardSelection(out);
                System.out.println("UnitSelectedState: Card Clicked");
                gameStateMachine.setState(new CardSelectedState(out, message, gameState), out, gameState);
            } else if (event instanceof OtherClicked) {
                gameState.resetBoardSelection(out);
                gameState.resetCardSelection(out);
                gameStateMachine.setState(new NoSelectionState(), out, gameState);
            } 
            else if(event instanceof EndTurnClicked)
            {
            	 gameState.resetBoardSelection(out);
                 gameState.resetCardSelection(out);
                 gameState.resetCardSelection(out);
                 if(gameState.currentTurn==Turn.PLAYER)
                	 gameStateMachine.setState(new EndTurnState(), out, gameState);
            }else {
                System.out.println("UnitSelectedState: Invalid Event");
            }
        }
    }

    @Override
    public void enter(ActorRef out, GameState gameState) {
        gameState.resetBoardSelection(out);
        gameState.resetCardSelection(out);
        if((!unitClicked.getMovement() && !unitClicked.canAttack()) || unitClicked.isAi()) {
            skip = true;
            return;
        }
        if(gameState.currentTurn==Turn.PLAYER)
            if(null!=this.tileClicked.getUnit()&&!this.tileClicked.getUnit().isAi())
    	    	highlightSurroundingTiles(out, this.tileClicked.getTilex(), this.tileClicked.getTiley(), tileClicked, gameState);

    }

    @Override
    public void exit(ActorRef out, GameState gameState) {

    }

    public void highlightSurroundingTiles(ActorRef out, int tilex, int tiley, Tile tileClicked, GameState gameState) {

        if(!unitClicked.getMovement() && unitClicked.canAttack()) {
            gameState.board.getUnits().stream()
                    .filter(unit -> { return unit.isAi() && unit.withinDistance(unitClicked);})
                    .map(Unit::getPosition)
                    .map(pos -> { return gameState.board.getTile(pos);})
                    .forEach(tile -> {
                        tile.setTileState(TileState.Occupied);
                        BasicCommands.drawTile(out, tile, 2);
                    });
            return;
        }

    	if(!unitClicked.isHasFlying()) {
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
                    if(surroundingTile.getUnit() == null ) {
                        if(unitClicked.getMovement())
                        surroundingTile.setTileState(TileState.Reachable);
                        BasicCommands.drawTile(out, surroundingTile, 1);
                    }
                    else if(surroundingTile.getUnit()!=null&&surroundingTile.getUnit().isAi()) {
                        surroundingTile.setTileState(TileState.Occupied);
                        BasicCommands.drawTile(out, surroundingTile, 2);
                    }
                }
            }
        }
    	}else if(unitClicked.isHasFlying()) {
    		for(int i=0;i<Constants.BOARD_WIDTH;i++) {
        		for(int j=0;j<Constants.BOARD_HEIGHT;j++) {   		
                    Tile surroundingTile = gameState.board.getTile(i, j);
                    if(surroundingTile.getUnit() == null) {
                    	 surroundingTile.setTileState(TileState.Reachable);
                         BasicCommands.drawTile(out, surroundingTile, 1);
                         try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
        		}
        	}
        }
     }
       
        //check if has ranged ability
        if(unitClicked.isHasRanged()) {
            for (int i = 0; i < Constants.BOARD_WIDTH; i++) {
                for (int j = 0; j < Constants.BOARD_HEIGHT; j++) {
                    Tile aiTile = gameState.board.getTile(i, j);
                    if (null != aiTile.getUnit()&&aiTile.getUnit().isAi()) {
                        aiTile.setTileState(TileState.Occupied);
                        BasicCommands.drawTile(out, aiTile, 2);
                    }
                }
            }
        }
       if(!unitClicked.isHasFlying()) {
        int x = tilex - 2;
        if(x >= 0) {
            if(gameState.board.playerCanTraverse(x + 1, tiley)) {
                TileState tileState = TileState.Reachable;
                if(gameState.board.hasAiUnitAtTile(x, tiley)) {
                    tileState = TileState.Occupied;
                }
                if(gameState.board.hasPlayerUnitAtTile(x, tiley)) {
                    tileState = TileState.None;
                }
                gameState.board.getTile(x, tiley).setTileState(tileState);
                BasicCommands.drawTile(out, gameState.board.getTile(x, tiley), tileState.ordinal());
            }
        }
       
        x = tilex + 2;
        if(x < Constants.BOARD_WIDTH) {
            if(gameState.board.playerCanTraverse(x - 1, tiley)) {
                TileState tileState = TileState.Reachable;
                if(gameState.board.hasAiUnitAtTile(x, tiley)) {
                    tileState = TileState.Occupied;
                }
                if(gameState.board.hasPlayerUnitAtTile(x, tiley)) {
                    tileState = TileState.None;
                }
                gameState.board.getTile(x, tiley).setTileState(tileState);
                BasicCommands.drawTile(out, gameState.board.getTile(x, tiley), tileState.ordinal());
            }
        }
        int y = tiley - 2;
        if(y >= 0) {
            if(gameState.board.playerCanTraverse(tilex, y + 1)) {
                TileState tileState = TileState.Reachable;
                if(gameState.board.hasAiUnitAtTile(tilex, y)) {
                    tileState = TileState.Occupied;
                }
                if(gameState.board.hasPlayerUnitAtTile(tilex, y)) {
                    tileState = TileState.None;
                }
                gameState.board.getTile(tilex, y).setTileState(tileState);
                BasicCommands.drawTile(out, gameState.board.getTile(tilex, y), tileState.ordinal());
            }
        }
        y = tiley + 2;
        if(y < Constants.BOARD_HEIGHT) {
            if(gameState.board.playerCanTraverse(tilex, y - 1)) {
                TileState tileState = TileState.Reachable;
                if(gameState.board.hasAiUnitAtTile(tilex, y)) {
                    tileState = TileState.Occupied;
                }
                if(gameState.board.hasPlayerUnitAtTile(tilex, y)) {
                    tileState = TileState.None;
                }
                gameState.board.getTile(tilex, y).setTileState(tileState);
                BasicCommands.drawTile(out, gameState.board.getTile(tilex, y), tileState.ordinal());
            }
        
        List<Tile> reachableTiles = gameState.board.getTiles().stream().filter(tile -> {
            return tile.getTileState() == TileState.Reachable;
        }).collect(Collectors.toList());
        gameState.board.getUnits().stream().filter((unit -> {
            if(unit.isAi()) {
                return reachableTiles.stream().anyMatch((tile) -> {return tile.withinDistance(unit);});
            }
            return false;
        })).forEach((unit -> {
            gameState.board.getTile(unit.getPosition()).setTileState(TileState.Occupied);
            BasicCommands.drawTile(out, gameState.board.getTile(unit.getPosition()),2);
        }));


            
            if(tilex-1>=0)
            {
            	if(gameState.board.getTile(tilex-1, y).getUnit() != null&&
            			gameState.board.getTile(tilex-1, y).getUnit().isAi())
            	{
            		gameState.board.getTile(tilex-1, y).setTileState(TileState.Occupied);
            		BasicCommands.drawTile(out, gameState.board.getTile(tilex-1, y),2);
            	}
            }
        }
       }
   }

    
/**
 * Ai highlight the surrounding tiles to move
 * @param out
 * @param tilex
 * @param tiley
 * @param tileClicked
 * @param gameState
 */
public void aiHighlightSurroundingTiles(ActorRef out, int tilex, int tiley, Tile tileClicked, GameState gameState) {
	if(!unitClicked.isHasFlying()) {
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
                if(surroundingTile.getUnit() == null ) {
                    surroundingTile.setTileState(TileState.Reachable);
                    BasicCommands.drawTile(out, surroundingTile, 1);
                }
                else if(surroundingTile.getUnit()!=null) {
                    surroundingTile.setTileState(TileState.Occupied);
                    BasicCommands.drawTile(out, surroundingTile, 2);
                }
                else
                {
                	//do nothing
                }
            }
        }
    }
	}else if(unitClicked.isHasFlying()) {
		for(int i=0;i<Constants.BOARD_WIDTH;i++) {
    		for(int j=0;j<Constants.BOARD_HEIGHT;j++) {   		
                Tile surroundingTile = gameState.board.getTile(i, j);
                if(surroundingTile.getUnit() == null) {
                	 surroundingTile.setTileState(TileState.Reachable);
                     BasicCommands.drawTile(out, surroundingTile, 1);
                     try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
    		}
    	}
    }
	}
    
  //check if has ranged ability
    if(unitClicked.isHasRanged()) {

        for (int i = 0; i < Constants.BOARD_WIDTH; i++) {
            for (int j = 0; j < Constants.BOARD_HEIGHT; j++) {
                Tile aiTile = gameState.board.getTile(i, j);
                if (null != aiTile.getUnit()&&aiTile.getUnit().isAi()) {
                    aiTile.setTileState(TileState.Occupied);
                    BasicCommands.drawTile(out, aiTile, 2);
                }
            }
        }

    }
    
    if(!unitClicked.isHasFlying()) {
    int x = tilex - 2;
    if(x >= 0) {
    	Unit unit=gameState.board.getTile(x + 1, tiley).getUnit();
        boolean occupied =(gameState.board.getTile(x + 1, tiley).getUnit() != null&&
        		gameState.board.getTile(x + 1, tiley).getUnit().isAi());
        if(!occupied) {
            TileState tileState = TileState.Reachable;
            if(gameState.board.getTile(x, tiley).getUnit() != null)
                tileState = TileState.Occupied;
            gameState.board.getTile(x, tiley).setTileState(tileState);
            BasicCommands.drawTile(out, gameState.board.getTile(x,tiley), tileState.ordinal());
        }
        
        //check the surrounding tiles
        if(tiley+1<Constants.BOARD_HEIGHT)
        {
        	if(gameState.board.getTile(x, tiley+1).getUnit() != null)
        	{
        		gameState.board.getTile(x, tiley+1).setTileState(TileState.Occupied);
        		BasicCommands.drawTile(out, gameState.board.getTile(x,tiley+1),2);
        	}
        }
                  
        if(tiley-1>=0)
        {
        	if(gameState.board.getTile(x, tiley-1).getUnit() != null)
        	{
        		gameState.board.getTile(x, tiley-1).setTileState(TileState.Occupied);
        		BasicCommands.drawTile(out, gameState.board.getTile(x,tiley-1),2);
        	}
        }
                  
    }
    x = tilex + 2;
    if(x < Constants.BOARD_WIDTH) {
        boolean occupied = (gameState.board.getTile(x - 1, tiley).getUnit() != null
        		&&gameState.board.getTile(x - 1, tiley).getUnit().isAi());
        if(!occupied) {
            TileState tileState = TileState.Reachable;
            if(gameState.board.getTile(x, tiley).getUnit() != null)
                tileState = TileState.Occupied;
            gameState.board.getTile(x, tiley).setTileState(tileState);
            BasicCommands.drawTile(out, gameState.board.getTile(x,tiley),tileState.ordinal());
        }
        
        //check the surrounding tiles
        if(tiley+1<Constants.BOARD_HEIGHT)
        {
        	if(gameState.board.getTile(x, tiley+1).getUnit() != null)
        	{
        		gameState.board.getTile(x, tiley+1).setTileState(TileState.Occupied);
        		BasicCommands.drawTile(out, gameState.board.getTile(x,tiley+1),2);
        	}
        }
        
        if(tiley-1>=0)
        {
        	if(gameState.board.getTile(x, tiley-1).getUnit() != null)
        	{
        		gameState.board.getTile(x, tiley-1).setTileState(TileState.Occupied);
        		BasicCommands.drawTile(out, gameState.board.getTile(x,tiley-1),2);
        	}
        }
    }
    
    int y = tiley - 2;
    if(y >= 0) {
        boolean occupied = gameState.board.getTile(tilex, y + 1).getUnit() != null;
        if(!occupied) {
            TileState tileState = TileState.Reachable;
            if(gameState.board.getTile(tilex, y).getUnit() != null)
                tileState = TileState.Occupied;
            gameState.board.getTile(tilex, y).setTileState(tileState);
            BasicCommands.drawTile(out, gameState.board.getTile(tilex, y), tileState.ordinal());
        }
        
        //check the surrounding tiles
        if(tilex+1<Constants.BOARD_WIDTH)
        {
        	if(gameState.board.getTile(tilex+1, y).getUnit() != null)
        	{
        		gameState.board.getTile(tilex+1, y).setTileState(TileState.Occupied);
        		BasicCommands.drawTile(out, gameState.board.getTile(tilex+1, y),2);
        	}
        }
        
        if(tilex-1>=0)
        {
        	if(gameState.board.getTile(tilex-1, y).getUnit() != null)
        	{
        		gameState.board.getTile(tilex-1, y).setTileState(TileState.Occupied);
        		BasicCommands.drawTile(out, gameState.board.getTile(tilex-1, y),2);
        	}
        }
       
    }
    
    
    y = tiley + 2;
    if(y < Constants.BOARD_HEIGHT) {
        boolean occupied = gameState.board.getTile(tilex, y - 1).getUnit() != null
        		&&gameState.board.getTile(tilex, y - 1).getUnit().isAi();
        if(!occupied) {
            TileState tileState = TileState.Reachable;
            if(gameState.board.getTile(tilex, y).getUnit() != null)
                tileState = TileState.Occupied;
            gameState.board.getTile(tilex, y).setTileState(tileState);
            BasicCommands.drawTile(out, gameState.board.getTile(tilex, y), tileState.ordinal());
        }
        
        //check the surrounding tiles
        if(tilex+1<=Constants.BOARD_WIDTH)
        {
        	if(gameState.board.getTile(tilex+1, y).getUnit() != null)
        	{
        		gameState.board.getTile(tilex+1, y).setTileState(TileState.Occupied);
        		BasicCommands.drawTile(out, gameState.board.getTile(tilex+1, y),2);
        	}
        }
        
        if(tilex-1>=0)
        {
        	if(gameState.board.getTile(tilex-1, y).getUnit() != null)
        	{
        		gameState.board.getTile(tilex-1, y).setTileState(TileState.Occupied);
        		BasicCommands.drawTile(out, gameState.board.getTile(tilex-1, y),2);
        	}
        }
     }}

	}
}  
