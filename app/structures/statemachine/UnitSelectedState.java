package structures.statemachine;

import akka.actor.ActorRef;
import com.fasterxml.jackson.databind.JsonNode;
import commands.BasicCommands;
import events.CardClicked;
import events.EventProcessor;
import events.OtherClicked;
import events.TileClicked;
import structures.GameState;
import structures.Turn;
import structures.basic.Tile;
import structures.basic.TileState;
import structures.basic.Unit;
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
                    gameStateMachine.setState(new UnitMovingState(unitClicked, tileClicked, tile), out, gameState);
                } else if (tile.getTileState() == TileState.Occupied) {
                    if (unitClicked.withinDistance(tile.getUnit())) {
                        gameStateMachine.setState(new UnitAttackState(unitClicked, tile, false, true), out, gameState);
                    } else {
                        List<Tile> reachableTiles = gameState.board.getTiles().stream().filter((t) -> {
                            return t.getTileState() == TileState.Reachable;
                        }).collect(Collectors.toList());
                        List<Tile> adjacentTiles = reachableTiles.stream().filter(t -> t.withinDistance(tile.getUnit())).collect(Collectors.toList());
                        adjacentTiles.sort(Comparator.comparingInt(t -> t.distanceToUnit(unitClicked)));
                        Tile adjacentTile = adjacentTiles.get(0);
                        State moveState = new UnitMovingState(unitClicked, gameState.board.getTile(unitClicked.getPosition()), adjacentTile);
                        State attackState = new UnitAttackState(unitClicked, tile, false, true);
                        moveState.setNextState(attackState);
                        gameStateMachine.setState(moveState, out, gameState);
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
            } else {
                System.out.println("UnitSelectedState: Invalid Event");
            }
        }
    }

    @Override
    public void enter(ActorRef out, GameState gameState) {
        if(!unitClicked.getMovement() && !unitClicked.canAttack() ) {
            skip = true;
        }
        gameState.resetBoardSelection(out);
        gameState.resetCardSelection(out);
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
                    .filter(unit -> { return unit.withinDistance(unitClicked);})
                    .map(Unit::getPosition)
                    .map(pos -> { return gameState.board.getTile(pos);})
                    .forEach(tile -> {
                        tile.setTileState(TileState.Occupied);
                        BasicCommands.drawTile(out, tile, 1);
                    });
        }

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

        int x = tilex - 2;
        if(x >= 0) {
            boolean occupied = gameState.board.getTile(x + 1, tiley).getUnit() != null;
            if(!occupied) {
                TileState tileState = TileState.Reachable;
                if(gameState.board.getTile(x, tiley).getUnit() != null)
                    tileState = TileState.Occupied;
                gameState.board.getTile(x, tiley).setTileState(tileState);
                BasicCommands.drawTile(out, gameState.board.getTile(x,tiley), tileState.ordinal());
            }
        }
        x = tilex + 2;
        if(x < Constants.BOARD_WIDTH) {
            boolean occupied = gameState.board.getTile(x - 1, tiley).getUnit() != null;
            if(!occupied) {
                TileState tileState = TileState.Reachable;
                if(gameState.board.getTile(x, tiley).getUnit() != null)
                    tileState = TileState.Occupied;
                gameState.board.getTile(x, tiley).setTileState(tileState);
                BasicCommands.drawTile(out, gameState.board.getTile(x,tiley),tileState.ordinal());
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
        }
        y = tiley + 2;
        if(y < Constants.BOARD_HEIGHT) {
            boolean occupied = gameState.board.getTile(tilex, y - 1).getUnit() != null;
            if(!occupied) {
                TileState tileState = TileState.Reachable;
                if(gameState.board.getTile(tilex, y).getUnit() != null)
                    tileState = TileState.Occupied;
                gameState.board.getTile(tilex, y).setTileState(tileState);
                BasicCommands.drawTile(out, gameState.board.getTile(tilex, y), tileState.ordinal());
            }
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
        }));
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
                else if(surroundingTile.getUnit()!=null&&!surroundingTile.getUnit().isAi()) {
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
    
    
    int x=tilex-3;
    if(x>=0) {
    	setOccupiedAiTiles(out, x, tiley, gameState,false);

	    if(tiley-1>=0)
	    {
	    	setOccupiedAiTiles(out, x, tiley-1, gameState,false);
	    }
	    
	    if(tiley+1<Constants.BOARD_HEIGHT)
	    {
	     	setOccupiedAiTiles(out, x, tiley+1, gameState,false);
	    }
    }
    x=tilex+3;
    if(x<Constants.BOARD_WIDTH) {
    	setOccupiedAiTiles(out, x, tiley, gameState,false);

	    if(tiley-1>=0)
	    {
	    	setOccupiedAiTiles(out, x, tiley-1, gameState,false);
	    }
	    
	    if(tiley+1<Constants.BOARD_HEIGHT)
	    {
	     	setOccupiedAiTiles(out, x, tiley+1, gameState,false);
	    }
    }
    
    int y=tiley-3;
    if(y>=0) {
    	setOccupiedAiTiles(out, tilex, y, gameState,false);

	    if(tilex-1>=0)
	    {
	    	setOccupiedAiTiles(out, tilex-1, y, gameState,false);
	    }
	    
	    if(tilex+1<Constants.BOARD_WIDTH)
	    {
	     	setOccupiedAiTiles(out, tilex+1, y, gameState,false);
	    }
    }
    
    y=tiley+3;
    if(y<Constants.BOARD_HEIGHT) {
    	setOccupiedAiTiles(out, tilex, y, gameState,false);

	    if(tilex-1>=0)
	    {
	    	setOccupiedAiTiles(out, tilex-1, y, gameState,false);
	    }
	    
	    if(tilex+1<Constants.BOARD_WIDTH)
	    {
	     	setOccupiedAiTiles(out, tilex+1, y, gameState,false);
	    }
    }

    x = tilex - 2;
    if(x >= 0) {
        boolean occupied = gameState.board.getTile(x + 1, tiley).getUnit() != null&&
        		!gameState.board.getTile(x+1, tiley).getUnit().isAi();
        setOccupiedAiTiles(out, x+1, tiley, gameState,true);
        if(!occupied) {
        	
       	 //check the surrounding tiles     
           setOccupiedAiTiles(out, x, tiley, gameState,true);
           
           if(tiley+1<Constants.BOARD_HEIGHT)
           {
           	setOccupiedAiTiles(out, x, tiley+1, gameState,false);
           }
           
           if(tiley-1>=0)
           {
           	setOccupiedAiTiles(out, x, tiley-1, gameState,false);
           }
           
           //check the surrounding tiles
           if(tiley+2<Constants.BOARD_HEIGHT)
           {
        	   if(gameState.board.getTile(x+1, tiley+1).getUnit()==null)
        		   setOccupiedAiTiles(out, x, tiley+2, gameState,false);
           }
           
           if(tiley-2>=0)
           {
        	   if(gameState.board.getTile(x+1, tiley-1).getUnit()==null)
        		   setOccupiedAiTiles(out, x, tiley-2, gameState,false);
           }
       }
                  
    }
    x = tilex + 2;
    if(x < Constants.BOARD_WIDTH) {
        boolean occupied = gameState.board.getTile(x - 1, tiley).getUnit() != null&&
        		!gameState.board.getTile(x-1,tiley).getUnit().isAi();
        setOccupiedAiTiles(out, x-1, tiley, gameState,true);
        if(!occupied) {
        	
        	 //check the surrounding tiles     
            setOccupiedAiTiles(out, x, tiley, gameState,true);
            
            if(tiley+1<Constants.BOARD_HEIGHT)
            {
            	setOccupiedAiTiles(out, x, tiley+1, gameState,false);
            }
            
            if(tiley-1>=0)
            {
            	setOccupiedAiTiles(out, x, tiley-1, gameState,false);
            }
            
            //check the surrounding tiles
            if(tiley+2<Constants.BOARD_HEIGHT)
            {
            	if(gameState.board.getTile(x-1, tiley+1).getUnit()==null)
            		setOccupiedAiTiles(out, x, tiley+2, gameState,false);
            }
            
            if(tiley-2>=0)
            {
            	if(gameState.board.getTile(x-1, tiley-1).getUnit()==null)
            		setOccupiedAiTiles(out, x, tiley-2, gameState,false);
            }
        }
        
       
    }
    
    x=tilex+1;
    if(x<Constants.BOARD_WIDTH) {
    	 setOccupiedAiTiles(out, x, tiley, gameState,true);
    }
    
    x=tilex-1;
    if(x>=0) {
    	 setOccupiedAiTiles(out, x, tiley, gameState,true);
    }
    
    y = tiley - 2;
    if(y >= 0) {
    	boolean occupied = gameState.board.getTile(tilex, y+1 ).getUnit() != null&&
        		!gameState.board.getTile(tilex,y+1).getUnit().isAi();
        setOccupiedAiTiles(out, tilex, y+1, gameState,true); 
        if(!occupied) {           
            //check the surrounding tiles
        	setOccupiedAiTiles(out, tilex, y, gameState,true);
        	
            if(tilex+1<Constants.BOARD_WIDTH)
            {
            	setOccupiedAiTiles(out, tilex+1, y, gameState,false);
            }
            
            if(tilex-1>=0)
            {
            	setOccupiedAiTiles(out, tilex-1, y, gameState,false);
            }
            
            if(tilex+2<Constants.BOARD_WIDTH)
            {
            	if(gameState.board.getTile(tilex+1, y+1).getUnit()==null)
            		setOccupiedAiTiles(out, tilex+2, y, gameState,false);
            }
            
            if(tilex-2>=0)
            {
            	if(gameState.board.getTile(tilex-1, y+1).getUnit()==null)
            		setOccupiedAiTiles(out, tilex-2, y, gameState,false);
            }
        }
    }
    
    
    y = tiley + 2;
    if(y < Constants.BOARD_HEIGHT) {
        boolean occupied = gameState.board.getTile(tilex, y-1 ).getUnit() != null&&
        		!gameState.board.getTile(tilex,y-1).getUnit().isAi();
        setOccupiedAiTiles(out, tilex, y-1, gameState,true); 
        if(!occupied) {           
            //check the surrounding tiles
        	
        	setOccupiedAiTiles(out, tilex, y, gameState,true);
        	
            if(tilex+1<Constants.BOARD_WIDTH)
            {
            	setOccupiedAiTiles(out, tilex+1, y, gameState,false);
            }
            
            if(tilex-1>=0)
            {
            	setOccupiedAiTiles(out, tilex-1, y, gameState,false);
            }
            
            if(tilex+2<Constants.BOARD_WIDTH)
            {
            	if(gameState.board.getTile(tilex+1, y-1).getUnit()==null)
            		setOccupiedAiTiles(out, tilex+2, y, gameState,false);
            }
            
            if(tilex-2>=0)
            {
            	if(gameState.board.getTile(tilex-1, y-1).getUnit()==null)
            		setOccupiedAiTiles(out, tilex-2, y, gameState,false);
            }
        }
    
     }

    y=tiley+1;
    if(y<Constants.BOARD_HEIGHT)
    {
    	setOccupiedAiTiles(out, tilex, y, gameState,true);
    }
    
    y=tiley-1;
    if(y>=0)
    {
    	setOccupiedAiTiles(out,tilex,y,gameState, true);
    }
  }
 /**
  * set the occupied tiles for User
  */
	private void setOccupiedAiTiles(ActorRef out,int x, int y, GameState gameState, boolean highlight)
	{
		if(gameState.board.getTile(x, y).getUnit() != null&&!gameState.board.getTile(x, y).getUnit().isAi())
    	{
    		gameState.board.getTile(x, y).setTileState(TileState.Occupied);
    		BasicCommands.drawTile(out, gameState.board.getTile(x, y),2);
    	}else if(gameState.board.getTile(x, y).getUnit() == null&&highlight)
    	{
    		gameState.board.getTile(x, y).setTileState(TileState.Reachable);
    		BasicCommands.drawTile(out, gameState.board.getTile(x, y),1);
    	}
	}
	

}  
