package events;

import java.util.Collections;
import java.util.List;
import com.fasterxml.jackson.databind.JsonNode;

import ai.Action;
import ai.ActionType;
import akka.actor.ActorRef;
import commands.BasicCommands;
import org.checkerframework.checker.signedness.qual.Constant;
import structures.GameState;
import structures.basic.*;
import structures.basic.Tile.Occupied;
import events.CastCard;
import utils.Constants;


/**
 * Indicates that the user has clicked an object on the game canvas, in this case a tile.
 * The event returns the x (horizontal) and y (vertical) indices of the tile that was
 * clicked. Tile indices start at 1.
 * 
 * { 
 *   messageType = "tileClicked"
 *   tilex = x index of the tile
 *   tiley = y index of the tile
 * }
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class TileClicked implements EventProcessor{	
	/**
	 * This method will search the neighboring tiles of a unit and returns a list of tile which are reachable by it.
	 * @param unit
	 * @param position
	 * @param gameState
	 * @return List of tiles that are reachable.
	 */
	
	//The position get when click the tile
	private Position position= new Position(2,2);
	
	//The tile that clicked;
	private Tile tile;
	
	//The x position of the clicked tile
	private int tilex;
	
	//The y position of the clicked tile
	private int tiley;

	//Get the position 
	public Position getPosition()
	{
	    return this.position;	
	}
	
	public List<Tile> getReachableTiles(Unit unit, Position position, GameState gameState) {
		return Collections.emptyList();
		//
	}
	
