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

public class UnitSelectedState extends State{
    private Unit unitClicked = null;
    private Tile tileClicked = null;
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
            	if(null!=tile.getUnit()&&tile.getUnit().isAi())
            	{
                    int distance = Tile.distance(tile, tileClicked);
                    if(distance == 2) {
                    	//X axis moving
                        if(Math.abs(tile.getTilex() - tileClicked.getTilex()) == 2) {
                            int diffX = tile.getTilex() - tileClicked.getTilex();
                            diffX = diffX/2;
                            Tile adjacentTile = gameState.board.getTile(tileClicked.getTilex() + diffX, tileClicked.getTiley());
                            if(adjacentTile.getUnit()==null)
                            {
                                State moveState = new UnitMovingState(unitClicked, tileClicked, adjacentTile);
                                State attackState = new UnitAttackState(unitClicked, tile, false, true);
                                gameStateMachine.setState(moveState, out, gameState);
                                try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
                                gameStateMachine.setState(attackState, out, gameState);
                            }
                       
                        }
                        //Y axis moving
                        else if(Math.abs(tile.getTiley() - tileClicked.getTiley()) == 2)
                        {
                        	int diffY = tile.getTiley() - tileClicked.getTiley();
                            diffY = diffY/2;
                            Tile adjacentTile = gameState.board.getTile(tileClicked.getTilex() , tileClicked.getTiley()+ diffY);
                            if(adjacentTile.getUnit()==null)
                            {
                            	 State attackState = new UnitAttackState(unitClicked, tile, false, true);
                                 State moveState = new UnitMovingState(unitClicked, tileClicked, adjacentTile);
                                 gameStateMachine.setState(moveState, out, gameState);
                                 try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
                                 gameStateMachine.setState(attackState, out, gameState);
                            }
                           
                        }
                        else
                        {
                        	//Only attack without moving
                        	State attackState = new UnitAttackState(unitClicked, tile, false, true);
                            gameStateMachine.setState(attackState, out, gameState);
                        }                      
                    } 
                    else if(distance == 3)
                    {
                        if(Math.abs(tile.getTilex() - tileClicked.getTilex()) == 2&&Math.abs(tile.getTiley() - tileClicked.getTiley()) == 1)
                        {
                        	int diffX = tile.getTilex() - tileClicked.getTilex();
                            diffX = diffX/2;
                            Tile adjacentTile = gameState.board.getTile(tileClicked.getTilex()+ diffX , tileClicked.getTiley());
                            if(adjacentTile.getUnit()==null)
                            {
                            	  State attackState = new UnitAttackState(unitClicked, tile, false, true);
                                  State moveState = new UnitMovingState(unitClicked, tileClicked, adjacentTile);
                                  //moveState.setNextState(attackState);
                                  gameStateMachine.setState(moveState, out, gameState);
                                  try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
                                  gameStateMachine.setState(attackState, out, gameState);
                                  
                            }
                          
                        }
                        else if(Math.abs(tile.getTilex() - tileClicked.getTilex()) == 1&&Math.abs(tile.getTiley() - tileClicked.getTiley()) == 2)
                        {
                        	int diffY = tile.getTiley() - tileClicked.getTiley();
                            diffY = diffY/2;
                            Tile adjacentTile = gameState.board.getTile(tileClicked.getTilex() , tileClicked.getTiley()+ diffY);
                            if(adjacentTile.getUnit()==null)
                            {
                                State attackState = new UnitAttackState(unitClicked, tile, false, true);
                                State moveState = new UnitMovingState(unitClicked, tileClicked, adjacentTile);
                                gameStateMachine.setState(moveState, out, gameState);
                                try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
                                gameStateMachine.setState(attackState, out, gameState);
                            }
                        
                        }
                        else
                        {
                        	//do nothing
                        	State noSelectionState = new NoSelectionState();
                        	gameStateMachine.setState(noSelectionState, out, gameState);
                        }
                    }

                    else if(distance>3)
                    {
                    	//do nothing
                    	State noSelectionState = new NoSelectionState();
                    	gameStateMachine.setState(noSelectionState, out, gameState);
                    }
                    else {
                        System.out.println("Get the Ai unit");
                        gameStateMachine.setState(new UnitAttackState(unitClicked, tile, false, true), out, gameState);
                    }
                    gameState.resetBoardSelection(out);
            	}

            }
        } else if(event instanceof CardClicked) {
            gameState.resetBoardSelection(out);
            System.out.println("UnitSelectedState: Card Clicked");
            gameStateMachine.setState(new CardSelectedState(out, message, gameState));
        }else if(event instanceof OtherClicked)
        {
        	 gameState.resetBoardSelection(out);
        	 gameState.resetCardSelection(out);
        	 gameStateMachine.setState(new NoSelectionState());
        } 
        else if(gameState.currentTurn==Turn.AI)
        {
        	Tile tile = gameState.board.getTile(unitClicked.getPosition());
        	Tile adjacentTile =null;
        	if(null!=tile.getUnit()&&tile.getUnit().isAi()&&null!=tileClicked.getUnit())
        	{
                int distance = Tile.distance(tile, tileClicked);
                if(distance == 2) {
                	//X axis moving
                    if(Math.abs(tile.getTilex() - tileClicked.getTilex()) == 2) {
                        int diffX = tile.getTilex() - tileClicked.getTilex();
                        diffX = diffX/2;
                        adjacentTile = gameState.board.getTile(tileClicked.getTilex() + diffX, tileClicked.getTiley());
                        if(adjacentTile.getUnit()==null)
                        {
                        	  State moveState = new UnitMovingState(unitClicked, tileClicked, adjacentTile);
                              State attackState = new UnitAttackState(unitClicked, tile, false, true);
                              gameStateMachine.setState(moveState, out, gameState);
                              try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
                              gameStateMachine.setState(attackState, out, gameState);
                        }
                      
                    }
                    //Y axis moving
                    else if(Math.abs(tile.getTiley() - tileClicked.getTiley()) == 2)
                    {
                    	int diffY = tile.getTiley() - tileClicked.getTiley();
                        diffY = diffY/2;
                        adjacentTile = gameState.board.getTile(tileClicked.getTilex() , tileClicked.getTiley()+ diffY);
                        if(adjacentTile.getUnit()==null)
                        {
                        	  State attackState = new UnitAttackState(unitClicked, tileClicked, false, true);
                              State moveState = new UnitMovingState(unitClicked, tileClicked, adjacentTile);
                              gameStateMachine.setState(moveState, out, gameState);
                              try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
                              gameStateMachine.setState(attackState, out, gameState);
                        }
                      
                    }
                    else
                    {
                    	//Only attack without moving
                    	State attackState = new UnitAttackState(unitClicked, tileClicked, false, true);
                        gameStateMachine.setState(attackState, out, gameState);
                    }                      
                } 
                else if(distance == 3)
                {
                    if(Math.abs(tile.getTilex() - tileClicked.getTilex()) == 2&&Math.abs(tile.getTiley() - tileClicked.getTiley()) == 1)
                    {
                    	int diffX = tile.getTilex() - tileClicked.getTilex();
                        diffX = diffX/2;
                        adjacentTile = gameState.board.getTile(tileClicked.getTilex()+ diffX , tileClicked.getTiley());
                        if(adjacentTile.getUnit()==null)
                        {
                        	  State attackState = new UnitAttackState(unitClicked, tileClicked, false, true);
                              State moveState = new UnitMovingState(unitClicked, tileClicked, adjacentTile);
                              gameStateMachine.setState(moveState, out, gameState);
                              try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
                              gameStateMachine.setState(attackState, out, gameState);
                        }
                      
                    }
                    else if(Math.abs(tile.getTilex() - tileClicked.getTilex()) == 1&&Math.abs(tile.getTiley() - tileClicked.getTiley()) == 2)
                    {
                    	int diffY = tile.getTiley() - tileClicked.getTiley();
                        diffY = diffY/2;
                        adjacentTile = gameState.board.getTile(tileClicked.getTilex() , tileClicked.getTiley()+ diffY);
                        if(adjacentTile.getUnit()==null)
                        {
                        	   State attackState = new UnitAttackState(unitClicked, tileClicked, false, true);
                               State moveState = new UnitMovingState(unitClicked, tileClicked, adjacentTile);
                               gameStateMachine.setState(moveState, out, gameState);
                               try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
                               gameStateMachine.setState(attackState, out, gameState);
                        }
                     
                    }
                    else
                    {
                    	//do nothing
                    	State noSelectionState = new NoSelectionState();
                    	gameStateMachine.setState(noSelectionState, out, gameState);
                    }
                }

                else if(distance>3)
                {
                	//do nothing
                	State noSelectionState = new NoSelectionState();
                	gameStateMachine.setState(noSelectionState, out, gameState);
                }
                else {
                    System.out.println("Get the unit");
                    gameStateMachine.setState(new UnitAttackState(unitClicked, tileClicked, false, true), out, gameState);
                }
                gameState.resetBoardSelection(out);
        	}
        	else
        	{
        		gameStateMachine.setState(new EndTurnState());
        	}
        	
        	if(adjacentTile.getUnit()!=null)
        	{
        		if(nextState==null)
            	{
            		gameStateMachine.setState(new EndTurnState(),out,gameState);
            	}
        		else
        		{
        			gameStateMachine.setState(this.getNextState(),out, gameState);
        		}
        	}
        	
        }
        else {
            System.out.println("UnitSelectedState: Invalid Event");
        }
    }

    @Override
    public void enter(ActorRef out, GameState gameState) {
    	if(gameState.currentTurn==Turn.PLAYER)
    	    if(null!=this.tileClicked.getUnit()&&!this.tileClicked.getUnit().isAi())
    	    	highlightSurroundingTiles(out, this.tileClicked.getTilex(), this.tileClicked.getTiley(), tileClicked, gameState);
    	else if(null!=this.tileClicked.getUnit()&&this.tileClicked.getUnit().isAi())
    		aiHighlightSurroundingTiles(out, this.unitClicked.getPosition().getTilex(),this.unitClicked.getPosition().getTiley(),gameState.board.getTile(unitClicked.getPosition()), gameState);
    	else 
    	{
    		gameState.resetBoardSelection(out);
    		gameState.resetBoardState();
    	}
    }

    @Override
    public void exit(ActorRef out, GameState gameState) {
    	
		if(gameState.currentTurn==Turn.AI)
		{
			if(nextState==null)
			{
				nextState=new EndTurnState();
			}
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
                    if(surroundingTile.getUnit() == null ) {
                        surroundingTile.setTileState(TileState.Reachable);
                        BasicCommands.drawTile(out, surroundingTile, 1);
                    }
                    else if(surroundingTile.getUnit()!=null&&surroundingTile.getUnit().isAi()) {
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

        int x = tilex - 2;
        if(x >= 0) {
            boolean occupied = gameState.board.getTile(x + 1, tiley).getUnit() != null;
            if(!occupied) {
                TileState tileState = TileState.Reachable;
                if(gameState.board.getTile(x, tiley).getUnit() != null&&gameState.board.getTile(x, tiley).getUnit().isAi())
                    tileState = TileState.Occupied;
                gameState.board.getTile(x, tiley).setTileState(tileState);
                BasicCommands.drawTile(out, gameState.board.getTile(x,tiley), tileState.ordinal());
            }
            
            //check the surrounding tiles
            if(tiley+1<Constants.BOARD_HEIGHT)
            {
            	if(gameState.board.getTile(x, tiley+1).getUnit() != null&&gameState.board.getTile(x, tiley+1).getUnit().isAi())
            	{
            		gameState.board.getTile(x, tiley+1).setTileState(TileState.Occupied);
            		BasicCommands.drawTile(out, gameState.board.getTile(x,tiley+1),2);
            	}
            }
                      
            if(tiley-1>=0)
            {
            	if(gameState.board.getTile(x, tiley-1).getUnit() != null&&gameState.board.getTile(x, tiley-1).getUnit().isAi())
            	{
            		gameState.board.getTile(x, tiley-1).setTileState(TileState.Occupied);
            		BasicCommands.drawTile(out, gameState.board.getTile(x,tiley-1),2);
            	}
            }
                      
        }
        x = tilex + 2;
        if(x < Constants.BOARD_WIDTH) {
            boolean occupied = gameState.board.getTile(x - 1, tiley).getUnit() != null;
            if(!occupied) {
                TileState tileState = TileState.Reachable;
                if(gameState.board.getTile(x, tiley).getUnit() != null&&gameState.board.getTile(x, tiley).getUnit().isAi())
                    tileState = TileState.Occupied;
                gameState.board.getTile(x, tiley).setTileState(tileState);
                BasicCommands.drawTile(out, gameState.board.getTile(x,tiley),tileState.ordinal());
            }
            
            //check the surrounding tiles
            if(tiley+1<Constants.BOARD_HEIGHT)
            {
            	if(gameState.board.getTile(x, tiley+1).getUnit() != null&&gameState.board.getTile(x, tiley+1).getUnit().isAi())
            	{
            		gameState.board.getTile(x, tiley+1).setTileState(TileState.Occupied);
            		BasicCommands.drawTile(out, gameState.board.getTile(x,tiley+1),2);
            	}
            }
            
            if(tiley-1>=0)
            {
            	if(gameState.board.getTile(x, tiley-1).getUnit() != null&&gameState.board.getTile(x, tiley-1).getUnit().isAi())
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
                if(gameState.board.getTile(tilex, y).getUnit() != null&&gameState.board.getTile(tilex, y).getUnit().isAi())
                    tileState = TileState.Occupied;
                gameState.board.getTile(tilex, y).setTileState(tileState);
                BasicCommands.drawTile(out, gameState.board.getTile(tilex, y), tileState.ordinal());
            }
            
            //check the surrounding tiles
            if(tilex+1<Constants.BOARD_WIDTH)
            {
            	if(gameState.board.getTile(tilex+1, y).getUnit() != null&&gameState.board.getTile(tilex+1, y).getUnit().isAi())
            	{
            		gameState.board.getTile(tilex+1, y).setTileState(TileState.Occupied);
            		BasicCommands.drawTile(out, gameState.board.getTile(tilex+1, y),2);
            	}
            }
            
            if(tilex-1>=0)
            {
            	if(gameState.board.getTile(tilex-1, y).getUnit() != null&&gameState.board.getTile(tilex-1, y).getUnit().isAi())
            	{
            		gameState.board.getTile(tilex-1, y).setTileState(TileState.Occupied);
            		BasicCommands.drawTile(out, gameState.board.getTile(tilex-1, y),2);
            	}
            }
           
        }
        
        
        y = tiley + 2;
        if(y < Constants.BOARD_HEIGHT) {
            boolean occupied = gameState.board.getTile(tilex, y - 1).getUnit() != null;
            if(!occupied) {
                TileState tileState = TileState.Reachable;
                if(gameState.board.getTile(tilex, y).getUnit() != null&&gameState.board.getTile(tilex, y).getUnit().isAi())
                    tileState = TileState.Occupied;
                gameState.board.getTile(tilex, y).setTileState(tileState);
                BasicCommands.drawTile(out, gameState.board.getTile(tilex, y), tileState.ordinal());
            }
            
            //check the surrounding tiles
            if(tilex+1<=Constants.BOARD_WIDTH)
            {
            	if(gameState.board.getTile(tilex+1, y).getUnit() != null&&gameState.board.getTile(tilex+1, y).getUnit().isAi())
            	{
            		gameState.board.getTile(tilex+1, y).setTileState(TileState.Occupied);
            		BasicCommands.drawTile(out, gameState.board.getTile(tilex+1, y),2);
            	}
            }
            
            if(tilex-1>=0)
            {
            	if(gameState.board.getTile(tilex-1, y).getUnit() != null&&gameState.board.getTile(tilex-1, y).getUnit().isAi())
            	{
            		gameState.board.getTile(tilex-1, y).setTileState(TileState.Occupied);
            		BasicCommands.drawTile(out, gameState.board.getTile(tilex-1, y),2);
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

    int x = tilex - 2;
    if(x >= 0) {
        boolean occupied = gameState.board.getTile(x + 1, tiley).getUnit() != null;
        if(!occupied) {
            TileState tileState = TileState.Reachable;
            if(gameState.board.getTile(x, tiley).getUnit() != null&&!gameState.board.getTile(x, tiley).getUnit().isAi())
                tileState = TileState.Occupied;
            gameState.board.getTile(x, tiley).setTileState(tileState);
            BasicCommands.drawTile(out, gameState.board.getTile(x,tiley), tileState.ordinal());
        }
        
        //check the surrounding tiles
        if(tiley+1<Constants.BOARD_HEIGHT)
        {
        	if(gameState.board.getTile(x, tiley+1).getUnit() != null&&!gameState.board.getTile(x, tiley+1).getUnit().isAi())
        	{
        		gameState.board.getTile(x, tiley+1).setTileState(TileState.Occupied);
        		BasicCommands.drawTile(out, gameState.board.getTile(x,tiley+1),2);
        	}
        }
                  
        if(tiley-1>=0)
        {
        	if(gameState.board.getTile(x, tiley-1).getUnit() != null&&!gameState.board.getTile(x, tiley-1).getUnit().isAi())
        	{
        		gameState.board.getTile(x, tiley-1).setTileState(TileState.Occupied);
        		BasicCommands.drawTile(out, gameState.board.getTile(x,tiley-1),2);
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
            BasicCommands.drawTile(out, gameState.board.getTile(x,tiley),tileState.ordinal());
        }
        
        //check the surrounding tiles
        if(tiley+1<Constants.BOARD_HEIGHT)
        {
        	if(gameState.board.getTile(x, tiley+1).getUnit() != null&&!gameState.board.getTile(x, tiley+1).getUnit().isAi())
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
            if(gameState.board.getTile(tilex, y).getUnit() != null&&!gameState.board.getTile(tilex, y).getUnit().isAi())
                tileState = TileState.Occupied;
            gameState.board.getTile(tilex, y).setTileState(tileState);
            BasicCommands.drawTile(out, gameState.board.getTile(tilex, y), tileState.ordinal());
        }
        
        //check the surrounding tiles
        if(tilex+1<Constants.BOARD_WIDTH)
        {
        	if(gameState.board.getTile(tilex+1, y).getUnit() != null&&!gameState.board.getTile(tilex+1, y).getUnit().isAi())
        	{
        		gameState.board.getTile(tilex+1, y).setTileState(TileState.Occupied);
        		BasicCommands.drawTile(out, gameState.board.getTile(tilex+1, y),2);
        	}
        }
        
        if(tilex-1>=0)
        {
        	if(gameState.board.getTile(tilex-1, y).getUnit() != null&&!gameState.board.getTile(tilex-1, y).getUnit().isAi())
        	{
        		gameState.board.getTile(tilex-1, y).setTileState(TileState.Occupied);
        		BasicCommands.drawTile(out, gameState.board.getTile(tilex-1, y),2);
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
            BasicCommands.drawTile(out, gameState.board.getTile(tilex, y), tileState.ordinal());
        }
        
        //check the surrounding tiles
        if(tilex+1<=Constants.BOARD_WIDTH)
        {
        	if(gameState.board.getTile(tilex+1, y).getUnit() != null&&!gameState.board.getTile(tilex+1, y).getUnit().isAi())
        	{
        		gameState.board.getTile(tilex+1, y).setTileState(TileState.Occupied);
        		BasicCommands.drawTile(out, gameState.board.getTile(tilex+1, y),2);
        	}
        }
        
        if(tilex-1>=0)
        {
        	if(gameState.board.getTile(tilex-1, y).getUnit() != null&&!gameState.board.getTile(tilex-1, y).getUnit().isAi())
        	{
        		gameState.board.getTile(tilex-1, y).setTileState(TileState.Occupied);
        		BasicCommands.drawTile(out, gameState.board.getTile(tilex-1, y),2);
        	}
        }
     }

	}
}  
