package ai.actions;

import structures.GameState;
import structures.basic.Card;
import structures.basic.Tile;
import structures.basic.TileState;
import structures.basic.Unit;
import structures.basic.UnitAbility;
import structures.statemachine.CardSelectedState;
import structures.statemachine.State;
import utils.Constants;
import java.util.Random;

import akka.actor.ActorRef;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import commands.BasicCommands;

public class CastUnitAction implements AiAction {

    private Card unitCard;
    private Tile targetTile;
	private  List<Card> cards;   
    private List<Tile> tiles;
    private List<Unit> markedUnits;
    private ActorRef out;

    public CastUnitAction(ActorRef out,Card unitCard, Tile targetTile) {
        this.unitCard = unitCard;
        this.targetTile = targetTile;
        this.out=out;

    }
    
    public CastUnitAction(ActorRef out, List<Unit> units) {
        this.out=out;
        this.markedUnits=units;
    }
    
    public List<Tile> getAvailableTiles(GameState gameState) {
        	return getSurroundingTiles(gameState);
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
        	if(unit.isAi())
        	{
                for (int i = -1; i <= 1; i++) {
                    for (int j = -1; j <= 1; j++) {
                        if(i == 0 && j == 0) continue;;
                        int x = unit.getPosition().getTilex() + i;
                        int y = unit.getPosition().getTiley() + j;
                        Tile surroundingTile = gameState.board.getTile(x, y);
                        if (surroundingTile == null ) {
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
        List<Tile> reachableTiles = getAvailableTiles(gameState);
        reachableTiles.sort(Comparator.comparingInt((a)-> a.distanceToUnit(markedUnit)));
        for(Tile reachableTile : reachableTiles) {
            if(reachableTile.getUnit() == null) {
                return reachableTile;
            }
        }
        return null;
    }
    
    /**
     * Get the lowest health unit
     * @param markedUnits
     * @return unit
     */
    
    @Override
    public State processAction(GameState gameState) {
    	cards=gameState.board.getAiCards();
    	Tile tile=null;
    	Card card=null;
    	List<Tile> tiles;
    	Random rand = new Random();
    	for(Card c:cards)
    	{
    		//only works on the unit card
    		if(c.getBigCard()!=null)
    		{
    			if(gameState.AiPlayer.getMana()>=c.getManacost()
    		       &&c.getBigCard().getHealth()>0)
	    		{
    				card=c;
    				tiles=getAvailableTiles(gameState);
    			    int index=rand.nextInt(tiles.size());
    			    tile = tiles.get(index);
	    		}
    		}
    		else
    		{
    			break;
    		}
    		
    	}
    	if(tile!=null)
    		return new  CardSelectedState(out,card.getCardPosition(), tile, gameState);
    	else
    		return null;
    }
}
