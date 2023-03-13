package structures.statemachine;

import akka.actor.ActorRef;
import commands.BasicCommands;

import com.fasterxml.jackson.databind.JsonNode;
import events.*;
import structures.GameState;
import structures.basic.Tile;

public class NoSelectionState extends State{

    public NoSelectionState() {

    }
    @Override
    public void handleInput(ActorRef out, GameState gameState, JsonNode message, EventProcessor event, GameStateMachine gameStateMachine) {

    	if(!checkWinner(out, gameState))
    	{
    		if (event instanceof TileClicked) {
                System.out.println("NoSelectionState: Unit clicked");
                int tilex = message.get("tilex").asInt();
                int tiley = message.get("tiley").asInt();
                Tile tile = gameState.board.getTile(tilex, tiley);
                if(tile.getUnit() != null) {
                    System.out.println("Exiting NoSelectionState 27");
                    gameStateMachine.setState(new UnitSelectedState(out, message, gameState), out, gameState);
                }
            } else if (event instanceof CardClicked) {
                System.out.println("NoSelectionState: Card clicked");
                System.out.println("Exiting NoSelectionState 32");
                gameStateMachine.setState(new CardSelectedState(out, message, gameState), out, gameState);
            } else if (event instanceof Heartbeat) {
                System.out.println("Heartbeat: NoSelectionState");
            } else if (event instanceof EndTurnClicked) {
                System.out.println("Exiting NoSelectionState 37");
                gameStateMachine.setState(new EndTurnState(), out, gameState);
            }
            else {
                System.out.println("NoSelectionState: Invalid input");
            }
    	}
    	else
    	{
    		gameStateMachine.setState(this, out, gameState);
    	}
        
    }
    
 /**
  * Check the winner
  * @param out
  * @param gameState
  */
	private boolean checkWinner(ActorRef out, GameState gameState) {
		if(0==gameState.AiPlayer.getHealth())
		{
		  BasicCommands.addPlayer1Notification(out, "Congratulations!You win the game:)", 5);
		  return true;
		}
		else if(0 ==gameState.humanPlayer.getHealth())
		{
			 BasicCommands.addPlayer1Notification(out, "Oops!You lose game:(", 5);
			 return true;
		}
		
		return false;
	}

    @Override
    public void enter(ActorRef out, GameState gameState) {
        System.out.println("Entering NoSelectionState");
    }

    @Override
    public void exit(ActorRef out, GameState gameState) {

    }
}
