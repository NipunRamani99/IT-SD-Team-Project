package structures.basic;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * This is a representation of a Unit on the game board.
 * A unit has a unique id (this is used by the front-end.
 * Each unit has a current UnitAnimationType, e.g. move,
 * or attack. The position is the physical position on the
 * board. UnitAnimationSet contains the underlying information
 * about the animation frames, while ImageCorrection has
 * information for centering the unit on the tile. 
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class Unit {

	@JsonIgnore
	protected static ObjectMapper mapper = new ObjectMapper(); // Jackson Java Object Serializer, is used to read java objects from a file
	
	int id;
	UnitAnimationType animation;
	Position position;
	UnitAnimationSet animations;
	ImageCorrection correction;
	
	//health and attcak for a unit
	
	private boolean isAi= false;

	private int attack;

	private int health;
	private int hpFromCard = 0;
	private boolean isChosed=false;

	private boolean canAttack = true;

	private boolean movement = true;

	//Choose the unit
	public boolean isChosed() {
		return isChosed;
	}

	//set the status to the unit
	public void setChosed(boolean isChosed) {
		this.isChosed = isChosed;
	}
	//set the type of the unit
	public boolean isAi() {
		return isAi;
	}

	public void setAi(boolean isAi) {
		this.isAi = isAi;
	}

	private Tile tile;

	public Unit() {}
	
	//get attack
	public int getAttack() {
		return attack;
	}
    //set attack
	public void setAttack(int attack) {
		this.attack = attack;
	}
	
	
	//get the health
	public int getHealth() {
		return health;
	}
	//set the health
	public void setHealth(int health) {
		this.health = health;
		if(hpFromCard==0)
		{
			this.hpFromCard=health;
		}
	}

	
	public Unit(int id, UnitAnimationSet animations, ImageCorrection correction) {
		super();
		this.id = id;
		this.animation = UnitAnimationType.idle;
		
		position = new Position(0,0,0,0);
		this.correction = correction;
		this.animations = animations;
	}
	
	public Unit(int id, UnitAnimationSet animations, ImageCorrection correction, Tile currentTile) {
		super();
		this.id = id;
		this.animation = UnitAnimationType.idle;
		
		position = new Position(currentTile.getXpos(),currentTile.getYpos(),currentTile.getTilex(),currentTile.getTiley());
		this.correction = correction;
		this.animations = animations;
	}
	
	
	
	public Unit(int id, UnitAnimationType animation, Position position, UnitAnimationSet animations,
			ImageCorrection correction) {
		super();
		this.id = id;
		this.animation = animation;
		this.position = position;
		this.animations = animations;
		this.correction = correction;
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public UnitAnimationType getAnimation() {
		return animation;
	}
	public void setAnimation(UnitAnimationType animation) {
		this.animation = animation;
	}

	public ImageCorrection getCorrection() {
		return correction;
	}

	public void setCorrection(ImageCorrection correction) {
		this.correction = correction;
	}

	public Position getPosition() {
		return position;
	}


	public void setPosition(Position position) {
		this.position = position;
	}

	public UnitAnimationSet getAnimations() {
		return animations;
	}

	public void setAnimations(UnitAnimationSet animations) {
		this.animations = animations;
	}

	public int gethpFromCard() {
		return hpFromCard;
	}
	
	/**
	 * This command sets the position of the Unit to a specified
	 * tile.
	 * @param tile
	 */
	@JsonIgnore
	public void setPositionByTile(Tile tile) {
		position = new Position(tile.getXpos(),tile.getYpos(),tile.getTilex(),tile.getTiley());
	}

	public Tile getTile() {
		return tile;
	}

	public void setTile(Tile tile) {
		this.tile = tile;
	}

	public int getDistance(Unit unit) {
		Position a = position;
		Position b = unit.getPosition();
		return Math.abs(a.tilex - b.tilex) + Math.abs(a.tiley - b.tiley);
	}

	public Position getDisplacement(Unit unit) {
		Position a = position;
		Position b = unit.getPosition();
		Position p = new Position(0, 0, a.tilex - b.tilex, a.tiley - b.tiley);
		return p;
	}

	public boolean withinDistance(Unit unit) {
		Position displacement = this.getDisplacement(unit);
		return Math.abs(displacement.tilex) <= 1 && Math.abs(displacement.tiley) <= 1;
	}

	public boolean canAttack() {
		return canAttack;
	}

	public void setCanAttack(boolean canAttack) {
		this.canAttack = canAttack;
	}

	public void setMovement(boolean movement) {
		this.movement = movement;
	}

	public boolean getMovement() {
		return movement;
	}
}
