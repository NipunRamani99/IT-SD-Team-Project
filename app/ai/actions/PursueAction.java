package ai.actions;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Position;
import structures.basic.Tile;
import structures.basic.TileState;
import structures.basic.Unit;
import structures.statemachine.State;
import structures.statemachine.UnitAttackState;
import structures.statemachine.UnitMovingState;
import structures.statemachine.UnitSelectedState;
import utils.Constants;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import ai.Action;

public class PursueAction implements AiAction {
    private Unit markedUnit = null;
    private Unit aiUnit = null;

    public PursueAction(Unit enemyUnit, Unit aiUnit) {
        this.markedUnit = enemyUnit;
        this.aiUnit = aiUnit;
    }

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
                    if(surroundingTile.getUnit() == null ) {
                        surroundingTile.setTileState(TileState.Reachable);
                        reachableTiles.add(surroundingTile);
                    }
                    else if(surroundingTile.getUnit()!=null&&!surroundingTile.getUnit().isAi()) {
                        surroundingTile.setTileState(TileState.Occupied);
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
                if(null!=aiTile.getUnit())
                {
                    aiTile.setTileState(TileState.Occupied);;
                }
            }

            int x = tilex - 2;
            if(x >= 0) {
                boolean occupied = gameState.board.getTile(x + 1, tiley).getUnit() != null;
                if(!occupied) {
                    if(gameState.board.getTile(x, tiley).getUnit() == null)
                        reachableTiles.add(gameState.board.getTile(x, tiley));
                }
            }
            x = tilex + 2;
            if(x < Constants.BOARD_WIDTH) {
                boolean occupied = gameState.board.getTile(x - 1, tiley).getUnit() != null;
                if(!occupied) {
                    if(gameState.board.getTile(x, tiley).getUnit() == null)
                        reachableTiles.add(gameState.board.getTile(x, tiley));
                }
            }
            int y = tiley - 2;
            if(y >= 0) {
                boolean occupied = gameState.board.getTile(tilex, y + 1).getUnit() != null;
                if(!occupied) {
                    if(gameState.board.getTile(tilex, y).getUnit() == null)
                        reachableTiles.add(gameState.board.getTile(tilex, y));
                }
            }
            y = tiley + 2;
            if(y < Constants.BOARD_HEIGHT) {
                boolean occupied = gameState.board.getTile(tilex, y - 1).getUnit() != null;
                if(!occupied) {
                    if(gameState.board.getTile(tilex, y).getUnit() == null)
                        reachableTiles.add(gameState.board.getTile(tilex,y));
                }
            }
        }
        return reachableTiles;
    }

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
          Tile highestHealthUnit= Action.searchHighestNonAvatarUnitHealth(gameState);
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

    @Override
    public State processAction(GameState gameState) {
        Tile closestTile = getClosestTile(gameState);
        Tile startTile = gameState.board.getTile(aiUnit.getPosition());
        if(closestTile == startTile) return null;
        return new UnitMovingState(aiUnit,startTile, closestTile);
    }
}
