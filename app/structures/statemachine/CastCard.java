package structures.statemachine;
import java.util.ArrayList;
import structures.basic.*;

import akka.actor.ActorRef;
import commands.AbilityCommands;
import commands.BasicCommands;
import events.CardClicked;
import events.TileClicked;
import structures.GameState;
import structures.basic.Units.*;
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

		ArrayList<Unit> initialiseUnits = new ArrayList<Unit>();

    	String cardName = card.getCardname();
    	switch(cardName)
    	{
    	  case "Azure Herald":
	    	  AzureHerald azureHerald = new AzureHerald();
			  azureHerald.loadUnit(StaticConfFiles.u_azure_herald, gameState.id, Unit.class);
	    	  card = BasicObjectBuilders.loadCard(StaticConfFiles.c_azure_herald, gameState.id++, Card.class);
			  azureHerald.setHealth(card.getBigCard().getHealth());
			  azureHerald.setAttack(card.getBigCard().getAttack());

			  initialiseUnits.add(azureHerald);

	    	  break;
    	  case "Azurite Lion":
			  AzuriteLion azuriteLion = new AzuriteLion();
			  azuriteLion.loadUnit(StaticConfFiles.u_azurite_lion, gameState.id, Unit.class);
	    	  card = BasicObjectBuilders.loadCard(StaticConfFiles.c_azurite_lion,gameState.id++, Card.class);
			  azuriteLion.setHealth(card.getBigCard().getHealth());
			  azuriteLion.setAttack(card.getBigCard().getAttack());

			  initialiseUnits.add(azuriteLion);

	    	  break;
    	  case "Comodo Charger":
	    	  Unit comodoCharger = new Unit();
			  comodoCharger.loadUnit(StaticConfFiles.u_comodo_charger, gameState.id, Unit.class);
	    	  card = BasicObjectBuilders.loadCard(StaticConfFiles.c_comodo_charger,gameState.id++, Card.class);
			  comodoCharger.setHealth(card.getBigCard().getHealth());
			  comodoCharger.setAttack(card.getBigCard().getAttack());

			  initialiseUnits.add(comodoCharger);

	    	  break;
    	  case "Fire Spitter":
			  FireSpitter fireSpitter = new FireSpitter();
			  fireSpitter.loadUnit(StaticConfFiles.u_fire_spitter,gameState.id, Unit.class);
	    	  card = BasicObjectBuilders.loadCard(StaticConfFiles.c_fire_spitter,gameState.id++, Card.class);
	    	  fireSpitter.setHealth(card.getBigCard().getHealth());
	    	  fireSpitter.setAttack(card.getBigCard().getAttack());

			  initialiseUnits.add(fireSpitter);

	    	  break;
    	  case "Hailstone Golem":
	    	  Unit hailstoneGolem = new Unit();
			  hailstoneGolem.loadUnit(StaticConfFiles.u_hailstone_golem, gameState.id, Unit.class);
	    	  card = BasicObjectBuilders.loadCard(StaticConfFiles.c_hailstone_golem, gameState.id++, Card.class);
			  hailstoneGolem.setHealth(card.getBigCard().getHealth());
			  hailstoneGolem.setAttack(card.getBigCard().getAttack());

			  initialiseUnits.add(hailstoneGolem);

	    	  break;
    	  case "Ironcliff Guardian":
			  IroncliffGuardian ironcliffGuardian = new IroncliffGuardian();
			  ironcliffGuardian.loadUnit(StaticConfFiles.u_ironcliff_guardian, gameState.id, Unit.class);
	    	  card = BasicObjectBuilders.loadCard(StaticConfFiles.c_ironcliff_guardian,gameState.id++, Card.class);
			  ironcliffGuardian.setHealth(card.getBigCard().getHealth());
			  ironcliffGuardian.setAttack(card.getBigCard().getAttack());

			  initialiseUnits.add(ironcliffGuardian);

	    	  break;	  
    	  case "Pureblade Enforcer":
			  PurebladeEnforcer purebladeEnforcer = new PurebladeEnforcer();
			  purebladeEnforcer.loadUnit(StaticConfFiles.u_pureblade_enforcer, gameState.id, Unit.class);
	    	  card = BasicObjectBuilders.loadCard(StaticConfFiles.c_pureblade_enforcer, gameState.id++, Card.class);
			  purebladeEnforcer.setHealth(card.getBigCard().getHealth());
			  purebladeEnforcer.setAttack(card.getBigCard().getAttack());

			  initialiseUnits.add(purebladeEnforcer);

	    	  break;
     	  case "Silverguard Knight":
			  SilverguardKnight silverguardKnight = new SilverguardKnight();
			  silverguardKnight.loadUnit(StaticConfFiles.u_silverguard_knight, gameState.id, Unit.class);
	    	  card = BasicObjectBuilders.loadCard(StaticConfFiles.c_silverguard_knight, gameState.id++, Card.class);
			  silverguardKnight.setHealth(card.getBigCard().getHealth());
			  silverguardKnight.setAttack(card.getBigCard().getAttack());

			  initialiseUnits.add(silverguardKnight);

	    	  break;
	      default:
	    	  break;	    
    	}

		for(Unit unit : initialiseUnits) {

			placeUnit(gameState, unit, tile, out);
			tile.setTileState(TileState.Occupied);
			//add attack and health to the unit
			BasicCommands.setUnitAttack(out, unit, unit.getAttack());
			try {
				Thread.sleep(5);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			BasicCommands.setUnitHealth(out, unit, unit.getHealth());
			try {
				Thread.sleep(5);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			BasicCommands.addPlayer1Notification(out, "Cast the " + card.getCardname(), 1);

			//Stop the animation
			BasicCommands.playUnitAnimation(out, unit, UnitAnimationType.idle);
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

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
    	try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
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
