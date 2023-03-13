package structures.statemachine;

import akka.actor.ActorRef;
import com.fasterxml.jackson.databind.JsonNode;
import commands.AbilityCommands;
import commands.BasicCommands;
import events.*;
import structures.GameState;
import structures.Turn;
import structures.basic.Tile;
import structures.basic.Unit;
import structures.basic.UnitAnimationType;

/**
 * UnitMovingState will initiate the move animation and update the unit position when the UnitStopped event is received
 */
public class UnitMovingState extends State {
    Tile targetTile = null;
    Tile startTile = null;
    Unit selectedUnit = null;

	/**
	 * UnitMovingState constructor
	 * @param selectedUnit
	 * @param startTile
	 * @param targetTile
	 */
	public UnitMovingState(Unit selectedUnit, Tile startTile, Tile targetTile) {
		this.selectedUnit = selectedUnit;
		this.startTile = startTile;
		this.targetTile = targetTile;
	}

	/**
	 * Initiate move checks what type of unit is being moved
	 * @param out
	 * @param gameState
	 */
    private void initiateMove(ActorRef out, GameState gameState ) {
     
        //Check the startTile has surrounding occupied tiles or not
        System.out.println("Start to move");
        if(!selectedUnit.isAi()&&gameState.currentTurn == Turn.PLAYER)
        {
        	unitMove(out, gameState);	
        }
        else if(selectedUnit.isAi())
        {
        	if(null==targetTile.getUnit())
        	{
        		aiUnitMove(out, gameState);
        	}
        }
    }

	/**
	 * unitMove sends the necessary commands to move the unit on the board
	 * @param out
	 * @param gameState
	 */
	private void unitMove(ActorRef out,GameState gameState)
	{
		//Get the target tile x, y position
		int targetX = this.targetTile.getTilex();
		int targetY = this.targetTile.getTiley();
		//Get the start tile x, y position
		int startX = startTile.getTilex();
		int startY = startTile.getTiley();

		if(1==Math.abs(targetX-startX)&&1==Math.abs(targetY-startY)&&null==targetTile.getUnit())
		{
			Unit unit1=gameState.board.getTile(startX,targetY).getUnit();
			Unit unit2=gameState.board.getTile(targetX,startY).getUnit();
			if((unit1==null)||(!unit1.isAi()))
			{
				startTile.clearUnit();
				BasicCommands.moveUnitToTile(out, selectedUnit, targetTile,true,gameState);
				playMoveAnimation(out,selectedUnit,targetTile);
			}
			else if((unit2==null)||(!unit2.isAi()))
			{
				//Move vertically first
				startTile.clearUnit();;
				BasicCommands.moveUnitToTile(out, selectedUnit, targetTile,true,gameState);
				playMoveAnimation(out,selectedUnit,targetTile);
			}
			else if(null==gameState.board.getTile(startX,targetY).getUnit()
					&&null==gameState.board.getTile(targetX, startY).getUnit())
			{
				startTile.clearUnit();
				BasicCommands.moveUnitToTile(out, selectedUnit, targetTile,false,gameState);
				playMoveAnimation(out,selectedUnit,targetTile);
			}
			else
			{
				//do not move
				BasicCommands.moveUnitToTile(out, selectedUnit, startTile, gameState);
				System.out.println("Do not move");
			}
		}
		else if (1==Math.abs(targetX-startX)&&2==Math.abs(targetY-startY)&&null==targetTile.getUnit())
		{
			if(targetY-startY<0&&!selectedUnit.isHasFlying())
			{
				if(gameState.board.getTile(targetX, targetY+1).getUnit()!=null&&gameState.board.getTile(targetX, targetY+1).getUnit().isAi())
				{
					//do not move
					startTile.clearUnit();
					BasicCommands.moveUnitToTile(out, selectedUnit, startTile, gameState);
				}
			}else if(targetY-startY>0&&!selectedUnit.isHasFlying())
			{
				if(gameState.board.getTile(targetX, targetY-1).getUnit()!=null&&gameState.board.getTile(targetX, targetY-1).getUnit().isAi())
				{
					//do not move
					startTile.clearUnit();
					BasicCommands.moveUnitToTile(out, selectedUnit, startTile, gameState);
				}
			}
			else
			{
				BasicCommands.moveUnitToTile(out, selectedUnit, targetTile, gameState);
				playMoveAnimation(out,selectedUnit,targetTile);
			}
		}
		else if (2==Math.abs(targetX-startX)&&1==Math.abs(targetY-startY)&&null==targetTile.getUnit())
		{
			if(targetX-startX<0&&!selectedUnit.isHasFlying())
			{
				if(gameState.board.getTile(targetX+1, targetY).getUnit()!=null&&gameState.board.getTile(targetX+1, targetY).getUnit().isAi())
				{
					//do not move
					startTile.clearUnit();
					BasicCommands.moveUnitToTile(out, selectedUnit, startTile, gameState);
				}
			}else if(targetX-startX>0&&!selectedUnit.isHasFlying())
			{
				if(gameState.board.getTile(targetX-1, targetY).getUnit()!=null&&gameState.board.getTile(targetX-1, targetY).getUnit().isAi())
				{
					//do not move
					startTile.clearUnit();
					BasicCommands.moveUnitToTile(out, selectedUnit, startTile, gameState);
				}
			}
			else
			{
				BasicCommands.moveUnitToTile(out, selectedUnit, targetTile, gameState);
				playMoveAnimation(out,selectedUnit,targetTile);
			}
		}
		else
		{
			BasicCommands.moveUnitToTile(out, selectedUnit, targetTile, gameState);
			playMoveAnimation(out,selectedUnit,targetTile);

		}
	}

