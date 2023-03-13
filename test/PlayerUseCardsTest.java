import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Ignore;
import org.junit.Test;

import com.fasterxml.jackson.databind.node.ObjectNode;

import ai.AIPlayer;
import commands.BasicCommands;
import commands.CheckMessageIsNotNullOnTell;
import events.Initalize;
import play.libs.Json;
import structures.GameState;
import structures.basic.Board;
import structures.basic.Card;
import structures.basic.Player;
import structures.basic.Tile;
import structures.basic.Unit;
import structures.statemachine.CastCard;
import structures.statemachine.GameStateMachine;

@Ignore
public class PlayerUseCardsTest {
	@Ignore
	@Test
	public void checkDrawACardInHand() {
		//initialization
		CheckMessageIsNotNullOnTell altTell = new CheckMessageIsNotNullOnTell();
		BasicCommands.altTell = altTell;
		GameState gameState = new GameState();
		GameStateMachine gameStateMachine = new GameStateMachine();
		Initalize initalizeProcessor = new Initalize();
		ObjectNode eventMessage = Json.newObject();
		initalizeProcessor.processEvent(null, gameState, eventMessage, gameStateMachine);
		
		//check draw a card in the hand from deck
		Player player = gameState.humanPlayer;
		Board board = gameState.board;
		board.drawBoard(null, gameState);
		
		
		assertTrue(gameState.board.getCards().size() == 3);
		
		board.drawCard();
		
		assertTrue(gameState.board.getCards().size() == 4);
		
		
		
	}
	
	
	
	@Ignore
	@Test
	public void checkOverDraw() {
		//initialization
		CheckMessageIsNotNullOnTell altTell = new CheckMessageIsNotNullOnTell();
		BasicCommands.altTell = altTell;
		GameState gameState = new GameState();
		GameStateMachine gameStateMachine = new GameStateMachine();
		Initalize initalizeProcessor = new Initalize();
		ObjectNode eventMessage = Json.newObject();
		initalizeProcessor.processEvent(null, gameState, eventMessage, gameStateMachine);
		
		//check when the player's hand overdraws(more than 6 cards), the last card 
		//will not appear in the hand
		
		Player player = gameState.humanPlayer;
		assertFalse(gameState.board.getCards().size() == 6);
		
		Board board = gameState.board;
		board.drawCard();
		board.drawCard();
		board.drawCard();
		board.drawCard();
		assertTrue(gameState.board.getCards().size() == 6);
		
	}
	
	
	
	
	@Ignore
	@Test
	public void checkUseUnitCard() throws Exception {
		//initialization
		CheckMessageIsNotNullOnTell altTell = new CheckMessageIsNotNullOnTell();
		BasicCommands.altTell = altTell;
		GameState gameState = new GameState();
		GameStateMachine gameStateMachine = new GameStateMachine();
		Initalize initalizeProcessor = new Initalize();
		ObjectNode eventMessage = Json.newObject();
		initalizeProcessor.processEvent(null, gameState, eventMessage, gameStateMachine);
		
		//check when using a unit such as "comodo charger" from the hand to the board
		
		Player player = gameState.humanPlayer;
		player.setMana(8);
		
		assertFalse(gameState.board.getCards().size() == 2);
		assertFalse(player.getMana() == 7);
		assertFalse(gameState.tile.getTilex() == 1 && gameState.tile.getTiley() == 1 && gameState.tile.getUnit().getName() == "comodo charger");
		
		Board board = gameState.board;
		Card card = new Card();
		card.setCardname("comodo charger");
		
		CastCard.castUnitCard(null, card, board.getTile(1, 1), gameState);
		
		assertTrue(gameState.board.getCards().size() == 2);
		assertTrue(player.getMana() == 7);
		assertTrue(gameState.tile.getTilex() == 1 && gameState.tile.getTiley() == 1 && gameState.tile.getUnit().getName() == "comodo charger");

	}
	
	
	@Ignore
	@Test
	public void checkUseSpellCard() throws Exception {
		//initialization
		CheckMessageIsNotNullOnTell altTell = new CheckMessageIsNotNullOnTell();
		BasicCommands.altTell = altTell;
		GameState gameState = new GameState();
		GameStateMachine gameStateMachine = new GameStateMachine();
		Initalize initalizeProcessor = new Initalize();
		ObjectNode eventMessage = Json.newObject();
		initalizeProcessor.processEvent(null, gameState, eventMessage, gameStateMachine);
		
		//check when using a spell card such as "true strike"
		
		Player hPlayer = gameState.humanPlayer;
		AIPlayer aPlayer = gameState.ai;
		Board board = gameState.board;
		Tile tile = board.getTile(5, 3);
		Unit unit = gameState.aiUnit;
		unit.setAvatar(true);
		tile.setUnit(unit);
		
		hPlayer.setMana(9);
		
		assertFalse(gameState.board.getCards().size() == 2);
		assertFalse(hPlayer.getMana() == 8);
		
		Card card = new Card();
		card.setCardname("true strike");
		CastCard.castSpellCard(null, card, board.getTile(5, 3), gameState);
		
		assertTrue(gameState.board.getCards().size() == 2);
		assertTrue(hPlayer.getMana() == 8);
	}
}
