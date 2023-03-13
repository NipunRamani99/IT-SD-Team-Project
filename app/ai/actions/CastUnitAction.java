package ai.actions;

import structures.GameState;
import structures.basic.Card;
import structures.basic.Tile;
import structures.basic.Unit;
import structures.basic.UnitAbility;
import structures.statemachine.CardSelectedState;
import structures.statemachine.State;
import utils.Constants;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * CastSpellAction creates the state change required to cast a unit card.
 */
public class CastUnitAction implements AiAction {

    private Card unitCard;
    private Tile targetTile;
	private  List<Card> cards;   
    private List<Tile> tiles;
    private List<Unit> markedUnits;


    /**
     *
     * @param unitCard
     * @param markedUnits
     */
    public CastUnitAction(Card unitCard, List<Unit> markedUnits) {
        this.unitCard = unitCard;
        this.markedUnits = markedUnits;

    }

    /**
     *
     * @param units
     */
    public CastUnitAction(List<Unit> units) {

        this.markedUnits=units;
    }

    /**
     *
     * @param gameState
     * @return
     */
    public List<Tile> getAvailableTiles(GameState gameState) {
        //Check if unit has ability to summon anywhere
        List<UnitAbility> abilityList= gameState.unitAbilityTable.getUnitAbilities(unitCard.getCardname());
        if(abilityList.contains(UnitAbility.SUMMON_ANYWHERE)) {

            //Return all empty tiles
        	return getAllEmptyTiles(gameState);
        }
        else {
            //return the available tiles for ai unit
            return getSurroundingTiles(gameState);
        }
    }
    
    /**
     * Return all empty tiles
     * @param gameState
     * @return
     */
    private List<Tile> getAllEmptyTiles(GameState gameState)
    {
         for (int i =0; i <Constants.BOARD_WIDTH; i++) {
             for (int j =0; j <Constants.BOARD_HEIGHT; j++) {
                 Tile surroundingTile = gameState.board.getTile(i, j);
                 if ( null==surroundingTile.getUnit())
                   tiles.add(surroundingTile);
             }
         }
    	return tiles;
    }
    
    /**
     * Return the ai unit surrounding tiles
     * @param gameState
     * @return
     */
    private List<Tile> getSurroundingTiles(GameState gameState)
    {
    	List<Tile> tiles= new ArrayList<Tile>();
        List<Unit> unitList = gameState.board.getUnits();     
        for(Unit unit : unitList) {
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    if(i == 0 && j == 0) continue;;
                    int x = unit.getPosition().getTilex() + i;
                    int y = unit.getPosition().getTiley() + j;
                    Tile surroundingTile = gameState.board.getTile(x, y);
                    if (surroundingTile != null && unit.isAi()) {
                        if (surroundingTile.getUnit() == null) {
                            tiles.add(surroundingTile);
                        }
                    }
                }
            }
        }
        return tiles;
    }
    
    /**
     * Get the closet available tiles
     * @param gameState
     * @param markedUnit
     * @return  reachableTile /null
     */
    public Tile getClosestAvailableTile(GameState gameState, Unit markedUnit) {
        List<Tile> reachableTiles = getSurroundingTiles(gameState);
        reachableTiles.sort(Comparator.comparingInt((a)-> a.distanceToUnit(markedUnit)));
        for(Tile reachableTile : reachableTiles) {
            if(reachableTile.getUnit() == null) {
                return reachableTile;
            }
        }
        return null;
    }

    /**
     * This method generates the state change required to cast the unit card selected by the AI.
     * @param gameState
     * @return CardSelectedState
     */
    @Override
    public State processAction(GameState gameState) {
    	cards=gameState.board.getAiCards();
        Tile tile = getClosestAvailableTile(gameState, markedUnits.get(0));
        if(unitCard == null || tile == null )
            return null;
    	else
    		return new CardSelectedState(unitCard.getCardPosition(), tile, gameState);
    }

}
