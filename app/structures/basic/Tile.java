package structures.basic;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * A basic representation of a tile on the game board. Tiles have both a pixel position
 * and a grid position. Tiles also have a width and height in pixels and a series of urls
 * that point to the different renderable textures that a tile might have.
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class Tile {

	@JsonIgnore
	private static ObjectMapper mapper = new ObjectMapper(); // Jackson Java Object Serializer, is used to read java objects from a file
	
	List<String> tileTextures;
	int xpos;
	int ypos;
	int width;
	int height;
	int tilex;
	int tiley;

	
	public enum Occupied
	{
		//NO occupied
		none,
		//Occupied by the human unit
		userOccupied,
		//Occupied by the AI unit
		aiOccupied;
	}
	
	//The occupied status
	private Occupied occupied;
	
	//The User unit
	private Unit unit=null;
	
	//The ai unit
	private Unit aiUnit;
	
	/**
	 * for the human player
	 * 
	 */
	
//	//Define the tile is occupied or not
//	private boolean occupied = false;
	
	public synchronized Unit getUnit() {
		if(null!=this.unit) unit.setChosed(true);
		return unit;
	}

	public synchronized void setUnit(Unit unit) {
		this.unit = unit;
		this.occupied=Occupied.userOccupied;
	}
	
	/**
	 * for the ai player
	 */
	
	//Set the Ai unit
	
	public synchronized void setAiUnit(Unit unit)
	{
		this.aiUnit = unit;
		//aiUnit.setChosed(false);
		this.occupied=Occupied.aiOccupied;
	}
	
	//Get the Ai unit
	public synchronized Unit getAiUnit() {
		
		if(null!=this.aiUnit) aiUnit.setChosed(true);
		return aiUnit;
	}

	//return the tile status
	public synchronized Occupied isOccupied() {
		return occupied;
	}
	
	
	//set the tile status
	public synchronized void clearUnit()
	{
		if(null!=this.unit) this.unit.setChosed(false);
		this.unit=null;
		this.occupied=Occupied.none;
	}
	
	public Tile()
	{
		this.occupied=Occupied.none;
	}

	private TileState tileState = TileState.None;

	
	public Tile(String tileTexture, int xpos, int ypos, int width, int height, int tilex, int tiley) {
		super();
		tileTextures = new ArrayList<String>(1);
		tileTextures.add(tileTexture);
		this.xpos = xpos;
		this.ypos = ypos;
		this.width = width;
		this.height = height;
		this.tilex = tilex;
		this.tiley = tiley;
		this.occupied=Occupied.none;
	}
	
	public Tile(List<String> tileTextures, int xpos, int ypos, int width, int height, int tilex, int tiley) {
		super();
		this.tileTextures = tileTextures;
		this.xpos = xpos;
		this.ypos = ypos;
		this.width = width;
		this.height = height;
		this.tilex = tilex;
		this.tiley = tiley;
		this.occupied=Occupied.none;
	}
	public List<String> getTileTextures() {
		return tileTextures;
	}
	public void setTileTextures(List<String> tileTextures) {
		this.tileTextures = tileTextures;
	}
	public int getXpos() {
		return xpos;
	}
	public void setXpos(int xpos) {
		this.xpos = xpos;
	}
	public int getYpos() {
		return ypos;
	}
	public void setYpos(int ypos) {
		this.ypos = ypos;
	}
	public int getWidth() {
		return width;
	}
	public void setWidth(int width) {
		this.width = width;
	}
	public int getHeight() {
		return height;
	}
	public void setHeight(int height) {
		this.height = height;
	}
	public int getTilex() {
		return tilex;
	}
	public void setTilex(int tilex) {
		this.tilex = tilex;
	}
	public int getTiley() {
		return tiley;
	}
	public void setTiley(int tiley) {
		this.tiley = tiley;
	}
	

	public TileState getTileState() {
		return tileState;
	}

	public void setTileState(TileState tileState) {
		this.tileState = tileState;
	}


	/**
	 * Loads a tile from a configuration file
	 * parameters.
	 * @param configFile
	 * @return
	 */
	public static Tile constructTile(String configFile) {

		List<String> tileTextures = new ArrayList<>();
		tileTextures.add("assets/game/extra/ui/tile_board.png");
		tileTextures.add("assets/game/extra/ui/tile_grid.png");
		tileTextures.add("assets/game/extra/ui/tile_grid_red.png");
		return new Tile(tileTextures,0, 0, 115, 115, 0, 0);
	}

	public static int distance(Tile A, Tile B) {
		return Math.abs(A.tilex - B.tilex) + Math.abs(A.tiley - B.tiley);
	}
}
