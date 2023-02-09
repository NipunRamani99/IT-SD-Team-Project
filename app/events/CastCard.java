package events;

import structures.basic.Position;
import structures.basic.Card;
import structures.basic.ImageCorrection;
import structures.basic.Unit;
import structures.basic.UnitAnimation;
import structures.basic.UnitAnimationSet;
import structures.basic.UnitAnimationType;

/**
 * This class will identify the card based on its type, i.e, a unit or spell card. It will then cast it on the tile when the user clicks on it card
 * and on the target tile. If it is a unit card then this class will spawn the corresponding unit on the target tile.
 * If it is a spell card then this class will cast the spell on the unit which occupies the target tile.
 */
public class CastCard {
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
    private Unit unit;

    /**
     * The constructor is to pass the reference of a TileCliked and CardClicked event object to create a
     * CastCard instance, and CastCard instance will identify the card into unit or spell.
     * @param cardclick A reference to a CardClicked event
     * @param tileclick A reference to a TileCliked event
     */
    public CastCard(CardClicked cardclick, TileClicked tileclick){
        this.cardClicked=cardclick;
        this.tileClicked=tileclick;
    }

    /**
     * The function will transform the card into unit or spell according to the card type
     */
    public void transform(){
    	//Get clicked card id
    	int id=cardClicked.getHandPosition();
    	//Get the position
    	structures.basic.Position position=tileClicked.getPosition();
    	//Get the unit animation type
    	UnitAnimationSet animation = new UnitAnimationSet();
    	
    	//Get the imagecorrection
    	ImageCorrection image = new ImageCorrection();
    	
    	unit= new Unit(id,UnitAnimationType.idle, position,animation,image);

    }


	/**
     * The function will place the unit on the tile
     */
    public void placeUnit(){
    	//The unit will display on the board
    	transform();
        
    }

    /**
     * The function will place the spell on a unit, and perform an action on that unit
     * @param unit The unit is the destination unit that the spell card will cast to
     */
    public void placeSpell(Unit unit){

    }
}
