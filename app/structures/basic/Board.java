package structures.basic;
import utils.BasicObjectBuilders;
import utils.Constants;
import utils.StaticConfFiles;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import akka.actor.ActorRef;
import commands.BasicCommands;

/**
 * Board class implements the 2-D grid over which the game is played.
 * It keeps track of all the information for each tile in the board and the units that are present on the tiles.
 * Board class provides methods which can be used to perform certain query operations, for e.g, fetching all the tiles that are reachable by a certain unit.
 */
public class Board {
    /**
     * The width of the board
     */
    private static int width = 0;
    /**
     * The height of the board
     */
    private static int height = 0;
    //List of tiles present in the board.
    /**
     * This collection contains all the tiles on the board
     */
    List<Tile> tiles = Collections.emptyList();

    /**
     * Board constructor to create a board of given width and height.
     * @param width width of the board.
     * @param height height of the board.
     */
    public Board(int width, int height) {
        this.width = width;
        this.height = height;
        tiles = new ArrayList<>(width * height);
    }

    /**
     * Default Board constructor which uses constants BOARD_WIDTH and BOARD_HEIGHT to define the dimensions of the 2-D grid.
     */
    public Board() {
        this.width = Constants.BOARD_WIDTH;
        this.height = Constants.BOARD_HEIGHT;
        this.tiles = new ArrayList<>(width * height);
    }

    /**
     * Get Tile at grid location x and y.
     * @param x The horizontal position of tile
     * @param y the vertical  postion of tile
     * @return Tile instance
     */
    Tile getTile(int x, int y) {
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
    public static void drawBoard(ActorRef out) {
       //Draw the tiles 
    	//Author: Jun Gao
		//Great a board
    	for(int h=0;h<5;h++)
    	{
			for(int w=0;w<9;w++)
			{
		   		Tile tile = BasicObjectBuilders.loadTile(w,h);
	    		BasicCommands.drawTile(out, tile, 0);
			}
 	
    	}	
//    	Tile tile = BasicObjectBuilders.loadTile(2,1);
//		BasicCommands.drawTile(out, tile, 0);
    	

    	String[] deck1Cards = {
				StaticConfFiles.c_azure_herald,
				StaticConfFiles.c_azurite_lion,
				StaticConfFiles.c_comodo_charger,
				StaticConfFiles.c_fire_spitter,
				StaticConfFiles.c_hailstone_golem,
				StaticConfFiles.c_ironcliff_guardian,
				StaticConfFiles.c_pureblade_enforcer,
				StaticConfFiles.c_silverguard_knight,
				StaticConfFiles.c_sundrop_elixir,
				StaticConfFiles.c_truestrike
		};

    	//Initialize 3 cards
		for (int i=0;i<3;i++) {
			// drawCard [1]
			//BasicCommands.addPlayer1Notification(out, deck1CardFile, 2);
			Card card = BasicObjectBuilders.loadCard(deck1Cards[i], i, Card.class);
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
    }
}
