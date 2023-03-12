package ai.actions;

import commands.AbilityCommands;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Unit;
import structures.statemachine.State;
import structures.statemachine.UnitAttackState;
import structures.statemachine.UnitSelectedState;

public class UnitAttackAction implements AiAction{

    Unit aiUnit = null;
    Unit markedUnit = null;

    public UnitAttackAction(Unit aiUnit, Unit markedUnit) {
        this.aiUnit = aiUnit;
        this.markedUnit = markedUnit;
    }

    public State processAction(GameState gameState) {
        if(aiUnit.withinDistance(markedUnit) && aiUnit.canAttack()) {
        	//check if the unit is provoked
        	AbilityCommands.checkIsProvoked(aiUnit, gameState);
        	if(!aiUnit.isIsProvoked() || gameState.board.getTile(markedUnit.getPosition()).getUnit().isHasProvoke()) {
            return new UnitAttackState(aiUnit, gameState.board.getTile(markedUnit.getPosition()), false, false);}
        	//return new UnitSelectedState(aiUnit, markedUnit,gameState);
        }
        return null;
    }
}
