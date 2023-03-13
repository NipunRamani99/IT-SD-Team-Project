import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.fasterxml.jackson.databind.node.ObjectNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import commands.CheckMessageIsNotNullOnTell;
import events.Initalize;
import events.TileClicked;
import play.libs.Json;
import structures.GameState;
import structures.Turn;
import structures.basic.Board;
import structures.basic.Player;
import structures.basic.Tile;
import structures.basic.TileState;
import structures.basic.Unit;
import structures.statemachine.GameStateMachine;
import structures.statemachine.NoSelectionState;
import structures.statemachine.UnitAttackState;
import structures.statemachine.UnitMovingState;
import structures.statemachine.UnitSelectedState;

//@Ignore
public class UnitActionsTest {

	@Ignore
	@Test
	public void checkUnitMove() {
		//initialization
		CheckMessageIsNotNullOnTell altTell = new CheckMessageIsNotNullOnTell();
		BasicCommands.altTell = altTell;
		GameState gameState = new GameState();
		GameStateMachine gameStateMachine = new GameStateMachine();
		Initalize initalizeProcessor = new Initalize();
		ObjectNode eventMessage = Json.newObject();
		initalizeProcessor.processEvent(null, gameState, eventMessage, gameStateMachine);
		
		//check the move
		
		Player player = gameState.humanPlayer;
		Unit unit = gameState.unit;
		unit.setAvatar(true);
		Board board = gameState.board;
		Tile startTile = gameState.board.getTile(5, 2);
		startTile.setUnit(unit);
		Tile targetTile = gameState.board.getTile(4, 1);
		
		UnitMovingState move = new UnitMovingState(unit,startTile,targetTile);
		//<NEED> private -->public
		//move.unitMove(null,gameState);
		
		//assertFalse(startTile.isOccupied());
		//assertTrue(targetTile.isOccupied());

	}

	
	
	
	@Ignore
	@Test
	public void checkUnitAttack() {
		//initialization
		CheckMessageIsNotNullOnTell altTell = new CheckMessageIsNotNullOnTell();
		BasicCommands.altTell = altTell;
		GameState gameState = new GameState();
		GameStateMachine gameStateMachine = new GameStateMachine();
		Initalize initalizeProcessor = new Initalize();
		ObjectNode eventMessage = Json.newObject();
		initalizeProcessor.processEvent(null, gameState, eventMessage, gameStateMachine);
		
		//check attack
		Board board = gameState.board;
		Tile tile1 = board.getTile(3, 3);
		Tile tile2 = board.getTile(3, 4);
		Unit hUnit = gameState.unit;
		Unit aUnit = gameState.aiUnit;
		
		tile1.setUnit(hUnit);
		tile2.setUnit(aUnit);
		
		assertFalse(aUnit.getHealth() == 18);
		
		UnitAttackState attack = new UnitAttackState(hUnit, tile2, true);
		// private-->public
		//attack.unitAttack(null, aUnit, aUnit.getHealth(), gameState);
		
		//set: aUnit attacks hUint
		
		assertTrue(aUnit.getHealth() == 18);
		
	}
	
	
	
	
	
	
	@Ignore
	@Test
	public void checkUnitDie() {
		//initialization
		CheckMessageIsNotNullOnTell altTell = new CheckMessageIsNotNullOnTell();
		BasicCommands.altTell = altTell;
		GameState gameState = new GameState();
		GameStateMachine gameStateMachine = new GameStateMachine();
		Initalize initalizeProcessor = new Initalize();
		ObjectNode eventMessage = Json.newObject();
		initalizeProcessor.processEvent(null, gameState, eventMessage, gameStateMachine);
		
		Board board = gameState.board;
		Tile tile1 = board.getTile(2, 2);
		Tile tile2 = board.getTile(2, 3);
		Unit hUnit = gameState.unit;
		Unit aUnit = gameState.aiUnit;
		hUnit.setTile(tile1);
		aUnit.setTile(tile2);
		
		aUnit.setHealth(2);
		
		//attacked
		UnitAttackState attack = new UnitAttackState(hUnit, tile2, true);
		// private-->public
		//attack.unitAttack(null, aUnit, aUnit.getHealth(), gameState);
		
		//set: aUnit attacks hUint
		
		assertNull(tile2.getUnit());
	}
	
	
	
	
	
	//@Ignore
	@Test
	public void checkHighlightSurrounding1() {
		//initialization
		CheckMessageIsNotNullOnTell altTell = new CheckMessageIsNotNullOnTell();
		BasicCommands.altTell = altTell;
		GameState gameState = new GameState();
		GameStateMachine gameStateMachine = new GameStateMachine();
		Initalize initalizeProcessor = new Initalize();
		ObjectNode eventMessage = Json.newObject();
		initalizeProcessor.processEvent(null, gameState, eventMessage, gameStateMachine);
		
		//check the highlight area
		
		Board board = gameState.board;
		Tile tile = board.getTile(5, 3);
		Unit unit = gameState.unit;
		tile.setUnit(unit);
		
		TileClicked tileClickedProcessor = new TileClicked();
		//x&y
		tileClickedProcessor.processEvent(null, gameState, eventMessage, gameStateMachine);
		
		UnitSelectedState click = new UnitSelectedState(null, eventMessage, gameState);
		//x&y
		click.highlightSurroundingTiles(null, 5, 3, tile, gameState);
		
		
		//assert that all surrounding tiles are highlighted
	    for(int i = 4; i <= 6; i++) {
	        for(int j = 2; j <= 4; j++) {
	            Tile testTile = gameState.board.getTile(i, j);
	            if(i == 5 && j == 3) { //center tile should not be highlighted
	                assertFalse(testTile.getTileState() == TileState.Reachable);
	            } else if(testTile.getUnit() != null || testTile.getAiUnit() != null) {
	                assertTrue(testTile.getTileState() == TileState.Occupied);
	            } else { //reachable tiles should be highlighted
	                assertNotNull(testTile.getTileState());
	                assertEquals(TileState.Reachable, testTile.getTileState());
	            }
	        }
	    }
		
	}
	
	
	
	
	@Ignore
	@Test
	public void checkHighlightSurrounding2() {
		//initialization
		CheckMessageIsNotNullOnTell altTell = new CheckMessageIsNotNullOnTell();
		BasicCommands.altTell = altTell;
		GameState gameState = new GameState();
		GameStateMachine gameStateMachine = new GameStateMachine();
		Initalize initalizeProcessor = new Initalize();
		ObjectNode eventMessage = Json.newObject();
		initalizeProcessor.processEvent(null, gameState, eventMessage, gameStateMachine);
		
		//check the highlight area
		
		Board board = gameState.board;
		Tile tileClicked = board.getTile(5, 3);
		Tile tileOccupied = board.getTile(4, 2);
		Unit hUnit = gameState.unit;
		Unit aUnit = gameState.aiUnit;
		tileClicked.setUnit(hUnit);
		tileOccupied.setUnit(aUnit);
		
		
		TileClicked tileClickedProcessor = new TileClicked();
		tileClickedProcessor.processEvent(null, gameState, eventMessage, gameStateMachine);
		
		UnitSelectedState click = new UnitSelectedState(null, eventMessage, gameState);
		click.highlightSurroundingTiles(null, 5, 3, tileClicked, gameState);
		
		assertTrue(tileOccupied.getTileState() == TileState.Occupied);
		// the number of mode

	    
	}

}