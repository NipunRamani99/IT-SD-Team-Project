package events;

import java.util.Collections;
import java.util.List;
import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import structures.GameState;
import structures.basic.*;

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
	private Position position;
	
	//Get the position 
	public Position getPosition()
	{
	    return position;	
	}
	
	public List<Tile> getReachableTiles(Unit unit, Position position, GameState gameState) {
		return Collections.emptyList();
		//
	}
	
	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {

		int tilex = message.get("tilex").asInt();
		int tiley = message.get("tiley").asInt();
		int xpos = message.get("xpos").asInt();
		int ypos = message.get("ypos").asInt();
		
		//Initialize the position when click the tile.
		position= new Position(tilex,tiley, xpos, ypos);
	     
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


}
