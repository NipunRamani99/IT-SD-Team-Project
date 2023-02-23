package ai;


import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Board;
import structures.basic.Player;
import structures.basic.Tile;
import structures.basic.Tile.Occupied;
import structures.basic.Unit;
import structures.basic.UnitAnimationType;
import utils.BasicObjectBuilders;
import utils.StaticConfFiles;
/**
 * Action class is used to implement the different actions an AI can perform while playing the game.
 * Each action is defined by an action type, instances of tiles on the board,
 * and a hand position if the action involves casting a card from the AI's hand.
 */
public class Action implements Runnable{
    /**
     * Define the action type
     */
    private ActionType actionType = ActionType.None;
    /**
     * The tile refer to the first clicked tile or friend unit tile
     */
    
    /**
     * The board
     */
    private Board board;
    
    /**
     * The AI player
     */
    private Player playerAI;
    
    
    private Tile startTile;
    /**
     * The tile refer to the destination tile or enemy unit tile
     */
    private Tile endTile;
    /**
     * To specify which card to use
     */
    private int handPosition = 0; //To specify which card to use
    
    /**
     *  The actor Reference
     */
    private ActorRef out;
    
    /**
     * Game state
     */
    private GameState gameState;

    /**
     * Constructor for actions like end turn.
     * @param actionType
     */
    public Action(ActionType actionType,ActorRef out, GameState gameState) {
        this.actionType = actionType;
      	this.out=out;
    	this.gameState=gameState;
    	
    	//initialize the board, player and action type
    	this.board=gameState.board;
    	this.playerAI=gameState.playerAi;
    }

    /**
     * Constructor for double tile actions like unit move and unit attack.
     * @param actionType the action type like move or attack
     * @param startTile  the first tile needed to click, like the friend unit tile
     * @param endTile the destination tile needed to click, like the enemy unit tile or the moving destination tile
     */
    public Action(ActionType actionType, Tile startTile, Tile endTile) {
        this.actionType = actionType;
        this.startTile = startTile;
        this.endTile = endTile;
    }

    /**
     * Constructor for single tile actions like cast unit card or cast spell card.
     * @param actionType  this refers to  what kind action need to be performed
     * @param startTile  this refers to which tile need to be cast
     * @param handPosition this refers to which card in hand needed to be cast
     */
    public Action(ActionType actionType, Tile startTile, int handPosition) {
        this.actionType = actionType;
        this.startTile = startTile;
        this.handPosition = handPosition;
    }

    /**
     * This is the moving function
     * @param start the start tile that click first
     * @param end  the destination tile that need to move to
     */
    public void MoveUnit(Tile start, Tile end) {
        //Unit unit = start.getUnit();
        //Call the command to move the unit
        //BasicCommand.moveUnitToTile(out, unit, endTile);
        //clear the start tile
        //start.setUnit(null);
        //Update the target tile with the unit
        //end.setUnit(unit);
    }
    
    /**
     * This is the action for the AI to initialize to draw the avatar
     */
    private void initialize()
    {
  
    	
    	//set the health and mama during the initialize
    	
    	//draw the avatar on the board
		
		Unit unit = BasicObjectBuilders.loadUnit(StaticConfFiles.aiAvatar, gameState.id++, Unit.class);	
		//Get related tiles
		Tile aiTile = board.getTile(8, 2);
		unit.setPositionByTile(aiTile); 
		//The tile set the unit
		aiTile.setAiUnit(unit);
		gameState.board.addUnit(unit);
		//draw unit
		BasicCommands.drawUnit(out, unit, aiTile);
		try { Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
		
		gameState.unit=unit;
		Tile aiTile1 = board.getTile(7, 3);
		BasicCommands.moveUnitToTile(out, unit, aiTile1);
		try {Thread.sleep(4000);} catch (InterruptedException e) {e.printStackTrace();}
		aiTile1.setAiUnit(unit);
		BasicCommands.playUnitAnimation(out,unit,UnitAnimationType.attack);
		//move unit to tile		
		try {Thread.sleep(3000);} catch (InterruptedException e) {e.printStackTrace();}
		
    }
    
    /**
     * User unit Attack the Ai unit
     */
    private synchronized void unitAttack()
    {
    	//Before the user unit attack, check if it is the user turn and if there is any unit is moving
    	if(!gameState.endTurn&&!gameState.isMove)
    	{
    		//Check if the second clicked tile is the ai unit
    		if(Occupied.aiOccupied==gameState.secondClickedTile.isOccupied()
    		&&Occupied.userOccupied==gameState.firstClickedTile.isOccupied())
    		{
    			//get the user unit and ai unit
    			Unit userUnit = gameState.firstClickedTile.getUnit();
    			Unit aiUnit = gameState.secondClickedTile.getAiUnit();
    			//Check both the units is not null
    			if(null!=userUnit&&null!=aiUnit)
    			{
    				//Choose this unit
    				userUnit.setChosed(true);
    				BasicCommands.addPlayer1Notification(out, "User attack AI ",1);
    				//user lunch an attack
    				if(userUnit.isChosed())
    					BasicCommands.playUnitAnimation(out,userUnit,UnitAnimationType.attack);
    				//function
    				try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
    				 userUnit.setChosed(false);
    			}
    		}
   
    	}
    }
    
    private synchronized void aiUnitAttack()
    {
  
		//Check if the second clicked tile is the ai unit
		if(Occupied.aiOccupied==gameState.secondClickedTile.isOccupied()
		&&Occupied.userOccupied==gameState.firstClickedTile.isOccupied())
		{
			//get the user unit and ai unit
			Unit userUnit = gameState.firstClickedTile.getUnit();
			Unit aiUnit = gameState.secondClickedTile.getAiUnit();
			//Check both the units is not null
			if(null!=userUnit&&null!=aiUnit)
			{
				if(gameState.endTurn&&!gameState.isMove)//ai turn
				{
	 				BasicCommands.addPlayer1Notification(out, "Ai attack user ",1);
	 				 try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
    				//ai lunch an attack
        			BasicCommands.playUnitAnimation(out,aiUnit,UnitAnimationType.attack);
        			try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
    			}
				else if(!gameState.endTurn&&!gameState.isMove)//user turn
				{
					if(gameState.playerAi.getHealth()>0)
					{
						//Choose this unit
						aiUnit.setChosed(true);
						BasicCommands.addPlayer1Notification(out, "Ai attack back ",1);
						 try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
	    				//ai lunch an attack
						if(aiUnit.isChosed())
						{
							BasicCommands.playUnitAnimation(out,aiUnit,UnitAnimationType.attack);
	        			    try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
						}
	        			aiUnit.setChosed(false);
					}
					else
					{
						// ai lose
					}
	 				
				}
				else
				{
					//do other things
				}
    				
    		}
    	}  	
    	
    }

	@Override
	public void run() {
		//Execute the action according to the AI action type
		switch(actionType)
		{
			case AIAttack:
				aiUnitAttack();
				break;
			case AIMove:
				break;
			case CastSpellCard:
				break;
			case CastUnitCard:
				break;
			case EndTurn:
				break;
			case Init:
				initialize();
				break;
			case None:
				break;
			case UnitAttack:
				unitAttack();
				break;
			case UnitMove:
				break;
			default:
				break; 
	
		}
			
	}
    
    
}
