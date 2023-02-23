package events;

import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Tile;
import structures.basic.TileState;
import structures.basic.Unit;
import utils.Constants;

/**
 * Indicates that the user has clicked an object on the game canvas, in this case
 * somewhere that is not on a card tile or the end-turn button.
 * 
 * { 
 *   messageType = "otherClicked"
 * }
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class OtherClicked implements EventProcessor{

	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {
		
		//If click the other place, all the state will be reset
		gameState.cardClick=null;
		gameState.cardIsClicked=false;
		gameState.firstClickedTile=null;
		gameState.secondClickedTile=null;
		resetCardSelection(out, gameState);
		resetBoard(out, gameState);
	}
	
	private void resetCardSelection(ActorRef out, GameState gameState) {
		BasicCommands.drawCard(out, gameState.card, gameState.handPosition, 0);
		gameState.card = null;
		gameState.handPosition = -1;
		gameState.cardIsClicked = false;
		//try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
	}
	
	private void resetBoard(ActorRef out, GameState gameState)
	{
		List<Unit> unitList = gameState.board.getUnits();
		for(Unit unit : unitList) {
			for (int i = -1; i <= 1; i++) {
				for (int j = -1; j <= 1; j++) {
					if(i == 0 && j == 0) continue;;
					int x = unit.getPosition().getTilex() + i;
					int y = unit.getPosition().getTiley() + j;
					Tile surroundingTile = gameState.board.getTile(x, y);
					if (surroundingTile != null) {
						if (surroundingTile.getUnit() == null) {
							surroundingTile.setTileState(TileState.Reachable);
							BasicCommands.drawTile(out, surroundingTile, 0);
							try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
						}
					}
				}
			}
		}
	}

}


