package ai.actions;

import ai.AIActionUtils;
import structures.GameState;
import structures.basic.Card;
import structures.basic.Tile;
import structures.statemachine.CardSelectedState;
import structures.statemachine.State;

import java.util.List;
import java.util.Optional;

public class CastSpellAction implements AiAction{

	public CastSpellAction()
	{

	}
	
	private Tile getSpellTile(Card card,GameState gameState)
	{
		Tile tile = null;
		switch(card.getCardname()) {
			case "Staff of Y'Kir'":
				tile = AIActionUtils.searchLowestAiUnitAttack(gameState);
				break;
			case "Entropic Decay":
				tile = AIActionUtils.searchHighestNonAvatarUnitHealth(gameState);
				break;

		}
		return tile;
	}
	
    @Override
    public State processAction(GameState gameState) {
    	List<Card> cards=gameState.board.getAiCards();
    	Optional<Card> card = cards.stream().filter(c -> {
			if(c.getBigCard() != null && c.getBigCard().getHealth() <0) {
				if(gameState.AiPlayer.getMana() >= c.getManacost()) {
					return true;
				}
			}
			return false;
		}).findFirst();
		if(card.isEmpty())
			return null;
		Tile tile = getSpellTile(card.get(), gameState);
		if(tile == null)
			return null;
		return new CardSelectedState(card.get().getCardPosition(), tile, gameState);

    }
}
