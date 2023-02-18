package events;

import structures.basic.Position;
import structures.basic.Tile;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.BigCard;
import structures.basic.Card;
import structures.basic.EffectAnimation;
import structures.basic.ImageCorrection;
import structures.basic.Unit;
import structures.basic.UnitAnimation;
import structures.basic.UnitAnimationSet;
import structures.basic.UnitAnimationType;
import utils.BasicObjectBuilders;
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
    
    //Gamestate
    private GameState gameState;
    
    //Actor
    private ActorRef out;
    

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
    public void transform(String card){  	    	
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
    	// unit=BasicObjectBuilders.loadUnit(StaticConfFiles.u_silverguard_knight, 0, Unit.class);
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
    	//play the animation 
    	//BasicCommands.playUnitAnimation(out, unit, UnitAnimationType.hit);
    	 //set the card click status to false when place the unit
        gameState.cardIsClicked=false;
        //Set you unit
        tile.setUnit(unit);
        
        try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
    }

    /**
     * The function will place the spell on a unit, and perform an action on that unit
     * @param unit The unit is the destination unit that the spell card will cast to
     */
    public void placeSpell(Unit unit){

    }

	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {
		// TODO Auto-generated method stub
		card=cardClicked.getCard();
		tile=tileClicked.getClickedTile();
		this.out=out;
		this.gameState=gameState;
			
	}

	@Override
	public void run() {
		if(gameState.cardIsClicked)
		{
		    //Play the card
			transform(card.getCardname());
			placeUnit();
			BasicCommands.addPlayer1Notification(out, "Cast the "+card.getCardname(),1);
			//delete the card when it is played
			BasicCommands.deleteCard(out, cardClicked.getHandPosition());
			//Stop the animation
			BasicCommands.playUnitAnimation(out, unit, UnitAnimationType.idle);
			try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
		}	
		
	}
}
