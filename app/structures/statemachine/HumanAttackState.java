package structures.statemachine;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import events.EventProcessor;
import structures.GameState;
import structures.basic.Unit;
import structures.basic.UnitAnimationType;
import utils.Constants;
import structures.basic.Tile;
import structures.basic.Tile.Occupied;
import structures.basic.TileState;

public class HumanAttackState implements State{


	HumanAttackState(ActorRef out, Unit selectedUnit, Tile startTile, Tile targetTile, GameState gameState,GameStateMachine gameStateMachine)
	{
		System.out.println("Unit attack"); 
		if(!gameState.moved)
		{
			//If it does not moved, go to search again
			moveAndAttack(out, startTile, targetTile, gameState, gameStateMachine);		
			if(!gameState.isAttacking) {}
			//If does not find a suitable tile to move and attack
			//getUnitOnTileAttack(out, startTile,targetTile);
		}
		else
		{
			try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
			getUnitOnTileAttack(out, gameState.chooseTile, gameState.targetTile);
		}
		
	}
	
	@Override
	public void handleInput(ActorRef out, GameState gameState, JsonNode message, EventProcessor event,
			GameStateMachine gameStateMachine) {
		// Try to get the unit and attack
		gameStateMachine.setState(new NoSelectionState());
		gameState.moved=false;
		gameState.isAttacking=false;
		gameState.chooseTile=null;
		gameState.targetTile=null;
		
	}

	
	private void getUnitOnTileAttack(ActorRef out,Tile startTile,Tile targetTile)
	{
		//get the unit from the first tile
		Unit unit =startTile.getUnit();
		//get ai unit from the second tile
		Unit enemyUnit= targetTile.getAiUnit();
		//Attack animation
		BasicCommands.playUnitAnimation(out, unit, UnitAnimationType.attack);
		try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
		//Set the animation to be idle
		BasicCommands.playUnitAnimation(out, unit, UnitAnimationType.idle);
		try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
		int attackHealth=enemyUnit.getHealth()-unit.getAttack();
		unitAttackBack(out, startTile, targetTile,unit, enemyUnit, attackHealth);		
	}
	
	/**
	 * If the distance is long, move and attack
	 * @param out
	 * @param startTile
	 * @param targetTile
	 */
    private void moveAndAttack(ActorRef out,Tile startTile,Tile targetTile,GameState gameState,GameStateMachine gameStateMachine)
    {
    	//check the reachable tile surrounding the ai unit
        Unit unit =startTile.getUnit();
       int targetX=targetTile.getTilex();
       int targetY=targetTile.getTiley();
       if(!searchUnitReachableTile(out, gameState, startTile,targetTile))
       {
    	   //we need to check the vertical tiles 
    	   for(int i=targetX-1;i<=targetX+1;i++)
    	   {
    		   int x=i;
    		   if(x<0) continue;
    		   if(x >= Constants.BOARD_WIDTH) continue;
    		   Tile surroundingTile = gameState.board.getTile(x, targetY);
    		   if(surroundingTile == targetTile)
 	              continue;
    		   if(surroundingTile.getUnit() == null && surroundingTile.getAiUnit()==null) {
	                  //surroundingTile.setTileState(TileState.Reachable);
	            	  if(TileState.Reachable==surroundingTile.getTileState())
	            	  {
	            		  gameState.chooseTile=surroundingTile;
	            		  gameState.isAttacking=true;
	            		  gameStateMachine.setState(new UnitMovingState(out, unit, startTile, surroundingTile, gameState,gameStateMachine));
	            		  if(gameState.moved)
	            		  {
	            			  return;
	            		  }
	            	  }
	                 // BasicCommands.drawTile(out, surroundingTile, 1);
	              }
	              else {
	                  surroundingTile.setTileState(TileState.Occupied);
	                 // BasicCommands.drawTile(out, surroundingTile, 2);
	              }
    	   }
    	   //we need to check the horizontal tiles 
    	   for(int i=targetY-1;i<=targetY+1;i++)
    	   {
    		   int y=i;
    		   if(y<0) continue;
    		   if(y >= Constants.BOARD_WIDTH) continue;
    		   Tile surroundingTile = gameState.board.getTile(targetX, y);
    		   if(surroundingTile == targetTile)
 	              continue;
    		   if(surroundingTile.getUnit() == null && surroundingTile.getAiUnit()==null) {
	                  //surroundingTile.setTileState(TileState.Reachable)
	            	  if(TileState.Reachable==surroundingTile.getTileState())
	            	  {
	            		  gameState.chooseTile=surroundingTile;
	            		  gameState.isAttacking=true;
	            		  gameStateMachine.setState(new UnitMovingState(out, unit, startTile, surroundingTile, gameState,gameStateMachine));
	            		  if(gameState.moved)
	            		  {
	            			  return;
	            		  }
	            	  }
	                 // BasicCommands.drawTile(out, surroundingTile, 1);
	              }
	              else {
	                  surroundingTile.setTileState(TileState.Occupied);
	                 // BasicCommands.drawTile(out, surroundingTile, 2);
	              }
    	   }
    	   //check the rest tiles
	       for(int i = targetX-1; i <= targetX+1; i++ ) {
	       for(int j = targetY-1; j <= targetY+1; j++) {
	    	  int x=i;    	 
	          if(x < 0) continue;
	          if(x >= Constants.BOARD_WIDTH) continue;
	          int y=j;
	          if(y < 0) continue;
	          if(y >= Constants.BOARD_HEIGHT) continue;
	          Tile surroundingTile = gameState.board.getTile(x, y);
	          if(surroundingTile == targetTile)
	              continue;
	          if (surroundingTile != null) {
	              if(surroundingTile.getUnit() == null && surroundingTile.getAiUnit()==null) {
	                  //surroundingTile.setTileState(TileState.Reachable)
	            	  if(TileState.Reachable==surroundingTile.getTileState())
	            	  {
	            		  gameState.chooseTile=surroundingTile;
	            		  gameState.isAttacking=true;
	            		  gameStateMachine.setState(new UnitMovingState(out, unit, startTile, surroundingTile, gameState,gameStateMachine));
	            		  if(gameState.moved)
	            		  {
	            			  return;
	            		  }
	            	  }
	                 // BasicCommands.drawTile(out, surroundingTile, 1);
	              }
	              else {
	                  surroundingTile.setTileState(TileState.Occupied);
	                 // BasicCommands.drawTile(out, surroundingTile, 2);
	              }
	          }
	       }
	     }
       }
      
    }
    /**
     * Search for the reachable tiles on the board
     * @param out
     * @param gameState
     * @param startTile
     */
    
