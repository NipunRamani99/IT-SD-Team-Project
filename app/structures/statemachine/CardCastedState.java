package structures.statemachine;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import events.EventProcessor;
import events.TileClicked;
import structures.GameState;
import structures.basic.Card;
import structures.basic.Tile;
import structures.basic.Unit;
import utils.BasicObjectBuilders;
import utils.StaticConfFiles;

public class CardCastedState implements State {


	@Override
	public void handleInput(ActorRef out, GameState gameState, JsonNode message, EventProcessor event,
			GameStateMachine gameStateMachine) {
		if(event instanceof TileClicked) 
		{
			drawUnitOnBoard(out, gameState);
		    gameStateMachine.setState(new NoSelectionState());
		}
		
	}
	
    //Draw the unit on the board
    private void drawUnitOnBoard(ActorRef out,GameState gameState)
    {
    	String carName=gameState.card.getCardname();
    	Unit unit =null;
    	switch(carName)
    	{
    	  case "Azure Herald":
	    	  unit=BasicObjectBuilders.loadUnit(StaticConfFiles.u_azure_herald, gameState.id++, Unit.class);
	    	  break;
    	  case "Azurite Lion":
	    	  unit=BasicObjectBuilders.loadUnit(StaticConfFiles.u_azurite_lion, gameState.id++, Unit.class);
	    	  break;
    	  case "Comodo Charger":
	    	  unit=BasicObjectBuilders.loadUnit(StaticConfFiles.u_comodo_charger,gameState.id++, Unit.class);
	    	  break;
    	  case "Fire Spitter":
	    	  unit=BasicObjectBuilders.loadUnit(StaticConfFiles.u_fire_spitter,gameState.id++, Unit.class);
	    	  break;
    	  case "Hailstone Golem":
	    	  unit=BasicObjectBuilders.loadUnit(StaticConfFiles.u_hailstone_golem, gameState.id++, Unit.class);
	    	  break;
    	  case "Ironcliff Guardian":
	    	  unit=BasicObjectBuilders.loadUnit(StaticConfFiles.u_ironcliff_guardian,gameState.id++, Unit.class);
	    	  break;	  
    	  case "Pureblade Enforcer":
	    	  unit=BasicObjectBuilders.loadUnit(StaticConfFiles.u_pureblade_enforcer,gameState.id++, Unit.class);
	    	  break;
     	  case "Silverguard Knight":
	    	  unit=BasicObjectBuilders.loadUnit(StaticConfFiles.u_silverguard_knight,gameState.id++, Unit.class);
	    	  break;
	      default:
	    	  break;
	    
    	}

    
    	
    	BasicCommands.drawTile(out, gameState.tile, 2);
    	
    	BasicCommands.drawUnit(out, unit, gameState.tile);
    	
    	try { Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
    
    	
    	BasicCommands.addPlayer1Notification(out, "Cast "+carName,1);
    	
    }

}