//	public TileClicked()
//	{
//		//Initialize the position when click the tile.
//		//position= new Position(tilex,tiley);
//	}
	
	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {

		if(!gameState.isMove)
		{
			tilex = message.get("tilex").asInt();
			tiley = message.get("tiley").asInt();
			
//			int xpos = message.get("xpos").asInt();
//			int ypos = message.get("ypos").asInt();
			tile=gameState.board.getTile(tilex,tiley);
			
			hightTiles(out, gameState,message);
			if(null==gameState.firstClickedTile&&null==gameState.secondClickedTile)
			{
				gameState.firstClickedTile=tile;
			}
			else if(null!=gameState.firstClickedTile&&null==gameState.secondClickedTile)
			{
				gameState.secondClickedTile=tile;
			}
			else
			{
				gameState.firstClickedTile=null;
				gameState.secondClickedTile=null;
			}
				
			//If card is clicked, play the card on the board
			if(true==gameState.cardIsClicked&& Occupied.none==tile.isOccupied())
			{
				castCard(out, gameState, message);
			}

			//moving the unit
			
			unitMoving(out, gameState, message);
			
			//Attack the unit
			if(null!=gameState.firstClickedTile&& null!=gameState.secondClickedTile)
			{
				//improve to check if there is an ai unit on the second tile
//				Unit aiUnit = gameState.secondClickedTile.getAiUnit();
//				if(gameState.playerAi.getHealth()>0)
//				{
//					BasicCommands.addPlayer1Notification(out, "Ai attack back ",1);
//    				//user lunch an attack
//					try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
//        			BasicCommands.playUnitAnimation(out,aiUnit,UnitAnimationType.attack);
//        			try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
//				}
				unitAttack(out, gameState, message);
			}
			
				
		}		

	}

	private void resetBoardSelection(ActorRef out, GameState gameState) {
		for(int i = 0; i < Constants.BOARD_WIDTH; i++ ) {
			for(int j = 0; j < Constants.BOARD_HEIGHT; j++) {
				Tile tile = gameState.board.getTile(i, j);
				BasicCommands.drawTile(out, tile, 0);
				tile.setTileState(TileState.None);
			}
		}
	}
	
	private void resetCardSelection(ActorRef out, GameState gameState) {
		BasicCommands.drawCard(out, gameState.card, gameState.handPosition, 0);
		gameState.card = null;
		gameState.handPosition = -1;
		gameState.cardIsClicked = false;
	}
	
	//Get the tile
	public Tile getClickedTile()
	{
		return this.tile;
	}
	
	public int getTileX()
	{
		return this.tilex;
	}
	
	public int getTileY()
	{
		return this.tiley;
	}
	
	private void hightTiles(ActorRef out, GameState gameState, JsonNode message)
	{
		if(tile.getTileState() == TileState.None) {
			gameState.unitIsClicked = false;
			gameState.cardIsClicked = false;
			resetBoardSelection(out, gameState);
			BasicCommands.drawCard(out, gameState.card, gameState.handPosition, 0);
			gameState.card = null;
			gameState.handPosition = -1;
			gameState.cardIsClicked = false;
		}
		if(gameState.cardIsClicked && tile.getTileState() == TileState.Reachable)
		{
			if(tile.getUnit() == null)
				gameState.castCard.processEvent(out, gameState, message);
		}
		if(tile.getUnit() != null && !gameState.unitIsClicked) {
			for(int i = -1; i <=1; i++ ) {
				for(int j = -1; j <= 1; j++) {
					int x = tilex + i;
					int y = tiley + j;
					Tile surroundingTile = gameState.board.getTile(x, y);
					if(surroundingTile == tile)
						continue;
					if (surroundingTile != null) {
						if(surroundingTile.getUnit() == null) {
							surroundingTile.setTileState(TileState.Reachable);
							BasicCommands.drawTile(out, surroundingTile, 1);
						}
						else {
							surroundingTile.setTileState(TileState.Occupied);
							BasicCommands.drawTile(out, surroundingTile, 2);
						}
					}
				}
			}
			gameState.unitIsClicked = true;
		}
		if(tile.getUnit() == null && gameState.unitIsClicked) {
			for(int i = 0; i < Constants.BOARD_WIDTH; i++ ) {
				for(int j = 0; j < Constants.BOARD_HEIGHT; j++) {
					Tile surroundingTile = gameState.board.getTile(i, j);
					BasicCommands.drawTile(out, surroundingTile, 0);
				}
			}
			gameState.unitIsClicked = false;
		}

	}
	
	/**
	 * Move the unit
	 * @param out
	 * @param gameState
	 * @param message
	 */
	private void unitMoving(ActorRef out,GameState gameState,JsonNode message)
	{
		// If the first tile has the unit without click the second tile
		//get the unit from the first tile
		if(null!=gameState.firstClickedTile&&null==gameState.secondClickedTile)
		{
			//For the user
			if(Occupied.userOccupied==gameState.firstClickedTile.isOccupied())
			{
				//get the unit from the tile
				gameState.unit = gameState.firstClickedTile.getUnit();
				gameState.unit.setChosed(true);
				BasicCommands.addPlayer1Notification(out, "get the unit ",1);
				//gameState.isMove=false;
			}
			//For the ai
			if(Occupied.aiOccupied==gameState.firstClickedTile.isOccupied())
			{
				//get the unit from the tile
				gameState.unit = gameState.firstClickedTile.getAiUnit();
				gameState.unit.setChosed(true);
				BasicCommands.addPlayer1Notification(out, "Ai get the unit ",1);
				//gameState.isMove=false;
			}
			
		}
		//If the first tile has the unit and second tile is clicked, move the unit
	   if(null!=gameState.firstClickedTile&&null!=gameState.secondClickedTile)
		{
			if(Occupied.userOccupied==gameState.firstClickedTile.isOccupied()&&
				Occupied.none==gameState.secondClickedTile.isOccupied())
			{
				UnitMoving move = new UnitMoving();
				move.processEvent(out, gameState, message);
				//Create the thread for the event
				Thread m= new Thread(move);
				m.start();
				//only execute the movement
				try {
					m.join();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}
	     
	}
	/**
	 * Cast the card on the tile
	 * @param out
	 * @param gameState
	 * @param message
	 */
	private void castCard(ActorRef out,GameState gameState,JsonNode message)
	{
		//Create a thread for cast event 
		Thread cast = new Thread(gameState.castCard);
		gameState.castCard.processEvent(out, gameState, message);
		cast.start();
		//Only playing the card
		try {
			cast.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Attack the Ai unit
	 * @param out
	 * @param gameState
	 * @param message
	 */
	private synchronized void unitAttack(ActorRef out,GameState gameState,JsonNode message)
	{
		//Create unit attack action 
		Action attack = new Action(ActionType.UnitAttack, out, gameState);
		//create a thread
		Thread userAttack = new Thread(attack);
		userAttack.start();	
		try {
			userAttack.join();
			try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
		} catch (InterruptedException e) {
			//Print the exception message
			e.printStackTrace();
		}
		
		//if the AI unit is not dead, ai attack back
		
		//Create ai unit attack action 
		Action aiUnitAttack = new Action(ActionType.AIAttack, out, gameState);
		//create a thread
		Thread aiAttack = new Thread(aiUnitAttack);
		aiAttack.start();
		
//		try {
//			aiAttack.join();
//			try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
//		} catch (InterruptedException e) {
//			//Print the exception message
//			e.printStackTrace();
//		}
		
	}
}
