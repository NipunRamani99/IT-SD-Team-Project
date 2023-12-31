package structures;


import java.util.ArrayList;

import ai.AIPlayer;
import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.basic.Position;
import structures.basic.Unit;
import structures.statemachine.CastCard;
import structures.basic.*;

import java.util.List;
import events.CardClicked;
import utils.Constants;

/**
 * This class can be used to hold information about the on-going game.
 * Its created with the GameActor.
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class GameState {
	public UnitAbilityTable unitAbilityTable = new UnitAbilityTable();
	public boolean gameInitalised = false;
	public boolean cardIsClicked =false;
	public boolean unitIsClicked = false;
	public CastCard castCard=null;
	public Board board = null;
	public Tile tile=null;
	public Tile firstClickedTile=null;
	public Tile secondClickedTile=null;
	public CardClicked cardClick=null;
	public boolean something = false;
	public ArrayList<Tile> tiles;
	public Position position;
	public Unit unit=null;
	public boolean isMove = false;
	public boolean endTurn =false;
	
	public boolean isAttacking=false;
	
	public boolean moved=false;
	
	public Tile chooseTile=null;
	
	public Tile targetTile=null;
	
	public boolean AiMarkEnemy =false;
	
	public int numPosition=3;
	public int aiNumPosition=3;
	
	public int id=0;
	//public ActionType type;
	//The mana for human player
	public int humanMana=2;
	//The mana for Ai player
	public int AiMana=2;
	
	public boolean humanRunOut=false;
	public boolean AiRunout=false;
	
	public AIPlayer ai;
	
	public Player AiPlayer;
	public Player humanPlayer;

	public int handPosition = -1;
	public Card card = null;
	public Unit aiUnit = null;
	public Turn currentTurn = Turn.PLAYER;

	public void resetBoardSelection(ActorRef out) {
		for(int i = 0; i < Constants.BOARD_WIDTH; i++ ) {
			for(int j = 0; j < Constants.BOARD_HEIGHT; j++) {
				Tile tile = board.getTile(i, j);
				BasicCommands.drawTile(out, tile, 0);
				try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}	
				if(tile.getTileState() != TileState.Occupied) {
					tile.setTileState(TileState.None);
				}
			}
		}
	}

	public void resetBoardState() {
		for(int i = 0; i < Constants.BOARD_WIDTH; i++ ) {
			for(int j = 0; j < Constants.BOARD_HEIGHT; j++) {
				Tile tile = board.getTile(i, j);
				tile.setTileState(TileState.None);
			}
		}
	}

	public void resetAiCardSelection(ActorRef out)
	{
		List<Card> cards = board.getAiCards();
		for(int i = 0; i < cards.size(); i++) {
			BasicCommands.drawCard(out, cards.get(i),i+1,0);
		}
	}
	
	public void resetCardSelection(ActorRef out) {
		List<Card> cards = board.getCards();
		for(int i = 0; i < cards.size(); i++) {
			BasicCommands.drawCard(out, cards.get(i),i+1,0);
		}
	}
}
