
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Ignore;
import org.junit.Test;

import com.fasterxml.jackson.databind.node.ObjectNode;

import ai.AIPlayer;
import commands.BasicCommands;
import commands.CheckMessageIsNotNullOnTell;
import events.EndTurnClicked;
import events.Initalize;
import play.libs.Json;
import structures.GameState;
import structures.Turn;
import structures.basic.Card;
import structures.basic.Player;
import structures.statemachine.EndTurnState;
import structures.statemachine.GameStateMachine;
import structures.statemachine.NoSelectionState;

 
public class GameLogicTest {
	//@Ignore
	@Test
	public void checkEndTurn() {
		//initialization
		CheckMessageIsNotNullOnTell altTell = new CheckMessageIsNotNullOnTell();
		BasicCommands.altTell = altTell;
		GameState gameState = new GameState();
		GameStateMachine gameStateMachine = new GameStateMachine();
		Initalize initalizeProcessor = new Initalize();
		ObjectNode eventMessage = Json.newObject();
		initalizeProcessor.processEvent(null, gameState, eventMessage, gameStateMachine);
		
		//check the end turn click
		//assertFalse(gameState.endTurn);
		//assertTrue(gameState.currentTurn == Turn.PLAYER);
		assertFalse(gameState.humanPlayer.getMana() == 3);
		
		EndTurnClicked endTurn = new EndTurnClicked();
		endTurn.processEvent(null, gameState, eventMessage, gameStateMachine);
		
		EndTurnState endTurnState = new EndTurnState();
		endTurnState.enter(null, gameState);
		
		//assertTrue(gameState.endTurn);
		//assertTrue(gameState.currentTurn == Turn.AI);
		assertTrue(gameState.humanPlayer.getMana() == 3);
		
	}
	
	
	@Ignore
	@Test
	public void checkGameWin() {
		//initialization
		CheckMessageIsNotNullOnTell altTell = new CheckMessageIsNotNullOnTell();
		BasicCommands.altTell = altTell;
		GameState gameState = new GameState();
		GameStateMachine gameStateMachine = new GameStateMachine();
		Initalize initalizeProcessor = new Initalize();
		ObjectNode eventMessage = Json.newObject();
		initalizeProcessor.processEvent(null, gameState, eventMessage, gameStateMachine);

		//check when Ai player avatar's health is less than 0 or its deck is empty,
		//human player wins the game
		
		Player humanPlayer = gameState.humanPlayer;
		AIPlayer aiPlayer = gameState.ai;
		
		//cannot find setHealth
		//aiPlayer.setHealth(0);
				
		//assertFalse(gameState.isGameOver);
		
		//checkWinner:[need] private --> public
		NoSelectionState state = new NoSelectionState();
		//state.checkWinner(null, gameState);
		
		//assertTrue(gameState.isGameOver);
		
		
		
		//new game
		initalizeProcessor.processEvent(null, gameState, eventMessage, gameStateMachine);
		
		//assertFalse(gameState.isGameOver);

		//int size = aiPlayer.deck.size();
		//for(int i=0; i<=size; i++) {
			//aiPlayer.consumeCard();
		//}
		
		//assertTrue(gameState.isGameOver);		
		
	}
	
	
	@Ignore
	@Test
	public void checkGameLose() {
		//initialization
		CheckMessageIsNotNullOnTell altTell = new CheckMessageIsNotNullOnTell();
		BasicCommands.altTell = altTell;
		GameState gameState = new GameState();
		GameStateMachine gameStateMachine = new GameStateMachine();
		Initalize initalizeProcessor = new Initalize();
		ObjectNode eventMessage = Json.newObject();
		initalizeProcessor.processEvent(null, gameState, eventMessage, gameStateMachine);

		//check when human player avatar's health is less than 0 or his deck is empty,
		//Ai player wins the game
		 
		Player humanPlayer = gameState.humanPlayer;
		AIPlayer aiPlayer = gameState.ai;
		
		humanPlayer.setHealth(0);
		
		//[need] private --> public
		NoSelectionState state = new NoSelectionState();
		//state.checkWinner(null, gameState);
		
		//assertTrue(gameState.isGameOver);
		
		//new game
		initalizeProcessor.processEvent(null, gameState, eventMessage, gameStateMachine);
		//assertFalse(gameState.isGameOver);
		
		int size = gameState.board.getCards().size();
		//for(int i=0; i<=size; i++) {
			//humanPlayer.deck.remove(i);
		//}
		
		//assertTrue(gameState.isGameOver);
		
	}
}
