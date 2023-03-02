package structures.basic;


/**
 * This is the base representation of a Card which is rendered in the player's hand.
 * A card has an id, a name (cardname) and a manacost. A card then has a large and mini
 * version. The mini version is what is rendered at the bottom of the screen. The big
 * version is what is rendered when the player clicks on a card in their hand.
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class Card {
	
	private int id;
	
	private String cardName;
	private int manaCost;
	
	private MiniCard miniCard;
	private BigCard bigCard;

	private boolean isUnit;

	public Card() {};
	
	public Card(int id, String cardName, int manaCost, MiniCard miniCard, BigCard bigCard, boolean isUnit) {
		super();
		this.id = id;
		this.cardName = cardName;
		this.manaCost = manaCost;
		this.miniCard = miniCard;
		this.bigCard = bigCard;

		this.isUnit = isUnit;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getCardName() {
		return cardName;
	}
	public void setCardName(String cardName) {
		this.cardName = cardName;
	}
	public int getManaCost() {
		return manaCost;
	}
	public void setManaCost(int manaCost) {
		this.manaCost = manaCost;
	}
	public MiniCard getMiniCard() {
		return miniCard;
	}
	public void setMiniCard(MiniCard miniCard) {
		this.miniCard = miniCard;
	}
	public BigCard getBigCard() {
		return bigCard;
	}
	public void setBigCard(BigCard bigCard) {
		this.bigCard = bigCard;
	}

	
}
