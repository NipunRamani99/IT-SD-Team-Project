package structures.basic;

import java.util.ArrayList;

import utils.BasicObjectBuilders;
import utils.StaticConfFiles;

//Deck class will generate a deck for the game
//There are two different decks, it depends on the number you enter
//when you generate the Deck object with new Deck(1), you will load deck 1
//when you generate the Deck object with new Deck(2), you will load deck 2

public class Deck {
	private ArrayList<Card> cardDeck = new ArrayList<Card>();
	
	public Deck(int deckNumber) {
		if(deckNumber == 1) {
			loadDeck1(this.cardDeck);
		}else if (deckNumber == 2) {
			loadDeck2(this.cardDeck);
		}
	}
	
	public void loadDeck1(ArrayList<Card> cardDeck) {
		ArrayList<String> deck = new ArrayList<String>();

		// creates two of each card for player deck
		for(int i=0; i<2; i++)
		{
			deck.add(StaticConfFiles.c_truestrike);
			deck.add(StaticConfFiles.c_sundrop_elixir);
			deck.add(StaticConfFiles.c_comodo_charger);
			deck.add(StaticConfFiles.c_azure_herald);
			deck.add(StaticConfFiles.c_azurite_lion);
			deck.add(StaticConfFiles.c_fire_spitter);
			deck.add(StaticConfFiles.c_hailstone_golem);
			deck.add(StaticConfFiles.c_ironcliff_guardian);
			deck.add(StaticConfFiles.c_pureblade_enforcer);
			deck.add(StaticConfFiles.c_silverguard_knight);

		}
		for(int i = 0; i < deck.size(); i++) {
			Card card = BasicObjectBuilders.loadCard(deck.get(i), i, Card.class);
			this.cardDeck.add(card);
		}
	}
	
	public void loadDeck2(ArrayList<Card> cardDeck) {
		ArrayList<String> deck = new ArrayList<String>();

		// creates two of each card for AI deck
		for(int i=0; i<2; i++)
		{
			deck.add(StaticConfFiles.c_staff_of_ykir);
			deck.add(StaticConfFiles.c_entropic_decay);
			deck.add(StaticConfFiles.c_blaze_hound);
			deck.add(StaticConfFiles.c_bloodshard_golem);
			deck.add(StaticConfFiles.c_planar_scout);
			deck.add(StaticConfFiles.c_pyromancer);
			deck.add(StaticConfFiles.c_hailstone_golem);
			deck.add(StaticConfFiles.c_rock_pulveriser);
			deck.add(StaticConfFiles.c_serpenti);
			deck.add(StaticConfFiles.c_windshrike);
		}
		
		for(int i = 0; i < deck.size(); i++) {
			Card card = BasicObjectBuilders.loadCard(deck.get(i), i, Card.class);
			this.cardDeck.add(card);
		}
	}
	
	//use this method when you need to draw a card from deck
	public Card getCard() {
		Card card = new Card();
		if (!cardDeck.isEmpty()) {
		    card = cardDeck.get(0);
		    this.cardDeck.remove(0);
		}
		return card;
	}
}
