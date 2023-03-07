package ai.actions;

import structures.GameState;
import structures.basic.Card;
import structures.basic.Tile;
import structures.basic.UnitAbility;
import structures.statemachine.State;

import java.util.List;

public class CastUnitAction implements AiAction {

    private Card unitCard;
    private Tile targetTile;

    public CastUnitAction(Card unitCard, Tile targetTile) {
        this.unitCard = unitCard;
        this.targetTile = targetTile;

    }
    public List<Tile> getAvailableTiles(GameState gameState) {
        //Find all available tiles to summon on
        List<UnitAbility> abilityList= gameState.unitAbilityTable.getUnitAbilities(unitCard.getCardname());
        if(abilityList.contains(UnitAbility.SUMMON_ANYWHERE)) {
            //Return all empty tiles
        }
        return null;
    }
    @Override
    public State processAction(GameState gameState) {
        return null;
    }
}
