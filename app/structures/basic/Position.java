package structures.basic;

/**
 * This contains the positional information for a unit
 * that is sitting on a tile. tilex/y are the index position
 * of the tile the unit is sitting upon. x/ypos are the pixel
 * position of the unit.
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class Position {

	int xpos;
	int ypos;
	int tilex;
	int tiley;
	
	public Position() {}
	
	public Position(int xpos, int ypos, int tilex, int tiley) {
		super();
		this.xpos = xpos;
		this.ypos = ypos;
		this.tilex = tilex;
		this.tiley = tiley;
	}
	
	//Define the position of the tile
	public Position(int tilex, int tiley)
	{
		super();
		this.tilex=tilex;
		this.tiley=tiley;
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
	public int getTilex() {
		return this.tilex;
	}
	public void setTilex(int tilex) {
		this.tilex = tilex;
	}
	public int getTiley() {
		return this.tiley;
	}
	public void setTiley(int tilexy) {
		this.tiley = tilexy;
	}
	
	public static Position getDisplacement(Position a, Position b) {
		return new Position(a.tilex - b.tilex, a.tiley - b.tiley);
	}
}
