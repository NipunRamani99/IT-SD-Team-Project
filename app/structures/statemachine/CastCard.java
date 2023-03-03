package structures.statemachine;

import structures.basic.*;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.AbilityCommands;
import commands.BasicCommands;
import events.CardClicked;
import events.EventProcessor;
import events.TileClicked;
import structures.GameState;
import utils.BasicObjectBuilders;
import utils.Constants;
import utils.StaticConfFiles;

/**
 * This class will identify the card based on its type, i.e, a unit or spell card. It will then cast it on the tile when the user clicks on it card
 * and on the target tile. If it is a unit card then this class will spawn the corresponding unit on the target tile.
 * If it is a spell card then this class will cast the spell on the unit which occupies the target tile.
 */
public class CastCard {
    /**
 
    

    /**
     * The constructor is to pass the reference of a TileCliked and CardClicked event object to create a
     * CastCard instance, and CastCard instance will identify the card into unit or spell.
     * @param cardclick A reference to a CardClicked event
     * @param tileclick A reference to a TileCliked event
     */
    public CastCard(CardClicked cardclick,TileClicked tileclick){}

    /**
     * The function will transform the card into unit or spell according to the card type
     */
    public static void castUnitCard(ActorRef out, Card card, Tile tile, GameState gameState){  	
    	//Transform the card into units
    	Unit unit = null;
    	String cardName = card.getCardname();
    	switch(cardName)
    	{
    	  case "Azure Herald":
	    	  unit=BasicObjectBuilders.loadUnit(StaticConfFiles.u_azure_herald, 0, Unit.class);
	    	  card = BasicObjectBuilders.loadCard(StaticConfFiles.c_azure_herald, 0, Card.class);
	    	  unit.setHealth(card.getBigCard().getHealth());
	    	  unit.setAttack(card.getBigCard().getAttack());
	    	  break;
    	  case "Azurite Lion":
	    	  unit=BasicObjectBuilders.loadUnit(StaticConfFiles.u_azurite_lion, 0, Unit.class);
	    	  card = BasicObjectBuilders.loadCard(StaticConfFiles.c_azurite_lion, 0, Card.class);
	    	  unit.setHealth(card.getBigCard().getHealth());
	    	  unit.setAttack(card.getBigCard().getAttack());
	    	  break;
    	  case "Comodo Charger":
	    	  unit=BasicObjectBuilders.loadUnit(StaticConfFiles.u_comodo_charger, 0, Unit.class);
	    	  card = BasicObjectBuilders.loadCard(StaticConfFiles.c_comodo_charger, 0, Card.class);
	    	  unit.setHealth(card.getBigCard().getHealth());
	    	  unit.setAttack(card.getBigCard().getAttack());
	    	  break;
    	  case "Fire Spitter":
	    	  unit=BasicObjectBuilders.loadUnit(StaticConfFiles.u_fire_spitter, 0, Unit.class);
	    	  card = BasicObjectBuilders.loadCard(StaticConfFiles.c_fire_spitter, 0, Card.class);
	    	  unit.setHealth(card.getBigCard().getHealth());
	    	  unit.setAttack(card.getBigCard().getAttack());
	    	  break;
    	  case "Hailstone Golem":
	    	  unit=BasicObjectBuilders.loadUnit(StaticConfFiles.u_hailstone_golem, 0, Unit.class);
	    	  card = BasicObjectBuilders.loadCard(StaticConfFiles.c_hailstone_golem, 0, Card.class);
	    	  unit.setHealth(card.getBigCard().getHealth());
	    	  unit.setAttack(card.getBigCard().getAttack());
	    	  break;
    	  case "Ironcliff Guardian":
	    	  unit=BasicObjectBuilders.loadUnit(StaticConfFiles.u_ironcliff_guardian, 0, Unit.class);
	    	  card = BasicObjectBuilders.loadCard(StaticConfFiles.c_ironcliff_guardian, 0, Card.class);
	    	  unit.setHealth(card.getBigCard().getHealth());
	    	  unit.setAttack(card.getBigCard().getAttack());
	    	  break;	  
    	  case "Pureblade Enforcer":
	    	  unit=BasicObjectBuilders.loadUnit(StaticConfFiles.u_pureblade_enforcer, 0, Unit.class);
	    	  card = BasicObjectBuilders.loadCard(StaticConfFiles.c_pureblade_enforcer, 0, Card.class);
	    	  unit.setHealth(card.getBigCard().getHealth());
	    	  unit.setAttack(card.getBigCard().getAttack());
	    	  break;
     	  case "Silverguard Knight":
	    	  unit=BasicObjectBuilders.loadUnit(StaticConfFiles.u_silverguard_knight, 0, Unit.class);
	    	  card = BasicObjectBuilders.loadCard(StaticConfFiles.c_silverguard_knight, 0, Card.class);
	    	  unit.setHealth(card.getBigCard().getHealth());
	    	  unit.setAttack(card.getBigCard().getAttack());
	    	  break;
	      default:
	    	  break;	    
    	}

    	placeUnit(gameState, unit, tile, out);
    	//set the unit to the tile
    	tile.setUnit(unit);
    	tile.setTileState(TileState.Occupied);
    	
		BasicCommands.addPlayer1Notification(out, "Cast the "+card.getCardname(),1);
	    //add attack and health to the unit
		BasicCommands.setUnitAttack(out, unit, unit.getAttack());
		BasicCommands.setUnitHealth(out, unit, unit.getHealth());
		//Stop the animation
		BasicCommands.playUnitAnimation(out, unit, UnitAnimationType.idle);
		try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}

    }
    
    public static void castSpellCard(ActorRef out, Card card, Tile tile, GameState gameState) {
    	//Transform the card into units
    	String spellName = null;
    	String cardName = card.getCardname();
    	switch(cardName)
    	{
     	  case "Truestrike":
     		  spellName = StaticConfFiles.f1_inmolation;
     		  placeSpell(out, gameState, spellName, tile);
     		  AbilityCommands.truestrikeAbility(out, tile.getUnit());
     		  break;
     	  case "Sundrop Elixir":
    		  spellName = StaticConfFiles.f1_summon;
    		  placeSpell(out, gameState, spellName, tile);
    		  AbilityCommands.sundropElixir(out, tile.getUnit());    		  
    		  break;
     	  case "Staff of Y’Kir":
   		      spellName = StaticConfFiles.f1_buff;
   		      placeSpell(out, gameState, spellName, tile);
   		      AbilityCommands.yKirAbility(out, tile.getUnit());
   		      break;
     	  case "Entropic Decay":
  		      spellName = StaticConfFiles.f1_martyrdom;
  		      placeSpell(out, gameState, spellName, tile);
  		      AbilityCommands.entropicDecay(out, tile.getUnit());
  		      break;
	      default:
	    	  break;
	    
    	} 	
    	
		BasicCommands.addPlayer1Notification(out, "Cast the "+card.getCardname(),1);
		//delete the card when it is played

		try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
			
}


	/**
     * The function will place the unit on the tile
	 * @param gameState 
	 * @param out 
     */
    private static void placeUnit(GameState gameState, Unit unit, Tile tile, ActorRef out){
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
    }
	

    /**
     * The function will place the spell on a unit, and perform an action on that unit
     * @param unit The unit is the destination unit that the spell card will cast to
     */
    private static void placeSpell(ActorRef out, GameState gameState, String spellName, Tile tile){
    	EffectAnimation ef = BasicObjectBuilders.loadEffect(spellName);
		BasicCommands.playEffectAnimation(out, ef, tile);
		try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
    }
    
    //this method will check if this card is a unit card
    public static boolean isUnitCard(Card card) {
    	boolean isUnitCard = false;
    	String cardName = card.getCardname();
    	switch(cardName)
    	{
    	  case "Azure Herald":
	    	  isUnitCard = true;
	    	  break;
    	  case "Azurite Lion":
    		  isUnitCard = true;
	    	  break;
    	  case "Comodo Charger":
    		  isUnitCard = true;
	    	  break;
    	  case "Fire Spitter":
    		  isUnitCard = true;
	    	  break;
    	  case "Hailstone Golem":
    		  isUnitCard = true;
	    	  break;
    	  case "Ironcliff Guardian":
    		  isUnitCard = true;
	    	  break;	  
    	  case "Pureblade Enforcer":
    		  isUnitCard = true;
	    	  break;
     	  case "Silverguard Knight":
     		  isUnitCard = true;
	    	  break;
	      default:
	    	  break;	    
    	}
    	return isUnitCard;
    }

}
