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
import structures.statemachine.CastCard;
import structures.statemachine.GameStateMachine;
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
	}

	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message, GameStateMachine gameStateMachine) {
		
	
		BasicCommands.addPlayer1Notification(out, "Tile Clicked ",1);
		int tilex = message.get("tilex").asInt();
		int tiley = message.get("tiley").asInt();
		gameState.position = new Position(tilex, tiley);
		gameStateMachine.processInput(out, gameState, message,this);

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
