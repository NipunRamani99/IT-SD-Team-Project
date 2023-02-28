package structures.basic;
import utils.BasicObjectBuilders;
import utils.Constants;
import utils.StaticConfFiles;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ai.AIPlayer;
import ai.ActionType;
import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;

/**
 * Board class implements the 2-D grid over which the game is played.
 * It keeps track of all the information for each tile in the board and the units that are present on the tiles.
 * Board class provides methods which can be used to perform certain query operations, for e.g, fetching all the tiles that are reachable by a certain unit.
 */
public class Board {
    /**
     * The width of the board
     */
    private int width = 0;
    /**
     * The height of the board
     */
    private int height = 0;
    //List of tiles present in the board.
    /**
     * This collection contains all the tiles on the board
     */
   //List<Tile> tiles = Collections.emptyList();
    static  ArrayList<Tile> tiles= new ArrayList<Tile>();
    
    //The card arrayList;cards in hand
    static ArrayList<Card> cards=  new ArrayList<Card>();
    
    static Deck deck1 = new Deck(1);
    
    static Deck deck2 = new Deck(2);

    static ArrayList<Unit> units = new ArrayList<>();

    /**
     * Board constructor to create a board of given width and height.
     * @param width width of the board.
     * @param height height of the board.
     */
    public Board(int width, int height) {
        this.width = width;
        this.height = height;
        
    }

    /**
     * Default Board constructor which uses constants BOARD_WIDTH and BOARD_HEIGHT to define the dimensions of the 2-D grid.
     */
    public Board() {
        this.width = Constants.BOARD_WIDTH;
        this.height = Constants.BOARD_HEIGHT;
       
    }

    /**
     * Get Tile at grid location x and y.
     * @param x The horizontal position of tile
     * @param y the vertical  position of tile
     * @return Tile instance
     */
    public Tile getTile(int x, int y) {
        if(x >= 0 && x < width && y >= 0 && y <height)
            return tiles.get(x + y*width);
        else
            return null;
    }

    /**
     * This method will return a list of tiles which are reachable by a unit when the player clicks on it on the board.
     * The movement range is fetched from the unit instance.
     * @param tile tile occupied by the unit.
     * @param unit the unit which is to be moved
     * @return A list of tiles
     */
    List<Tile> getReachableTiles(Tile tile, Unit unit) {
        return Collections.emptyList();
    }

    /**
     * This method will go through every tile in the 2-D grid and then visualize it and the unit placed on the tile by
     * sending the appropriate draw commands.
     */
    public void drawBoard(ActorRef out,GameState gameState) {
		//Great a board
    	
    	for(int h=0;h<height;h++)
    	{
			for(int w=0;w<width;w++)
			{
		   		Tile tile = BasicObjectBuilders.loadTile(w,h);
		   		tiles.add(w+h*width,tile);
	    		BasicCommands.drawTile(out, tile, 0);
			}
    	}	
    	

    	//Initialize 3 cards
		for (int i=0;i<3;i++) {
			// drawCard [1]
			//BasicCommands.addPlayer1Notification(out, deck1CardFile, 2);
			Card card = deck1.getCard();
			//Add the card in the arraylist
			cards.add(i, card);
			BasicCommands.drawCard(out, card, i+1, 0);

		}
		
		// Draw a unit
		Unit unit = BasicObjectBuilders.loadUnit(StaticConfFiles.humanAvatar, gameState.id++, Unit.class);	
		BasicCommands.playUnitAnimation(out, unit, UnitAnimationType.hit);
		//Get related tiles
		Tile tile=this.getTile(5, 2);
		unit.setPositionByTile(tile); 
		//The tile set the unit
		this.addUnit(unit);
		tile.setUnit(unit);	
		BasicCommands.drawUnit(out, unit, tile);
		//unit attack and health
		BasicCommands.setUnitAttack(out, unit, 2);
		BasicCommands.setUnitHealth(out, unit,20);
		
		
		//Create an AI player
		//Initialize the AI part
	    
	    //draw the avatar on the board
		
		Unit aiUnit = BasicObjectBuilders.loadUnit(StaticConfFiles.aiAvatar, gameState.id++, Unit.class);	
		//Get related tiles
		Tile aiTile = getTile(8, 2);
		aiUnit.setPositionByTile(aiTile); 
		//The tile set the unit
		aiTile.setAiUnit(aiUnit);
	    addUnit(aiUnit);
		//draw unit
		BasicCommands.drawUnit(out, aiUnit, aiTile);
		//unit attack and health
		BasicCommands.setUnitAttack(out, aiUnit, 2);
		BasicCommands.setUnitHealth(out, aiUnit,20);
		
		//gameState.unit=aiUnit;
		Tile aiTile1 = getTile(7, 3);
		BasicCommands.moveUnitToTile(out, aiUnit, aiTile1);
		aiTile1.setAiUnit(aiUnit);

		BasicCommands.playUnitAnimation(out,aiUnit,UnitAnimationType.attack);
		//move unit to tile		


		
//		Unit unit2 = BasicObjectBuilders.loadUnit(StaticConfFiles.u_azure_herald, 0, Unit.class);
//		unit2.setPositionByTile(getTile(1,2)); 	
//		BasicCommands.drawUnit(out, unit2, getTile(1,2));
		// Move unit, default, horizontal then vertical		
//		BasicCommands.moveUnitToTile(out, unit, tile2);	
//		unit.setPositionByTile(tile2); 
    }
    
    
   //Get the card on the board with the handposition
    public Card getCard(int pos)
    {
    	return cards.get(pos-1);
    }
    public List<Card> getCards(){return cards;}
    public List<Unit> getUnits() {return units; }
    
    public void addUnit(Unit unit) {
        units.add(unit);
    }
    
    public void deleteCard(int position) {
    	cards.set(position-1, new Card());
    }

}
