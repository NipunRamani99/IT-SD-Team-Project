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
	
	private int id;
	private UnitAnimationType animation;
	private Position position;
	private UnitAnimationSet animations;
	private ImageCorrection correction;


	String name = "";

	//health and attcak for a unit
	
	private boolean isAi= false;

	private int attack;

	private int health;
	private int hpFromCard = 0;
	private boolean isChosed=false;

	private boolean canAttack = true;	

	private int attackTimes=0;

	public int getAttackTimes() {
		return attackTimes;
	}

	public void setAttackTimes(int attackTimes) {
		this.attackTimes = attackTimes;
	}

	private boolean movement = true;
	
	private boolean canAttackBack=true;
	
	private boolean hasProvoke = false;
	
	private boolean isProvoked = false;
	
	private boolean hasRanged = false;
	
	private boolean hasFlying = false;
	
	private boolean hasAirDrop = false;


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
	
	//set attack back status
	public void setAttackBack(boolean attackBack)
	{
		this.canAttackBack=attackBack;
	}
	
	public boolean isAttackBack()
	{
		return this.canAttackBack;
	}

	//check it is avatar or not
	private boolean isAvatar=false;
	
	public boolean isAvatar() {
		return isAvatar;
	}

	public void setAvatar(boolean isAvatar) {
		this.isAvatar = isAvatar;
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
		return Math.abs(a.getTilex() - b.getTilex()) + Math.abs(a.getTiley() - b.getTiley());
	}

	public Position getDisplacement(Unit unit) {
		Position p=null;
		if(unit!=null)
		{
			Position a = position;
			Position b = unit.getPosition();
			p = new Position(0, 0, a.tilex - b.tilex, a.tiley - b.tiley);
		}

		return p;
	}

	public boolean withinDistance(Unit unit) {
		Position displacement = this.getDisplacement(unit);
		if(Math.abs(displacement.tilex)<=1&&Math.abs(displacement.tiley)<=1) return true;	
		return false;
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


	
	public boolean isIsProvoked() {
		return isProvoked;
	}
	public void setIsProvoked(boolean isProvoked) {
		this.isProvoked = isProvoked;
	}

	public boolean isHasProvoke() {
		return hasProvoke;
	}

	public void setHasProvoke(boolean hasProvoke) {
		this.hasProvoke = hasProvoke;
	}

	public boolean isHasRanged() {
		return hasRanged;
	}

	public void setHasRanged(boolean hasRanged) {
		this.hasRanged = hasRanged;
	}

	public boolean isHasFlying() {
		return hasFlying;
	}

	public void setHasFlying(boolean hasFlying) {
		this.hasFlying = hasFlying;
	}

	public boolean isHasAirDrop() {
		return hasAirDrop;
	}

	public void setHasAirDrop(boolean hasAirDrop) {
		this.hasAirDrop = hasAirDrop;
	}

}
