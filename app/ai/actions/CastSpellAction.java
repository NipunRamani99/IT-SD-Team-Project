package ai.actions;

import ai.AIActionUtils;
import structures.GameState;
import structures.basic.Card;
import structures.basic.Tile;
import structures.statemachine.CardSelectedState;
import structures.statemachine.State;

import java.util.List;
import java.util.Optional;

/**
 * CastSpellAction creates the state change required to cast a spell card.
 */
public class CastSpellAction implements AiAction{

	public CastSpellAction()
	{

	}

	/**
	 *
	 * @param card
	 * @param gameState
	 * @return
	 */
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

	/**
	 * This method generates the state change required to cast a spell which is present in the deck.
	 * It will find the first spell in the deck which can be cast on the board and find an appropriate tile based on the spell name.
	 *
	 * @param gameState
	 * @return CardSelectedState
	 */
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