	/**
	 *
	 * @param out
	 * @param gameState
	 */
	private void aiUnitMove(ActorRef out,GameState gameState)
	{
		//Get the target tile x, y position
		int targetX = this.targetTile.getTilex();
		int targetY = this.targetTile.getTiley();
		//Get the start tile x, y position
		int startX = startTile.getTilex();
		int startY = startTile.getTiley();
		if(1==Math.abs(targetX-startX)&&1==Math.abs(targetY-startY))
		{
			Unit unit1=gameState.board.getTile(startX,targetY).getUnit();
			Unit unit2=gameState.board.getTile(targetX,startY).getUnit();
			if((unit1==null)||(unit1.isAi()))
			{
				//Move vertically first
				startTile.clearUnit();
				BasicCommands.moveUnitToTile(out, selectedUnit, targetTile,true,gameState);
				playMoveAnimation(out,selectedUnit,targetTile);
			}
			else if(unit2==null || unit2.isAi())
			{
				//Move horizontally first
				startTile.clearUnit();
				BasicCommands.moveUnitToTile(out, selectedUnit, targetTile,false,gameState);
				playMoveAnimation(out,selectedUnit,targetTile);
			}
			else
			{
				BasicCommands.moveUnitToTile(out, selectedUnit, targetTile, gameState);
				playMoveAnimation(out,selectedUnit,targetTile);
			}
		}
		else if (1==Math.abs(targetX-startX)&&2==Math.abs(targetY-startY)&&null==targetTile.getUnit())
		{
			if(targetY-startY<0&&!selectedUnit.isHasFlying())
			{
				if(gameState.board.getTile(targetX, targetY+1).getUnit()!=null&&!gameState.board.getTile(targetX, targetY+1).getUnit().isAi())
				{
					//do not move
					startTile.clearUnit();
					BasicCommands.moveUnitToTile(out, selectedUnit, startTile, gameState);
					selectedUnit.setMovement(false);
				}
			}else if(targetY-startY>0&&!selectedUnit.isHasFlying())
			{
				if(gameState.board.getTile(targetX, targetY-1).getUnit()!=null&&!gameState.board.getTile(targetX, targetY-1).getUnit().isAi())
				{
					//do not move
					startTile.clearUnit();
					BasicCommands.moveUnitToTile(out, selectedUnit, startTile, gameState);
					selectedUnit.setMovement(false);
				}
			}
			else
			{
				BasicCommands.moveUnitToTile(out, selectedUnit, targetTile, gameState);
				playMoveAnimation(out,selectedUnit,targetTile);
			}
		}
		else if (2==Math.abs(targetX-startX)&&1==Math.abs(targetY-startY)&&null==targetTile.getUnit())
		{
			if(targetX-startX<0)
			{
				if(gameState.board.getTile(targetX+1, targetY).getUnit()!=null&&!gameState.board.getTile(targetX+1, targetY).getUnit().isAi())
				{
					//do not move
					startTile.clearUnit();
					BasicCommands.moveUnitToTile(out, selectedUnit, startTile, gameState);
					selectedUnit.setMovement(false);
				}
			}else if(targetX-startX>0)
			{
				if(gameState.board.getTile(targetX-1, targetY).getUnit()!=null&&!gameState.board.getTile(targetX-1, targetY).getUnit().isAi())
				{
					//do not move
					startTile.clearUnit();
					BasicCommands.moveUnitToTile(out, selectedUnit, startTile, gameState);
					selectedUnit.setMovement(false);
				}
			}
			else
			{
				BasicCommands.moveUnitToTile(out, selectedUnit, targetTile, gameState);
				playMoveAnimation(out,selectedUnit,targetTile);
			}
		}
		else
		{

			BasicCommands.moveUnitToTile(out, selectedUnit, targetTile,gameState);
			playMoveAnimation(out,selectedUnit,targetTile);
		}
	}



