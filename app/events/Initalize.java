package events;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import demo.CheckMoveLogic;
import demo.CommandDemo;
import structures.GameState;
import structures.basic.Board;
import structures.basic.Player;
import structures.statemachine.GameStateMachine;

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
	public void processEvent(ActorRef out, GameState gameState, JsonNode message,  GameStateMachine gameStateMachine) {
		// hello this is a change	
		gameState.something = true;
		
		//initialize the ai player 
		Player PlayerAi =new Player();
		PlayerAi.setHealth(10);
		
		//Initialize the board height and width and draw it
		Board board= new Board(9,5);
	    board.drawBoard(out,gameState);
	    gameState.board=board;
		// setPlayer1Health
		Player humanPlayer = new Player(20, 2);
		BasicCommands.setPlayer1Health(out, humanPlayer);
		BasicCommands.setPlayer1Mana(out, humanPlayer);
		gameState.humanPlayer=humanPlayer;
		try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}

		// setPlayer2Health
		Player aiPlayer = new Player(20, 2);
		BasicCommands.setPlayer2Health(out, aiPlayer);
		BasicCommands.setPlayer2Mana(out, aiPlayer);
		gameState.AiPlayer=aiPlayer;
		try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
		//Board.drawBoard(out);
		// User 1 makes a change
		//CommandDemo.executeDemo(out); // this executes the command demo, comment out this when implementing your solution
		//CheckMoveLogic.executeDemo(out);
		gameState.gameInitalised = true;
	}

}


