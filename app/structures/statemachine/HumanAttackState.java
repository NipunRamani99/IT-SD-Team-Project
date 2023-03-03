package structures.statemachine;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import events.EventProcessor;
import events.Heartbeat;
import structures.GameState;
import structures.basic.Unit;
import structures.basic.UnitAnimationType;
import utils.Constants;
import structures.basic.Tile;
import structures.basic.Tile.Occupied;
import structures.basic.TileState;
import utils.BasicObjectBuilders;

public class HumanAttackState extends State{


	private Unit selectedUnit = null;

	private Unit enemyUnit = null;

	HumanAttackState(Unit selectedUnit, Tile targetTile, boolean isPlayer)
	{
		this.selectedUnit = selectedUnit;
		if(isPlayer)
			this.enemyUnit = targetTile.getAiUnit();
		else
			this.enemyUnit = targetTile.getUnit();
	}

	HumanAttackState(Unit selectedUnit, Unit enemyUnit)
	{
		this.selectedUnit = selectedUnit;
		this.enemyUnit = enemyUnit;
	}

	HumanAttackState(Unit selectedUnit, Tile targetTile, boolean reactAttack, boolean isPlayer)
	{
		this.selectedUnit = selectedUnit;

		this.enemyUnit = isPlayer ? targetTile.getAiUnit() : targetTile.getUnit();
		if(!reactAttack) {
			State reactState = new HumanAttackState(isPlayer ? targetTile.getAiUnit() : targetTile.getUnit(), selectedUnit);
			if (nextState != null) {
				reactState.setNextState(nextState);
			}
			this.nextState = reactState;
		}
	}
	
	@Override
	public void handleInput(ActorRef out, GameState gameState, JsonNode message, EventProcessor event,
			GameStateMachine gameStateMachine) {
		// Try to get the unit and attack
		if(event instanceof  Heartbeat)
			gameStateMachine.setState(nextState != null ? nextState : new NoSelectionState(), out, gameState);
	}