	/**
	 *
	 * @param out
	 * @param gameState
	 * @param message
	 * @param event
	 * @param gameStateMachine
	 */
	@Override
    public void handleInput(ActorRef out, GameState gameState, JsonNode message, EventProcessor event, GameStateMachine gameStateMachine) {
            if(event instanceof UnitStopped) {
            	//Check the unit is moved or not
				targetTile.setUnit(selectedUnit);
                selectedUnit.setPositionByTile(targetTile);
				System.out.println("Exiting UnitMovingState");
                if(gameState.currentTurn==Turn.PLAYER)
                	gameStateMachine.setState( nextState != null? nextState : new NoSelectionState(), out, gameState);
                else
                	gameStateMachine.setState( nextState != null? nextState : new EndTurnState(), out, gameState);
            } else if(event instanceof Heartbeat) {
				System.out.println("Heartbeat: UnitMovingState");
			}
             else if(event instanceof CardClicked && gameState.currentTurn==Turn.PLAYER) {
                gameStateMachine.setState(new NoSelectionState(), out, gameState);
            }
             else if(event instanceof EndTurnClicked&& gameState.currentTurn==Turn.PLAYER) {
                 gameStateMachine.setState(new NoSelectionState(), out, gameState);
             }
             else if(event instanceof TileClicked&& gameState.currentTurn==Turn.PLAYER) {
                 gameStateMachine.setState(new NoSelectionState(), out, gameState);
             }
    }


	/**
	 *
	 * @param out
	 * @param gameState
	 */
	@Override
	public void enter(ActorRef out, GameState gameState) {
		System.out.println("Entering UnitMovingState");
		AbilityCommands.checkIsProvoked(selectedUnit, gameState);
        if(!selectedUnit.isIsProvoked()) {
        	if(selectedUnit.getMovement())
    			initiateMove(out,gameState);
        }
        else 
        {
        	exit(out, gameState);
        }
	}

	/**
	 *
	 * @param out
	 * @param gameState
	 */
	public void exit(ActorRef out, GameState gameState) {
			System.out.println("Can not move");
			BasicCommands.moveUnitToTile(out, selectedUnit, startTile,gameState);
        	selectedUnit.setPositionByTile(startTile);
            BasicCommands.playUnitAnimation(out, selectedUnit, UnitAnimationType.idle);
            selectedUnit.setMovement(false);

	}

	/**
	 * Unit move animation
	 * @param out
	 * @param unit
	 */
	private void playMoveAnimation(ActorRef out, Unit unit, Tile tile)
	{
		//Play move animation
		try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}
		BasicCommands.playUnitAnimation(out, this.selectedUnit, UnitAnimationType.move);
		try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
		BasicCommands.playUnitAnimation(out, this.selectedUnit, UnitAnimationType.idle);
		unit.setPositionByTile(tile);
		unit.setMovement(false);
	}
}
