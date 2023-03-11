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
                gameStateMachine.setState(new NoSelectionState(), out, gameState);
            } else if (tile.getTileState() == TileState.Reachable) {
                gameState.resetBoardSelection(out);
                System.out.println("UnitSelectedState: Reachable Tile Clicked");
                gameStateMachine.setState(new UnitMovingState(unitClicked, tileClicked, tile), out, gameState);
            }
          
            else if(tile.getTileState()==TileState.Occupied)
            {
            	Tile adjacentTile=null;
            	if(null!=tile.getUnit()&&tile.getUnit().isAi())
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
                    	//X axis moving
                        if(Math.abs(tile.getTilex() - tileClicked.getTilex()) == 3) {
                            int diffX = tile.getTilex() - tileClicked.getTilex();
                            if(diffX>=0)
                            	diffX = diffX/2+1;
                            else
                            	diffX=diffX/2-1;
                            adjacentTile = gameState.board.getTile(tileClicked.getTilex() + diffX, tileClicked.getTiley());
                            if(adjacentTile.getUnit()==null)
                            {
                            	  State attackState = new UnitAttackState(unitClicked, tile, false, true);
                                  State moveState = new UnitMovingState(unitClicked, tileClicked, adjacentTile);
                                  gameStateMachine.setState(moveState, out, gameState);
                                  try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
                                  gameStateMachine.setState(attackState, out, gameState);
                            }                     
                        }
                        //Y axis moving
                        else if(Math.abs(tile.getTiley() - tileClicked.getTiley()) == 3)
                        {
                        	int diffY = tile.getTiley() - tileClicked.getTiley();
                            if(diffY>=0)
                            	diffY = diffY/2+1;
                            else
                            	diffY=diffY/2-1;
                            adjacentTile = gameState.board.getTile(tileClicked.getTilex() , tileClicked.getTiley()+ diffY);
                            if(adjacentTile.getUnit()==null)
                            {
                                State attackState = new UnitAttackState(unitClicked, tile, false, true);
                                State moveState = new UnitMovingState(unitClicked, tileClicked, adjacentTile);
                                gameStateMachine.setState(moveState, out, gameState);
                                try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
                                gameStateMachine.setState(attackState, out, gameState);
                            }
                        
                        }
                        if(Math.abs(tile.getTilex() - tileClicked.getTilex()) == 2&&Math.abs(tile.getTiley() - tileClicked.getTiley()) == 1)
                        {
                        	int diffX = tile.getTilex() - tileClicked.getTilex();
                            diffX = diffX/2;
                            adjacentTile = gameState.board.getTile(tileClicked.getTilex()+ diffX , tileClicked.getTiley());
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
                            adjacentTile = gameState.board.getTile(tileClicked.getTilex() , tileClicked.getTiley()+ diffY);
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
                    else if(distance==4)
                    {
                     	//X axis moving
                        if(Math.abs(tile.getTilex() - tileClicked.getTilex()) == 3) {
                            int diffX = tile.getTilex() - tileClicked.getTilex();
                            if(diffX>=0)
                            	diffX = diffX/2+1;
                            else
                            	diffX=diffX/2-1;
                            adjacentTile = gameState.board.getTile(tileClicked.getTilex() + diffX, tileClicked.getTiley());
                            if(adjacentTile.getUnit()==null)
                            {
                                State attackState = new UnitAttackState(unitClicked, tile, false, true);
                                State moveState = new UnitMovingState(unitClicked, tileClicked, adjacentTile);
                                gameStateMachine.setState(moveState, out, gameState);
                                try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
                                gameStateMachine.setState(attackState, out, gameState);
                            }
                       
                        }
                        //Y axis moving
                        else if(Math.abs(tile.getTiley() - tileClicked.getTiley()) == 3)
                        {
                        	int diffY = tile.getTiley() - tileClicked.getTiley();
                            if(diffY>=0)
                            	diffY = diffY/2+1;
                            else
                            	diffY=diffY/2-1;
                            adjacentTile = gameState.board.getTile(tileClicked.getTilex() , tileClicked.getTiley()+ diffY);
                            if(adjacentTile.getUnit()==null)
                            {
                            	State attackState = new UnitAttackState(unitClicked, tile, false, true);
                                State moveState = new UnitMovingState(unitClicked, tileClicked, adjacentTile);
                                gameStateMachine.setState(moveState, out, gameState);
                                try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
                                gameStateMachine.setState(attackState, out, gameState);
                            }
                            
                        }
                        else if(Math.abs(tile.getTiley() - tileClicked.getTiley()) == 2)
                        {
                        	int diffY= tile.getTiley()-tileClicked.getTiley();
                        	int diffX= tile.getTilex()-tileClicked.getTilex();
                        	diffY=diffY/2;
                        	diffX=diffX/2;
    	                	adjacentTile = gameState.board.getTile(tileClicked.getTilex()+diffX , tileClicked.getTiley()+ diffY);
    	                    if(adjacentTile.getUnit()==null)
    	                    {
    	                     	State attackState = new UnitAttackState(unitClicked, tile, false, true);
    	                         State moveState = new UnitMovingState(unitClicked, tileClicked, adjacentTile);
    	                         gameStateMachine.setState(moveState, out, gameState);
    	                         try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
    	                         gameStateMachine.setState(attackState, out, gameState);
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
                        gameStateMachine.setState(new UnitAttackState(unitClicked, tile, false, true), out, gameState);
                    }
                    gameState.resetBoardSelection(out);
            	}

            }
        } else if(event instanceof CardClicked) {
            gameState.resetBoardSelection(out);
            System.out.println("UnitSelectedState: Card Clicked");
            gameStateMachine.setState(new CardSelectedState(out, message, gameState), out, gameState);
        }else if(event instanceof OtherClicked)
        {
        	 gameState.resetBoardSelection(out);
        	 gameState.resetCardSelection(out);
        	 gameStateMachine.setState(new NoSelectionState(), out, gameState);
        } 
        else if(gameState.currentTurn==Turn.AI)
        {
        	tile = gameState.board.getTile(unitClicked.getPosition());
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
                 	//X axis moving
                    if(Math.abs(tile.getTilex() - tileClicked.getTilex()) == 3) {
                        int diffX = tile.getTilex() - tileClicked.getTilex();
                        if(diffX>=0)
                        	diffX = diffX/2+1;
                        else
                        	diffX=diffX/2-1;
                        adjacentTile = gameState.board.getTile(tileClicked.getTilex() + diffX, tileClicked.getTiley());
                        if(adjacentTile.getUnit()==null)
                        {
                        	  State attackState = new UnitAttackState(unitClicked, tile, false, true);
                              State moveState = new UnitMovingState(unitClicked, tileClicked, adjacentTile);
                              gameStateMachine.setState(moveState, out, gameState);
                              try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
                              gameStateMachine.setState(attackState, out, gameState);
                        }                     
                    }
                    //Y axis moving
                    else if(Math.abs(tile.getTiley() - tileClicked.getTiley()) == 3)
                    {
                    	int diffY = tile.getTiley() - tileClicked.getTiley();
                        if(diffY>=0)
                        	diffY = diffY/2+1;
                        else
                        	diffY=diffY/2-1;
                        adjacentTile = gameState.board.getTile(tileClicked.getTilex() , tileClicked.getTiley()+ diffY);
                        if(adjacentTile.getUnit()==null)
                        {
                            State attackState = new UnitAttackState(unitClicked, tile, false, true);
                            State moveState = new UnitMovingState(unitClicked, tileClicked, adjacentTile);
                            gameStateMachine.setState(moveState, out, gameState);
                            try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
                            gameStateMachine.setState(attackState, out, gameState);
                        }
                    
                    }
                    
                    else if(Math.abs(tile.getTilex() - tileClicked.getTilex()) == 2&&Math.abs(tile.getTiley() - tileClicked.getTiley()) == 1)
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
                else if(distance==4)
                {
                 	//X axis moving
                    if(Math.abs(tile.getTilex() - tileClicked.getTilex()) == 3) {
                        int diffX = tile.getTilex() - tileClicked.getTilex();
                        if(diffX>=0)
                        	diffX = diffX/2+1;
                        else
                        	diffX=diffX/2-1;
                        adjacentTile = gameState.board.getTile(tileClicked.getTilex() + diffX, tileClicked.getTiley());
                        if(adjacentTile.getUnit()==null)
                        {
                            State attackState = new UnitAttackState(unitClicked, tile, false, true);
                            State moveState = new UnitMovingState(unitClicked, tileClicked, adjacentTile);
                            gameStateMachine.setState(moveState, out, gameState);
                            try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
                            gameStateMachine.setState(attackState, out, gameState);
                        }
                   
                    }
                    //Y axis moving
                    else if(Math.abs(tile.getTiley() - tileClicked.getTiley()) == 3)
                    {
                    	int diffY = tile.getTiley() - tileClicked.getTiley();
                        if(diffY>=0)
                        	diffY = diffY/2+1;
                        else
                        	diffY=diffY/2-1;
                        adjacentTile = gameState.board.getTile(tileClicked.getTilex() , tileClicked.getTiley()+ diffY);
                        if(adjacentTile.getUnit()==null)
                        {
                        	State attackState = new UnitAttackState(unitClicked, tile, false, true);
                            State moveState = new UnitMovingState(unitClicked, tileClicked, adjacentTile);
                            gameStateMachine.setState(moveState, out, gameState);
                            try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
                            gameStateMachine.setState(attackState, out, gameState);
                        }
                        
                    }
                    else if(Math.abs(tile.getTiley() - tileClicked.getTiley()) == 2)
                    {
                    	int diffY= tile.getTiley()-tileClicked.getTiley();
                    	int diffX= tile.getTilex()-tileClicked.getTilex();
                    	diffY=diffY/2;
                    	diffX=diffX/2;
	                	adjacentTile = gameState.board.getTile(tileClicked.getTilex()+diffX , tileClicked.getTiley()+ diffY);
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

                else if(distance>4)
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
        		gameStateMachine.setState(new EndTurnState(), out, gameState);
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
    	else if(null!=this.tileClicked.getUnit()&&this.tileClicked.getUnit().isAi()&&gameState.currentTurn==Turn.AI)
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
        
        
        int x=tilex-3;
        if(x>=0) {
        	setOccupiedUserTiles(out, x, tiley, gameState,false);

    	    if(tiley-1>=0)
    	    {
    	    	setOccupiedUserTiles(out, x, tiley-1, gameState,false);
    	    }
    	    
    	    if(tiley+1<Constants.BOARD_HEIGHT)
    	    {
    	     	setOccupiedUserTiles(out, x, tiley+1, gameState,false);
    	    }
        }
        x=tilex+3;
        if(x<Constants.BOARD_WIDTH) {
        	setOccupiedUserTiles(out, x, tiley, gameState,false);

    	    if(tiley-1>=0)
    	    {
    	    	setOccupiedUserTiles(out, x, tiley-1, gameState,false);
    	    }
    	    
    	    if(tiley+1<Constants.BOARD_HEIGHT)
    	    {
    	     	setOccupiedUserTiles(out, x, tiley+1, gameState,false);
    	    }
        }
        
        int y=tiley-3;
        if(y>=0) {
        	setOccupiedUserTiles(out, tilex, y, gameState,false);

    	    if(tilex-1>=0)
    	    {
    	    	setOccupiedUserTiles(out, tilex-1, y, gameState,false);
    	    }
    	    
    	    if(tilex+1<Constants.BOARD_WIDTH)
    	    {
    	     	setOccupiedUserTiles(out, tilex+1, y, gameState,false);
    	    }
        }
        
        y=tiley+3;
        if(y<Constants.BOARD_HEIGHT) {
        	setOccupiedUserTiles(out, tilex, y, gameState,false);

    	    if(tilex-1>=0)
    	    {
    	    	setOccupiedUserTiles(out, tilex-1, y, gameState,false);
    	    }
    	    
    	    if(tilex+1<Constants.BOARD_WIDTH)
    	    {
    	     	setOccupiedUserTiles(out, tilex+1, y, gameState,false);
    	    }
        }

        x = tilex - 2;
        if(x >= 0) {
            boolean occupied = gameState.board.getTile(x + 1, tiley).getUnit() != null&&
            		gameState.board.getTile(x+1, tiley).getUnit().isAi();
    		
            setOccupiedUserTiles(out, x+1, tiley, gameState,true);
            if(!occupied) {
            	
           	 //check the surrounding tiles     
               setOccupiedUserTiles(out, x, tiley, gameState,true);
               
               if(tiley+1<Constants.BOARD_HEIGHT)
               {
               	setOccupiedUserTiles(out, x, tiley+1, gameState,false);
               }
               
               if(tiley-1>=0)
               {
               	setOccupiedUserTiles(out, x, tiley-1, gameState,false);
               }
               
               //check the surrounding tiles
               if(tiley+2<Constants.BOARD_HEIGHT)
               {
            	   if(gameState.board.getTile(x+1, tiley+1).getUnit()==null)
            		   setOccupiedUserTiles(out, x, tiley+2, gameState,false);
               }
               
               if(tiley-2>=0)
               {
            	   if(gameState.board.getTile(x+1, tiley-1).getUnit()==null)
            		   setOccupiedUserTiles(out, x, tiley-2, gameState,false);
               }
           }
                      
        }
        x = tilex + 2;
        if(x < Constants.BOARD_WIDTH) {
            boolean occupied = gameState.board.getTile(x - 1, tiley).getUnit() != null&&
            		gameState.board.getTile(x-1,tiley).getUnit().isAi();
            setOccupiedAiTiles(out, x-1, tiley, gameState,true);
            if(!occupied) {
            	
            	 //check the surrounding tiles     
                setOccupiedUserTiles(out, x, tiley, gameState,true);
                
                if(tiley+1<Constants.BOARD_HEIGHT)
                {
                	setOccupiedUserTiles(out, x, tiley+1, gameState,false);
                }
                
                if(tiley-1>=0)
                {
                	setOccupiedUserTiles(out, x, tiley-1, gameState,false);
                }
                
                //check the surrounding tiles
                if(tiley+2<Constants.BOARD_HEIGHT)
                {
                	if(gameState.board.getTile(x-1, tiley+1).getUnit()==null)
                		setOccupiedUserTiles(out, x, tiley+2, gameState,false);
                }
                
                if(tiley-2>=0)
                {
                	if(gameState.board.getTile(x-1, tiley-1).getUnit()==null)
                		setOccupiedUserTiles(out, x, tiley-2, gameState,false);
                }
            }
           
        }
        x = tilex+1;
        if(x<Constants.BOARD_WIDTH) {
       	 setOccupiedUserTiles(out,x,tiley,gameState,true);
        }
        
        x=tilex-1;
        if(x>=0) {
        	setOccupiedUserTiles(out, x, tiley, gameState,true);
        }
        
        y = tiley - 2;
        if(y >= 0) {
        	boolean occupied = gameState.board.getTile(tilex, y+1 ).getUnit() != null&&
            		gameState.board.getTile(tilex,y+1).getUnit().isAi();
            setOccupiedUserTiles(out, tilex, y+1, gameState,true); 
            if(!occupied) {           
                //check the surrounding tiles
            	setOccupiedUserTiles(out, tilex, y, gameState,true);
            	
                if(tilex+1<Constants.BOARD_WIDTH)
                {
                	setOccupiedUserTiles(out, tilex+1, y, gameState,false);
                }
                
                if(tilex-1>=0)
                {
                	setOccupiedUserTiles(out, tilex-1, y, gameState,false);
                }
                
                if(tilex+2<Constants.BOARD_WIDTH)
                {
                	if(gameState.board.getTile(tilex+1, y+1).getUnit()==null)
                		setOccupiedUserTiles(out, tilex+2, y, gameState,false);
                }
                
                if(tilex-2>=0)
                {
                	if(gameState.board.getTile(tilex-1, y+1).getUnit()==null)
                		setOccupiedUserTiles(out, tilex-2, y, gameState,false);
                }
            }
        }
        
        
        y = tiley + 2;
        if(y < Constants.BOARD_HEIGHT) {
            boolean occupied = gameState.board.getTile(tilex, y-1 ).getUnit() != null&&
            		gameState.board.getTile(tilex,y-1).getUnit().isAi();
            setOccupiedUserTiles(out, tilex, y-1, gameState,true); 
            if(!occupied) {           
                //check the surrounding tiles
            	
            	setOccupiedUserTiles(out, tilex, y, gameState,true);
            	
                if(tilex+1<Constants.BOARD_WIDTH)
                {
                	setOccupiedUserTiles(out, tilex+1, y, gameState,false);
                }
                
                if(tilex-1>=0)
                {
                	setOccupiedUserTiles(out, tilex-1, y, gameState,false);
                }
                
                if(tilex+2<Constants.BOARD_WIDTH)
                {
                	if(gameState.board.getTile(tilex+1, y-1).getUnit()==null)
                		setOccupiedUserTiles(out, tilex+2, y, gameState,false);
                }
                
                if(tilex-2>=0)
                {
                	if(gameState.board.getTile(tilex-1, y-1).getUnit()==null)
                		setOccupiedUserTiles(out, tilex-2, y, gameState,false);
                }
            }
        
         }
        
        y=tiley-1;
        if(y>0)
        {
        	setOccupiedUserTiles(out, tilex, y, gameState,true);
        }
        
        y=tiley+1;
        if(y<Constants.BOARD_HEIGHT)
        {
        	setOccupiedUserTiles(out,tilex,y,gameState,true);
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
	
	
	private void setOccupiedUserTiles(ActorRef out,int x, int y, GameState gameState, boolean highlight)
	{
		if(gameState.board.getTile(x, y).getUnit() != null&&gameState.board.getTile(x, y).getUnit().isAi())
    	{
    		gameState.board.getTile(x, y).setTileState(TileState.Occupied);
    		try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
    		BasicCommands.drawTile(out, gameState.board.getTile(x, y),2);
    	}
		else if(gameState.board.getTile(x, y).getUnit() == null&&highlight)
    	{
    		gameState.board.getTile(x, y).setTileState(TileState.Reachable);
    		try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
    		BasicCommands.drawTile(out, gameState.board.getTile(x, y),1);
    		
    	}
	}
}  
