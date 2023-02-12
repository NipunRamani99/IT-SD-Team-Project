package structures;
import structures.basic.Player;
import structures.basic.Tile;
import structures.basic.Board;

import java.util.ArrayList;

import structures.basic.Position;
import events.CardClicked;
import events.CastCard;
import events.TileClicked;
/**
 * This class can be used to hold information about the on-going game.
 * Its created with the GameActor.
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class GameState {

	
	public boolean gameInitalised = false;
	public boolean cardIsClicked =false; 
	public CastCard castCard=null;
	public Board board = null;
	public static Tile clickedTile=null;
	public CardClicked cardClick=null;
	public boolean something = false;
	public ArrayList<Tile> tiles;
	public Position position;
	
	Player playerAi;
	Player user;

}
