package ai.actions;

import java.util.ArrayList;
import java.util.List;

import commands.BasicCommands;
import structures.GameState;
import structures.basic.Tile;
import structures.basic.TileState;
import structures.basic.Unit;
import structures.statemachine.State;
import structures.statemachine.UnitAttackState;
import structures.statemachine.UnitSelectedState;
import utils.Constants;

public class UnitAttackAction implements AiAction{

    private Unit aiUnit = null;
    private Unit markedUnit = null;
    private GameState gameState;

    public UnitAttackAction(Unit aiUnit, Unit markedUnit) {
        this.aiUnit = aiUnit;
        this.markedUnit = markedUnit;
    }
    
    public UnitAttackAction(Unit aiUnit, GameState gameState)
    {
    	 this.aiUnit = aiUnit;
    	 this.gameState=gameState;
    }
    
    
    public  List<Tile> getOccupiedTile(GameState gameState)
    {
    	  int tilex = aiUnit.getPosition().getTilex();
          int tiley = aiUnit.getPosition().getTiley();
          Tile aiUnitTile = gameState.board.getTile(aiUnit.getPosition().getTilex(), aiUnit.getPosition().getTiley());
          List<Tile> occupiedTiles = new ArrayList<Tile>();
          gameState.resetBoardState();
          for(int i = -1; i <=1; i++ ) {
              for(int j = -1; j <= 1; j++) {
                  int x = tilex + i;
                  if(x < 0) continue;
                  if(x >= Constants.BOARD_WIDTH) continue;
                  int y = tiley + j;
                  if(y < 0) continue;
                  if(y >= Constants.BOARD_HEIGHT) continue;
                  Tile surroundingTile = gameState.board.getTile(x, y);
                  if(surroundingTile == aiUnitTile)
                      continue;
                  if (surroundingTile != null) {
                      if(surroundingTile.getUnit() == null ) {
                          surroundingTile.setTileState(TileState.Reachable);
                          
                      }
                      else if(surroundingTile.getUnit()!=null&&!surroundingTile.getUnit().isAi()) {
                          surroundingTile.setTileState(TileState.Occupied);
                          occupiedTiles.add(surroundingTile);
                      }
                  }
              }
          }
          

          int x = tilex - 2;
          if(x >= 0) {
              boolean occupied = gameState.board.getTile(x + 1, tiley).getUnit() != null;
              if(!occupied) {
                  TileState tileState = TileState.Reachable;
                  if(gameState.board.getTile(x, tiley).getUnit() != null&&!gameState.board.getTile(x, tiley).getUnit().isAi())
                      tileState = TileState.Occupied;
                  gameState.board.getTile(x, tiley).setTileState(tileState);
                  occupiedTiles.add(gameState.board.getTile(x, tiley));
              }
              
              //check the surrounding tiles
              if(tiley+1<Constants.BOARD_HEIGHT)
              {
              	if(gameState.board.getTile(x, tiley+1).getUnit() != null&&!gameState.board.getTile(x, tiley+1).getUnit().isAi())
              	{
              		gameState.board.getTile(x, tiley+1).setTileState(TileState.Occupied);
              		occupiedTiles.add(gameState.board.getTile(x, tiley+1));
              	}
              }
                        
              if(tiley-1>=0)
              {
              	if(gameState.board.getTile(x, tiley-1).getUnit() != null&&!gameState.board.getTile(x, tiley-1).getUnit().isAi())
              	{
              		gameState.board.getTile(x, tiley-1).setTileState(TileState.Occupied);
              		occupiedTiles.add(gameState.board.getTile(x, tiley-1));
              	}
              }
                        
          }
          x = tilex + 2;
          if(x < Constants.BOARD_WIDTH) {
              boolean occupied = gameState.board.getTile(x - 1, tiley).getUnit() != null;
              if(!occupied) {
                  TileState tileState = TileState.Reachable;
                  if(gameState.board.getTile(x, tiley).getUnit() != null&&!gameState.board.getTile(x, tiley).getUnit().isAi())
                      tileState = TileState.Occupied;
                  gameState.board.getTile(x, tiley).setTileState(tileState);
                  occupiedTiles.add(gameState.board.getTile(x, tiley));
              }
              
              //check the surrounding tiles
              if(tiley+1<Constants.BOARD_HEIGHT)
              {
              	if(gameState.board.getTile(x, tiley+1).getUnit() != null&&!gameState.board.getTile(x, tiley+1).getUnit().isAi())
              	{
              		gameState.board.getTile(x, tiley+1).setTileState(TileState.Occupied);
              		occupiedTiles.add(gameState.board.getTile(x, tiley+1));
              	}
              }
              
              if(tiley-1>=0)
              {
              	if(gameState.board.getTile(x, tiley-1).getUnit() != null&&!gameState.board.getTile(x, tiley-1).getUnit().isAi())
              	{
              		gameState.board.getTile(x, tiley-1).setTileState(TileState.Occupied);
              		occupiedTiles.add(gameState.board.getTile(x, tiley+1));
              	}
              }
          }
          
          int y = tiley - 2;
          if(y >= 0) {
              boolean occupied = gameState.board.getTile(tilex, y + 1).getUnit() != null;
              if(!occupied) {
                  TileState tileState = TileState.Reachable;
                  if(gameState.board.getTile(tilex, y).getUnit() != null&&!gameState.board.getTile(tilex, y).getUnit().isAi())
                      tileState = TileState.Occupied;
                  gameState.board.getTile(tilex, y).setTileState(tileState);
                  occupiedTiles.add(gameState.board.getTile(tilex, y));
              }
              
              //check the surrounding tiles
              if(tilex+1<Constants.BOARD_WIDTH)
              {
              	if(gameState.board.getTile(tilex+1, y).getUnit() != null&&!gameState.board.getTile(tilex+1, y).getUnit().isAi())
              	{
              		gameState.board.getTile(tilex+1, y).setTileState(TileState.Occupied);
              	  occupiedTiles.add(gameState.board.getTile(tilex+1, y));
              	}
              }
              
              if(tilex-1>=0)
              {
              	if(gameState.board.getTile(tilex-1, y).getUnit() != null&&!gameState.board.getTile(tilex-1, y).getUnit().isAi())
              	{
              		gameState.board.getTile(tilex-1, y).setTileState(TileState.Occupied);
              	  occupiedTiles.add(gameState.board.getTile(tilex-1, y));
              	}
              }
             
          }
          
          
          y = tiley + 2;
          if(y < Constants.BOARD_HEIGHT) {
              boolean occupied = gameState.board.getTile(tilex, y - 1).getUnit() != null;
              if(!occupied) {
                  TileState tileState = TileState.Reachable;
                  if(gameState.board.getTile(tilex, y).getUnit() != null&&!gameState.board.getTile(tilex, y).getUnit().isAi())
                      tileState = TileState.Occupied;
                  gameState.board.getTile(tilex, y).setTileState(tileState);
                  occupiedTiles.add(gameState.board.getTile(tilex, y));
              }
              
              //check the surrounding tiles
              if(tilex+1<=Constants.BOARD_WIDTH)
              {
              	if(gameState.board.getTile(tilex+1, y).getUnit() != null&&!gameState.board.getTile(tilex+1, y).getUnit().isAi())
              	{
              		gameState.board.getTile(tilex+1, y).setTileState(TileState.Occupied);
              	  occupiedTiles.add(gameState.board.getTile(tilex+1, y));
              	}
              }
              
              if(tilex-1>=0)
              {
              	if(gameState.board.getTile(tilex-1, y).getUnit() != null&&!gameState.board.getTile(tilex-1, y).getUnit().isAi())
              	{
              		gameState.board.getTile(tilex-1, y).setTileState(TileState.Occupied);
              	  occupiedTiles.add(gameState.board.getTile(tilex-1, y));
              	}
              }
           }

      	
         return occupiedTiles;
    }
   
    /**
     * Get the ai unit surrounding lowest health point
     * @return tile
     */
    public Tile getLowestHealthUnitTile(GameState gameState)
    {
    	int health=20;
    	Tile lowestHealthTile=null;
    	if(null!=getOccupiedTile(gameState))
    	{
    		for(Tile tile :getOccupiedTile(gameState) )
        	{
        		if(tile.getUnit()!=null&&!tile.getUnit().isAi()&&tile.getUnit().getHealth()<=health)
        		{
        			lowestHealthTile=tile;
        		}
        	}
        	
    	}
    	
    	return lowestHealthTile;
    }
    

    public State processAction(GameState gameState) {  
        if(aiUnit.withinDistance(markedUnit) && aiUnit.canAttack()) {
            return new UnitAttackState(aiUnit, gameState.board.getTile(markedUnit.getPosition()), false, false);
        }
        return null;
    }
}
