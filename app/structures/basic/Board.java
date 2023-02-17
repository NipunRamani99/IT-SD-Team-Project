package structures.basic;
import utils.BasicObjectBuilders;
import utils.Constants;
import utils.StaticConfFiles;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
        return tiles.get(x + y*width);
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
       //Draw the tiles 
    	//Author: Jun Gao
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
//    	Tile tile = BasicObjectBuilders.loadTile(2,1);
//		BasicCommands.drawTile(out, tile, 0);
    	//gameState.tiles=tiles;

    	//Initialize 3 cards
		for (int i=0;i<3;i++) {
			// drawCard [1]
			//BasicCommands.addPlayer1Notification(out, deck1CardFile, 2);
			Card card = deck1.getCard();
			//Add the card in the arraylist
			cards.add(i, card);
			BasicCommands.drawCard(out, card, i+1, 0);
			try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}

//			// drawCard [1] Highlight
//			BasicCommands.addPlayer1Notification(out, deck1CardFile+" Highlight", 2);
//			BasicCommands.drawCard(out, card, 1, 1);
//			try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
//
//			// deleteCard [1]
//			BasicCommands.addPlayer1Notification(out, "deleteCard", 2);
//			BasicCommands.deleteCard(out, 1);
//			try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
		}
		
		// Draw a unit
		Unit unit = BasicObjectBuilders.loadUnit(StaticConfFiles.humanAvatar, 0, Unit.class);	
		//Get related tiles
		unit.setPositionByTile(getTile(0,2)); 	
		BasicCommands.drawUnit(out, unit, getTile(0,2));
		
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
}
