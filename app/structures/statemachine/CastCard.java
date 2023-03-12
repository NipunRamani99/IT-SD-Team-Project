package structures.statemachine;
import java.util.ArrayList;
import structures.basic.*;

import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.AbilityCommands;
import commands.BasicCommands;
import events.CardClicked;
import events.TileClicked;
import structures.GameState;
import structures.Turn;
import utils.BasicObjectBuilders;
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
//    	Unit unit = null;

    	String cardName = card.getCardname();
    	switch(cardName)
    	{
    	  case "Azure Herald":
	    	  unit = BasicObjectBuilders.loadUnit(StaticConfFiles.u_azure_herald, gameState.id, Unit.class);
	    	 // card = BasicObjectBuilders.loadCard(StaticConfFiles.c_azure_herald, gameState.id, Card.class);
	    	  unit.setHealth(card.getBigCard().getHealth());
	    	  unit.setAttack(card.getBigCard().getAttack());
	    	  break;
    	  case "Azurite Lion":
	    	  unit=BasicObjectBuilders.loadUnit(StaticConfFiles.u_azurite_lion, gameState.id, Unit.class);
	    	 // card = BasicObjectBuilders.loadCard(StaticConfFiles.c_azurite_lion,gameState.id, Card.class);
	    	  unit.setHealth(card.getBigCard().getHealth());
	    	  unit.setAttack(card.getBigCard().getAttack());
	    	  break;
    	  case "Comodo Charger":
	    	  unit=BasicObjectBuilders.loadUnit(StaticConfFiles.u_comodo_charger, gameState.id, Unit.class);
	    	//  card = BasicObjectBuilders.loadCard(StaticConfFiles.c_comodo_charger,gameState.id, Card.class);
	    	  unit.setHealth(card.getBigCard().getHealth());
	    	  unit.setAttack(card.getBigCard().getAttack());
	    	  break;
    	  case "Fire Spitter":
	    	  unit=BasicObjectBuilders.loadUnit(StaticConfFiles.u_fire_spitter,gameState.id, Unit.class);
	    	 // card = BasicObjectBuilders.loadCard(StaticConfFiles.c_fire_spitter,gameState.id, Card.class);
	    	  unit.setHealth(card.getBigCard().getHealth());
	    	  unit.setAttack(card.getBigCard().getAttack());
	    	  break;
    	  case "Hailstone Golem":
	    	  unit=BasicObjectBuilders.loadUnit(StaticConfFiles.u_hailstone_golem, gameState.id, Unit.class);
	    	//  card = BasicObjectBuilders.loadCard(StaticConfFiles.c_hailstone_golem, gameState.id, Card.class);
	    	  unit.setHealth(card.getBigCard().getHealth());
	    	  unit.setAttack(card.getBigCard().getAttack());
	    	  break;
    	  case "Ironcliff Guardian":
	    	  unit=BasicObjectBuilders.loadUnit(StaticConfFiles.u_ironcliff_guardian, gameState.id, Unit.class);
	    	//  card = BasicObjectBuilders.loadCard(StaticConfFiles.c_ironcliff_guardian,gameState.id, Card.class);
	    	  unit.setHealth(card.getBigCard().getHealth());
	    	  unit.setAttack(card.getBigCard().getAttack());
	    	  break;	  
    	  case "Pureblade Enforcer":
	    	  unit=BasicObjectBuilders.loadUnit(StaticConfFiles.u_pureblade_enforcer, gameState.id, Unit.class);
	    	//  card = BasicObjectBuilders.loadCard(StaticConfFiles.c_pureblade_enforcer, gameState.id, Card.class);
	    	  unit.setHealth(card.getBigCard().getHealth());
	    	  unit.setAttack(card.getBigCard().getAttack());
	    	  break;
     	  case "Silverguard Knight":
	    	  unit=BasicObjectBuilders.loadUnit(StaticConfFiles.u_silverguard_knight, gameState.id, Unit.class);
	    	//  card = BasicObjectBuilders.loadCard(StaticConfFiles.c_silverguard_knight, gameState.id, Card.class);
	    	  unit.setHealth(card.getBigCard().getHealth());
	    	  unit.setAttack(card.getBigCard().getAttack());
	    	  break;
     	  case "Blaze Hound":
     		  unit=BasicObjectBuilders.loadUnit(StaticConfFiles.u_blaze_hound, gameState.id, Unit.class);
	    	//  card = BasicObjectBuilders.loadCard(StaticConfFiles.c_blaze_hound, gameState.id, Card.class);
	    	  unit.setHealth(card.getBigCard().getHealth());
	    	  unit.setAttack(card.getBigCard().getAttack());
     		  break;
     	  case "Bloodshard Golem":
     		  unit=BasicObjectBuilders.loadUnit(StaticConfFiles.u_bloodshard_golem, gameState.id, Unit.class);
	    	//  card = BasicObjectBuilders.loadCard(StaticConfFiles.c_bloodshard_golem, gameState.id, Card.class);
	    	  unit.setHealth(card.getBigCard().getHealth());
	    	  unit.setAttack(card.getBigCard().getAttack());
     		  break;
     	  case "Planar Scout":
     		  unit=BasicObjectBuilders.loadUnit(StaticConfFiles.u_planar_scout, gameState.id, Unit.class);
	    	//  card = BasicObjectBuilders.loadCard(StaticConfFiles.c_planar_scout, gameState.id, Card.class);
	    	  unit.setHealth(card.getBigCard().getHealth());
	    	  unit.setAttack(card.getBigCard().getAttack());
     		  break;
     	  case "Pyromancer":
     		  unit=BasicObjectBuilders.loadUnit(StaticConfFiles.u_pyromancer, gameState.id, Unit.class);
	    //	  card = BasicObjectBuilders.loadCard(StaticConfFiles.c_pyromancer, gameState.id, Card.class);
	    	  unit.setHealth(card.getBigCard().getHealth());
	    	  unit.setAttack(card.getBigCard().getAttack());
     		  break;
     	  case "Rock Pulveriser":
     		  unit=BasicObjectBuilders.loadUnit(StaticConfFiles.u_rock_pulveriser, gameState.id, Unit.class);
	    //	  card = BasicObjectBuilders.loadCard(StaticConfFiles.c_rock_pulveriser, gameState.id, Card.class);
	    	  unit.setHealth(card.getBigCard().getHealth());
	    	  unit.setAttack(card.getBigCard().getAttack());
     		  break;
     	  case "Serpenti":
     		  unit=BasicObjectBuilders.loadUnit(StaticConfFiles.u_serpenti, gameState.id, Unit.class);
	    //	  card = BasicObjectBuilders.loadCard(StaticConfFiles.c_serpenti, gameState.id, Card.class);
	    	  unit.setHealth(card.getBigCard().getHealth());
	    	  unit.setAttack(card.getBigCard().getAttack());
     		  break;
     	  case "WindShrike":
     		  unit=BasicObjectBuilders.loadUnit(StaticConfFiles.u_windshrike, gameState.id, Unit.class);
	    //	  card = BasicObjectBuilders.loadCard(StaticConfFiles.c_windshrike, gameState.id, Card.class);
	    	  unit.setHealth(card.getBigCard().getHealth());
	    	  unit.setAttack(card.getBigCard().getAttack());
     		  break;
	      default:
	    	  break;	    
    	}

    	gameState.id++;
		unit.setName(cardName);
    	placeUnit(gameState, unit, tile, out);
    	tile.setTileState(TileState.Occupied);
   	    //add attack and health to the unit
    	try {Thread.sleep(200);} catch (InterruptedException e) {e.printStackTrace();}
		BasicCommands.setUnitAttack(out, unit, unit.getAttack());
		try {Thread.sleep(200);} catch (InterruptedException e) {e.printStackTrace();}
		BasicCommands.setUnitHealth(out, unit, unit.getHealth());
    	
    	
    	//update the position
    	updatePosition(out, card, gameState);
    	//update the player mana;
    	updatePlayerMana(out, card, gameState);

		//Stop the animation
		BasicCommands.playUnitAnimation(out, unit, UnitAnimationType.idle);

    public static void castSpellCard(ActorRef out, Card card, Tile tile, GameState gameState) {
    	//Transform the card into units
    	String spellName = null;
    	String cardName = card.getCardname();
    	switch(cardName)
    	{
     	  case "Truestrike":
     		  spellName = StaticConfFiles.f1_inmolation;
     		  //Only works on enemy unit
     		  if(null!=tile.getUnit()&&tile.getUnit().isAi())
     		  {
     			 placeSpell(out, gameState,spellName, card, tile);
        		 AbilityCommands.truestrikeAbility(out, tile.getUnit(),gameState);
     		  }
     		  else
     		  {
     			 gameState.resetBoardSelection(out);
    			 gameState.resetCardSelection(out);
     		  }
     		  break;
     	  case "Sundrop Elixir":
    		  spellName = StaticConfFiles.f1_summon;
    		  placeSpell(out, gameState,spellName,card, tile);
    		  AbilityCommands.sundropElixir(out, tile.getUnit(),gameState);
    		  break;
     	  case "Staff of Y'Kir'":
   		      spellName = StaticConfFiles.f1_buff;
   		      //works on your avatar
   		      if(null!=tile.getUnit()&&tile.getUnit().isAvatar()&&tile.getUnit().isAi())
     		  {
     			 placeSpell(out, gameState, spellName,card, tile);
        		 AbilityCommands.yKirAbility(out, tile.getUnit());
     		  }
   		      else
   		      {
   		    	 gameState.resetBoardSelection(out);
    			 gameState.resetCardSelection(out);
   		      }
   		      break;
     	  case "Entropic Decay":
  		      spellName = StaticConfFiles.f1_martyrdom;
  		      //Reduce a non-avatar unit to 0 health
  		      Unit targetUnit= tile.getUnit();
  		      if(null!=targetUnit&&!tile.getUnit().isAvatar())
  		      {
  	  		      placeSpell(out, gameState,spellName,card, tile);
  	  		      AbilityCommands.entropicDecay(out, tile.getUnit(),gameState);
  		      }
  		      else
  		      {
  		    	  gameState.resetBoardSelection(out);
  		    	  gameState.resetCardSelection(out);
  		      }
  		      break;
	      default:
	    	  break;
	    
    	} 	
    	
    	
		BasicCommands.addPlayer1Notification(out, "Cast the "+card.getCardname(),1);
		//delete the card when it is played

}


	/**
     * The function will place the unit on the tile
	 * @param gameState 
	 * @param out 
     */
    private static void placeUnit(GameState gameState, Unit unit, Tile tile, ActorRef out){
    	//The unit will display on the board with animation
    	unit.setPositionByTile(tile);
    	 	
		gameState.board.addUnit(unit);
		//set the unit according to the user
		if(gameState.currentTurn==Turn.AI)
			unit.setAi(true);
		tile.setUnit(unit);	
    	//when first placed, the unit can not attack and move
    	unit.setCanAttack(false);
    	unit.setMovement(false);
    	//draw the unit
    	BasicCommands.drawUnit(out, unit, tile);
    	try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}
    }
	

    /**
     * The function will place the spell on a unit, and perform an action on that unit
     * @param unit The unit is the destination unit that the spell card will cast to
     */
    private static void placeSpell(ActorRef out, GameState gameState,String spellName, Card card, Tile tile){
    	EffectAnimation ef = BasicObjectBuilders.loadEffect(spellName);
		BasicCommands.playEffectAnimation(out, ef, tile);
		try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}
		//update the position
    	updatePosition(out, card, gameState);
    	
    	//update the player mana;
    	updatePlayerMana(out, card, gameState);
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

    
    public static void updatePlayerMana(ActorRef out,Card card,GameState gameState)
    {
    	if(gameState.currentTurn==Turn.AI)
    	{
    		 //cost the ai mana
    		int mana=gameState.AiPlayer.getMana();
   		 	gameState.AiPlayer.setMana( mana-card.getManacost());
   		 	BasicCommands.setPlayer2Mana(out, gameState.AiPlayer);
    	}
    	else
    	{
    		//cost the human player mana
    		int mana=gameState.humanPlayer.getMana();
   		 	gameState.humanPlayer.setMana( mana-card.getManacost());
   		 	BasicCommands.setPlayer1Mana(out, gameState.humanPlayer);
    	}
    	
    	try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
    }
    
    public static void updatePosition(ActorRef out, Card card, GameState gameState)
    {
    	int handPosition=0;
    	handPosition=card.getCardPosition();
        BasicCommands.deleteCard(out, handPosition);
        if(gameState.currentTurn==Turn.AI)
        	gameState.board.deleteAiCard(handPosition);
        else
        	gameState.board.deleteCard(handPosition);
        try {Thread.sleep(5);} catch (InterruptedException e) {e.printStackTrace();}
    }
}
