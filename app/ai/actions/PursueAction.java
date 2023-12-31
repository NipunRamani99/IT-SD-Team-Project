package ai.actions;

import ai.AIActionUtils;
import structures.GameState;
import structures.basic.Tile;
import structures.basic.TileState;
import structures.basic.Unit;
import structures.statemachine.State;
import structures.statemachine.UnitMovingState;
import utils.Constants;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * PursueAction will find the closest tile to a marked unit and create the state change required to move the AI unit closer to the enemy unit.
 */
public class PursueAction implements AiAction {
    private Unit markedUnit = null;
    private Unit aiUnit = null;

    public PursueAction(Unit enemyUnit, Unit aiUnit) {
        this.markedUnit = enemyUnit;
        this.aiUnit = aiUnit;
    }

    /**
     * Creates a list of tiles which are reachable by the AI.
     * @param gameState
     * @return List of reachable tiles
     */
    public List<Tile> getReachableTiles(GameState gameState) {
        int tilex = aiUnit.getPosition().getTilex();
        int tiley = aiUnit.getPosition().getTiley();
        Tile aiUnitTile = gameState.board.getTile(aiUnit.getPosition().getTilex(), aiUnit.getPosition().getTiley());
        List<Tile> reachableTiles = new ArrayList<Tile>();
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
                    if(surroundingTile.getUnit() == null) {
                        surroundingTile.setTileState(TileState.Reachable);
                        reachableTiles.add(surroundingTile);
                    }
                    else if(surroundingTile.getUnit()!=null&&!surroundingTile.getUnit().isAi()) {
                        surroundingTile.setTileState(TileState.Occupied);
                    }
                }
            }
        }

        int x = tilex - 2;
        if(x >= 0) {
            boolean traversable = gameState.board.aiCanTraverse(x + 1, tiley);
            if(traversable) {
                if(gameState.board.getTile(x, tiley).getUnit() == null)
                    reachableTiles.add(gameState.board.getTile(x, tiley));
            }
        }
        x = tilex + 2;
        if(x < Constants.BOARD_WIDTH) {
            boolean traversable = gameState.board.aiCanTraverse(x - 1, tiley);
            if(traversable) {
                if(gameState.board.getTile(x, tiley).getUnit() == null)
                    reachableTiles.add(gameState.board.getTile(x, tiley));
            }
        }
        int y = tiley - 2;
        if(y >= 0) {
            boolean traversable = gameState.board.aiCanTraverse(tilex, y + 1);
            if(!traversable) {
                if(gameState.board.getTile(tilex, y).getUnit() == null)
                    reachableTiles.add(gameState.board.getTile(tilex, y));
            }
        }
        y = tiley + 2;
        if(y < Constants.BOARD_HEIGHT) {
            boolean traversable = gameState.board.aiCanTraverse(tilex, y - 1);
            if(!traversable) {
                if(gameState.board.getTile(tilex, y).getUnit() == null)
                    reachableTiles.add(gameState.board.getTile(tilex,y));
            }
        }

        return reachableTiles;
    }


    /**
     * Find the reachable tile which is closest to the marked unit.
     * @param gameState
     * @return Tile
     */
    public Tile getClosestTile(GameState gameState) {
        List<Tile> reachableTiles = getReachableTiles(gameState);
        reachableTiles.sort(Comparator.comparingInt((a)-> a.distanceToUnit(markedUnit)));
        for(Tile reachableTile : reachableTiles) {
            if(reachableTile.getUnit() == null) {
                return reachableTile;
            }
        }
        return null;
    }
    
    public  List<Tile> getOccupiedTile(GameState gameState)
    {
    	  int tilex = aiUnit.getPosition().getTilex();
          int tiley = aiUnit.getPosition().getTiley();
          Tile highestHealthUnit= AIActionUtils.searchHighestNonAvatarUnitHealth(gameState);
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
                  if(surroundingTile == highestHealthUnit) continue;
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
          return occupiedTiles;
    }

    /**
     * Method to generate the state change
     * @param gameState
     * @return UnitMovingState
     */
    @Override
    public State processAction(GameState gameState) {
        Tile closestTile = getClosestTile(gameState);
        Tile startTile = gameState.board.getTile(aiUnit.getPosition());
        if(closestTile == startTile) return null;
        return new UnitMovingState(aiUnit,startTile, closestTile);
    }
}