    private boolean searchUnitReachableTile(ActorRef out,GameState gameState,Tile startTile,Tile targetTile)
    {
    	int startX=startTile.getTilex();
    	int startY=startTile.getTiley();
        for(int i = -1; i <=1; i++ ) {
            for(int j = -1; j <= 1; j++) {
               int x = startX + i;
               if(x < 0) continue;
               if(x >= Constants.BOARD_WIDTH) continue;
               int y = startY + j;
               if(y < 0) continue;
               if(y >= Constants.BOARD_HEIGHT) continue;
               Tile surroundingTile = gameState.board.getTile(x, y);
               if(surroundingTile == startTile)
                   continue;
               if (surroundingTile != null) {
                   if(surroundingTile.getUnit() == null && surroundingTile.getAiUnit()==null) {
                       surroundingTile.setTileState(TileState.Reachable);
                      // BasicCommands.drawTile(out, surroundingTile, 1);
                   }
                   else {
                	   //if the target tile within the range of the surroundings, attack
                	   if(surroundingTile==targetTile)
                	   {
                		   getUnitOnTileAttack(out, startTile, targetTile);	
                		   return true;
					   }
                	   surroundingTile.setTileState(TileState.Occupied);
                	 }
                      
                      // BasicCommands.drawTile(out, surroundingTile, 2);
                   }
               }
            }
         return false;
    }
    
    
    /**
     * User unit Attack the Ai unit
     */
    private void unitAttackBack(ActorRef out,Tile startTile,Tile targetTile, Unit unit,Unit enemyUnit, int health)
    {
    	
    	if(health>0)
		{
    		BasicCommands.setUnitHealth(out, enemyUnit,health );
			BasicCommands.playUnitAnimation(out, enemyUnit, UnitAnimationType.attack);			
			try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
			//Set the animation to be idle
			BasicCommands.playUnitAnimation(out, enemyUnit, UnitAnimationType.idle);
			try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
			//The human unit will be attacked
			int attackBackHealth=unit.getHealth()-enemyUnit.getAttack();
			if(attackBackHealth>0)
			{
				setUnitHealth(out, unit, attackBackHealth);
			}
			else //the human unit dead
			{
				setUnitHealth(out, unit, 0);
				BasicCommands.playUnitAnimation(out, enemyUnit, UnitAnimationType.death);		
				try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
				BasicCommands.deleteUnit(out, enemyUnit);
				startTile.clearUnit();
				
			}
		
		}
		else
		{
			setUnitHealth(out, enemyUnit, 0);
			BasicCommands.playUnitAnimation(out, enemyUnit, UnitAnimationType.death);
			try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
			BasicCommands.deleteUnit(out, enemyUnit);
			targetTile.clearUnit();
		}	   	
    	
    }
    
    private void setUnitHealth(ActorRef out, Unit unit,int health)
    {
    	BasicCommands.setUnitHealth(out,unit,health );
		try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
    }
}
