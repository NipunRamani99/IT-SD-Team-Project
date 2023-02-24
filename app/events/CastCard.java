package events;

import structures.basic.*;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.statemachine.GameStateMachine;
import utils.BasicObjectBuilders;
import utils.Constants;
import utils.StaticConfFiles;

/**
 * This class will identify the card based on its type, i.e, a unit or spell card. It will then cast it on the tile when the user clicks on it card
 * and on the target tile. If it is a unit card then this class will spawn the corresponding unit on the target tile.
 * If it is a spell card then this class will cast the spell on the unit which occupies the target tile.
 */
public class CastCard implements EventProcessor,Runnable {
    /**
     * A reference to CardClicked event.
     */
    private CardClicked cardClicked;

    /**
     * A reference to TileCliked event.
     */
    private TileClicked tileClicked;
    
    /**
     * A unit that casted
     */
    private Unit unit=null;
    
    /**
     * A tile to be placed 
     */
    private Tile tile=null;
    
    //A card
    private Card card;
    
    //Game state
    private GameState gameState;
    
    //Actor
    private ActorRef out;
    
    //unit health
    private int health;
    
    //unit attack
    private int attack;
    

    /**
     * The constructor is to pass the reference of a TileCliked and CardClicked event object to create a
     * CastCard instance, and CastCard instance will identify the card into unit or spell.
     * @param cardclick A reference to a CardClicked event
     * @param tileclick A reference to a TileCliked event
     */
    public CastCard(CardClicked cardClick, TileClicked tileClick){
        this.cardClicked=cardClick;
        this.tileClicked=tileClick;
    }

    /**
     * The function will transform the card into unit or spell according to the card type
     */
    public void transform(String card,int health, int attack){  	    	
    	//Transform the card into units
    	
    	switch(card)
    	{
    	  case "Azure Herald":
	    	  unit=BasicObjectBuilders.loadUnit(StaticConfFiles.u_azure_herald, 0, Unit.class);
	    	  break;
    	  case "Azurite Lion":
	    	  unit=BasicObjectBuilders.loadUnit(StaticConfFiles.u_azurite_lion, 0, Unit.class);
	    	  break;
    	  case "Comodo Charger":
	    	  unit=BasicObjectBuilders.loadUnit(StaticConfFiles.u_comodo_charger, 0, Unit.class);
	    	  break;
    	  case "Fire Spitter":
	    	  unit=BasicObjectBuilders.loadUnit(StaticConfFiles.u_fire_spitter, 0, Unit.class);
	    	  break;
    	  case "Hailstone Golem":
	    	  unit=BasicObjectBuilders.loadUnit(StaticConfFiles.u_hailstone_golem, 0, Unit.class);
	    	  break;
    	  case "Ironcliff Guardian":
	    	  unit=BasicObjectBuilders.loadUnit(StaticConfFiles.u_ironcliff_guardian, 0, Unit.class);
	    	  break;	  
    	  case "Pureblade Enforcer":
	    	  unit=BasicObjectBuilders.loadUnit(StaticConfFiles.u_pureblade_enforcer, 0, Unit.class);
	    	  break;
     	  case "Silverguard Knight":
	    	  unit=BasicObjectBuilders.loadUnit(StaticConfFiles.u_silverguard_knight, 0, Unit.class);
	    	  break;
	      default:
	    	  break;
	    
    	}

    	 unit=BasicObjectBuilders.loadUnit(StaticConfFiles.u_silverguard_knight, gameState.id++, Unit.class);
    }


	/**
     * The function will place the unit on the tile
     */
    public void placeUnit(){
    	//The unit will display on the board with animation
    	unit.setPositionByTile(tile);
    	//set the unit to the tile
    	tile.setUnit(unit);
    	//draw the unit
    	BasicCommands.drawUnit(out, unit, tile);
		gameState.board.addUnit(unit);
    	//play the animation 
    	//BasicCommands.playUnitAnimation(out, unit, UnitAnimationType.hit);
    	 //set the card click status to false when place the unit

        resetBoardSelection(out, gameState);
		resetCardSelection(out, gameState);
		gameState.cardIsClicked=false;

        try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
    }
	private void resetCardSelection(ActorRef out, GameState gameState) {
		BasicCommands.drawCard(out, gameState.card, gameState.handPosition, 0);
		gameState.card = null;
		gameState.handPosition = -1;
		gameState.cardIsClicked = false;
	}
	private void resetBoardSelection(ActorRef out, GameState gameState) {
		for(int i = 0; i < Constants.BOARD_WIDTH; i++ ) {
			for(int j = 0; j < Constants.BOARD_HEIGHT; j++) {
				Tile tile = gameState.board.getTile(i, j);
				BasicCommands.drawTile(out, tile, 0);
				try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
				tile.setTileState(TileState.None);
			}
		}
	}

    /**
     * The function will place the spell on a unit, and perform an action on that unit
     * @param unit The unit is the destination unit that the spell card will cast to
     */
    public void placeSpell(Unit unit){

    }

	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message,  GameStateMachine gameStateMachine) {
		// TODO Auto-generated method stub
		gameStateMachine.processInput(out, gameState, message,this);
//		this.card=cardClicked.getCard();
//		this.attack = card.getBigCard().getAttack();
//		this.health = card.getBigCard().getHealth();
//		tile=tileClicked.getClickedTile();
//		this.out=out;
//		this.gameState=gameState;
			
	}

	@Override
	public void run() {
		if(gameState.cardIsClicked)
		{
			BasicCommands.addPlayer1Notification(out, "Cast the "+card.getCardname(),1);
			try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
		    //Play the card
			transform(card.getCardname(), health, attack);
			placeUnit();
			//delete the card when it is played
			BasicCommands.deleteCard(out, cardClicked.getHandPosition());
			//Stop the animation
			BasicCommands.playUnitAnimation(out, unit, UnitAnimationType.idle);
			try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
		}	
		
	}
}
