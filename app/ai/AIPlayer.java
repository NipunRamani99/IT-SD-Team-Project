package ai;

import ai.actions.*;
import akka.actor.ActorRef;
import structures.GameState;
import structures.basic.Card;
import structures.basic.Unit;
import structures.statemachine.GameStateMachine;
import structures.statemachine.State;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

class TurnCache {
    public List<Unit> markedUnits = new ArrayList<>();
    public List<Unit> aiUnits = new ArrayList<>();
    public List<Unit> playerUnits = new ArrayList<>();

    public TurnCache() {

    }

    public TurnCache(GameState gameState) {
        this.playerUnits = searchTargets(gameState);
        this.aiUnits = getAvailableUnits(gameState);
    }

    public List<Unit> searchTargets(GameState gameState) {
        List<Unit> units = gameState.board.getUnits().stream().filter((unit -> !unit.isAi())).collect(Collectors.toList());
        return units;
    }

    public List<Unit> getAvailableUnits(GameState gameState) {
        List<Unit> units = gameState.board.getUnits();
        units = units.stream().filter((unit -> {
            return unit.isAi() && (unit.canAttack() || unit.getMovement());
        })).collect(Collectors.toList());
        return units;
    }
}

/**
 * AIPlayer implements an AI which can analyse the positions on the board, calculate different actions,
 * and execute the actions one by one over the board. The actions it can perform includes moving a unit from one tile to another,
 * selecting a unit and then using it to attack an enemy unit, cast a card from its hand, etc.
 */
public class AIPlayer{

    private boolean canPlay = true;

    private State nextAiMove = null;

    List<Card> cards;

    private TurnCache turnCache = null;

    private List<AiAction> aiActions = new ArrayList<>();

    public AIPlayer() {
        this.nextAiMove = null;
        turnCache = new TurnCache();
    }

    public void beginTurn(GameState gameState) {
        turnCache = new TurnCache(gameState);
    }

    /**
     * The search action method will analyze the AI's position on the board and then find appropriate actions it should perform.
     * The actions it can perform will be stored in the class variable 'actions'.
     */

    public boolean searchAction(ActorRef out,GameState gameState,GameStateMachine gameStateMachine) {
        //Firstly, AI will search and attack
        //get the all the ai hand card
        cards = gameState.board.getAiCards();
        nextAiMove = null;
        turnCache.aiUnits = turnCache.getAvailableUnits(gameState);
        markEnemy();
        aiCastCard(gameState);
        for(AiAction action : aiActions) {
            State s  = action.processAction(gameState);
            if(s == null) continue;
            if(nextAiMove == null) {
                nextAiMove = s;
            } else {
                nextAiMove.appendState(s);
            }
        }
        canPlay = !aiActions.isEmpty();
        if(canPlay) {
            aiActions.clear();
            return canPlay;
        }
        pursueEnemy();
        gameState.AiMarkEnemy=true;
        for(AiAction action : aiActions) {
            State s  = action.processAction(gameState);
            if(s == null) continue;
            if(nextAiMove == null) {
                nextAiMove = s;
            } else {
                nextAiMove.appendState(s);
            }
        }
        canPlay = !aiActions.isEmpty();
        aiActions.clear();
        return canPlay;
    }

    public void markEnemy() {

        turnCache.markedUnits = turnCache.playerUnits;
        turnCache.markedUnits.removeIf(unit->unit.getName().contains("Hailstone Golem"));
        turnCache.markedUnits.sort(Comparator.comparingInt(a->a.getHealth()));
    }

    public void pursueEnemy() {
        if(turnCache.aiUnits.isEmpty())
            return;

        for(Unit markedUnit : turnCache.markedUnits) {
            turnCache.aiUnits.sort(Comparator.comparingInt(a -> a.getDistance(markedUnit)));

            turnCache.aiUnits.stream()
                    .filter(aiUnit -> {return (aiUnit.canAttack() && aiUnit.withinDistance(markedUnit)) || aiUnit.getMovement();})
                    .findFirst()
                    .ifPresent((aiUnit -> {
                        AiAction action = null;
                        if(aiUnit.canAttack() && aiUnit.withinDistance(markedUnit)) {
                            action = new UnitAttackAction(aiUnit, markedUnit);
                        } else if(aiUnit.getMovement() && !aiUnit.withinDistance(markedUnit)) {
                            action = new PursueAction(markedUnit, aiUnit);
                        }
                        if(action != null) {
                            aiActions.add(action);
                            turnCache.aiUnits.remove(aiUnit);
                        }
                    }));
        }

    }

    /**
     * Check the available unit
     * @param unit
     * @param markedUnit
     * @return boolean value
     */
//    private boolean checkAvailableUnit(Unit unit,Unit markedUnit)
//    {
//    	if((unit.canAttack()&&unit.withinDistance(markedUnit))||unit.getMovement())
//    	{
//    		return true;
//    	}
//    	else
//    	{
//    		return false;
//    	}
//    }

    /**
     * Ai cast the card from the hand
     */
    private void aiCastCard(GameState gameState)
    {
        //check the card on hand
        // AiAction castSpell = new CastSpellAction(out);
        Optional<Card> unitCard =  cards.stream().filter(card -> card.getBigCard() != null && card.getBigCard().getHealth() > 0)
                .filter(card -> {return card.getManacost() <= gameState.AiPlayer.getMana();})
                .findFirst();
        if(unitCard.isPresent()) {
            AiAction castUnit = new CastUnitAction(unitCard.get(), this.turnCache.markedUnits);
            aiActions.add(castUnit);
        }
        //aiActions.add(castSpell);

    }


    private int chooseAiCardPosition(GameState gameState)
    {

        for(int i=1;i<=cards.size();i++)
        {
            Card aiCard=cards.get(i-1);
            if(null!=aiCard&&gameState.AiPlayer.getMana()>=aiCard.getManacost())
            {
                return i;
            }
        }
        return 0;
    }

    public State getNextAiMove() {
        return nextAiMove;
    }

}
