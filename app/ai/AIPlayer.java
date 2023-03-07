package ai;

import ai.actions.PursueAction;
import ai.actions.Action;
import ai.actions.UnitAttackAction;
import org.hibernate.validator.internal.util.privilegedactions.GetAnnotationAttribute;
import structures.GameState;
import structures.basic.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import events.EventProcessor;
import structures.statemachine.EndTurnState;
import structures.statemachine.NoSelectionState;
import structures.statemachine.State;
import structures.statemachine.UnitMovingState;

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

    private TurnCache turnCache = null;

    private List<Action> aiActions = new ArrayList<>();

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
    public boolean searchAction(GameState gameState) {
        //Create a list of actions to be performed
        //Check if deck of cards has unit card

            nextAiMove = null;
            turnCache.aiUnits = turnCache.getAvailableUnits(gameState);
            markEnemy();
            pursueEnemy();
            for(Action action : aiActions) {
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
                         Action action = null;
                         if(aiUnit.canAttack() && aiUnit.withinDistance(markedUnit)) {
                             action = new UnitAttackAction(aiUnit, markedUnit);
                         } else if(aiUnit.getMovement()) {
                             action = new PursueAction(markedUnit, aiUnit);
                         }
                         aiActions.add(action);
                         turnCache.aiUnits.remove(aiUnit);
                     }));

//             turnCache.aiUnits.removeIf(aiUnit -> {
//                 boolean hit = (aiUnit.canAttack() && aiUnit.withinDistance(markedUnit)) || aiUnit.getMovement();
//                 if(!hit) return false;
//                 Action pursueAction = new PursueAction(markedUnit, aiUnit);
//                 aiActions.add(pursueAction);
//                 return true;
//             });
        }
    }

    public State getNextAiMove() {
        return nextAiMove;
    }
}
