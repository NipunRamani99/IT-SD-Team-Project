package events;

import java.util.Collections;
import java.util.List;
import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.*;
import events.CastCard;

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

		tilex = message.get("tilex").asInt();
		tiley = message.get("tiley").asInt();
		
		//int xpos = message.get("xpos").asInt();
		//int ypos = message.get("ypos").asInt();
		tile=gameState.board.getTile(tilex,tiley);
		
		//If card is clicked, play the card on the board
		if(true==gameState.cardIsClicked&& !tile.isOccupied())
		{
			gameState.castCard.processEvent(out, gameState, message);
		}
	
		// If the tile has the unit
		if(tile.isOccupied())
		{
			//get the unit from the tile
			gameState.unit = tile.getUnit();
			BasicCommands.addPlayer1Notification(out, "get the unit ",1);
		}
		//Execute the unit moving
		else if(!tile.isOccupied()&&null!=gameState.unit)
		{
//			//get the position and tile if this unit
//			Position position=gameState.unit.getPosition();
//			//According to the position to get the tile
//			Tile unitTile= gameState.board.getTile(position.getTilex(), position.getTiley());
			//moving the unit
			BasicCommands.addPlayer1Notification(out, "Moving ",1);
			BasicCommands.moveUnitToTile(out, gameState.unit, tile);
			gameState.unit.setPositionByTile(tile);
			tile.setUnit(gameState.unit);
			try {Thread.sleep(4000);} catch (InterruptedException e) {e.printStackTrace();}
			//clear the unit in the gameState
			gameState.unit=null;
			
		}
		else
		{
			gameState.unit=null;
		}
	     
		
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
	
	//Move the unit
	public void unitMoving()
	{
		
	}
}
