package ai.actions;

import commands.AbilityCommands;
import structures.GameState;
import structures.basic.Unit;
import structures.statemachine.State;
import structures.statemachine.UnitAttackState;

/**
 * UnitAttackAction generates the state change required to make an AI unit attack a player unit.
 */
public class UnitAttackAction implements AiAction{

    private Unit aiUnit = null;
    private Unit markedUnit = null;

    public UnitAttackAction(Unit aiUnit, Unit markedUnit) {
        this.aiUnit = aiUnit;
        this.markedUnit = markedUnit;
    }

    /**
     * This method generates the UnitAttackState to make the AI unit attack the player unit
     * @param gameState
     * @return UnitAttackState
     */
    public State processAction(GameState gameState) {  
        if(aiUnit.withinDistance(markedUnit) && aiUnit.canAttack()) {
        	//check if the unit is provoked
        	AbilityCommands.checkIsProvoked(aiUnit, gameState);
        	if(!aiUnit.isIsProvoked() || gameState.board.getTile(markedUnit.getPosition()).getUnit().isHasProvoke()) {
            return new UnitAttackState(aiUnit, gameState.board.getTile(markedUnit.getPosition()), false, false);}
        }
        return null;
    }
}
