package structures;


import java.util.ArrayList;

import ai.AIPlayer;
import ai.ActionType;
import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.basic.Position;
import structures.statemachine.CastCard;
import structures.basic.*;
import java.util.ArrayList;
import java.util.List;

import events.CardClicked;
import events.TileClicked;
import utils.Constants;

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
	public boolean unitIsClicked = false;
	public CastCard castCard=null;
	public Board board = null;
	public Tile firstClickedTile=null;
	public Tile secondClickedTile=null;
	public CardClicked cardClick=null;
	public boolean something = false;
	public ArrayList<Tile> tiles;
	public Position position;
	public Unit unit=null;
	public boolean isMove = false;
	public boolean endTurn =false;
	
	public int id=0;
	//public ActionType type;
	
	public AIPlayer ai;
	
	public Player playerAi;
	public Player user;

	public int handPosition = -1;
	public Card card = null;

	public void resetBoardSelection(ActorRef out) {
		for(int i = 0; i < Constants.BOARD_WIDTH; i++ ) {
			for(int j = 0; j < Constants.BOARD_HEIGHT; j++) {
				Tile tile = board.getTile(i, j);
				BasicCommands.drawTile(out, tile, 0);
				try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
				tile.setTileState(TileState.None);
			}
		}
	}

	public void resetCardSelection(ActorRef out) {
		List<Card> cards = board.getCards();
		for(int i = 0; i < cards.size(); i++) {
			BasicCommands.drawCard(out, cards.get(i),i+1,0);
		}
	}
}