	@Override
	public void enter(ActorRef out, GameState gameState) {
		try {
			System.out.println("Unit attack");
			getUnitOnTileAttack(out, gameState);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void exit(ActorRef out, GameState gameState) {

	}


	private void getUnitOnTileAttack(ActorRef out, GameState gameState)throws InterruptedException
	{
		//Attack animation

		BasicCommands.playUnitAnimation(out, this.selectedUnit, UnitAnimationType.attack);
		int attackHealth = this.enemyUnit.getHealth() - this.selectedUnit.getAttack();
		unitAttack(out, enemyUnit, attackHealth, gameState);

	}
	
//	/**
//	 * If the distance is long, move and attack
//	 * @param out
//	 * @param startTile
//	 * @param targetTile
//	 */
//    private void moveAndAttack(ActorRef out,Tile startTile,Tile targetTile,GameState gameState,GameStateMachine gameStateMachine)
//    {
//    	//check the reachable tile surrounding the ai unit
//        Unit unit =startTile.getUnit();
//       int targetX=targetTile.getTilex();
//       int targetY=targetTile.getTiley();
//       if(!searchUnitReachableTile(out, gameState, startTile,targetTile))
//       {
//    	   //we need to check the vertical tiles 
//    	   for(int i=targetX-1;i<=targetX+1;i++)
//    	   {
//    		   int x=i;
//    		   if(x<0) continue;
//    		   if(x >= Constants.BOARD_WIDTH) continue;
//    		   Tile surroundingTile = gameState.board.getTile(x, targetY);
//    		   if(surroundingTile == targetTile)
// 	              continue;
//    		   if(surroundingTile.getUnit() == null && surroundingTile.getAiUnit()==null) {
//	                  //surroundingTile.setTileState(TileState.Reachable);
//	            	  if(TileState.Reachable==surroundingTile.getTileState())
//	            	  {
//	            		  gameState.chooseTile=surroundingTile;
//	            		  gameState.isAttacking=true;
//	            		  gameStateMachine.setState(new UnitMovingState(unit, startTile, surroundingTile));
//	            		  if(gameState.moved)
//	            		  {
//	            			  return;
//	            		  }
//	            	  }
//	                 // BasicCommands.drawTile(out, surroundingTile, 1);
//	              }
//	              else {
//	                  surroundingTile.setTileState(TileState.Occupied);
//	                 // BasicCommands.drawTile(out, surroundingTile, 2);
//	              }
//    	   }
//    	   //we need to check the horizontal tiles 
//    	   for(int i=targetY-1;i<=targetY+1;i++)
//    	   {
//    		   int y=i;
//    		   if(y<0) continue;
//    		   if(y >= Constants.BOARD_WIDTH) continue;
//    		   Tile surroundingTile = gameState.board.getTile(targetX, y);
//    		   if(surroundingTile == targetTile)
// 	              continue;
//    		   if(surroundingTile.getUnit() == null && surroundingTile.getAiUnit()==null) {
//	                  //surroundingTile.setTileState(TileState.Reachable)
//	            	  if(TileState.Reachable==surroundingTile.getTileState())
//	            	  {
//	            		  gameState.chooseTile=surroundingTile;
//	            		  gameState.isAttacking=true;
//	            		  gameStateMachine.setState(new UnitMovingState(unit, startTile, surroundingTile));
//	            		  if(gameState.moved)
//	            		  {
//	            			  return;
//	            		  }
//	            	  }
//	                 // BasicCommands.drawTile(out, surroundingTile, 1);
//	              }
//	              else {
//	                  surroundingTile.setTileState(TileState.Occupied);
//	                 // BasicCommands.drawTile(out, surroundingTile, 2);
//	              }
//    	   }
//    	   //check the rest tiles
//	       for(int i = targetX-1; i <= targetX+1; i++ ) {
//	       for(int j = targetY-1; j <= targetY+1; j++) {
//	    	  int x=i;    	 
//	          if(x < 0) continue;
//	          if(x >= Constants.BOARD_WIDTH) continue;
//	          int y=j;
//	          if(y < 0) continue;
//	          if(y >= Constants.BOARD_HEIGHT) continue;
//	          Tile surroundingTile = gameState.board.getTile(x, y);
//	          if(surroundingTile == targetTile)
//	              continue;
//	          if (surroundingTile != null) {
//	              if(surroundingTile.getUnit() == null && surroundingTile.getAiUnit()==null) {
//	                  //surroundingTile.setTileState(TileState.Reachable)
//	            	  if(TileState.Reachable==surroundingTile.getTileState())
//	            	  {
//	            		  gameState.chooseTile=surroundingTile;
//	            		  gameState.isAttacking=true;
//	            		  gameStateMachine.setState(new UnitMovingState(unit, startTile, surroundingTile));
//	            		  if(gameState.moved)
//	            		  {
//	            			  return;
//	            		  }
//	            	  }
//	                 // BasicCommands.drawTile(out, surroundingTile, 1);
//	              }
//	              else {
//	                  surroundingTile.setTileState(TileState.Occupied);
//	                 // BasicCommands.drawTile(out, surroundingTile, 2);
//	              }
//	          }
//	       }
//	     }
//       }
//      
//    }
//    /**
//     * Search for the reachable tiles on the board
//     * @param out
//     * @param gameState
//     * @param startTile
//     */
//    
//    private boolean searchUnitReachableTile(ActorRef out,GameState gameState,Tile startTile,Tile targetTile)
//    {
//    	int startX=startTile.getTilex();
//    	int startY=startTile.getTiley();
//        for(int i = -1; i <=1; i++ ) {
//            for(int j = -1; j <= 1; j++) {
//               int x = startX + i;
//               if(x < 0) continue;
//               if(x >= Constants.BOARD_WIDTH) continue;
//               int y = startY + j;
//               if(y < 0) continue;
//               if(y >= Constants.BOARD_HEIGHT) continue;
//               Tile surroundingTile = gameState.board.getTile(x, y);
//               if(surroundingTile == startTile)
//                   continue;
//               if (surroundingTile != null) {
//                   if(surroundingTile.getUnit() == null && surroundingTile.getAiUnit()==null) {
//                       surroundingTile.setTileState(TileState.Reachable);
//                      // BasicCommands.drawTile(out, surroundingTile, 1);
//                   }
//                   else {
//                	   //if the target tile within the range of the surroundings, attack
//                	   if(surroundingTile==targetTile)
//                	   {
//                		   try {
//							getUnitOnTileAttack(out, gameState);
//						} catch (InterruptedException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}	
//                		   return true;
//					   }
//                	   surroundingTile.setTileState(TileState.Occupied);
//                	 }
//                      
//                      // BasicCommands.drawTile(out, surroundingTile, 2);
//                   }
//               }
//            }
//         return false;
//    }
//    
    
    /**
     * User unit Attack the Ai unit
     */

    private void unitAttack(ActorRef out, Unit enemyUnit, int health, GameState gameState)
    {
		BasicCommands.setUnitHealth(out, enemyUnit, health);
    	if(health == 0)

		{
			setUnitHealth(out, enemyUnit, 0);
			BasicCommands.playUnitAnimation(out, enemyUnit, UnitAnimationType.death);
			try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
			BasicCommands.deleteUnit(out, enemyUnit);

			gameState.board.getTile(enemyUnit.getPosition().getTilex(), enemyUnit.getPosition().getTiley()).clearUnit();
			nextState = nextState.getNextState();
		}

    }
    
    private void setUnitHealth(ActorRef out, Unit unit,int health)
    {
    	BasicCommands.setUnitHealth(out,unit,health );
		try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
    }
}
