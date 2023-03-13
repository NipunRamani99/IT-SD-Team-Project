import static org.junit.Assert.assertTrue;

import org.junit.Ignore;
import org.junit.Test;

import com.fasterxml.jackson.databind.node.ObjectNode;

import commands.AbilityCommands;
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
public class UnitAbilityTest {
	@Ignore
	@Test
	public void checkProvoke() throws Exception {
		//initialization
		CheckMessageIsNotNullOnTell altTell = new CheckMessageIsNotNullOnTell();
		BasicCommands.altTell = altTell;
		GameState gameState = new GameState();
		GameStateMachine gameStateMachine = new GameStateMachine();
		Initalize initalizeProcessor = new Initalize();
		ObjectNode eventMessage = Json.newObject();
		initalizeProcessor.processEvent(null, gameState, eventMessage, gameStateMachine);
		
		//check provoke
		Board board = gameState.board;
		Tile hTile = board.getTile(5, 3);
		Tile aTile = board.getTile(5, 4);
		Unit hUnit = gameState.unit;
		
		hUnit.setAvatar(true);
		hUnit.setTile(hTile);
		
		//assetFalse(hUnit.isProvoked);
		
		Card cardProvoke = new Card();
		cardProvoke.setCardname("Rock Pulveriser");
		
		
		CastCard.castUnitCard(null, cardProvoke, aTile, gameState);
		
		Unit aUnit = aTile.getUnit();
		
		// call provoke
		
		//assertTrue(hUnit.isProvoked);
		assertTrue(hUnit.getHealth() == 19);
		//assertTrue(aUnit.hasProvoke);
	}

	
	
	
	
	
	
	@Ignore
	@Test
	public void checkRanged() {
		//initialization
		CheckMessageIsNotNullOnTell altTell = new CheckMessageIsNotNullOnTell();
		BasicCommands.altTell = altTell;
		GameState gameState = new GameState();
		GameStateMachine gameStateMachine = new GameStateMachine();
		Initalize initalizeProcessor = new Initalize();
		ObjectNode eventMessage = Json.newObject();
		initalizeProcessor.processEvent(null, gameState, eventMessage, gameStateMachine);
		
		//check ranged
		Board board = gameState.board;
		Tile hTile = board.getTile(2, 3);
		Tile aTile = board.getTile(8, 3);
		Unit hUint = gameState.unit;
		Unit aUnit = gameState.aiUnit;
		
		
		//call ranged
		
		
	}	
	
	
	
	
	
	
	
	

	@Ignore
	@Test
	public void checkSummonedAnywhere() {
		//initialization
		CheckMessageIsNotNullOnTell altTell = new CheckMessageIsNotNullOnTell();
		BasicCommands.altTell = altTell;
		GameState gameState = new GameState();
		GameStateMachine gameStateMachine = new GameStateMachine();
		Initalize initalizeProcessor = new Initalize();
		ObjectNode eventMessage = Json.newObject();
		initalizeProcessor.processEvent(null, gameState, eventMessage, gameStateMachine);
		
		//check summoned anywhere
		
		
		
	}	
	
	
	
	
	
	
	
	
	
	
	@Ignore
	@Test
	public void checkFlying() {
		//initialization
		CheckMessageIsNotNullOnTell altTell = new CheckMessageIsNotNullOnTell();
		BasicCommands.altTell = altTell;
		GameState gameState = new GameState();
		GameStateMachine gameStateMachine = new GameStateMachine();
		Initalize initalizeProcessor = new Initalize();
		ObjectNode eventMessage = Json.newObject();
		initalizeProcessor.processEvent(null, gameState, eventMessage, gameStateMachine);
		
		//check flying
		
		
	}	
	
	
	
	
	
	
	
	
	@Ignore
	@Test
	public void checkAttackTwice() {
		//initialization
		CheckMessageIsNotNullOnTell altTell = new CheckMessageIsNotNullOnTell();
		BasicCommands.altTell = altTell;
		GameState gameState = new GameState();
		GameStateMachine gameStateMachine = new GameStateMachine();
		Initalize initalizeProcessor = new Initalize();
		ObjectNode eventMessage = Json.newObject();
		initalizeProcessor.processEvent(null, gameState, eventMessage, gameStateMachine);
		
		//check attack twice
		
		
	}	
	
	
	
	
	
	
	
	
	@Ignore
	@Test
	public void checkRangedProvoke() {
		//initialization
		CheckMessageIsNotNullOnTell altTell = new CheckMessageIsNotNullOnTell();
		BasicCommands.altTell = altTell;
		GameState gameState = new GameState();
		GameStateMachine gameStateMachine = new GameStateMachine();
		Initalize initalizeProcessor = new Initalize();
		ObjectNode eventMessage = Json.newObject();
		initalizeProcessor.processEvent(null, gameState, eventMessage, gameStateMachine);
		
		//check ranged provoke
		
		
	}	
	
	

	@Ignore
	@Test
	public void checkHeal() {
		//initialization
		CheckMessageIsNotNullOnTell altTell = new CheckMessageIsNotNullOnTell();
		BasicCommands.altTell = altTell;
		GameState gameState = new GameState();
		GameStateMachine gameStateMachine = new GameStateMachine();
		Initalize initalizeProcessor = new Initalize();
		ObjectNode eventMessage = Json.newObject();
		initalizeProcessor.processEvent(null, gameState, eventMessage, gameStateMachine);
		
		//check whether an avatar unit's health will add 3 but not over 20
		Player player = gameState.humanPlayer;
		Unit unit = gameState.unit;
		unit.setAvatar(true);
		
		int unitHealth = unit.getHealth();//health=20
		assertTrue(unitHealth == 20);
		
		AbilityCommands.Heal_Avatar_On_Summon(null, gameState);
		
		assertTrue(unitHealth == 20);
		
		unit.setHealth(15);
		assertTrue(unitHealth == 15);
		
		AbilityCommands.Heal_Avatar_On_Summon(null, gameState);
		
		assertTrue(unitHealth == 18);
		
	}	

	
}