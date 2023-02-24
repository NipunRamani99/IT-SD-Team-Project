package structures.statemachine;

import akka.actor.ActorRef;
import com.fasterxml.jackson.databind.JsonNode;
import commands.BasicCommands;
import events.CardClicked;
import events.EventProcessor;
import events.TileClicked;
import structures.GameState;
import structures.basic.Card;
import structures.basic.Tile;
import structures.basic.TileState;
import structures.basic.Unit;
import utils.BasicObjectBuilders;
import utils.StaticConfFiles;

import java.util.List;

public class CardSelectedState implements State{
    private int handPosition = 0;
    private Card cardSelected = null;

    public CardSelectedState(ActorRef out, JsonNode message, GameState gameState) {
        gameState.resetCardSelection(out);
        handPosition = message.get("position").asInt();
        cardSelected=gameState.board.getCard(handPosition);
        BasicCommands.drawCard(out, cardSelected, handPosition, 1);
        highlightCardSelection(out, gameState);
    }
    @Override
    public void handleInput(ActorRef out, GameState gameState, JsonNode message, EventProcessor event, GameStateMachine gameStateMachine) {
        if(event instanceof TileClicked) {
            int tilex = gameState.position.getTilex();
            int tiley = gameState.position.getTiley();
            Tile tile = gameState.board.getTile(tilex, tiley);
            System.out.println(String.format("tiles:%d,%d",tilex,tiley));
            System.out.println("TileSelectedState: Tile Clicked");
            if(tile.getTileState() == TileState.None) {
                gameState.resetBoardSelection(out);
                gameState.resetCardSelection(out);
                System.out.println("CardSelectedState: None Tile Clicked");
                gameStateMachine.setState(new NoSelectionState());
            } else if (tile.getTileState() == TileState.Reachable) {
                gameState.resetBoardSelection(out);
                //assign the reachable tile to the gameState
                drawUnitOnBoard(out, gameState,cardSelected,tile);
            	//after the cast the unit, delete the card
            	BasicCommands.deleteCard(out, handPosition);
                System.out.println("CardSelectedState: Reachable Tile Clicked");
//                gameStateMachine.setState(new NoSelectionState());
            }
        } else if(event instanceof CardClicked) {
            gameState.resetBoardSelection(out);
            System.out.println("CardSelectedState: Card Clicked");
            gameStateMachine.setState(new CardSelectedState(out, message, gameState));
        } else {
            System.out.println("CardSelectedState: Invalid Event");
        }
    }

    private void highlightCardSelection(ActorRef out, GameState gameState)
    {
        BasicCommands.addPlayer1Notification(out, "Card highlight ",1);
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
                            BasicCommands.drawTile(out, surroundingTile, 1);
                            try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
                        }
                    }
                }
            }
        }
    }
    
    //Draw the unit on the board
    private void drawUnitOnBoard(ActorRef out,GameState gameState,Card card, Tile tile)
    {
    	String carName=card.getCardname();
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

    
    
    	unit.setPositionByTile(tile); 
    	BasicCommands.drawUnit(out, unit, tile);
    	gameState.board.addUnit(unit);
		tile.setUnit(unit);	
    	
    	try { Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
    
    	
    	BasicCommands.addPlayer1Notification(out, "Cast "+carName,1);
    	
    }
    
}
