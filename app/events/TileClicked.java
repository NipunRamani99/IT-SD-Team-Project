<<<<<<< HEAD
package events;


import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import structures.GameState;

/**
 * Indicates that the user has clicked an object on the game canvas, in this case a tile.
 * The event returns the x (horizontal) and y (vertical) indices of the tile that was
 * clicked. Tile indices start at 1.
 * 
 * { 
 *   messageType = “tileClicked”
 *   tilex = <x index of the tile>
 *   tiley = <y index of the tile>
 * }
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class TileClicked implements EventProcessor{

	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {

		int tilex = message.get("tilex").asInt();
		int tiley = message.get("tiley").asInt();
		
		if (gameState.something == true) {
			// do some logic
		}
		
	}

}
=======
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
		
		BasicCommands.addPlayer1Notification(out, "The tilex "+tilex, 1);
		//int xpos = message.get("xpos").asInt();
		//int ypos = message.get("ypos").asInt();
		tile=gameState.board.getTile(tilex,tiley);
		
		if(true==gameState.cardIsClicked)
		{
			gameState.castCard.processEvent(out, gameState, message);
		}
		
		//this.position= new Position(tilex,tiley);
		
//		if(tile==null)
//		BasicCommands.addPlayer1Notification(out, " get position "+ "tile empty", 5);
//		else
//		BasicCommands.addPlayer1Notification(out, " get position "+ "has tile", 5);
//		if(gameState.cardIsClicked)
//		{
////			CastCard castcard = new CastCard(gameState); 
////			castcard.processEvent(out, gameState, message);
//		}
		
	     
		//Check if Unit is clicked
//		if(gameState.cardClicked == true) {
//			//Perform the action corresponding to the card
//			gameState.cardClicked = false;
//		}
//		if (gameState.unitClicked == true) {
//			//If tile is empty move the unit
//			//if tile is not empty perform attack action	
//			gameState.unitClicked  = false;
//		}
		
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
}
>>>>>>> JunGao
