package events;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import demo.CheckMoveLogic;
import demo.CommandDemo;
import structures.GameState;
import structures.basic.Board;
import structures.basic.Player;

/**
 * Indicates that both the core game loop in the browser is starting, meaning
 * that it is ready to recieve commands from the back-end.
 * 
 * { 
 *   messageType = "initalize"
 * }
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class Initalize implements EventProcessor{

	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {
		// hello this is a change
		
		gameState.gameInitalised = true;
		
		gameState.something = true;
		
		//initialize the ai player 
		Player PlayerAi =new Player();
		PlayerAi.setHealth(10);
		gameState.playerAi=PlayerAi;
		
		//Initialize the board height and width and draw it
		Board board= new Board(9,5);
		gameState.board  =board;
		gameState.board.drawBoard(out,gameState);
		//Board.drawBoard(out);
		// User 1 makes a change
		//CommandDemo.executeDemo(out); // this executes the command demo, comment out this when implementing your solution
		//CheckMoveLogic.executeDemo(out);
		
	}

}


